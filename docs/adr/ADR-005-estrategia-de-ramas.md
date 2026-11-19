# ADR-005: Estrategia de ramas para la re-estructuración

- **Estado:** Aceptado
- **Fecha:** 2026-11-19
- **Área:** Gobierno

## Contexto
La re-estructuración ([ADR-001](ADR-001-arquitectura-hexagonal-por-feature.md)) se aplica en varias fases y actividades, y cada una debe poder probarse por separado. `main` no debe recibir cambios parciales mientras dura el proceso.

Al iniciar, la rama más reciente era `Contenerizacion` (`fb0ca82`): estaba 3 commits por delante de `main` y no le faltaba ningún commit de `main`.

## Decisión
1. **Rama de integración: `feat/re-estructuracion`.** Se crea desde `Contenerizacion` (`fb0ca82`). Mientras dure la re-estructuración funciona como la nueva `main`: allí se integra, se prueba y se despliega en entornos de prueba.
2. **Ramas de actividad.** Cada fase o actividad sale de `feat/re-estructuracion` y vuelve a ella. Prefijos:

   | Prefijo | Uso | Ejemplo |
   |---------|-----|---------|
   | `refactor/` | Cambios estructurales sin cambiar comportamiento | `refactor/fase-1-paquetes-y-nombres` |
   | `feat/` | Funcionalidad nueva | `feat/puertos-persistencia-dataset` |
   | `fix/` | Corrección de bugs | `fix/dataset-update-validacion` |
   | `docs/` | Solo documentación | `docs/adr-xxx` |

   No se usa `feat/re-estructuracion/<actividad>`. Git no permite que coexistan una rama `a/b` y otra `a/b/c`.
3. **Flujo:**
   ```
   Contenerizacion ──► feat/re-estructuracion ──► (al final) main
                           ▲        │
                           │ merge  │ branch
                           └── refactor/… | feat/… | fix/… | docs/…
   ```
   - Antes de integrar una rama de actividad: debe compilar y su ADR o documentación debe estar actualizada.
   - Cada rama de actividad incluye en sus propios commits los ADR que origina.
4. **Cierre:** al terminar la re-estructuración, `feat/re-estructuracion` se integra en `main` y se eliminan las ramas de actividad ya integradas.

## Consecuencias
- `main` queda estable hasta el final y cada actividad se puede revisar y revertir por separado.
- Si `main` recibe cambios durante el proceso, hay que traerlos a `feat/re-estructuracion` para evitar un merge final grande.
- Las ramas viejas (`Contenerizacion`, `services`, `master`, etc.) quedan fuera de este flujo. Su limpieza es una decisión aparte.
