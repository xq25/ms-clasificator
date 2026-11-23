# ADR-011: Soft delete de datasets y esquema de BD versionado

- **Estado:** Aceptado
- **Fecha:** 2026-11-23
- **Área:** Persistencia / Infraestructura
- **Modifica:** [ADR-010](ADR-010-clasificacion-por-categoria.md). Las clasificaciones usan la definición de su categoría vigente **al deslizar**, y el bloqueo de borrado desaparece.

## Contexto
Los datasets evolucionan: se rediseñan categorías, se retiran clases y se cambian los diagnósticos que definen una categoría. Las clasificaciones de los doctores son el resultado del sistema y no deben perderse ni cambiar de significado. El ADR-010 bloqueaba el borrado de categorías y datasets con clasificaciones, pero eso impedía administrarlos con el tiempo.

Además, la inicialización de la BD no funcionaba con una base vacía:
- El esquema lo creaba Hibernate (`ddl-auto=update`) al arrancar la app.
- `data.sql` se ejecutaba **antes**: desde el contenedor de MySQL y desde `spring.sql.init` en el perfil Docker. En una BD nueva insertaba en tablas inexistentes (bug #11).
- El healthcheck de MySQL tenía la contraseña equivocada (bug #10).

## Decisión

### 1. Soft delete
- `Dataset`, `DatasetCategory` y `CategoryDiagnostic` tienen **`active`** (para filtrar y validar) y **`deleted_at`** (cuándo se retiraron). Solo cambian juntos, mediante `retire()` en la entidad.
- `CategoryDiagnostic` tiene además **`created_at`**: inicio de su vigencia en la categoría.
- **El endpoint `DELETE` retira en lugar de borrar.** Retirar un dataset retira también sus categorías activas.
- **Puertos:** sus lecturas significan "activos" (adaptador con `...AndActiveTrue`), salvo los métodos que dicen lo contrario en su nombre o comentario:
  - `countByDatasetIdIncludingRetired` y `existsByDatasetIdIncludingRetired`;
  - `existsByMedicalDiagnosticId` de `CategoryDiagnostic`;
  - `findDefinitionAt`.

  No se usa `@SQLRestriction`, porque ocultaría la categoría al cargarla desde una clasificación histórica. Desaparece `delete` de estos tres puertos.
- **Unicidad solo entre activos:** nombre del dataset, un dataset activo por tipo de imagen y por diagnóstico, y un diagnóstico en una sola categoría activa del dataset. Por eso `dataset.name` ya no tiene `UNIQUE`, y `Dataset → MedicalImageType` pasa de `@OneToOne` (que generaba `UNIQUE`) a `@ManyToOne`.
- **`numValue` nunca se reutiliza:** el siguiente cuenta también las categorías retiradas. Desaparece la regla "solo se borra la última categoría".
- **Cambiar el diagnóstico de un dataset** sigue prohibido si tiene categorías, **incluidas las retiradas**.
- **Un `MedicalDiagnostic` no se puede borrar del catálogo** si alguna vez definió una categoría, aunque esté retirada.

### 2. Definición histórica de las categorías
- Los diagnósticos de una clasificación son la **definición de su categoría en la fecha del deslizamiento**: asignaciones con `created_at ≤ fecha` y (`deleted_at` nulo o `deleted_at > fecha`). Se obtienen con `CategoryDiagnosticRepository.findDefinitionAt`, que es JPQL.
- El detalle opcional (`ClassificationDiagnosis`) se valida contra esa misma definición.
- El deslizamiento solo acepta categorías activas de un dataset activo.
- **Esto modifica el ADR-010.** Allí, lo añadido a una categoría después aparecía en las clasificaciones previas. Ahora cada clasificación conserva lo que significaba la categoría cuando el doctor deslizó.

### 3. Esquema versionado y relanzamiento
- **`docker/init/01-schema.sql`:** DDL generado una vez desde las entidades (generación de esquema de Hibernate, sin dependencias nuevas) y revisado a mano.
- **`docker/init/02-data.sql`:** catálogos (CIE-10, áreas, tipos de imagen y datos primitivos).
- **`spring.jpa.hibernate.ddl-auto=validate`:** Hibernate verifica que el esquema coincida con las entidades y nunca modifica la BD. Cualquier cambio de entidades exige actualizar `01-schema.sql`.
- Se elimina `spring.sql.init` del perfil Docker y la copia de `data.sql` del classpath. Los scripts los ejecuta MySQL (`docker-entrypoint-initdb.d`) cuando el volumen está vacío; en local se ejecutan a mano en orden.
- La BD actual se **relanza** desde estos scripts. No se migran los datos de prueba.

## Alternativas consideradas
- **Solo `deleted_at`, sin `active`.** Es más normalizado, pero se eligió añadir `active` para simplificar validaciones y consultas. El riesgo de desincronización se cubre con `retire()`.
- **`@SQLRestriction` / `@Where`.** Se descarta: oculta la categoría retirada al cargarla desde una clasificación histórica.
- **Mantener `ddl-auto=update`.** Se descarta: el esquema no quedaría versionado y Hibernate podría alterar la BD sin aviso.
- **Flyway/Liquibase.** Se descartan por ahora, porque son dependencias nuevas ([ADR-004](ADR-004-politica-de-dependencias.md)). Pueden reconsiderarse cuando haya una BD con datos reales que migrar.

## Consecuencias
- Datasets, categorías y definiciones se pueden retirar sin afectar las clasificaciones históricas, que conservan su clase (`numValue`) y sus diagnósticos.
- Las categorías activas de un dataset pueden tener huecos en `numValue`, pero un mismo `numValue` nunca cambia de significado.
- Cada cambio de entidad requiere actualizar `01-schema.sql`. Si no se actualiza, la app no arranca, porque `validate` falla.
- No hay endpoint para restaurar lo retirado; queda como mejora futura.
