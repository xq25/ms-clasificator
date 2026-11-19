# ADR-006: Integración de actividades vía Pull Request

- **Estado:** Aceptado
- **Fecha:** 2026-11-19
- **Área:** Gobierno
- **Modifica:** [ADR-005](ADR-005-estrategia-de-ramas.md) (punto 3, flujo de integración)

## Contexto
En ADR-005, las ramas de actividad se integraban en `feat/re-estructuracion` con merge local. Así se integró la fase 1 (merge `7671ead`). Desde que `feat/re-estructuracion` está publicada en `origin`, se quiere que cada integración quede revisada y registrada en GitHub.

## Decisión
Cada rama de actividad se integra en `feat/re-estructuracion` **mediante un Pull Request en GitHub**. El responsable del proyecto lo abre, lo revisa y hace el merge manualmente.

### Flujo por actividad
1. **Partir actualizado:**
   ```
   git switch feat/re-estructuracion
   git pull --ff-only
   git switch -c <prefijo>/<actividad>
   ```
2. **Trabajar y hacer commit** en la rama de actividad. Antes de subir, el código debe compilar y los ADR o la documentación de la actividad deben estar incluidos.
3. **Publicar:** `git push -u origin <prefijo>/<actividad>`.
4. **PR:** abrir el PR con **base `feat/re-estructuracion`**, nunca `main`. Lo revisa y lo integra el responsable del proyecto.
5. **Sincronizar:** después del merge, actualizar `feat/re-estructuracion` en local antes de crear la siguiente rama.

### Responsabilidades del asistente (Claude)
- Puede crear ramas de actividad, hacer commit y publicar la rama de actividad (`push`).
- **No** abre, aprueba ni integra PR, y **no** hace merge local en `feat/re-estructuracion` ni en `main`.
- **No** hace `push` directo a `feat/re-estructuracion` ni a `main`.

## Consecuencias
- Cada actividad queda en GitHub con su PR, diff y revisión.
- El merge de la fase 1 (`7671ead`) es la única integración local. Es anterior a este ADR y no tiene PR.
- Si `feat/re-estructuracion` avanza mientras una rama de actividad está abierta, la rama se actualiza con merge desde `feat/re-estructuracion` antes del PR. No se usa rebase sobre ramas ya publicadas.
