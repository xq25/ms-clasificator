# Bugs conocidos

Comportamientos incorrectos detectados durante la re-estructuración. La re-estructuración preserva el comportamiento, y cada corrección cambia reglas de negocio, así que requiere aprobación ([política](../CLAUDE.md)).

Cada corrección se hace en su propia rama `fix/`. Si el bug tiene un test de caracterización (`BUG CONOCIDO`, ver [ADR-008](adr/ADR-008-estrategia-de-pruebas.md)), ese test se actualiza en el mismo PR.

## Pendientes

| # | Ubicación | Problema | Impacto | Test |
|---|-----------|----------|---------|------|
| 7 | `ClassificationDiagnosisService.create` | Un doctor puede asignar a la clasificación de una imagen **cualquier** diagnóstico, sin validar que pertenezca al árbol del dataset del tipo de imagen | **Alto:** etiquetas fuera del dataset contaminan los datos de clasificación. **Requiere definir reglas antes de corregir:** qué pasa si el tipo de imagen no tiene dataset, si la raíz es válida, etc. | — |
| 8 | `PatientService.create` | Si falla la asignación del rol, el mensaje dice "No se pudo asignar el rol **'doctor'**" (debería decir `'patient'`). Además, falta un espacio en "No existe un usuario con id" + userId | Mensajes engañosos para el cliente | `PatientServiceTest` |
| 9 | `PatientService.create` (revisar también `DoctorService.create`) | El rol se asigna en ms-security **antes** de guardar el paciente, y `create` no es `@Transactional`. Además, `existUserById` y `assignDefaultRole` se tragan los errores de ms-security y devuelven `false` (ver ADR-009) | Si el guardado falla, el usuario queda con el rol en ms-security pero sin paciente. **Requiere decidir** el orden y la compensación | `PatientServiceTest` (fija el orden actual) |

## Resueltos

| # | Ubicación | Problema | Resolución | PR |
|---|-----------|----------|------------|----|
| 1 | `CategoryDiagnosticService.isValidSubDiagnostic` | Era un stub sin implementar: ignoraba el resultado de la consulta y siempre devolvía `true`, así que se aceptaba cualquier diagnóstico en una categoría | Se valida que el diagnóstico descienda, a cualquier profundidad, de la raíz del dataset. La raíz no es válida. Regla documentada en [domain-model.md](domain-model.md#reglas-de-negocio). Antes de corregir se comprobó que la BD de prueba no tenía asignaciones inválidas | `fix/validacion-arbol-diagnosticos` |
| 2 | `DatasetService.update` | Las validaciones de duplicado (diagnóstico y tipo de imagen) no excluían el propio dataset, y cualquier actualización con categorías se bloqueaba | Las validaciones excluyen el propio dataset (`...AndIdNot`). Con categorías solo se bloquea el **cambio de diagnóstico**; nombre y tipo de imagen se pueden cambiar | `fix/dataset-service` |
| 3 | `DatasetService.update` / `create` | `update` no asignaba el `name` del DTO. Además, ni `create` ni `update` validaban nombres duplicados, aunque la columna es `unique`: un duplicado terminaba en error de BD no controlado | `update` guarda el nombre. Ambos validan el nombre único con el mensaje "Ya existe un dataset con el nombre: X"; `update` excluye el propio dataset | `fix/dataset-service` |
| 4 | `DatasetService.create` | Sin `@Transactional` | Se añade `@Transactional`: validaciones y guardado en una sola transacción | `fix/dataset-service` |
| 5 | `PatientService` y `DoctorService` (`findById`, `findByDocument`/`findByCode`, `findByUserId`) | Si ms-security no devolvía nombre o email, se respondía éxito sin `userInfo` y sin aviso (en `PatientService` el `ApiResponse.error` se creaba y se descartaba). Si ms-security fallaba, la respuesta era distinta en cada servicio: 400 en uno, 500 en el otro | `SecurityService.getUserInfo` lanza `SecurityServiceException` ante respuesta incompleta o `RestClientException`, y el `GlobalExceptionHandler` la traduce a **503**. Regla: sin la información del usuario, la consulta falla ([ADR-009](adr/ADR-009-errores-de-servicios-externos.md)) | `fix/patient-userinfo` |
| 6 | `application.properties` | Línea suelta `@@ -23,4 +23,7 @@ ...`, resto de un diff pegado en el commit `e15dd12` (2026-05-19). Ese commit también eliminó varias líneas de configuración | Se elimina la línea suelta. De lo perdido, recuperado del historial, solo se restaura lo que tiene efecto: `hikari.minimum-idle=2` (verificado: la app en reposo mantiene 2 conexiones). `org.hibernate.SQL=DEBUG` pasa a `application-dev.yml` y se apaga en `application-docker.properties`, porque Docker activa `dev,docker`. Se descartan `connection-timeout` (efecto mínimo) y `springframework.boot=INFO` (ya es el valor por defecto) | `fix/application-properties` |
