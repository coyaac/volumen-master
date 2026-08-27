# VolumeLock

App Android nativa (uso personal) que **bloquea el volumen del dispositivo** para
evitar cambios no deseados: botón físico atascado, apps que bajan el volumen solas
o perfiles de sonido que se pisan. Cuando el candado está activo, las teclas físicas
de volumen se ignoran y cualquier cambio externo se **revierte automáticamente** al
valor objetivo.

Verificado en un **Redmi Note 9 (MIUI 11, Android 10)**. Distribución por sideload,
sin Play Store.

## Cómo funciona

- **AccessibilityService** consume las teclas físicas de volumen mientras el candado
  está activo (única forma sin root de interceptarlas a nivel de sistema).
- **Foreground Service** con un `BroadcastReceiver` de `VOLUME_CHANGED_ACTION`:
  detecta cambios de volumen, los registra en un historial (Room) y, con el candado
  activo, restaura el volumen objetivo (`AudioManager.setStreamVolume`).
- **DataStore** guarda el estado del candado y el volumen objetivo por canal.
- **Quick Settings Tile** y **reactivar tras reinicio** como extras.

## Pantallas

- **Candado** — switch grande, volumen objetivo por canal (música, llamada,
  notificación, alarma), estado del candado.
- **Historial** — cambios detectados con hora y valor anterior → nuevo, filtro
  Hoy / 7 días / Todo. Sirve para diagnosticar qué está bajando el volumen.
- **Ajustes** — permisos (Accesibilidad, batería) y "reactivar al reiniciar".

Diseño según `docs/VolumeLock design system/` (Material 3, índigo; verde solo para
"Bloqueado"). Documentación funcional en `docs/volumelock-especificacion.md` y
`docs/volumelock-tareas.md`.

## Stack

Kotlin · Jetpack Compose · Material 3 · MVVM (`StateFlow`) · DataStore · Room ·
Coroutines/Flow · Gradle (Kotlin DSL). `minSdk 26`, `targetSdk 36`.

## Compilar

Requiere Android Studio (JDK incluido) y el SDK de Android.

```bash
# Debug
./gradlew assembleDebug        # -> app/build/outputs/apk/debug/app-debug.apk

# Release firmado (necesita .env.local, ver abajo)
./gradlew assembleRelease      # -> app/build/outputs/apk/release/app-release.apk

# Tests unitarios (JVM)
./gradlew testDebugUnitTest

# Tests instrumentados (necesita emulador/dispositivo conectado)
./gradlew connectedDebugAndroidTest
```

## Instalar en el teléfono (sideload)

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

En **MIUI/Xiaomi** hay que preparar el teléfono primero:

1. Opciones de desarrollador → **Depuración USB** y **"Depuración USB (ajustes de
   seguridad)"** activadas; desactivar **Optimización MIUI** si `adb install` da
   `INSTALL_FAILED_USER_RESTRICTED`.
2. Tras instalar, abrir la app → pestaña **Ajustes** y conceder:
   - **Accesibilidad** (obligatorio; sin esto no detecta ni bloquea el volumen).
   - **Batería sin restricciones** para VolumeLock.
   - En MIUI, activar además **Autostart** para la app (si no, el sistema cierra el
     servicio en segundo plano).

## Probar la restauración (el caso principal)

1. En la pantalla **Candado**, ajustar el volumen objetivo (o "Fijar con el volumen
   actual") y activar el switch → queda **Bloqueado**.
2. Bajar/subir el volumen desde otra app o con los botones físicos.
3. El volumen vuelve solo al objetivo y el cambio aparece en **Historial** como
   "Revertido".

## Firma release (`.env.local`)

Los secretos de firma **no se versionan**. Se leen de `.env.local` en la raíz del
repo (gitignored). Formato:

```properties
storeFile=volumelock-release.jks
storePassword=TU_CLAVE
keyAlias=volumelock
keyPassword=TU_CLAVE
```

Si `.env.local` no existe, `assembleRelease` genera un APK **sin firmar**.

> ⚠️ **Respalda `volumelock-release.jks` fuera del repo.** Si se pierde, no se puede
> publicar una actualización firmada con la misma identidad y habría que reinstalar
> desde cero. Para generar uno nuevo:
>
> ```bash
> keytool -genkeypair -v -keystore volumelock-release.jks -alias volumelock \
>   -keyalg RSA -keysize 2048 -validity 10000
> ```
