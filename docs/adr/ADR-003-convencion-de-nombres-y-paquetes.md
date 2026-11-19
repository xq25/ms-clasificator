# ADR-003: Convención de nombres y paquetes

- **Estado:** Aceptado
- **Fecha:** 2026-11-19
- **Área:** Código

## Contexto
La base de código tenía nombres inconsistentes:
- paquetes en mayúscula (`Backend`, `Controllers`, `DTOs`), contra la convención Java
- singular y plural mezclados (`SecurityServices`, `DatasetMappers` frente a `DiagnosisMapper`)
- `Img` e `Image` usados para lo mismo
- nombres de entidades que no describen su rol (`DiagnosticCategoryDataset`, `ImageDoctorDiagnostics`)

## Decisión

### Paquetes
- Paquete raíz: `backend.msclasificator` (antes `Backend.ms_clasificator`). El `groupId`/`artifactId` de Maven no cambia.
- Todos los paquetes van en minúscula y en singular, organizados según [ADR-001](ADR-001-arquitectura-hexagonal-por-feature.md).

### Clases
- Sufijos en singular: `XService`, `XController`, `XMapper`, `XRepository`.
- DTOs: `XCreateDTO`, `XUpdateDTO`, `XResponseDTO`, `XSummaryDTO`.
- `Image` en lugar de `Img`.

### Renombres aplicados

| Antes | Después | Motivo |
|-------|---------|--------|
| `MedicalImg` (+ DTOs, Mapper, Repository) | `MedicalImage` | Unificar `Img`/`Image` |
| `DiagnosticCategoryDataset` (+ derivados) | `CategoryDiagnostic` | Es la asignación de un sub-diagnóstico a una categoría |
| `ImageDiagnostic` (+ derivados) | `ImageClassification` | Representa el acto de un doctor clasificando una imagen |
| `ImageDoctorDiagnostics` (+ derivados) | `ClassificationDiagnosis` | Cada diagnóstico asignado dentro de una clasificación |
| `SecurityServices` | `SecurityService` | Singular |
| `DatasetMappers`, `DatasetCategoryMappers` | `DatasetMapper`, `DatasetCategoryMapper` | Singular |
| `DoctorBaseDTO` | `DoctorCreateDTO` | Convención de DTOs |

### Invariantes del renombre (contratos que NO cambian)
Para que el renombre sea puramente estructural:
1. **Esquema de BD:** las entidades renombradas fijan su tabla con `@Table(name = "<tabla_actual>")`. Los nombres de campos y columnas no cambian.
2. **Contrato de la API:** las rutas de los endpoints y los nombres de campos de los DTOs (JSON) no cambian.
3. **JPQL:** las consultas usan el nombre de entidad, que por defecto es el nombre de la clase, y se actualizan junto con él.

## Pendiente (fuera de esta decisión)
- Renombrar campos, columnas, rutas y propiedades JSON para alinearlos con los nombres nuevos (por ejemplo `imageDiagnostic` → `imageClassification`). Rompe el contrato con el frontend y requiere migración de BD, así que va en un ADR propio.
- Revisar la familia `SystemDatum` / `PrimitiveDatum` / `PatientDatum`, con propuesta inicial `ClinicalVariable` / `PatientVariableValue`.
- Usar el enum `PatientSex` en `Patient.sex`, que hoy es `String`. Cambia el tipo de columna y la validación.

## Consecuencias
- Los imports y el historial de Git cambian de ruta. Se usa `git mv` para conservar la trazabilidad.
- La configuración de logging que referencia el paquete raíz (`application-*.yml`) se actualiza.
- Mientras no se renombren los campos, conviven nombres viejos en campos y JSON con nombres nuevos en las clases.
