# Modelo de Dominio — ms-clasificator

## Diagrama de relaciones conceptuales

```
ms-security (externo)
    ↑ userId (FK lógica)
    │
    ├── Patient ──────────────────────────────────────────────────────┐
    │     └── ClinicalRecord (1:N)                                     │
    │           ├── PatientDatum (1:N) ← PrimitiveDatum               │
    │           ├── Diagnosis (1:N) → MedicalDiagnostic               │
    │           └── MedicalImage (opcional, N:1)                         │
    │                                                                  │
    └── Doctor                                                         │
          ├── DoctorArea (N:M) → EvaluationArea                       │
          └── ImageClassification (1:N)                                    │
                ├── MedicalImage ─────────────────────────────────────── ┘
                └── ClassificationDiagnosis (1:N) → MedicalDiagnostic


MedicalDiagnostic (CIE-10, jerárquico)
    └── parentDiagnostic (self-ref)
    └── Dataset (1:N)
          └── DatasetCategory (1:N)
                └── CategoryDiagnostic (1:N) → MedicalDiagnostic (sub-dx)

MedicalImageType → EvaluationArea (opcional)
    └── Dataset (1:1)
```

---

## Entidades principales

### Patient
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK |
| document | String (unique) | Documento de identidad |
| dob | Date | Fecha de nacimiento |
| sex | String | Sexo |
| userId | String (unique) | FK al usuario en ms-security |

### Doctor
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK |
| code | String (unique) | Código del médico |
| userId | String (unique, immutable) | FK al usuario en ms-security |

### ClinicalRecord
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK |
| chiefComplaint | String | Motivo de consulta |
| visitDate | Date | Fecha de visita |
| patient | Patient | Paciente (ManyToOne) |

### MedicalDiagnostic
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK |
| diagnosticCode | String (unique) | Código CIE-10 |
| diagnosticName | String | Nombre del diagnóstico |
| parentDiagnostic | MedicalDiagnostic | Diagnóstico padre (nullable) |

### Dataset
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK |
| name | String (unique) | Nombre del dataset |
| medicalDiagnostic | MedicalDiagnostic | Diagnóstico a clasificar |
| medicalImageType | MedicalImageType | Tipo de imagen a clasificar (1:1) |

**Restricciones:**
- Un `MedicalDiagnostic` solo puede tener un Dataset activo.
- Un `MedicalImageType` solo puede estar en un Dataset.
- No se puede cambiar el diagnóstico si ya existen `DatasetCategory` definidas.

### DatasetCategory
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK |
| name | String | Nombre de la categoría |
| numValue | Integer | Valor numérico para encoding ML |
| dataset | Dataset | Dataset al que pertenece |

### CategoryDiagnostic
Tabla de relación: asigna sub-diagnósticos CIE-10 a una categoría del dataset.
| Campo | Tipo |
|-------|------|
| datasetCategory | DatasetCategory |
| medicalDiagnostic | MedicalDiagnostic |

### MedicalImageType
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK |
| name | String (unique) | Nombre del tipo |
| evaluationArea | EvaluationArea | Área asignada (opcional) |

### MedicalImage
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Integer | PK (hereda de SystemDatum) |
| imageKey | String (unique) | Clave en el storage (ej. `diagnostics/uuid.jpg`) |
| provider | String | `minio`, `s3`, `r2` |
| contentType | String | MIME type |
| fileSize | Long | Bytes |
| createdAt | LocalDateTime | Timestamp de creación |
| medicalImageType | MedicalImageType | Tipo de imagen |
| clinicalRecord | ClinicalRecord | Historia clínica (nullable) |

> La URL **no** se persiste. Se genera on-demand desde `imageKey` usando el puerto `ImageStorage` (ADR-012).

