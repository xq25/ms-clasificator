# ADR-007: Puertos de persistencia y paginación propia

- **Estado:** Aceptado
- **Fecha:** 2026-11-19
- **Área:** Persistencia

## Contexto
La fase 2 de [ADR-001](ADR-001-arquitectura-hexagonal-por-feature.md) busca que la capa `application` deje de depender de Spring Data. Antes de este cambio los servicios:
- inyectaban interfaces que extienden `JpaRepository`;
- paginaban con `Pageable` y `Page`.

Por eso cambiar el motor de persistencia obligaba a tocar la lógica de negocio. El primer feature migrado es `dataset`.

## Decisión

### 1. Nombres de puertos y adaptadores
| Pieza | Ubicación | Nombre | Ejemplo |
|-------|-----------|--------|---------|
| Puerto (interfaz pura) | `<feature>/application/port/out/` | `XRepository` | `DatasetRepository` |
| Repositorio Spring Data | `<feature>/infrastructure/persistence/` | `XJpaRepository` | `DatasetJpaRepository` |
| Adaptador | `<feature>/infrastructure/persistence/` | `XPersistenceAdapter` (`@Component`, visibilidad de paquete) | `DatasetPersistenceAdapter` |

- El dominio conserva el nombre natural (`XRepository`). Spring Data queda como detalle de infraestructura.
- El puerto expone **solo los métodos que la aplicación usa** y con nombres de dominio. Por ejemplo, `existsByDatasetIdAndMedicalDiagnosticId` en vez del nombre derivado de Spring Data `existsByDatasetCategoryDatasetIdAndMedicalDiagnosticId`.
- `count()` reemplaza a `countAll()` en el puerto. El adaptador sigue usando la misma `@Query`.

### 2. Paginación propia
- `shared/application/PageQuery` (página, tamaño, `sortBy`, dirección) y `shared/application/PageResult<T>` (contenido y metadatos, con `map`) reemplazan a `Pageable` y `Page` en `application`.
- La conversión vive solo en `shared/infrastructure/persistence/PageMapper`, con el mismo orden y los mismos límites que tenía `PageRequestDTO.toPageable()`.
- `PageRequestDTO.toPageQuery()` crea la consulta. `toPageable()` se conserva temporalmente para los features aún no migrados y delega en `PageMapper`. Se elimina al terminar la fase 2.

### 3. Dueño de cada puerto
- Cada puerto pertenece al feature **dueño de los datos**, y solo ese feature lo implementa.
- Otros features pueden depender del puerto (`application/port/out`) de otro feature, pero nunca de su `infrastructure`.
- Mientras un feature no esté migrado, quien lo consume sigue usando su repositorio Spring Data. Es un incumplimiento conocido y temporal.

### 4. Transacciones
`@Transactional` se mantiene en los servicios de aplicación con los mismos límites. Los adaptadores no declaran transacciones propias y participan en la del servicio.

## Estado de migración
| Feature | Estado |
|---------|--------|
| `dataset` | Migrado |
| `diagnosiscatalog`, `medicalimage`, `clinicalrecord`, `doctor`, `patient`, `classification` | Pendiente |

Incumplimientos conocidos tras migrar `dataset`:
- `dataset/application` todavía importa `MedicalDiagnosticRepository` (de `diagnosiscatalog`) y `MedicalImageTypeRepository` (de `medicalimage`). Se resuelven al migrar esos features.

## Alternativas consideradas
- **Puerto `XStore` y conservar `XRepository` para Spring Data.** Se descarta porque deja el nombre natural del dominio en manos de la infraestructura.
- **Puertos genéricos (`CrudPort<T, ID>`).** Se descarta porque expone operaciones que la aplicación no usa y ata los puertos a la forma de Spring Data.

## Consecuencias
- Cambiar de motor de persistencia en un feature migrado solo requiere un adaptador nuevo.
- El refactor no cambia comportamiento: mismas consultas, validaciones y límites transaccionales. Los bugs conocidos se conservan y se tratan aparte ([política de consulta](../../CLAUDE.md)).
- Hay una clase adaptador más por agregado, con código de delegación repetitivo.
