# ADR-008: Estrategia de pruebas

- **Estado:** Aceptado
- **Fecha:** 2026-11-21
- **Área:** Calidad
- **Amplía:** [ADR-005](ADR-005-estrategia-de-ramas.md) (nuevo prefijo de rama `test/`)

## Contexto
El proyecto no tenía tests, salvo `contextLoads`, que levanta todo el contexto de Spring y necesita MySQL y MinIO. La re-estructuración (ADR-001, ADR-007) cambia muchos archivos sin querer cambiar comportamiento, y hoy la única verificación posible es compilar y probar a mano.

Los puertos de persistencia (ADR-007) permiten probar los servicios de aplicación aislados de la base de datos y de los servicios externos.

## Decisión

### 1. Tests unitarios de servicios de aplicación
- JUnit 5, Mockito y AssertJ, que ya incluye `spring-boot-starter-test`. **No se agregan dependencias** ([ADR-004](ADR-004-politica-de-dependencias.md)).
- Sin contexto de Spring: `@ExtendWith(MockitoExtension.class)`, colaboradores con `@Mock` y servicio con `@InjectMocks`.
- Se simulan los puertos, los mappers y cualquier otro colaborador. La lógica del servicio es lo único real.
- Mockito en modo estricto (por defecto): un stub que no se usa hace fallar el test.

### 2. Tests de caracterización
- Cada test fija el comportamiento **actual** del servicio: resultado, mensaje exacto y efectos (qué se guarda o borra, y qué no).
- Los bugs conocidos también se fijan. Su test se marca con `BUG CONOCIDO` en el `@DisplayName` y se registra en [bugs-conocidos.md](../bugs-conocidos.md).
- Cuando se corrige un bug en una rama `fix/` aprobada, el test correspondiente se modifica en el mismo PR, de forma visible en el diff.

### 3. Convenciones
- Ubicación: `src/test/java/backend/msclasificator/<feature>/application/<Servicio>Test.java`.
- Un `@Nested` por método público del servicio y un `@DisplayName` en español que describe el caso.
- Ejecución de los tests unitarios: `mvnw test -Dtest='*ServiceTest,*ControllerTest,*ProviderTest' -Dsurefire.failIfNoSpecifiedTests=false` (`*ProviderTest` cubre los adaptadores de servicios externos, ADR-012).

### 4. Ramas
- Se añade el prefijo `test/` a la tabla de [ADR-005](ADR-005-estrategia-de-ramas.md) para actividades que solo agregan o modifican tests. Ejemplo: `test/servicios-dataset`.

### 5. Alcance incremental
| Feature | Estado |
|---------|--------|
| `dataset` | Cubierto (3 servicios) |
| `clinicalrecord` | Cubierto (`ClinicalRecordService`, `DiagnosisService`, `PatientDatumService`, `PrimitiveDatumService`) |
| `patient` | Cubierto (`PatientService`) |
| `identity` | Cubierto (`InternalController` registro, adaptador `MsSecurityIdentityProvider`); `InternalService` pendiente |
| `classification` | `ClassificationDiagnosisService` cubierto; `ImageClassificationService` solo `create` y `findById` |
| `medicalimage` | Cubierto (`MedicalImageService`, `MedicalImageTypeService`) |
| `doctor` | Cubierto (`DoctorService`, `DoctorAreaService`, `EvaluationAreaService`) |
| `diagnosiscatalog` | Cubierto (`MedicalDiagnosticService`) |

Orden de los PRs pendientes: `classification` (incluye `InternalService`).

## Alternativas consideradas
- **Tests de integración con H2 o Testcontainers.** Prueban también los adaptadores y las consultas, pero requieren dependencias nuevas. Quedan pendientes de aprobación.
- **Mover `contextLoads` a un perfil `integration`.** Requiere cambiar la configuración del build. Se deja como está: `mvnw test` sin filtro sigue necesitando MySQL y MinIO.

## Consecuencias
- Los refactors futuros tienen una red de seguridad que no depende del entorno.
- Los tests documentan el comportamiento real, incluidos los bugs. Así no se "corrige" nada por accidente.
- Los adaptadores y las consultas JPQL no quedan cubiertos por estos tests. Se siguen verificando con el arranque de la app y con pruebas manuales.
