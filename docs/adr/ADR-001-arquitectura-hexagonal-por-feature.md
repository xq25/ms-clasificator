# ADR-001: Arquitectura hexagonal organizada por feature

- **Estado:** Aceptado
- **Fecha:** 2026-11-19
- **Área:** Arquitectura

## Contexto
El proyecto aún no se despliega y siguen abiertas decisiones de infraestructura: motor de base de datos, proveedor de almacenamiento de imágenes (MinIO, S3, R2) y forma de integración con `ms-security`.

La estructura original estaba organizada por capa técnica (`Controllers/`, `Services/`, `Repositories/`, `Models/`, `DTOs/`, `Mappers/`). Los servicios dependían directamente de detalles de infraestructura:
- `JpaRepository` y `Page` de Spring Data
- `RestTemplate` para llamar a `ms-security`
- `MultipartFile` en la lógica de imágenes

Con esa dependencia, cambiar una tecnología obligaba a tocar la lógica de negocio.

## Decisión
1. **Organización por feature (bounded context ligero).** Cada feature agrupa todo lo suyo:
   ```
   <feature>/
   ├── api/                 Controllers REST
   │   ├── dto/             DTOs de entrada/salida
   │   └── mapper/          Conversión entidad ↔ DTO
   ├── application/         Servicios (casos de uso)
   │   └── port/out/        Interfaces que la aplicación necesita (persistencia, storage, servicios externos)
   ├── domain/              Entidades y enums del dominio
   └── infrastructure/      Adaptadores: persistence/ (Spring Data), storage/, http/...
   ```
2. **Features definidas:**

   | Feature | Contenido |
   |---------|-----------|
   | `patient` | Patient, PatientSex |
   | `doctor` | Doctor, DoctorArea, EvaluationArea |
   | `clinicalrecord` | ClinicalRecord, Diagnosis, PatientDatum, PrimitiveDatum (+ tipos y unidades) |
   | `diagnosiscatalog` | MedicalDiagnostic (catálogo CIE-10) |
   | `medicalimage` | MedicalImage, MedicalImageType, puerto y adaptadores de storage |
   | `dataset` | Dataset, DatasetCategory, CategoryDiagnostic |
   | `classification` | ImageClassification, ClassificationDiagnosis |
   | `identity` | Integración con ms-security y endpoints internos |
   | `shared` | ApiResponse, paginación, Mapper base, manejo global de excepciones, `SystemDatum` |
   | `platform` | Configuración transversal (CORS, Web, HttpClient) e interceptor de seguridad |

3. **Regla de dependencia:** `api → application → domain`. `infrastructure` implementa los puertos de `application`. Ni `domain` ni `application` importan Spring Data, `RestTemplate` ni `MultipartFile`. Entre features solo se permiten dependencias hacia `domain` o `application` de la otra feature, nunca hacia su `infrastructure`.

4. **Aplicación incremental:**
   - Fase 1: reorganizar paquetes y renombrar clases ([ADR-003](ADR-003-convencion-de-nombres-y-paquetes.md)). Mientras dure, los servicios siguen usando los repositorios Spring Data, que ya viven en `infrastructure/persistence`.
   - Fase 2: puertos de persistencia por feature.
   - Fase 3: puerto de identidad (`ms-security`) y ajuste del puerto de storage.
   - Fase 4: verificación automática de la regla, con la herramienta pendiente de aprobación según [ADR-004](ADR-004-politica-de-dependencias.md).

## Alternativas consideradas
- **Mantener la organización por capa con subcarpetas.** Se descarta porque dispersa cada feature en seis carpetas y no deja ver la frontera entre lo que es dominio y lo que es infraestructura.
- **Módulos Maven separados por capa.** Se descarta por ahora. Hace cumplir la regla en compilación, pero es demasiado costoso para el tamaño actual del proyecto.

## Consecuencias
- Cambiar de base de datos o de proveedor de storage solo requiere un adaptador nuevo.
- Cada feature se puede leer y extraer a otro microservicio de forma aislada.
- Entre la fase 1 y la 2 la regla de dependencia se incumple de forma conocida y temporal: `application` importa repositorios de `infrastructure`.
- Algunas entidades se relacionan entre features (por ejemplo, `Dataset → MedicalDiagnostic`). Por ahora se mantienen como referencias JPA directas ([ADR-002](ADR-002-entidades-jpa-en-dominio.md)).
