# ms-clasificator — Contexto del proyecto

## Propósito
Microservicio de clasificación de imágenes médicas. Permite crear **datasets** de entrenamiento/evaluación donde se especifica:
- Qué tipo de imagen médica se va a clasificar (Rx de tórax, ECG, RM cerebral, etc.)
- Bajo qué **diagnóstico médico padre** se clasifica (CIE-10)
- En qué **categorías** (grupos de sub-diagnósticos CIE-10) se divide la clasificación

Los doctores usan el sistema para etiquetar imágenes de pacientes según la configuración del dataset.

---

## Stack
| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.0.5 |
| Persistencia | Spring Data JPA + Hibernate |
| BD | MySQL 8 |
| Almacenamiento de imágenes | MinIO (dev/docker), AWS S3 compatible |
| Seguridad | Interceptor HTTP → ms-security (JWT) |
| Build | Maven |
| Contenedores | Docker + docker-compose |

Puerto por defecto: **8081**

---

## Arquitectura del módulo
```
Controllers → Services → Repositories → Models
                   ↓
             DTOs / Mappers
```

Cada entidad sigue el patrón: `Model`, `CreateDTO`, `UpdateDTO`, `ResponseDTO`, `SummaryDTO`, `Mapper`.

---

## Dominio: modelo conceptual

### Jerarquía de usuarios
- **Patient** — paciente con `document` (único), `dob`, `sex`, `userId` (FK a ms-security).
  - Tiene muchas **ClinicalRecord** (historias clínicas).
- **Doctor** — doctor con `code` único y `userId` (FK a ms-security).
  - Se asigna a **EvaluationArea** vía **DoctorArea** (N:M).

### Historia clínica y diagnósticos del paciente
- **ClinicalRecord** — visita médica con `chiefComplaint` y `visitDate`.
  - Tiene muchos **PatientDatum** (datos clínicos como signos vitales, laboratorio).
  - Tiene muchos **Diagnosis** → cada uno apunta a un **MedicalDiagnostic**.

### Imágenes médicas
- **MedicalImageType** — tipo de imagen (Electrocardiograma, Rx de tórax, etc.), asociado opcionalmente a un **EvaluationArea**.
- **MedicalImage** — imagen almacenada. Campos clave: `imageKey` (clave en storage), `provider` (minio/s3/r2), `contentType`, `fileSize`. **NO guarda URL** (se genera on-demand). Puede o no estar ligada a un **ClinicalRecord**.

### Clasificación por doctores ("Tinder médico", ADR-010)
- **ImageClassification** — el doctor **desliza** una imagen hacia una **DatasetCategory** del dataset de su tipo (Doctor + MedicalImage único por par, + `datasetCategory`). Un deslizamiento = un `POST` atómico. Los diagnósticos CIE-10 son la **definición de la categoría vigente al deslizar** (ADR-011); la clase es su `numValue`.
  - **ClassificationDiagnosis** — detalle opcional: sub-diagnósticos de esa definición de la categoría. Las clasificaciones previas a ADR-010 no tienen categoría.
- **Soft delete (ADR-011):** `Dataset`, `DatasetCategory` y `CategoryDiagnostic` se **retiran** (`active=false` + `deleted_at`, solo vía `retire()`), nunca se borran. Los puertos leen solo activos salvo métodos `...IncludingRetired`/`findDefinitionAt`. Unicidad solo entre activos; `numValue` nunca se reutiliza. No usar `@SQLRestriction`.

### Datasets
- **MedicalDiagnostic** — diagnóstico CIE-10, con jerarquía `parentDiagnostic` (self-referencing). Pre-cargados vía `docker/init/02-data.sql`.
- **Dataset** — configuración de clasificación. Vincula 1 **MedicalDiagnostic** (padre) con 1 **MedicalImageType**. Un tipo de imagen solo puede tener un dataset **activo** (puede tener retirados).
- **DatasetCategory** — categoría dentro del dataset (ej. "Normal", "Patológico"). Tiene `numValue` para encoding.
  - Contiene muchos **CategoryDiagnostic** → asigna sub-diagnósticos CIE-10 a esta categoría.

