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

> La URL **no** se persiste. Se genera on-demand desde `imageKey` usando el `ImageStorageService`.

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

3. **Clasificación por doctor**
   - Doctor crea un `ImageClassification` para una imagen.
   - Agrega `ClassificationDiagnosis` seleccionando los `MedicalDiagnostic` correspondientes.

---

## Reglas de negocio

Reglas vigentes del feature `dataset`. Están verificadas por los tests de `src/test/java/.../dataset/application/`. Los comportamientos incorrectos aún no corregidos están en [bugs-conocidos.md](bugs-conocidos.md).

### Dataset
- El nombre del dataset es único.
- Un `MedicalDiagnostic` solo puede tener un dataset, y un `MedicalImageType` solo puede estar en un dataset. Al actualizar, estas validaciones excluyen al propio dataset.
- Si el dataset ya tiene categorías, **no se puede cambiar su diagnóstico**, porque las categorías dependen de su árbol. El nombre y el tipo de imagen sí se pueden cambiar.
- Al eliminar un dataset se eliminan en cascada sus categorías y las asignaciones de diagnósticos.

### DatasetCategory
- `numValue` se asigna automáticamente y en secuencia: cantidad de categorías del dataset + 1.
- Solo se puede cambiar el nombre.
- Solo se puede eliminar la categoría con el `numValue` más alto, para que la secuencia no tenga huecos.

### CategoryDiagnostic (asignación de sub-diagnósticos a una categoría)
- **El diagnóstico debe descender, a cualquier profundidad, del diagnóstico raíz del dataset.** Ejemplo con raíz `I20`: `I20.1` es válido; un futuro `I20.11` también; `J45.0` no.
- **La raíz del dataset no se puede asignar a una categoría.** Las categorías agrupan solo sub-diagnósticos.
- Un mismo diagnóstico no puede estar en dos categorías del mismo dataset.

### ClassificationDiagnosis (etiquetado por doctores)
- Un diagnóstico no se puede asignar dos veces a la misma clasificación de imagen.
- *Pendiente de definir:* si el diagnóstico debe pertenecer al árbol del dataset del tipo de imagen (bug #7).

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