### ImageClassification
Acto de clasificación: un doctor diagnostica una imagen.
| Campo | Tipo | Descripción |
|-------|------|-------------|
| doctor | Doctor | Médico clasificador |
| medicalImg | MedicalImage | Imagen clasificada |
| diagnosticDate | LocalDateTime | Fecha del diagnóstico |

**Restricción:** unique(doctor_id, medical_img_id) — un doctor no puede diagnosticar la misma imagen dos veces.

### ClassificationDiagnosis
Sub-diagnósticos asignados por un doctor en un `ImageClassification`.
| Campo | Tipo |
|-------|------|
| imageDiagnostic | ImageClassification |
| medicalDiagnostic | MedicalDiagnostic |

**Restricción:** unique(image_diagnostic_id, medical_diagnostic_id).

### EvaluationArea
| Campo | Tipo | Descripción |
|-------|------|-------------|
| codeArea | String (unique) | Código del área |
| name | String (unique) | Nombre |
| doctorsCount | Integer | Conteo de doctores (denormalizado) |

### PrimitiveDatum (extiende SystemDatum)
Definición de un dato clínico medible.
| Campo | Tipo | Descripción |
|-------|------|-------------|
| name | String | Nombre (ej. "Frecuencia Cardíaca") |
| type | PrimitiveType | DOUBLE, INTEGER, etc. |
| unit | String | Unidad (BPM, MMHG, etc.) |

### PatientDatum
Valor de un `PrimitiveDatum` para una historia clínica específica.

---

## Flujo de clasificación

1. **Configuración previa** (admin)
   - Crear `Dataset` vinculando `MedicalDiagnostic` padre + `MedicalImageType`.
   - Crear `DatasetCategory` para ese dataset.
   - Asignar sub-diagnósticos CIE-10 a cada `DatasetCategory` vía `CategoryDiagnostic`.

2. **Carga de imágenes**
   - Se sube una imagen → se crea `MedicalImage` con `imageKey` en MinIO/S3.
   - Opcionalmente se asocia a un `ClinicalRecord` del paciente.

3. **Clasificación por doctor (deslizamiento, ADR-010)**
   - El doctor recibe las imágenes pendientes de un tipo (`findUndiagnosedImagesByDoctorAndMedicalImageType`).
   - Desliza cada imagen hacia una **categoría** del dataset: una sola petición `POST /api/image-diagnostics` con `doctorId`, `medicalImgId` y `datasetCategoryId`.
   - La clasificación guarda la categoría. Sus diagnósticos CIE-10 son los de esa categoría y su clase es el `numValue`.
   - Opcionalmente, el doctor puede precisar sub-diagnósticos de esa misma categoría (`ClassificationDiagnosis`).

---

## Reglas de negocio

Reglas vigentes del feature `dataset`. Están verificadas por los tests de `src/test/java/.../dataset/application/`. Los comportamientos incorrectos aún no corregidos están en [bugs-conocidos.md](bugs-conocidos.md).

### Dataset
- El nombre del dataset es único.
- Un `MedicalDiagnostic` solo puede tener un dataset, y un `MedicalImageType` solo puede estar en un dataset. Al actualizar, estas validaciones excluyen al propio dataset.
- Si el dataset ya tiene categorías (activas o retiradas), **no se puede cambiar su diagnóstico**, porque las categorías y sus clasificaciones dependen de su árbol. El nombre y el tipo de imagen sí se pueden cambiar.
- **Soft delete (ADR-011):** eliminar un dataset lo **retira** (`active=false`, `deleted_at`) junto con sus categorías activas. Las clasificaciones históricas no se tocan.
- La unicidad (nombre, tipo de imagen, diagnóstico) se valida **solo entre datasets activos**: se puede crear un dataset nuevo para el tipo de imagen de uno retirado.
- El cambio de diagnóstico se bloquea si el dataset tiene categorías, **incluidas las retiradas**.