### Áreas y datos primitivos
- **EvaluationArea** — área hospitalaria (Cardiología, Neuroimagen, etc.). Pre-cargadas vía `docker/init/02-data.sql`.
- **PrimitiveDatum** (extiende `SystemDatum`) — definición de un dato clínico (nombre, tipo, unidad). Pre-cargados vía `docker/init/02-data.sql` (35 datos: signos vitales, glucemia, perfil lipídico, etc.).
- **PatientDatum** — valor concreto de un dato primitivo para un **ClinicalRecord**.

---

## Seguridad (inter-servicios)
- Cada request pasa por **SecurityInterceptor** que llama a `ms-security` en `/api/public/security/permissions-validation` con el JWT del cliente.
- Las llamadas internas al ms-security usan un header `secretKey` (valor en `ms.secret.key`).
- Al crear Doctor o Patient, se llama a ms-security para verificar que el `userId` exista y asignarle el rol por defecto (`patient` / `doctor`).

---

## Configuración por perfil
| Perfil | BD | MinIO | ms-security |
|--------|-----|-------|------------|
| `dev` (default) | `localhost:3306/db-clasificator` | `localhost:9000` | `localhost:8080` |
| `docker` | `mysql:3306/db-clasificator` | `minio:9000` | `ms-security:8080` |

Archivo base: `application.properties`. Sobreescrituras: `application-dev.yml`, `application-docker.properties`.

---

## Esquema e inicialización de la BD (ADR-011)
- **Esquema versionado:** `docker/init/01-schema.sql` (generado desde las entidades y revisado). La app usa `spring.jpa.hibernate.ddl-auto=validate`: **si cambias una entidad, actualiza `01-schema.sql`** o la app no arranca.
- **Datos:** `docker/init/02-data.sql` (catálogos, `INSERT IGNORE`).
- Docker: MySQL los ejecuta en orden (`docker-entrypoint-initdb.d`) solo cuando el volumen está vacío. Local: ejecutarlos a mano en orden contra `db-clasificator`.

Catálogos que carga `02-data.sql`:
1. **MedicalDiagnostic** — 86 diagnósticos padre + ~206 hijos, siguiendo CIE-10 (capítulos I, II, IX, X, XI, XIII, XIV, XIX).
2. **EvaluationArea** — 5 áreas (CARDIO, CARDINT, NEURO, PULMON, OFTALMO).
3. **MedicalImageType** — 13 tipos de imagen.
4. **PrimitiveDatum** — 35 datos clínicos (signos vitales, glucemia, lípidos, función renal, hepática, hemograma, electrolitos, marcadores cardíacos, tiroides, orina).

---

