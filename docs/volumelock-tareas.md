# VolumeLock — Plan de Tareas (para Claude Code)

Basado en `volumelock-especificacion.md`. Orden pensado para minimizar riesgo: primero se valida la pieza incierta (AccessibilityService), después se construye el resto encima.

---

## FASE 0 — Setup del proyecto

### T0.1 — Crear proyecto base Android
**Descripción:** Crear proyecto Kotlin + Jetpack Compose en Android Studio, `minSdk 26`, `targetSdk 35`, arquitectura de carpetas por capa (`ui`, `domain`, `data`, `service`).

**Criterios de aceptación:**
- [ ] El proyecto compila y corre un "Hello World" en Compose sin errores.
- [ ] Estructura de paquetes creada: `ui/`, `data/`, `service/`, `di/` (opcional).
- [ ] `build.gradle.kts` configurado con Kotlin, Compose, Coroutines.
- [ ] Repositorio Git inicializado con `.gitignore` apropiado para Android.

### T0.2 — Dependencias base
**Descripción:** Agregar Room, DataStore, Coroutines/Flow, dependencias de test (JUnit5, Turbine, Espresso).

**Criterios de aceptación:**
- [ ] `./gradlew build` pasa sin errores de resolución de dependencias.
- [ ] Versión de cada librería fijada explícitamente (no `+`).

---

## FASE 1 — Validar el AccessibilityService (pieza de mayor riesgo)

### T1.1 — Implementar AccessibilityService mínimo
**Descripción:** Crear `VolumeAccessibilityService` que solo loguee (Logcat) cuando detecta `KEYCODE_VOLUME_UP`/`KEYCODE_VOLUME_DOWN`, sin consumir el evento todavía. Declarar en manifest + `accessibility_service_config.xml` con `filterKeyEvents = true`.

**Criterios de aceptación:**
- [ ] El servicio aparece en Ajustes > Accesibilidad y se puede activar manualmente.
- [ ] Al presionar los botones físicos de volumen con el servicio activo, aparece el log correspondiente.
- [ ] El volumen del sistema sigue cambiando normalmente (evento NO consumido aún).

### T1.2 — Consumir el evento (bloqueo real)
**Descripción:** Modificar el servicio para que, cuando un flag `lockActive` sea `true`, retorne `true` en `onKeyEvent` (consume el evento) y no deje pasar el cambio de volumen al sistema.

**Criterios de aceptación:**
- [ ] Con `lockActive = true` (hardcodeado para esta prueba), presionar los botones físicos NO cambia el volumen del sistema.
- [ ] Con `lockActive = false`, los botones funcionan normal.
- [ ] Probado en al menos: pantalla apagada, pantalla encendida con otra app en primer plano, pantalla encendida con VolumeLock en primer plano.

> ⚠️ Si T1.2 falla en tu celular específico (fabricante con restricciones propias, ej. algunos Xiaomi/Huawei), hay que reevaluar el enfoque antes de seguir a la Fase 2.

---

## FASE 2 — Persistencia y estado

### T2.1 — DataStore para configuración
**Descripción:** Implementar `VolumeRepository` con Jetpack DataStore para guardar: estado del lock (on/off), volumen objetivo por stream (música, llamada, notificación, alarma).

**Criterios de aceptación:**
- [ ] El estado del lock persiste tras cerrar y reabrir la app.
- [ ] Los valores objetivo por stream persisten correctamente.
- [ ] Cubierto con al menos 1 test unitario por método del repositorio.

### T2.2 — Room DB para el log de cambios
**Descripción:** Crear `VolumeLogEntity`, `VolumeLogDao`, y la lógica de inserción de registros (timestamp, stream, valor anterior, valor nuevo, si fue revertido).

**Criterios de aceptación:**
- [ ] Se puede insertar y leer registros vía `Flow`.
- [ ] Existe función de limpieza (`deleteOlderThan`) probada con test unitario.
- [ ] Límite de registros aplicado (ver RNF05 del documento base).

---

## FASE 3 — Servicio de protección activa

### T3.1 — BroadcastReceiver de cambios de volumen
**Descripción:** Implementar `VolumeChangeReceiver` escuchando `VOLUME_CHANGED_ACTION`, comparando contra el valor objetivo guardado, y registrando el evento en el log (T2.2).

