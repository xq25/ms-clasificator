package backend.msclasificator.medicalimage.application;

import backend.msclasificator.classification.application.port.out.ImageClassificationRepository;
import backend.msclasificator.clinicalrecord.application.port.out.ClinicalRecordRepository;
import backend.msclasificator.clinicalrecord.domain.ClinicalRecord;
import backend.msclasificator.medicalimage.api.dto.MedicalImageCreateDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageResponseDTO;
import backend.msclasificator.medicalimage.api.dto.MedicalImageSummaryDTO;
import backend.msclasificator.medicalimage.api.mapper.MedicalImageMapper;
import backend.msclasificator.medicalimage.application.exception.ImageStorageException;
import backend.msclasificator.medicalimage.application.port.out.ImageFile;
import backend.msclasificator.medicalimage.application.port.out.ImageStorage;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageRepository;
import backend.msclasificator.medicalimage.application.port.out.MedicalImageTypeRepository;
import backend.msclasificator.medicalimage.domain.MedicalImage;
import backend.msclasificator.medicalimage.domain.MedicalImageType;
import backend.msclasificator.shared.api.ApiResponse;
import backend.msclasificator.shared.api.PageRequestDTO;
import backend.msclasificator.shared.api.PagedResponse;
import backend.msclasificator.shared.application.PageQuery;
import backend.msclasificator.shared.application.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

// Cubre uploadImage (validación de archivo, ADR-012), delete y las lecturas (ADR-008)
@ExtendWith(MockitoExtension.class)
class MedicalImageServiceTest {

    private static final long MB = 1024 * 1024;

    @Mock private MedicalImageRepository medicalImageRepository;
    @Mock private MedicalImageTypeRepository medicalImageTypeRepository;
    @Mock private ImageClassificationRepository imageClassificationRepository;
    @Mock private ClinicalRecordRepository clinicalRecordRepository;
    @Mock private ImageStorage imageStorage;
    @Mock private MedicalImageMapper mapper;

    @InjectMocks private MedicalImageService service;

    private static ImageFile archivo(String contentType, long size) {
        return new ImageFile("rx.png", contentType, size, new ByteArrayInputStream(new byte[0]));
    }

    private static MedicalImageCreateDTO dto(String folder, Integer clinicalRecordId) {
        return MedicalImageCreateDTO.builder().medicalImageTypeId(1).folder(folder).clinicalRecordId(clinicalRecordId).build();
    }

