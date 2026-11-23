# ADR-012: Puertos hacia servicios externos (identidad y almacenamiento)

- **Estado:** Aceptado
- **Fecha:** 2026-11-23
- **Área:** Arquitectura

## Contexto
La fase 2 ([ADR-007](ADR-007-puertos-de-persistencia.md)) dejó a la capa `application` sin dependencias de Spring Data. Aún quedaban dos dependencias de infraestructura en `application`:
- `identity/application/SecurityService` llamaba a ms-security con `RestTemplate` y `org.springframework.http`. Patient y Doctor dependían de esa clase concreta.
- El puerto de storage de `medicalimage` (`ImageStorageService`) recibe `MultipartFile`, un tipo de la capa web.

Por eso cambiar el proveedor de identidad o el de almacenamiento obligaba a tocar la lógica de negocio, y los tests de los servicios tenían que simular una clase HTTP.

## Decisión

### 1. Puerto de identidad: `IdentityProvider`
| Pieza | Ubicación | Nombre |
|-------|-----------|--------|
| Puerto | `identity/application/port/out/` | `IdentityProvider` |
| Adaptador HTTP | `identity/infrastructure/http/` | `MsSecurityIdentityProvider` (`@Component`, visibilidad de paquete) |

- El puerto expone solo lo que la aplicación usa, con nombres de dominio:
  - `existsUser(userId)`, antes `existUserById`;
  - `assignDefaultRole(userId, roleKey)`;
  - `getUserInfo(userId)`.
- Se elimina `existUserByEmail` (sin consumidores). `getUserNameById` y `getUserEmailById` pasan a ser detalles privados del adaptador.
- El contrato de errores de [ADR-009](ADR-009-errores-de-servicios-externos.md) es parte del puerto:
  - ante fallo o respuesta incompleta, el adaptador lanza `SecurityServiceException` (→ 503) y nunca devuelve `null`;
  - `existsUser` devuelve `false` solo si ms-security responde que el usuario no existe (404 o `data=false`).
- `SecurityServiceException` se queda en `identity/application/exception`: es parte del contrato del puerto, no de la infraestructura.
- `PatientService` y `DoctorService` dependen del puerto. Las URLs, el header `secretKey` y las transacciones de alta (bug #9) no cambian.

### 2. Puerto de almacenamiento: `ImageStorage`
Aplicado en `refactor/fase-3-puerto-storage`.

| Pieza | Ubicación | Nombre |
|-------|-----------|--------|
| Archivo | `medicalimage/application/port/out/` | `record ImageFile(String originalFilename, String contentType, long size, InputStream content)` |
| Puerto | `medicalimage/application/port/out/` | `ImageStorage` (antes `ImageStorageService`) |
| Adaptador MinIO | `medicalimage/infrastructure/storage/` | `MinioImageStorage` (visibilidad de paquete, lo crea `StorageConfig`) |
| Adaptador S3/R2 | `medicalimage/infrastructure/storage/` | `S3ImageStorage` (comentado hasta incorporar el AWS SDK, ADR-004) |

- `ImageFile` reemplaza a `MultipartFile` en `application`. `MultipartFile` solo existe en `api`.
- El puerto expone `upload(ImageFile, folder)`, `generatePublicUrl`, `delete`, `exists` y `providerName`.
- **Validación en `MedicalImageService`:**
  - las validaciones de tipo MIME (jpeg, png, webp, tiff, dicom) y tamaño (10 MB) pasan del adaptador MinIO al servicio;
  - mismos mensajes y mismo punto del flujo: después de buscar el tipo de imagen y la historia clínica, antes de subir;
  - un adaptador nuevo no tiene que repetirlas.
- La generación del `imageKey` (`{folder}/{uuid}.{extensión}`) sigue en el adaptador (`ImageStorageUtils`), porque es un detalle de almacenamiento.
- **El stream lo cierra quien lo abre:**
  - el controlador abre el stream del `MultipartFile` con `try-with-resources` y lo cierra al terminar;
  - los adaptadores solo lo leen;
  - si leer el archivo falla (`IOException`), el controlador responde 400 con `"No se pudo leer el archivo de imagen: ..."`. Antes ese caso caía en el adaptador como error de storage.

## Alternativas consideradas
- **Mantener `SecurityService` como clase concreta en `application`.** Se descarta porque deja `RestTemplate` dentro de `application` e incumple la regla de dependencias de ADR-001.
- **Mover `SecurityServiceException` a `infrastructure`.** Se descarta porque los servicios la relanzan y el `GlobalExceptionHandler` la traduce. Es parte del contrato que ven los consumidores.
- **Llamar al puerto `SecurityGateway` o `UserDirectory`.** Se elige `IdentityProvider` porque describe el rol (proveedor de identidad) sin atarlo a ms-security.

## Consecuencias
- Cambiar de proveedor de identidad (otro microservicio, un IdP) solo requiere un adaptador nuevo.
- Cambiar de proveedor de almacenamiento (activar S3/R2) solo requiere el adaptador y su bean en `StorageConfig`. La validación del archivo no se repite.
- Los tests de `PatientService`, `DoctorService` y `MedicalImageService` simulan los puertos. El adaptador conserva sus propios tests sobre `RestTemplate` (`MsSecurityIdentityProviderTest`).
- Los tests de adaptadores no terminan en `ServiceTest`, así que el comando de ADR-008 se amplía con `*ProviderTest`.
