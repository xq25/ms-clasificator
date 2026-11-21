# Bugs conocidos

Comportamientos incorrectos detectados durante la re-estructuración. **Ninguno se ha corregido todavía.** La re-estructuración preserva el comportamiento, y cada corrección cambia reglas de negocio, así que requiere aprobación ([política](../CLAUDE.md)).

Cada corrección se hace en su propia rama `fix/`. Si el bug tiene un test de caracterización (`BUG CONOCIDO`, ver [ADR-008](adr/ADR-008-estrategia-de-pruebas.md)), ese test se actualiza en el mismo PR.

| # | Ubicación | Problema | Impacto | Test |
|---|-----------|----------|---------|------|
| 1 | `CategoryDiagnosticService.isValidSubDiagnostic` | Ignora el resultado de `existsByIdAndParentDiagnosticId` y siempre devuelve `true` | **Alto:** se puede asignar a una categoría cualquier diagnóstico, aunque no pertenezca al árbol del diagnóstico del dataset | `CategoryDiagnosticServiceTest` |
| 2 | `DatasetService.update` | `existsByMedicalDiagnosticId` / `existsByMedicalImageTypeId` no excluyen el propio dataset | Rechaza cualquier actualización que mantenga el mismo diagnóstico o el mismo tipo de imagen | `DatasetServiceTest` |
| 3 | `DatasetService.update` | `DatasetUpdateDTO.name` es obligatorio, pero nunca se asigna a la entidad | El nombre del dataset no se puede cambiar, aunque la respuesta indica éxito | `DatasetServiceTest` |
| 4 | `DatasetService.create` | Sin `@Transactional` | Las validaciones y el guardado no van en una misma transacción | — |
| 5 | `PatientService.findById` / `findByDocument` / `findByUserId` | Se crea `ApiResponse.error(...)` y se descarta cuando no se obtiene la información del usuario | El cliente recibe éxito sin `userInfo` y sin aviso | — |
| 6 | `application.properties` | Línea suelta `@@ -23,4 +23,7 @@ ...`, resto de un diff pegado. Faltan las líneas que cubría ese diff | Configuración incompleta o inesperada | — |
