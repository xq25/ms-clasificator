# ADR-013: Soft delete de las entidades esenciales de la clasificación

- **Estado:** Aceptado (implementación por etapas)
- **Fecha:** 2026-11-27
- **Área:** Persistencia / Dominio
- **Extiende:** [ADR-011](ADR-011-soft-delete-y-esquema-versionado.md), que introdujo el soft delete en `Dataset`, `DatasetCategory` y `CategoryDiagnostic`.

## Contexto
Las correcciones de los bugs #15, #17 y #23 bloquearon el borrado físico de entidades con historial, para no perder clasificaciones ni configuraciones. El efecto colateral es que esas entidades ya **no se pueden eliminar nunca**:
- un tipo de imagen usado alguna vez en un dataset;
- un diagnóstico CIE-10 usado en categorías, datasets o historias clínicas;
- una imagen médica que ya tiene clasificaciones.

Se necesita poder sacarlas de circulación sin perder el historial.

## Decisión

### 1. Alcance
| Entidad | Borrado | Etapa |
|---------|---------|-------|
| `MedicalImageType` | Soft delete | `feat/soft-delete-tipos-imagen` |
| `MedicalDiagnostic` | Soft delete | `feat/soft-delete-diagnosticos` |
| `MedicalImage` | Soft delete | `feat/soft-delete-imagenes` (pendiente) |
| `Dataset`, `DatasetCategory`, `CategoryDiagnostic` | Soft delete (ya existente, ADR-011) | — |
| `Doctor`, `EvaluationArea`, `DoctorArea`, `Patient`, `ClinicalRecord`, `PrimitiveDatum`, `PatientDatum`, `Diagnosis` | Se conserva el borrado actual (con las reglas de los bugs #12–#21) | — |
| `ImageClassification`, `ClassificationDiagnosis` | Sin cambios en esta decisión | — |

### 2. Reglas comunes (mismo patrón que ADR-011)
- Columnas `active` y `deleted_at`, que solo cambian juntas a través de `retire()`. No se usa `@SQLRestriction`.
- `DELETE` sobre la entidad la **retira**; nunca se borra físicamente.
- Los puertos leen **solo activos**, salvo los métodos `...IncludingRetired`.
- Los listados y búsquedas muestran solo activos. Por id se sigue viendo, con `active=false` en la respuesta.
- Una entidad retirada no se puede usar en nada nuevo. Lo histórico que la referencia se conserva tal cual.
- No hay endpoint para restaurar; queda como mejora futura (igual que en ADR-011).
- `active` lleva `DEFAULT 1` en `01-schema.sql` para que los `INSERT` de catálogos de `02-data.sql`, que no lo nombran, creen filas activas.

### 3. `MedicalImageType`
- **Dataset activo:** retirar un tipo que tiene un dataset activo se **bloquea**; primero se retira el dataset. Como clasificar exige un dataset activo del tipo (ADR-010), una vez retirado el tipo nadie puede clasificar sus imágenes.
- **Imágenes:** un tipo retirado no admite imágenes nuevas ("El tipo de imagen '…' está retirado"). Las existentes siguen visibles (por id, por historia clínica, en el listado general y por tipo) y muestran que su tipo está retirado.
- **Nombre único solo entre activos:** se quita el índice único de `medical_image_type.name`; la unicidad la valida el servicio.
- **Área de evaluación:** al retirar el tipo se le quita el área (`retire()` la pone en `null`). El área solo da permiso para clasificar, que ya no aplica, y así el tipo retirado no impide borrar el área.
- Para crear o editar datasets, un tipo retirado se trata como inexistente (los puertos leen solo activos).
- Reemplaza la corrección del bug #23: ya no se bloquea por datasets retirados ni por imágenes; solo por un dataset activo.

### 4. `MedicalDiagnostic`
- **En uso vigente:** retirar un diagnóstico que es padre de un dataset activo o que define una categoría activa se **bloquea**. Los datasets y categorías retirados no bloquean.
- **Jerarquía:** retirar un diagnóstico con sub-diagnósticos activos se **bloquea**.
- **Código CIE-10 único entre todos**, incluidos los retirados: un código nunca se reutiliza (identifica el diagnóstico en el historial de clasificaciones). No cambia el índice único.
- **Historias clínicas:** los diagnósticos ya registrados se conservan; no se pueden registrar nuevos con un código retirado. Retirar no se bloquea por historias clínicas.
- Un diagnóstico retirado no se puede usar en categorías, datasets, jerarquía (`addSubDiagnostic`) ni historias clínicas nuevas. Para historias clínicas el mensaje es explícito ("El diagnóstico … está retirado del catálogo"); en el resto se trata como inexistente.
- Al retirarse no cambia su jerarquía: conserva su padre. Separar un sub-diagnóstico de su padre sigue bloqueado si alguna vez definió una categoría (incluidas retiradas).
- Se quitan las cascadas JPA de la entidad hacia diagnósticos clínicos, datasets y categorías.
- Reemplaza la corrección del bug #17: ya no se bloquea por historias clínicas, clasificaciones ni usos retirados; solo por usos vigentes y por hijos activos.

### 5. `MedicalImage` (pendiente de implementar)
- **Con clasificaciones:** se puede retirar, y sus clasificaciones se conservan.
- **Visibilidad:** sale de la cola de imágenes por clasificar de los doctores y de los listados (general, por tipo, por historia clínica). Por id se sigue viendo.
- **Clasificaciones congeladas:** no se puede volver a clasificar la imagen ni agregar o quitar sub-diagnósticos a sus clasificaciones.
- **Archivo en storage:** se conserva. **Pendiente de decisión:** si más adelante se borra (liberar espacio) o se mueve a otro bucket; hoy la URL se sigue generando para el historial.

## Alternativas consideradas
- **Mantener el bloqueo de borrado físico (estado tras #15, #17 y #23).** Se descarta: las entidades usadas nunca podrían salir de circulación.
- **Soft delete en todas las entidades.** Se descarta por decisión del responsable: doctores, áreas, pacientes y datos clínicos conservan su borrado actual, con la eliminación de asociaciones ya definida.
- **Soft delete de `ImageClassification`.** Se descarta por ahora: obligaría a cambiar la unicidad doctor+imagen a "solo entre activas" en la BD.

## Consecuencias
- Cada etapa cambia `01-schema.sql` y exige relanzar la BD (`ddl-auto=validate`), con respaldo previo.
- Las respuestas de las entidades retiradas incluyen `active` (campo nuevo en los DTO; el resto del contrato no cambia, ADR-003).
- El volumen de MySQL en Docker debe relanzarse con el esquema nuevo antes de desplegar.
