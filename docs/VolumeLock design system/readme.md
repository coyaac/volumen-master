# VolumeLock — Design System

Sistema de diseño para **VolumeLock**, una app Android nativa (Kotlin + Jetpack Compose) de uso personal que bloquea el volumen del dispositivo para evitar cambios no deseados: botón físico atascado, apps que bajan el volumen solas, perfiles de sonido que se pisan entre sí.

No hay distribución en Play Store: un solo dispositivo, un solo usuario. El sistema puede apoyarse en Material 3 sin adaptaciones a iOS ni compromisos de compatibilidad amplia.

## Fuentes

No se entregó código, Figma ni assets de marca. Todo lo que hay aquí se derivó del brief escrito del usuario (agosto 2026) y de las especificaciones públicas de Material Design 3. **No existe logotipo**: donde correspondería una marca se compone el nombre "VolumeLock" en tipografía plana. No se ha dibujado ningún símbolo de marca.

## Producto y pantallas

| Pantalla | Rol |
| --- | --- |
| Principal | Estado del candado (switch grande), sliders de volumen objetivo por stream, indicador de estado inequívoco |
| Historial | Log cronológico de cambios detectados: hora, stream, valor anterior → nuevo, si se revirtió; filtro por fecha |
| Onboarding / Permisos | Guía para activar Accesibilidad y excluir la app de la optimización de batería; los avisos desaparecen al resolverse |
| Quick Settings Tile | Toggle rápido desde el panel desplegable de Android |

Streams gestionados: **música**, **llamada**, **notificación**, **alarma**.

## Fundamentos visuales

**Color.** Esquema Material 3 generado desde la semilla índigo `#4A4FA6`. Las paletas tonales (`tokens/palette.css`) se calcularon en OKLCH sobre una escala perceptual compartida, con los tonos M3 habituales (0, 4, 6, 10, 12, 17, 20, 22, 24, 30, 40, 50, 60, 70, 80, 87, 90, 92, 94, 95, 96, 98, 99, 100). Los roles semánticos viven en `tokens/color.css`, con esquema claro por defecto y oscuro bajo `[data-theme="dark"]`.

Adición intencional al set de roles M3: **`--vl-locked`** (verde, tono 152). Material 3 no define un rol "éxito", y esta app necesita que "candado activo" se lea a un metro de distancia sin confundirse con el primario. El verde se usa **sólo** para el estado bloqueado; el primario índigo, para acciones. Nunca al revés.

**Tipografía.** Roboto Flex para toda la interfaz, con la escala M3 completa (`tokens/typography.css`). Roboto Mono, únicamente, para cifras de volumen y valores del log, donde la alineación tabular importa. Sin sustituciones creativas: es la tipografía del sistema y eso ayuda a la confianza.

**Forma.** Esquinas de 12dp en tarjetas, 28dp en el bloque de estado y en hojas modales, pastilla completa en botones. Nada de cuadrados duros ni de círculos decorativos.

**Elevación.** Sombras M3 niveles 0–5. La app vive casi entera en nivel 0 y 1; los niveles altos se reservan a diálogos. Las superficies se diferencian por tono (`surface-container-*`), no por sombra.

**Espaciado.** Rejilla de 4dp, margen lateral de pantalla de 16dp, objetivo táctil mínimo 48dp — sin excepciones, incluidos los sliders.

**Movimiento.** Transiciones de estado a 300ms con la curva `emphasized` de M3. El cambio de bloqueado ↔ desbloqueado es la única animación con presencia: color y forma del bloque de estado cruzan juntos. No hay rebotes, ni entradas escalonadas, ni animaciones decorativas.

**Estados.** Capas de estado M3 por opacidad sobre el color de contenido: hover 8%, focus 10%, pressed 10%, arrastre 16%. Deshabilitado: contenido al 38%, contenedor al 12%. Foco de teclado con anillo de 3px en el secundario.

**Fondos e imágenes.** Sin fotografía, sin ilustración, sin degradados, sin texturas. Es una app de control: cada superficie es color plano.

## Fundamentos de contenido

Español de España neutro, sin regionalismos. Se trata al usuario de **tú**. La app habla de sí misma en tercera persona ("VolumeLock revirtió el cambio"), nunca en primera.

- **Estados, no eufemismos.** "Bloqueado" / "Desbloqueado". No "Protegido", no "Activo", no "Modo seguro".
- **Frases cortas y literales.** "Volumen fijado en 12 de 15." "Se revirtió un cambio hace 4 minutos."
- **Los permisos se explican por su consecuencia**, no por su nombre técnico: "Sin este permiso, VolumeLock no puede detectar los cambios de volumen."
- **Mayúscula sólo inicial** en títulos, etiquetas y botones ("Guardar cambios", no "Guardar Cambios").
- **Botones en infinitivo o imperativo corto**: "Activar", "Abrir ajustes", "Entendido".
- **Sin emoji. Sin signos de exclamación.** El tono es el de un panel de instrumentos.
- **Los números se escriben en cifra** y siempre con su escala: "12 / 15", nunca "80%" salvo que el stream se muestre en porcentaje.

## Iconografía

**Material Symbols Rounded**, cargado desde Google Fonts en `tokens/fonts.css` y usado con la clase `.md-icon` (eje `FILL` a 0 por defecto, 1 para el estado activo). Es el set nativo de Android, así que no hay sustitución que declarar. Tamaños: 24dp en línea y en listas, 20dp dentro de botones, 40dp en el bloque de estado principal.

Glifos de referencia: `lock`, `lock_open`, `volume_up`, `ring_volume`, `notifications`, `alarm`, `history`, `accessibility_new`, `battery_saver`, `undo`, `check_circle`, `error`.

Nada de emoji, nada de caracteres unicode como iconos, nada de SVG dibujado a mano.

## Índice

- `styles.css` — punto de entrada; sólo `@import`.
- `tokens/` — `palette.css` (rampas tonales), `color.css` (roles semánticos claro/oscuro), `typography.css`, `shape.css`, `elevation.css`, `spacing.css`, `motion.css`, `fonts.css`, `base.css`.
- `SKILL.md` — envoltorio para usar este sistema como Agent Skill.

- `foundations/` — color, tipografía, forma y espacio, movimiento y estados, iconografía.
- `components/` — cada componente en su carpeta con `.jsx`, `.d.ts`, `.prompt.md` y una ficha `.card.html` por familia: `actions/`, `inputs/`, `containment/`, `communication/`, `navigation/` y `volumelock/` (los específicos del producto: `LockHero`, `LogEntry`, `QsTile`).
- `_bundle.jsx` — todos los componentes concatenados en `window.VL` para las maquetas. Generado; edita el `.jsx` de origen, no el bundle.
- `templates/ui-kit.html` — las cuatro pantallas montadas: Principal, Historial, Onboarding y Quick Settings.

## Uso en maquetas

```html
<link rel="stylesheet" href="styles.css">
<script src="https://unpkg.com/react@18/umd/react.development.js"></script>
<script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
<script src="https://unpkg.com/@babel/standalone@7.20.6/babel.min.js"></script>
<script type="text/babel" src="_bundle.jsx"></script>
<script type="text/babel">
  const { LockHero, VolumeSlider } = window.VL;
</script>
```

En producción (Compose) los archivos `.jsx` son la referencia visual, no el código a portar: los tokens de `tokens/` se trasladan a un `ColorScheme` y una `Typography` de Material 3.
