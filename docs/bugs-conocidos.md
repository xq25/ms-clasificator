# Bugs conocidos

Comportamientos incorrectos detectados durante la re-estructuración. La re-estructuración preserva el comportamiento, y cada corrección cambia reglas de negocio, así que requiere aprobación ([política](../CLAUDE.md)).

Cada corrección se hace en su propia rama `fix/`. Si el bug tiene un test de caracterización (`BUG CONOCIDO`, ver [ADR-008](adr/ADR-008-estrategia-de-pruebas.md)), ese test se actualiza en el mismo PR.

## Pendientes

| # | Ubicación | Problema | Impacto | Test |
|---|-----------|----------|---------|------|
| 2 | `DatasetService.update` | `existsByMedicalDiagnosticId` / `existsByMedicalImageTypeId` no excluyen el propio dataset | Rechaza cualquier actualización que mantenga el mismo diagnóstico o el mismo tipo de imagen | `DatasetServiceTest` |
| 3 | `DatasetService.update` | `DatasetUpdateDTO.name` es obligatorio, pero nunca se asigna a la entidad | El nombre del dataset no se puede cambiar, aunque la respuesta indica éxito | `DatasetServiceTest` |
| 4 | `DatasetService.create` | Sin `@Transactional` | Las validaciones y el guardado no van en una misma transacción | — |
| 5 | `PatientService.findById` / `findByDocument` / `findByUserId` | Se crea `ApiResponse.error(...)` y se descarta cuando no se obtiene la información del usuario | El cliente recibe éxito sin `userInfo` y sin aviso | — |
| 6 | `application.properties` | Línea suelta `@@ -23,4 +23,7 @@ ...`, resto de un diff pegado. Faltan las líneas que cubría ese diff | Configuración incompleta o inesperada | — |
| 7 | `ClassificationDiagnosisService.create` | Un doctor puede asignar a la clasificación de una imagen **cualquier** diagnóstico, sin validar que pertenezca al árbol del dataset del tipo de imagen | **Alto:** etiquetas fuera del dataset contaminan los datos de clasificación. **Requiere definir reglas antes de corregir:** qué pasa si el tipo de imagen no tiene dataset, si la raíz es válida, etc. | — |

## Resueltos

| # | Ubicación | Problema | Resolución | PR |
|---|-----------|----------|------------|----|
| 1 | `CategoryDiagnosticService.isValidSubDiagnostic` | Era un stub sin implementar: ignoraba el resultado de la consulta y siempre devolvía `true`, así que se aceptaba cualquier diagnóstico en una categoría | Se valida que el diagnóstico descienda, a cualquier profundidad, de la raíz del dataset. La raíz no es válida. Regla documentada en [domain-model.md](domain-model.md#reglas-de-negocio). Antes de corregir se comprobó que la BD de prueba no tenía asignaciones inválidas | `fix/validacion-arbol-diagnosticos` |
