# ADR-004: Política de incorporación de dependencias

- **Estado:** Aceptado
- **Fecha:** 2026-11-19
- **Área:** Gobierno

## Contexto
El proyecto sigue evaluando alternativas de librerías (storage, persistencia, mapeo, testing). Agregar dependencias sin control compromete decisiones que todavía están abiertas y aumenta la superficie de mantenimiento y seguridad.

## Decisión
- Ninguna librería, plugin o dependencia nueva (`pom.xml`, imágenes Docker base, herramientas de build) se incorpora sin aprobación explícita del responsable del proyecto.
- La propuesta debe indicar el propósito, la alternativa sin la dependencia, la versión exacta y la licencia.
- Toda dependencia aprobada que condicione la arquitectura se registra en un ADR.

## Consecuencias
- Algunas mejoras quedan en espera de aprobación. Por ejemplo, la verificación automática de la regla de dependencia (fase 4 de [ADR-001](ADR-001-arquitectura-hexagonal-por-feature.md)) requeriría una librería como ArchUnit.
- Los mappers siguen siendo manuales mientras no se apruebe una librería de mapeo.