    private void tipoExiste() {
        when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.of(new MedicalImageType()));
    }

    @Nested
    @DisplayName("uploadImage")
    class UploadImage {

        @Test
        void archivoVacio_devuelveError_sinTocarStorage() {
            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(archivo("image/png", 0), dto(null, null));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("El archivo de imagen no puede estar vacío");
            verify(imageStorage, never()).upload(any(), anyString());
        }

        @Test
        void tipoDeImagenMedicaInexistente_devuelveError() {
            when(medicalImageTypeRepository.findById(1)).thenReturn(Optional.empty());

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(archivo("image/png", 100), dto(null, null));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Tipo de imagen medica no encontrada con ID: 1");
            verify(imageStorage, never()).upload(any(), anyString());
        }

        @Test
        void historiaClinicaInexistente_devuelveError() {
            tipoExiste();
            when(clinicalRecordRepository.findById(7)).thenReturn(Optional.empty());

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(archivo("image/png", 100), dto(null, 7));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Clinical record no encontrado con ID: 7");
            verify(imageStorage, never()).upload(any(), anyString());
        }

        @Test
        void tipoMimeNoPermitido_devuelveError_sinSubir() {
            tipoExiste();

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(archivo("image/gif", 100), dto(null, null));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Tipo de imagen no permitido: image/gif");
            verify(imageStorage, never()).upload(any(), anyString());
        }

        @Test
        void tipoMimeNulo_devuelveError_sinSubir() {
            tipoExiste();

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(archivo(null, 100), dto(null, null));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Tipo de imagen no permitido: null");
            verify(imageStorage, never()).upload(any(), anyString());
        }

        @Test
        void archivoMayorA10MB_devuelveError_sinSubir() {
            tipoExiste();

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(archivo("image/png", 10 * MB + 1), dto(null, null));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("La imagen excede el tamaño máximo permitido de 10 MB");
            verify(imageStorage, never()).upload(any(), anyString());
        }

        @Test
        void exito_subeConCarpetaPorDefecto_yGuardaMetadatosDelArchivo() {
            tipoExiste();
            ImageFile file = archivo("application/dicom", 10 * MB); // límite exacto: permitido
            when(imageStorage.upload(file, "diagnostics")).thenReturn("diagnostics/abc.png");
            when(imageStorage.providerName()).thenReturn("minio");
            when(medicalImageRepository.save(any(MedicalImage.class))).thenAnswer(inv -> inv.getArgument(0));
            MedicalImageResponseDTO responseDTO = new MedicalImageResponseDTO();
            when(mapper.toResponseDTO(any(MedicalImage.class))).thenReturn(responseDTO);

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(file, dto("  ", null));

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("Imagen médica subida exitosamente");
            assertThat(r.getData()).isSameAs(responseDTO);
            ArgumentCaptor<MedicalImage> guardada = ArgumentCaptor.forClass(MedicalImage.class);
            verify(medicalImageRepository).save(guardada.capture());
            assertThat(guardada.getValue().getImageKey()).isEqualTo("diagnostics/abc.png");
            assertThat(guardada.getValue().getProvider()).isEqualTo("minio");
            assertThat(guardada.getValue().getContentType()).isEqualTo("application/dicom");
            assertThat(guardada.getValue().getFileSize()).isEqualTo(10 * MB);
            assertThat(guardada.getValue().getClinicalRecord()).isNull();
        }

        @Test
        void exito_conCarpetaEHistoriaClinica() {
            tipoExiste();
            ClinicalRecord record = new ClinicalRecord();
            when(clinicalRecordRepository.findById(7)).thenReturn(Optional.of(record));
            ImageFile file = archivo("image/jpeg", 100);
            when(imageStorage.upload(file, "labels")).thenReturn("labels/abc.jpg");
            when(imageStorage.providerName()).thenReturn("minio");
            when(medicalImageRepository.save(any(MedicalImage.class))).thenAnswer(inv -> inv.getArgument(0));

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(file, dto("labels", 7));

            assertThat(r.isSuccess()).isTrue();
            ArgumentCaptor<MedicalImage> guardada = ArgumentCaptor.forClass(MedicalImage.class);
            verify(medicalImageRepository).save(guardada.capture());
            assertThat(guardada.getValue().getClinicalRecord()).isSameAs(record);
        }

        @Test
        void falloDelStorage_devuelveError_yNoGuarda() {
            tipoExiste();
            ImageFile file = archivo("image/png", 100);
            when(imageStorage.upload(file, "diagnostics"))
                    .thenThrow(new ImageStorageException("No se pudo subir la imagen a MinIO", "minio"));

            ApiResponse<MedicalImageResponseDTO> r = service.uploadImage(file, dto(null, null));

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Error al subir imagen al storage: No se pudo subir la imagen a MinIO");
            verify(medicalImageRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        private MedicalImage imagen() {
            MedicalImage img = MedicalImage.builder().imageKey("diagnostics/abc.png").build();
            when(medicalImageRepository.findById(5)).thenReturn(Optional.of(img));
            return img;
        }

        @Test
        void eliminaDelStorageYLuegoDeLaBD() {
            MedicalImage img = imagen();
            when(imageClassificationRepository.existsByMedicalImageId(5)).thenReturn(false);

            ApiResponse<Void> r = service.delete(5);

            assertThat(r.isSuccess()).isTrue();
            InOrder orden = inOrder(imageStorage, medicalImageRepository);
            orden.verify(imageStorage).delete("diagnostics/abc.png");
            orden.verify(medicalImageRepository).delete(img);
        }

        @Test
        void conClasificaciones_noElimina() {
            imagen();
            when(imageClassificationRepository.existsByMedicalImageId(5)).thenReturn(true);

            ApiResponse<Void> r = service.delete(5);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("No se puede eliminar: la imagen tiene diagnósticos asignados");
            verify(imageStorage, never()).delete(anyString());
            verify(medicalImageRepository, never()).delete(any());
        }

        @Test
        void falloDelStorage_noEliminaDeLaBD() {
            imagen();
            when(imageClassificationRepository.existsByMedicalImageId(5)).thenReturn(false);
            doThrow(new ImageStorageException("boom", "minio")).when(imageStorage).delete("diagnostics/abc.png");

            ApiResponse<Void> r = service.delete(5);

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("Error al eliminar imagen del storage. El registro no fue eliminado.");
            verify(medicalImageRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("lecturas")
    class Lecturas {

        private final PageQuery query = new PageQuery(0, 10, "id", "asc");
        private final MedicalImage imagen = MedicalImage.builder().imageKey("diagnostics/abc.png").build();
        private final MedicalImageResponseDTO responseDTO = new MedicalImageResponseDTO();
        private final MedicalImageSummaryDTO summary = new MedicalImageSummaryDTO();

        @Test
        @DisplayName("findAll: página de resúmenes")
        void findAll() {
            when(medicalImageRepository.findAll(query)).thenReturn(new PageResult<>(List.of(imagen), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(imagen)).thenReturn(summary);

            ApiResponse<PagedResponse<MedicalImageSummaryDTO>> r = service.findAll(new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Imágenes médicas obtenidas exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("count: devuelve el total del repositorio")
        void count() {
            when(medicalImageRepository.count()).thenReturn(36L);

            assertThat(service.count().getData()).isEqualTo(36L);
        }

        @Test
        @DisplayName("findById existente: devuelve el DTO (con la URL que genera el mapper)")
        void findByIdExiste() {
            when(medicalImageRepository.findById(5)).thenReturn(Optional.of(imagen));
            when(mapper.toResponseDTO(imagen)).thenReturn(responseDTO);

            ApiResponse<MedicalImageResponseDTO> r = service.findById(5);

            assertThat(r.getData()).isSameAs(responseDTO);
            assertThat(r.getMessage()).isEqualTo("Imagen médica obtenida exitosamente");
        }

        @Test
        @DisplayName("findById inexistente: error con el ID")
        void findByIdNoExiste() {
            when(medicalImageRepository.findById(5)).thenReturn(Optional.empty());

            assertThat(service.findById(5).getMessage()).isEqualTo("Imagen médica no encontrada con ID: 5");
        }

        @Test
        @DisplayName("findByMedicalImageType con tipo inexistente: error")
        void porTipoInexistente() {
            when(medicalImageTypeRepository.existsById(1)).thenReturn(false);

            ApiResponse<PagedResponse<MedicalImageResponseDTO>> r = service.findByMedicalImageType(1, new PageRequestDTO());

            assertThat(r.isSuccess()).isFalse();
            assertThat(r.getMessage()).isEqualTo("No se encontró el tipo de imagen médica con ID: 1");
        }

        @Test
        @DisplayName("findByMedicalImageType: página con el DTO de respuesta")
        void porTipo() {
            when(medicalImageTypeRepository.existsById(1)).thenReturn(true);
            when(medicalImageRepository.findByMedicalImageTypeId(1, query)).thenReturn(new PageResult<>(List.of(imagen), 0, 10, 1, 1, true));
            when(mapper.toResponseDTO(imagen)).thenReturn(responseDTO);

            ApiResponse<PagedResponse<MedicalImageResponseDTO>> r = service.findByMedicalImageType(1, new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Imágenes médicas obtenidas exitosamente");
            assertThat(r.getData().getContent()).containsExactly(responseDTO);
        }

        @Test
        @DisplayName("findByClinicalRecord con historia inexistente: error")
        void porHistoriaInexistente() {
            when(clinicalRecordRepository.existsById(7)).thenReturn(false);

            ApiResponse<PagedResponse<MedicalImageSummaryDTO>> r = service.findByClinicalRecord(7, new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("No se encontró el registro clínico con ID: 7");
        }

        @Test
        @DisplayName("findByClinicalRecord con imágenes: mensaje de encontradas")
        void porHistoria() {
            when(clinicalRecordRepository.existsById(7)).thenReturn(true);
            when(medicalImageRepository.findByClinicalRecordId(7, query)).thenReturn(new PageResult<>(List.of(imagen), 0, 10, 1, 1, true));
            when(mapper.toSummaryDTO(imagen)).thenReturn(summary);

            ApiResponse<PagedResponse<MedicalImageSummaryDTO>> r = service.findByClinicalRecord(7, new PageRequestDTO());

            assertThat(r.getMessage()).isEqualTo("Imágenes médicas obtenidas exitosamente");
            assertThat(r.getData().getContent()).containsExactly(summary);
        }

        @Test
        @DisplayName("findByClinicalRecord sin imágenes: éxito con mensaje de vacío")
        void porHistoriaVacia() {
            when(clinicalRecordRepository.existsById(7)).thenReturn(true);
            when(medicalImageRepository.findByClinicalRecordId(7, query)).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0, true));

            ApiResponse<PagedResponse<MedicalImageSummaryDTO>> r = service.findByClinicalRecord(7, new PageRequestDTO());

            assertThat(r.isSuccess()).isTrue();
            assertThat(r.getMessage()).isEqualTo("No se encontraron imágenes médicas para este clinical record");
        }

        @Test
        @DisplayName("findUndiagnosedByDoctorAndMedicalImageType con tipo inexistente: error")
        void pendientesTipoInexistente() {
            when(medicalImageTypeRepository.existsById(1)).thenReturn(false);

            assertThat(service.findUndiagnosedByDoctorAndMedicalImageType(2, 1).getMessage())
                    .isEqualTo("No se encontró el tipo de imagen médica con ID: 1");
        }

        @Test
        @DisplayName("findUndiagnosedByDoctorAndMedicalImageType: imágenes que el doctor aún no clasificó")
        void pendientes() {
            when(medicalImageTypeRepository.existsById(1)).thenReturn(true);
            when(medicalImageRepository.findUndiagnosedImagesByDoctorAndMedicalImageType(2, 1)).thenReturn(List.of(imagen));
            when(mapper.toResponseDTO(imagen)).thenReturn(responseDTO);

            ApiResponse<List<MedicalImageResponseDTO>> r = service.findUndiagnosedByDoctorAndMedicalImageType(2, 1);

            assertThat(r.getMessage()).isEqualTo("Imágenes médicas no diagnosticadas obtenidas exitosamente");
            assertThat(r.getData()).containsExactly(responseDTO);
        }
    }
}
