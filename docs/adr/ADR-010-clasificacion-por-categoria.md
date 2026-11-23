# ADR-010: Clasificación de imágenes por categoría (deslizamiento)

- **Estado:** Aceptado
- **Fecha:** 2026-11-23
- **Área:** Arquitectura / Persistencia

## Contexto
El sistema funciona como un "Tinder médico": el doctor ve una imagen y la **desliza hacia una categoría** del dataset de su tipo de imagen (por ejemplo, "Grave" = {I20.1, I20.2}). La decisión clínica es la categoría; los diagnósticos CIE-10 de esa categoría son su definición.

Hasta ahora el modelo no lo reflejaba:
- `ImageClassification` (doctor + imagen) no guardaba ninguna categoría.
- Los diagnósticos se asignaban uno a uno como `ClassificationDiagnosis`, con una petición por diagnóstico. Cada deslizamiento requería N llamadas no atómicas.
- La clase de la imagen (el `numValue` para entrenamiento) había que deducirla de los diagnósticos.

## Decisión

### Modelo
- `ImageClassification` añade `datasetCategory` (`ManyToOne` LAZY, columna `dataset_category_id`): la categoría hacia la que se deslizó la imagen.
- La columna es **nullable en la BD** solo por las clasificaciones creadas antes de esta decisión. Para las nuevas es obligatoria (validación en el DTO).
- Los diagnósticos de una clasificación **se derivan de su categoría**. Si después se añade un diagnóstico a la categoría, las clasificaciones que ya la tenían lo incluyen, porque lo validado por el doctor es la categoría.

### API: un deslizamiento es una sola petición atómica
- `POST /api/image-diagnostics` exige `doctorId`, `medicalImgId` y **`datasetCategoryId`**. El flujo en dos pasos (crear la clasificación vacía y después asignarle diagnósticos) deja de existir.
- Validaciones, en orden:
  1. El doctor y la imagen existen.
  2. El tipo de imagen tiene dataset.
  3. El tipo tiene área de evaluación y el doctor pertenece a ella.
  4. **La categoría existe y pertenece al dataset del tipo de imagen.**
  5. El doctor no había clasificado ya esa imagen.
- La respuesta incluye `datasetCategory` (id, nombre, `numValue`) y `diagnostics`, que son los diagnósticos de la categoría, obtenidos por el servicio. Los resúmenes de los listados incluyen `datasetCategoryId`.

### `ClassificationDiagnosis`: detalle opcional
- Se mantiene para que el doctor pueda precisar sub-diagnósticos, pero no forma parte del deslizamiento.
- Si la clasificación tiene categoría, **solo acepta diagnósticos de esa categoría**.
- Las clasificaciones previas sin categoría siguen las reglas del bug #7: diagnósticos de alguna categoría del dataset, todos de la misma.

### Protección de los datos etiquetados
- No se puede eliminar una `DatasetCategory` que tenga clasificaciones, ni un `Dataset` con clasificaciones en sus categorías. Es una red de seguridad temporal: el **soft delete** la reemplazará (actividad posterior, con relanzamiento de la BD).

### Esquema
- `ddl-auto=update` añade la columna `dataset_category_id` y su FK al arrancar. No se incorpora una herramienta de migraciones ([ADR-004](ADR-004-politica-de-dependencias.md)).
- No se rellenan las clasificaciones existentes: la BD se relanzará con un script de inicialización nuevo cuando se implemente el soft delete.

## Alternativas consideradas
- **Endpoint nuevo `/swipe`, conservando el POST actual.** Se descarta porque dejaría dos formas de clasificar. El proyecto no está desplegado, así que se acepta romper el contrato.
- **Guardar una "foto" de los diagnósticos en `ClassificationDiagnosis` en cada deslizamiento.** Se descarta: es redundante, la escritura no es atómica si se hace desde el cliente, y contradice que lo validado es la categoría.
- **Borrado con `SET NULL` en las clasificaciones.** Se descarta porque las clasificaciones perderían su clase.

## Consecuencias
- Un deslizamiento es una sola llamada atómica, y la clase para entrenamiento (`numValue`) es directa.
- Cambiar los diagnósticos de una categoría afecta retroactivamente a lo que significan sus clasificaciones. Es lo deseado por definición.
- Mientras no exista el soft delete, una categoría o un dataset con clasificaciones no se puede eliminar.
- `classification` depende de los puertos de `dataset` (`DatasetRepository`, `DatasetCategoryRepository`, `CategoryDiagnosticRepository`), y `dataset` del puerto de `classification` (`ImageClassificationRepository`). Siempre a través de puertos ([ADR-007](ADR-007-puertos-de-persistencia.md)).
