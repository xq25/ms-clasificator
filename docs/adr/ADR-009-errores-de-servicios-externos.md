# ADR-009: Manejo de errores de servicios externos

- **Estado:** Aceptado — aplicado también a `existUserById` y `assignDefaultRole` (bug #9, ver `bugs-conocidos.md`)
- **Fecha:** 2026-11-21
- **Área:** Arquitectura

## Contexto
El microservicio depende de servicios externos: ms-security para identidad y roles, y MinIO/S3 para imágenes. Antes de esta decisión, cada consumidor manejaba los fallos de ms-security a su manera:

| Situación | `PatientService` | `DoctorService` |
|-----------|------------------|-----------------|
| ms-security responde sin nombre o email | Éxito sin `userInfo` y sin aviso (el `ApiResponse.error` se creaba y se descartaba) | Éxito sin `userInfo` y sin aviso |
| ms-security caído o con error HTTP | Su `catch (Exception)` lo convertía en un error 400 | Excepción no capturada, 500 genérico |

Es decir, el mismo fallo producía tres respuestas distintas, y la responsabilidad de detectarlo estaba repartida entre los consumidores.

Ya existía un precedente para el storage: `ImageStorageException` se traduce a 503 en el `GlobalExceptionHandler`.

## Decisión
1. **El cliente del servicio externo detecta el fallo.** El componente que habla con el servicio externo (hoy `SecurityService`) nunca devuelve `null` ni datos incompletos. Ante un fallo de red, un error HTTP (`RestClientException`) o una respuesta incompleta, lanza **una excepción propia del feature**. Para ms-security es `identity/application/exception/SecurityServiceException`, reutilizable para cualquier llamada a ese servicio.
2. **La traducción a HTTP se hace una sola vez**, en `shared/exception/GlobalExceptionHandler`: **503 Service Unavailable** con `ApiResponse.error(mensaje)`. El 503 distingue "falló una dependencia" de "datos inválidos" (400) y de "error interno" (500).
3. **Los servicios de aplicación no capturan estas excepciones.** Si un método tiene un `catch (Exception)` genérico, debe relanzarlas explícitamente antes, para que ese catch no se las trague:
   ```java
   } catch (SecurityServiceException ex) {
       throw ex;
   } catch (Exception ex) { ... }
   ```
4. **Regla de negocio asociada:** si no se puede obtener la información del usuario desde ms-security, la consulta del paciente o doctor **falla**. No se devuelven datos parciales.

## Alternativas consideradas
- **Éxito con aviso y datos parciales.** Se descarta por decisión del responsable: la respuesta debe estar completa o no darse.
- **Cada servicio captura la excepción y devuelve `ApiResponse.error` (400).** Se descarta porque repite el `catch` en cada método y responde 400 a un problema que no es del cliente.

## Consecuencias
- El mismo fallo de ms-security produce siempre la misma respuesta (503 y el mismo mensaje) en todos los endpoints.
- Agregar otro servicio externo implica: crear su excepción propia, lanzarla desde su cliente o adaptador, y registrar su traducción en el `GlobalExceptionHandler`.
- Los demás métodos de `SecurityService` (`existUserById`, `assignDefaultRole`) todavía se tragan los errores y devuelven `false`. Alinearlos con esta decisión cambia el flujo de alta de pacientes y doctores, así que queda pendiente de decisión (ver bug #9 en `bugs-conocidos.md`).
- En la fase 3, `SecurityService` pasará a ser un puerto (`IdentityProvider`) con su adaptador HTTP. La excepción y su traducción se mantienen.
