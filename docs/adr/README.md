# Architecture Decision Records (ADR)

Registro de las decisiones de arquitectura del proyecto. Cada decisión vive en su propio archivo numerado y **no se edita después de aceptada**. Si cambia, se crea un ADR nuevo que la reemplaza (`Reemplazado por ADR-XXX`).

## Índice

| # | Título | Estado | Área |
|---|--------|--------|------|
| [001](ADR-001-arquitectura-hexagonal-por-feature.md) | Arquitectura hexagonal organizada por feature | Aceptado | Arquitectura |
| [002](ADR-002-entidades-jpa-en-dominio.md) | Entidades JPA dentro del dominio (temporal) | Aceptado | Persistencia |
| [003](ADR-003-convencion-de-nombres-y-paquetes.md) | Convención de nombres y paquetes | Aceptado | Código |
| [004](ADR-004-politica-de-dependencias.md) | Política de incorporación de dependencias | Aceptado | Gobierno |
| [005](ADR-005-estrategia-de-ramas.md) | Estrategia de ramas para la re-estructuración | Aceptado (modificado por 006 y 008) | Gobierno |
| [006](ADR-006-integracion-via-pull-request.md) | Integración de actividades vía Pull Request | Aceptado | Gobierno |
| [007](ADR-007-puertos-de-persistencia.md) | Puertos de persistencia y paginación propia | Aceptado | Persistencia |
| [008](ADR-008-estrategia-de-pruebas.md) | Estrategia de pruebas | Aceptado | Calidad |
| [009](ADR-009-errores-de-servicios-externos.md) | Manejo de errores de servicios externos | Aceptado | Arquitectura |
| [010](ADR-010-clasificacion-por-categoria.md) | Clasificación de imágenes por categoría (deslizamiento) | Aceptado (modificado por 011) | Arquitectura / Persistencia |
| [011](ADR-011-soft-delete-y-esquema-versionado.md) | Soft delete de datasets y esquema de BD versionado | Aceptado | Persistencia / Infraestructura |

## Plantilla

```markdown
# ADR-XXX: Título

- **Estado:** Propuesto | Aceptado | Reemplazado por ADR-YYY
- **Fecha:** AAAA-MM-DD
- **Área:** Arquitectura | Persistencia | Código | Gobierno | Infraestructura

## Contexto
Qué problema o fuerza motiva la decisión.

## Decisión
Qué se decidió, en concreto.

## Alternativas consideradas
Opciones descartadas y por qué.

## Consecuencias
Qué se gana, qué se pierde, qué queda pendiente.
```
