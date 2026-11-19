# ADR-002: Entidades JPA dentro del dominio (temporal)

- **Estado:** Aceptado
- **Fecha:** 2026-11-19
- **Área:** Persistencia

## Contexto
La arquitectura hexagonal ([ADR-001](ADR-001-arquitectura-hexagonal-por-feature.md)) pide en su forma pura un modelo de dominio sin anotaciones de framework, separado de las entidades de persistencia.

Las entidades actuales mezclan tres cosas:
- dominio
- mapeo JPA (`@Entity`, `@ManyToOne`, cascadas)
- serialización (`@JsonIgnore`)

El motor de base de datos aún no está decidido de forma definitiva.

## Decisión
Por ahora las entidades JPA se mantienen como modelo de dominio, en `<feature>/domain/`. El aislamiento de la persistencia se logra con **puertos de repositorio** en `application/port/out/`, implementados por adaptadores en `infrastructure/persistence/`. Los servicios dependen solo de esos puertos.

La separación entre modelo de dominio y entidad de persistencia se hará **por agregado** cuando haya un cambio real de motor de base de datos o cuando las anotaciones JPA impidan modelar una regla de negocio.

## Alternativas consideradas
- **Separar ya dominio y entidad JPA.** Es lo más limpio, pero duplica unas 20 clases y sus mappers sin un beneficio inmediato mientras el motor siga siendo MySQL.

## Consecuencias
- El dominio sigue acoplado a anotaciones de `jakarta.persistence`. Es un acoplamiento de compilación, no de comportamiento.
- Los puertos de repositorio deben devolver tipos propios (`PageResult`, `Optional`, listas) y nunca `Page` ni `Specification` de Spring Data. Así un cambio futuro queda confinado al adaptador.
- `@JsonIgnore` sigue en las entidades. Debe desaparecer cuando ninguna respuesta de la API serialice entidades directamente, solo DTOs.
- Renombrar clases no puede cambiar el esquema: toda entidad renombrada fija su tabla con `@Table(name = ...)` ([ADR-003](ADR-003-convencion-de-nombres-y-paquetes.md)).
