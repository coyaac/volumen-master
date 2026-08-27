# Changelog

Todas las versiones notables de VolumeLock. Formato basado en Keep a Changelog.

## [0.2.0] — 2026-08-27

### Añadido
- **Globo flotante de volumen** (estilo chat-head): un globo arrastrable sobre las
  demás apps que se despliega en un panel con los 4 sliders de volumen. Requiere el
  permiso "mostrar sobre otras apps".
- Permiso de acceso a **No molestar**, necesario para fijar el volumen de llamada y
  notificación (con aviso en Ajustes).

### Corregido
- La app ya no se cierra al ajustar el volumen de **llamada o notificación**: se
  atrapa la `SecurityException` de No molestar (`setStreamVolumeSafe`).

## [0.1.0] — 2026-08-27

Primera versión funcional (uso personal, sideload).

### Añadido
- **Candado de volumen** vía AccessibilityService: bloquea las teclas físicas de
  volumen sistema-wide cuando está activo (verificado en Redmi Note 9 / MIUI).
- **Restauración automática**: un Foreground Service detecta cambios de volumen
  (BroadcastReceiver de `VOLUME_CHANGED_ACTION`) y revierte al valor objetivo.
- **Volumen objetivo por canal** (multimedia, llamada, notificación, alarma),
  persistido con DataStore.
- **Historial de cambios** (Room) con filtro Hoy / 7 días / Todo, rotación por
  límite de registros.
- **Quick Settings Tile** para activar/desactivar el candado desde el panel rápido.
- **Reactivar al reiniciar** (opcional): reactiva la protección tras el arranque.
- **Onboarding de permisos**: guía para Accesibilidad y exclusión de batería.
- UI en español según el design system (Material 3, índigo; verde para "Bloqueado").

### Notas
- Requiere activar el permiso de Accesibilidad y, en MIUI, Autostart + batería
  sin restricciones para que el sistema no cierre el servicio.
- Distribución por sideload (`adb install`), sin Play Store.