## Convenciones de desarrollo
- Respuesta estándar: `ApiResponse<T>` con `success`, `data` y `message`.
- Paginación: `PageRequestDTO.toPageQuery()` → puerto devuelve `PageResult<T>` → `PagedResponse<T>` (ADR-007).
- Mappers manuales (sin MapStruct): un Mapper por entidad en `<feature>/api/mapper/`.
- Tests (ADR-008): unitarios de servicios con JUnit 5 + Mockito (sin Spring ni BD), de **caracterización** (fijan el comportamiento actual; bugs marcados `BUG CONOCIDO`). En `src/test/java/.../<feature>/application/<Servicio>Test.java`. Ejecutar: `mvnw test -Dtest='*ServiceTest,*ControllerTest' -Dsurefire.failIfNoSpecifiedTests=false`. Cubierto: `dataset`, `PatientService`, `SecurityService.getUserInfo`, las búsquedas y el alta de `DoctorService`, `InternalController` (registro), `ClassificationDiagnosisService` e `ImageClassificationService` (create y findById). `contextLoads` requiere MySQL + MinIO.
- Bugs detectados y pendientes de decisión: [`docs/bugs-conocidos.md`](docs/bugs-conocidos.md). No se corrigen dentro de refactors.
- Servicios externos (ADR-009): el cliente del servicio externo lanza una excepción propia del feature (p. ej. `SecurityServiceException`) ante fallo o respuesta incompleta; nunca devuelve `null`. El `GlobalExceptionHandler` la traduce a **503**. Los servicios no la capturan; si tienen `catch (Exception)` deben relanzarla antes. Excepción: `InternalController` (endpoints que llama ms-security) la convierte en `false`. Altas de Patient/Doctor: `@Transactional`, guardar y asignar rol como último paso (bug #9).
- Persistencia (ADR-007): puerto `XRepository` en `application/port/out/`, Spring Data `XJpaRepository` + `XPersistenceAdapter` en `infrastructure/persistence/`. Todos los features migrados. Los métodos del puerto usan el vocabulario de dominio actual; el adaptador traduce a las consultas derivadas JPA (que conservan nombres de campo viejos, ADR-003).
- Proceso: **explicar + ejemplificar ANTES de aplicar cambios**; solo implementar con autorización explícita.

---

## Estructura de carpetas (hexagonal por feature — ADR-001)
```
src/main/java/backend/msclasificator/
├── <feature>/                 patient, doctor, clinicalrecord, diagnosiscatalog,
│   │                          medicalimage, dataset, classification, identity
│   ├── api/                   Controllers  (+ dto/, mapper/)
│   ├── application/           Services     (+ port/out/ interfaces hacia infraestructura)
│   ├── domain/                Entidades y enums
│   └── infrastructure/        Adaptadores: persistence/ (Spring Data), storage/ ...
├── shared/                    ApiResponse, paginación, Mapper base, excepciones globales, SystemDatum
└── platform/                  config/ (CORS, Web, HttpClient) y security/ (SecurityInterceptor)
```
Regla de dependencia: `api → application → domain`; `infrastructure` implementa puertos de `application`.
Estado: fase 1 completada (paquetes y nombres). Fase 2 completada: todos los features usan puertos de persistencia; ninguna capa `application` importa `infrastructure` ni Spring Data (ADR-007). Fase 3 pendiente: eliminar `PageRequestDTO.toPageable()`, puerto hacia ms-security (`SecurityService` usa `RestTemplate`) y revisar el puerto de storage (`MultipartFile`).

---

## Reglas de trabajo
- **Explicar + ejemplificar antes de aplicar cambios**; implementar solo con autorización explícita.
- **No agregar librerías/dependencias sin aprobación** (ADR-004).
- **Ramas (ADR-005):** `feat/re-estructuracion` es la rama de integración (nueva main). Cada actividad sale de ella como `refactor/…`, `feat/…`, `fix/…`, `docs/…` o `test/…` (ADR-008) y vuelve a ella; `main` se actualiza solo al final.
- **Integración vía PR (ADR-006):** Claude crea la rama de actividad, hace commit y `push` de esa rama. El usuario abre el PR (base `feat/re-estructuracion`) y hace el merge en GitHub. Claude no hace merge ni push directo a `feat/re-estructuracion`/`main`. Antes de cada actividad: `git pull --ff-only` en `feat/re-estructuracion`.
- **Toda decisión de arquitectura se registra como ADR** en [`docs/adr/`](docs/adr/README.md).
- Renombrar clases no debe cambiar esquema ni API: fijar `@Table`/secuencias y conservar campos JSON y rutas (ADR-003).

---

## Documentación interna
- [`docs/adr/`](docs/adr/README.md) — decisiones de arquitectura (ADR-001…)
- [`docs/domain-model.md`](docs/domain-model.md) — modelo de dominio
- [`docs/bugs-conocidos.md`](docs/bugs-conocidos.md) — bugs detectados pendientes de corrección