### DatasetCategory
- `numValue` se asigna automáticamente: categorías del dataset **incluidas las retiradas** + 1. **Nunca se reutiliza**, así que una clase de entrenamiento no cambia de significado.
- Solo se puede cambiar el nombre.
- **Soft delete (ADR-011):** cualquier categoría se puede retirar. Las activas pueden tener huecos en `numValue`.

### CategoryDiagnostic (asignación de sub-diagnósticos a una categoría)
- **El diagnóstico debe descender, a cualquier profundidad, del diagnóstico raíz del dataset.** Ejemplo con raíz `I20`: `I20.1` es válido; un futuro `I20.11` también; `J45.0` no.
- **La raíz del dataset no se puede asignar a una categoría.** Las categorías agrupan solo sub-diagnósticos.
- Un mismo diagnóstico no puede estar en dos categorías **activas** del mismo dataset.
- **Soft delete con historial (ADR-011):** quitar un diagnóstico de una categoría lo retira (`deleted_at`). `created_at` y `deleted_at` definen su vigencia en la categoría.
- Un `MedicalDiagnostic` que alguna vez definió una categoría no se puede borrar del catálogo.

### Alta de Patient y Doctor
- El `userId` debe existir en ms-security. Si ms-security responde que no existe (o 404), el alta da error. Si ms-security falla, responde **503** (ADR-009).
- **Todo o nada:** se valida, se guarda y, como último paso, se asigna el rol por defecto (`patient`/`doctor`) en ms-security. Si la asignación falla, se revierte el guardado y no queda ningún registro sin rol.
- Los endpoints internos `/api/public/internal/register/*`, que invoca ms-security, responden `true`/`false` y nunca 503.

### ImageClassification (un doctor desliza una imagen hacia una categoría)
- **Solo se pueden clasificar imágenes cuyo tipo tiene un dataset configurado.**
- El tipo de imagen debe tener un área de evaluación, y el doctor debe pertenecer a esa área.
- **La categoría es obligatoria, debe estar activa y debe pertenecer al dataset activo del tipo de imagen.**
- Un doctor solo puede clasificar una vez la misma imagen.
- Los diagnósticos de la clasificación son la **definición de su categoría en la fecha del deslizamiento** (ADR-011). Lo que se añada o se retire después no cambia las clasificaciones previas.
- Las clasificaciones creadas antes de esta regla no tienen categoría (`null`).

### ClassificationDiagnosis (detalle opcional de sub-diagnósticos)
- No forma parte del deslizamiento. Si la clasificación tiene categoría, **solo acepta diagnósticos de la definición de esa categoría en la fecha del deslizamiento**.
- Para clasificaciones previas sin categoría:
- **Solo son válidos los diagnósticos asignados a alguna categoría del dataset del tipo de imagen.** Así, toda etiqueta se traduce a una clase (`numValue`) del dataset.
- **Una clasificación puede tener varios diagnósticos, pero todos de la misma categoría.** La imagen queda en una sola clase y el doctor puede precisar varios sub-diagnósticos de esa clase.
- Un diagnóstico no se puede asignar dos veces a la misma clasificación.
- Los datos creados antes de estas reglas no se modifican. Si una clasificación ya tiene diagnósticos sin categoría, no se le pueden agregar más (ver [bugs-conocidos.md](bugs-conocidos.md), #7).

---

## Integración con ms-security

| Acción | Endpoint | Autenticación |
|--------|----------|---------------|
| Validar JWT por request | `POST /api/public/security/permissions-validation` | JWT Bearer |
| Verificar userId | `GET /getway/security/api/{userId}/exist` | Header `secretKey` |
| Verificar email | `GET /getway/security/api/user-exist/email/{email}` | Header `secretKey` |
| Asignar rol | `POST /getway/security/api/{userId}/assign-role/{key}` | Header `secretKey` |
| Obtener nombre | `GET /getway/security/api/get-name/{userId}` | Header `secretKey` |
| Obtener email | `GET /getway/security/api/get-email/{userId}` | Header `secretKey` |
