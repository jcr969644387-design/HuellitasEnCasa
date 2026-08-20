# HuellitasEnCasa

**Centro virtual de cuidado animal para niños y niñas de 8 a 12 años.**
Aplicación Android nativa (Kotlin + Jetpack Compose + Room), 100% offline, sin cuentas,
sin anuncios y sin recolección de datos personales.

Package: `com.educalab.huellitasencasa` · Versión: `1.0.0`

---

## Qué es HuellitasEnCasa

En HuellitasEnCasa adoptas una mascota virtual (perro, gato, conejo, hámster o ave), preparas
su hogar, la alimentas, la aseas, juegas con ella, aprendes a reconocer señales de bienestar y
planificas su rutina diaria. El progreso real (misiones, insignias, decoraciones) se guarda en
el dispositivo mediante una base de datos Room.

## Estructura del repositorio

```
HuellitasEnCasa/
├── app/                        Código fuente Android (Kotlin/Compose/Room)
├── database/                   schema.sql y sample_data.sql de referencia
├── docs/                       Documentación (Markdown + PDF)
│   └── pdf/                    Versión PDF de los 3 manuales principales
├── deliverables/                APK, ZIP fuente y PDFs listos para entregar
├── tools/                      Scripts auxiliares locales (generación de PDF)
├── .github/workflows/          Workflow de GitHub Actions para compilar el APK en la nube
├── gradle/ , gradlew*          Gradle Wrapper (Gradle 8.7)
├── build.gradle.kts            Configuración raíz (versiones de plugins fijas)
├── settings.gradle.kts
└── gradle.properties
```

## Cómo compilar

Requiere Android Studio (Koala o superior) o un JDK 17 + Android SDK (compileSdk 34) instalados
localmente, con acceso a `google()`/`mavenCentral()`.

```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

El APK de depuración queda en `app/build/outputs/apk/debug/app-debug.apk`.

> **Nota sobre esta entrega:** el entorno usado para generar este proyecto es un sandbox sin
> acceso a `dl.google.com`/`maven.google.com` ni al servidor de distribución de Gradle, por lo
> que **no fue posible ejecutar `assembleDebug` ni generar un APK verificado en esa sesión**.
> Sí se pudo compilar y ejecutar realmente, con un compilador Kotlin y JUnit locales, toda la
> capa de lógica de dominio (43 tests reales, ver `docs/BUILD_REPORT.md`). El proyecto está
> listo para compilar en cualquier máquina con Android Studio.

## Compilación automática en la nube (GitHub Actions)

Este repositorio incluye `.github/workflows/build-apk.yml`. Al hacer `git push` a un repositorio
de GitHub (no incluido en esta entrega: no se hizo ningún push), la Action instala JDK 17 y el
Android SDK, ejecuta los tests y compila el APK de depuración, dejándolo como artefacto
descargable.

## Privacidad

HuellitasEnCasa no usa Internet, no tiene backend, no usa Firebase/analítica/anuncios, no pide
email/teléfono/ubicación/contactos y no requiere cámara ni micrófono. El único dato que se pide
es un alias elegido libremente por quien juega. Más detalle en `docs/MEMORIA_DESCRIPTIVA.md`.

## Documentación

- `docs/MEMORIA_DESCRIPTIVA.md` / `.pdf` — visión de producto, requisitos, arquitectura, UX.
- `docs/MANUAL_USUARIO.md` / `.pdf` — cómo instalar y usar la app, módulo a módulo.
- `docs/MANUAL_TECNICO.md` / `.pdf` — stack, arquitectura, base de datos, cómo extender la app.
- `docs/BASE_DE_DATOS.md` — modelo relacional completo con diagrama Mermaid.
- `docs/BUILD_REPORT.md` — estado real de compilación y pruebas (sin resultados inventados).