**Criterios de aceptación:**
- [ ] Cada cambio real de volumen en el sistema genera una entrada en el log, con o sin lock activo.
- [ ] El campo `reverted` queda correctamente marcado cuando corresponde.

### T3.2 — Foreground Service de restauración
**Descripción:** `VolumeForegroundService` que, cuando el lock esté activo y se detecte un cambio no originado por la propia app, restaure el volumen objetivo vía `AudioManager.setStreamVolume()`. Debe mostrar notificación persistente obligatoria.

**Criterios de aceptación:**
- [ ] Con lock activo, si algo cambia el volumen (ej. otra app), se revierte en menos de 1 segundo perceptible.
- [ ] La notificación persistente muestra el estado (bloqueado/desbloqueado).
- [ ] El servicio sobrevive al menos 30 minutos en background sin ser matado (probar con pantalla apagada).

---

## FASE 4 — UI

### T4.1 — Pantalla principal (toggle + config)
**Descripción:** Compose UI con: switch grande para activar/desactivar lock, sliders/inputs para volumen objetivo por stream, indicador de estado.

**Criterios de aceptación:**
- [ ] El switch refleja y modifica el estado real del repositorio (T2.1).
- [ ] Cambios en los sliders de volumen objetivo se guardan y aplican sin reiniciar la app.

### T4.2 — Pantalla de historial/log
**Descripción:** Lista (LazyColumn) mostrando el historial de cambios de volumen (T2.2), con filtro por fecha.

**Criterios de aceptación:**
- [ ] La lista se actualiza en tiempo real cuando hay nuevos eventos.
- [ ] El filtro por fecha reduce correctamente los resultados mostrados.

### T4.3 — Onboarding de permisos
**Descripción:** Pantalla/flujo que detecta si falta el permiso de Accesibilidad o si la app no está excluida de optimización de batería, y guía al usuario a los Ajustes correspondientes.

**Criterios de aceptación:**
- [ ] Si falta el permiso de Accesibilidad, se muestra un aviso claro con botón que abre Ajustes > Accesibilidad directamente.
- [ ] Si la app no está excluida de optimización de batería, se muestra aviso con botón que dispara el intent correspondiente.
- [ ] Ambos avisos desaparecen automáticamente una vez resuelto el estado.

---

## FASE 5 — Extras

### T5.1 — Quick Settings Tile
**Descripción:** `TileService` para activar/desactivar el lock desde el panel rápido de notificaciones.

**Criterios de aceptación:**
- [ ] El tile aparece disponible para agregar al panel rápido.
- [ ] Tocar el tile cambia el estado del lock y se refleja también en la app abierta (estado sincronizado).

### T5.2 — Persistencia tras reinicio (opcional, HU07)
**Descripción:** `BroadcastReceiver` de `BOOT_COMPLETED` que reactiva el servicio si el usuario configuró esa opción.

**Criterios de aceptación:**
- [ ] Con la opción activada, tras reiniciar el celular el lock queda en el mismo estado que tenía antes de apagar.
- [ ] Con la opción desactivada, tras reiniciar el lock queda apagado por defecto.

---

## FASE 6 — Cierre

### T6.1 — Pruebas de estabilidad
**Descripción:** Uso real durante al menos 48 horas con lock activo, revisando batería y que no haya crasheos.

**Criterios de aceptación:**
- [ ] Sin crashes registrados en Logcat durante el período de prueba.
- [ ] Consumo de batería de la app dentro de rangos razonables (comparable a apps similares en segundo plano).
- [ ] El bug original (volumen bajando solo) queda documentado en el log: se puede identificar si ocurre o no mientras el lock está activo.

### T6.2 — Build final firmado
**Descripción:** Generar APK firmado para instalación directa (sideload), sin dependencias de Play Store.

**Criterios de aceptación:**
- [ ] APK instalable vía `adb install` en el celular de destino.
- [ ] Versión y changelog básico documentados en el repo (`CHANGELOG.md` o tag de Git).

---

## Notas para Claude Code

- Cada tarea es idealmente un commit o PR separado.
- La Fase 1 (T1.1/T1.2) es bloqueante: si falla, todo lo posterior debe reevaluarse antes de continuar.
- El documento `volumelock-especificacion.md` (historias de usuario, RF/RNF, UML, stack) es el contexto de referencia para todas las tareas de este archivo.
