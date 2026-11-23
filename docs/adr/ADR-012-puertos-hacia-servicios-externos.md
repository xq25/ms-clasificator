# ADR-012: Puertos hacia servicios externos (identidad y almacenamiento)

- **Estado:** Aceptado
- **Fecha:** 2026-11-23
- **Área:** Arquitectura

## Contexto
La fase 2 ([ADR-007](ADR-007-puertos-de-persistencia.md)) dejó a la capa `application` sin dependencias de Spring Data. Aún quedaban dos dependencias de infraestructura en `application`:
- `identity/application/SecurityService` llamaba a ms-security con `RestTemplate` y `org.springframework.http`. Patient y Doctor dependían de esa clase concreta.
- El puerto de storage de `medicalimage` (`ImageStorageService`) recibe `MultipartFile`, un tipo de la capa web.

Por eso cambiar el proveedor de identidad o el de almacenamiento obligaba a tocar la lógica de negocio, y los tests de los servicios tenían que simular una clase HTTP.

## Decisión

### 1. Puerto de identidad: `IdentityProvider`
| Pieza | Ubicación | Nombre |
|-------|-----------|--------|
| Puerto | `identity/application/port/out/` | `IdentityProvider` |
| Adaptador HTTP | `identity/infrastructure/http/` | `MsSecurityIdentityProvider` (`@Component`, visibilidad de paquete) |

- El puerto expone solo lo que la aplicación usa, con nombres de dominio:
  - `existsUser(userId)`, antes `existUserById`;
  - `assignDefaultRole(userId, roleKey)`;
  - `getUserInfo(userId)`.
- Se elimina `existUserByEmail` (sin consumidores). `getUserNameById` y `getUserEmailById` pasan a ser detalles privados del adaptador.
- El contrato de errores de [ADR-009](ADR-009-errores-de-servicios-externos.md) es parte del puerto:
  - ante fallo o respuesta incompleta, el adaptador lanza `SecurityServiceException` (→ 503) y nunca devuelve `null`;
  - `existsUser` devuelve `false` solo si ms-security responde que el usuario no existe (404 o `data=false`).
- `SecurityServiceException` se queda en `identity/application/exception`: es parte del contrato del puerto, no de la infraestructura.
- `PatientService` y `DoctorService` dependen del puerto. Las URLs, el header `secretKey` y las transacciones de alta (bug #9) no cambian.

### 2. Puerto de almacenamiento: `ImageStorage` (pendiente, `refactor/fase-3-puerto-storage`)
Diseño aprobado, se aplica en el siguiente PR:
- `record ImageFile(String originalFilename, String contentType, long size, InputStream content)` reemplaza a `MultipartFile` en `application`. El controlador hace la conversión.
- El puerto `ImageStorage` expone `upload(ImageFile, folder)`, `generatePublicUrl`, `delete`, `exists` y `providerName`.
- Las validaciones de tipo y tamaño pasan del adaptador MinIO a `MedicalImageService`.
- El adaptador S3 sigue comentado, pero se adapta a la nueva firma.

## Alternativas consideradas
- **Mantener `SecurityService` como clase concreta en `application`.** Se descarta porque deja `RestTemplate` dentro de `application` e incumple la regla de dependencias de ADR-001.
- **Mover `SecurityServiceException` a `infrastructure`.** Se descarta porque los servicios la relanzan y el `GlobalExceptionHandler` la traduce. Es parte del contrato que ven los consumidores.
- **Llamar al puerto `SecurityGateway` o `UserDirectory`.** Se elige `IdentityProvider` porque describe el rol (proveedor de identidad) sin atarlo a ms-security.

## Consecuencias
- Cambiar de proveedor de identidad (otro microservicio, un IdP) solo requiere un adaptador nuevo.
- Los tests de `PatientService` y `DoctorService` simulan el puerto. El adaptador conserva sus propios tests sobre `RestTemplate` (`MsSecurityIdentityProviderTest`).
- Los tests de adaptadores no terminan en `ServiceTest`, así que el comando de ADR-008 se amplía con `*ProviderTest`.
