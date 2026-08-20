# BUILD_REPORT — HuellitasEnCasa v1.0.0

Generado: 2026-08-20 (sesión de construcción del proyecto). Este documento registra
únicamente hechos verificados en el propio sandbox de generación. Ningún resultado de
compilación de Gradle ni ningún APK se han inventado.

---

## 1. Entorno de generación

| Herramienta | Disponible en el sandbox | Notas |
|---|---|---|
| JDK | ✅ OpenJDK 21.0.10 | `java -version` |
| Kotlin compiler (sistema) | ✅ kotlinc 1.3.31 | Muy anterior al 2.0.21 del proyecto; solo se usó para verificación puntual (ver §3) |
| Gradle Wrapper (`gradlew`) | ⚠️ Descargado (script + `gradle-wrapper.jar` reales desde GitHub), pero **no puede ejecutar tareas** | El bootstrap necesita descargar `gradle-8.7-bin.zip` desde `services.gradle.org` |
| Android SDK | ❌ No disponible | No hay forma de instalarlo sin acceso a `dl.google.com` |
| Acceso a `google()` (Maven de Google) | ❌ Bloqueado | Necesario para AGP, AndroidX, Jetpack Compose, Room |
| JUnit 4 + Hamcrest (paquete del sistema) | ✅ `/usr/share/java/junit4.jar` | Usado para ejecutar de verdad la capa de dominio (ver §3) |
| SQLite (vía Python `sqlite3`) | ✅ | Usado para validar `database/schema.sql` y `database/sample_data.sql` (ver §4) |

### Comprobación real de la limitación de red

```
$ curl -s -o /dev/null -w "%{http_code}\n" https://dl.google.com/android/repository/repository2-3.xml
403
$ curl -s -o /dev/null -w "%{http_code}\n" https://services.gradle.org/distributions/gradle-8.7-bin.zip
403
$ ./gradlew --version
Downloading https://services.gradle.org/distributions/gradle-8.7-bin.zip
Exception in thread "main" java.io.IOException: Server returned HTTP response code: 403
    for URL: https://services.gradle.org/distributions/gradle-8.7-bin.zip
    at org.gradle.wrapper.Install.forceFetch(SourceFile:2)
    ...
```

**Conclusión:** `./gradlew clean|testDebugUnitTest|lintDebug|assembleDebug` **no se pudieron
ejecutar** en este entorno. Estado oficial: **COMPILACIÓN NO VERIFICADA.**

---

## 2. Qué SÍ se pudo verificar realmente (y cómo)

Como el entorno no permite un build de Gradle/Android, se usaron herramientas equivalentes ya
instaladas en el sistema para verificar, de forma real (no simulada), las partes del proyecto
que no dependen del SDK de Android:

1. **Sintaxis y tipos de toda la capa `domain/`** (modelo + lógica de negocio), compilada con
   `kotlinc` de verdad.
2. **43 de los 54 tests unitarios**, compilados y ejecutados con JUnit 4.13.2 real contra el
   bytecode real de `domain/`.
3. **`database/schema.sql`**, ejecutado contra una base SQLite real (motor idéntico al que usa
   Room en Android).
4. **`database/sample_data.sql`**, cargado sobre ese mismo esquema, con verificación de
   conteos y de integridad referencial (`PRAGMA foreign_key_check`).
5. **Los 55 vector drawables** (`res/drawable/*.xml` + `drawable-v24/*.xml`), parseados como
   XML válido uno por uno.
6. **Los 3 PDF** de `docs/pdf/`, generados con una herramienta real de conversión y
   verificados con `pypdf` (número de páginas y presencia de caracteres en español).
7. **El YAML de GitHub Actions**, parseado y validado.

Nada de esto sustituye a `./gradlew assembleDebug` — sigue siendo necesario compilar el
proyecto completo en una máquina con Android Studio/SDK para obtener el APK — pero da
evidencia real de que el código no tiene errores triviales de sintaxis/tipos y de que los
datos y el esquema son consistentes entre sí.

---

## 3. Tests unitarios

### 3.1 Ejecutados de verdad en este sandbox (JVM pura, sin Android)

```
$ kotlinc domain/model/*.kt domain/logic/*.kt \
    test/domain/CareEngineTest.kt test/domain/MissionEngineTest.kt \
    test/domain/PlannerValidatorTest.kt test/domain/ScenarioGraderAndProgressStateTest.kt \
    test/domain/WellbeingCalculatorTest.kt test/domain/AcademyCurriculumTest.kt \
    -cp junit4.jar:hamcrest-core.jar -d out
# compiló sin errores

$ java -cp junit4.jar:hamcrest-core.jar:out:kotlin-stdlib.jar org.junit.runner.JUnitCore \
    com.educalab.huellitasencasa.domain.CareEngineTest \
    com.educalab.huellitasencasa.domain.MissionEngineTest \
    com.educalab.huellitasencasa.domain.PlannerValidatorTest \
    com.educalab.huellitasencasa.domain.ScenarioGraderTest \
    com.educalab.huellitasencasa.domain.ProgressStateResolverTest \
    com.educalab.huellitasencasa.domain.WellbeingCalculatorTest \
    com.educalab.huellitasencasa.domain.AcademyCurriculumTest

JUnit version 4.13.2
...........................................
Time: 0.09

OK (43 tests)
```

> Nota técnica: el `kotlinc` 1.3.31 del sistema es anterior al `2.0.21` que usa el proyecto y
> no soporta `Enum.entries` (API de Kotlin 1.9+). Para esta verificación puntual se usó una
> **copia temporal** de los archivos con `.entries` sustituido por `.values().toList()`
> (equivalente funcional). El código fuente real entregado en `app/` **no se tocó** y usa
> `.entries` con normalidad, válido para Kotlin 2.0.21 vía Gradle.

**Resultado: 43/43 tests pasaron.**

Archivos y conteo de tests:

| Archivo | Tests | Ejecutado en sandbox |
|---|---:|:---:|
| `domain/CareEngineTest.kt` | 10 | ✅ |
| `domain/MissionEngineTest.kt` | 7 | ✅ |
| `domain/PlannerValidatorTest.kt` | 7 | ✅ |
| `domain/ScenarioGraderAndProgressStateTest.kt` | 7 | ✅ |
| `domain/WellbeingCalculatorTest.kt` | 8 | ✅ |
| `domain/AcademyCurriculumTest.kt` | 4 | ✅ |
| `data/SeedProviderTest.kt` | 8 | ⚠️ Requiere Robolectric + Android SDK |
| `data/MissionRewardFlowTest.kt` | 3 | ⚠️ Requiere Robolectric + Android SDK |
| **Total** | **54** | **43 verificados, 11 pendientes de Gradle** |

### 3.2 Pendientes de ejecutar vía Gradle (`testDebugUnitTest`)

`SeedProviderTest` y `MissionRewardFlowTest` usan `@RunWith(RobolectricTestRunner::class)` y
una base de datos Room en memoria (`HuellitasDatabase.inMemory`). Robolectric necesita el
Android SDK/`android.jar`, que no está disponible aquí. Su código fue escrito y revisado
manualmente con el mismo cuidado que el resto (ver contenido en `app/src/test/.../data/`), pero
**su ejecución queda pendiente de un entorno con Gradle + Android SDK completo**.

### 3.3 Cobertura por área (de los 54 tests)

- Reglas de cuidado y piso protector anti-castigo (`CareEngine`): límites, clamping,
  decaimiento por sesión, valores negativos, `PetIndicators` fuera de rango.
- Cálculo de bienestar y niveles (`WellbeingCalculator`): promedio, redondeo, empates.
- Progreso de misiones (`MissionEngine`): incremento, tope en el objetivo, misión ya
  completada, tipo de evento no coincidente, `amount` no positivo.
- Validación del planificador (`PlannerValidator`): franjas permitidas por tipo de acción,
  plan vacío, ratio de acierto, umbral de aprobación (80%).
- Corrección de escenarios (`ScenarioGrader`) y resolución de estados visuales
  (`ProgressStateResolver`): los 5 estados (bloqueado/disponible/iniciado/completado/dominado).
- Integridad del contenido semilla (`SeedProviderTest`): conteos exactos (5/30/50/30/30/10/8/8)
  e idempotencia (sembrar dos veces no duplica).
- Flujo real de recompensas (`MissionRewardFlowTest`): completar una misión de verdad entrega
  su insignia/decoración una única vez, sin duplicados.

---

## 4. Base de datos: `schema.sql` y `sample_data.sql`

Verificado cargando ambos ficheros en una base SQLite real (`sqlite3` vía Python):

```
pet_species: 5 (esperado 5) [OK]
pet_need_definitions: 30 (esperado 30) [OK]
food_items: 50 (esperado 50) [OK]
wellbeing_scenarios: 30 (esperado 30) [OK]
missions: 30 (esperado 30) [OK]
badges: 10 (esperado 10) [OK]
decorations: 8 (esperado 8) [OK]
home_items: 8 (esperado 8) [OK]
user_profiles: 1 (esperado 1) [OK]
virtual_pets: 1 (esperado 1) [OK]
foreign_key_check errors: []
ALL OK
```

21 tablas creadas (las 20 del listado BASE + `academy_lesson_progress`, documentada como
adición necesaria).

---

## 5. Recursos visuales

- 55 archivos `.xml` en `res/drawable/` y `res/drawable-v24/`, generados como vectores propios
  (no Material Icons genéricos): insignias, decoraciones, iconos de módulo, objetos del hogar,
  estados de progreso, categorías de alimentos, logo, motivos decorativos e icono adaptativo.
- Todos parseados con `xml.etree.ElementTree` sin errores.
- La mascota animada (`PetIllustration.kt`) se dibuja con Compose Canvas (prioridad 3 de la
  especificación maestra), con 4 expresiones (feliz/neutral/cansado/hambriento) por especie.

---

## 6. Documentación y PDF

| Archivo | Estado |
|---|---|
| `README.md` | ✅ |
| `docs/MEMORIA_DESCRIPTIVA.md` | ✅ |
| `docs/MANUAL_USUARIO.md` | ✅ |
| `docs/MANUAL_TECNICO.md` | ✅ |
| `docs/BASE_DE_DATOS.md` | ✅ (incluye DER en Mermaid) |
| `docs/pdf/MEMORIA_DESCRIPTIVA.pdf` | ✅ Real, 6 páginas, SHA-256 abajo |
| `docs/pdf/MANUAL_USUARIO.pdf` | ✅ Real, 3 páginas, SHA-256 abajo |
| `docs/pdf/MANUAL_TECNICO.pdf` | ✅ Real, 5 páginas, SHA-256 abajo |

Generados con `tools/generate_pdfs.py` (markdown → HTML → PDF vía `xhtml2pdf`), verificados
después con `pypdf` (apertura correcta, conteo de páginas y presencia de acentos/ñ/¿).

---

## 7. Checksums SHA-256 (archivos verificables generados en esta sesión)

```
87887a88f9c25d2966fbf8e4ad7bb1ee1ca810dc9bcce03eac0c0d3a89cd3aa9  database/schema.sql
69b159afb360f8900c6afa231e28b593c863d29079771c4b1e368e1ef6f51c95  database/sample_data.sql
3f883f92ba2f98ce5c2164c31518e16993f49f8f07696340d1d86c64a20a6260  docs/pdf/MANUAL_TECNICO.pdf
844dffc350b93bf355fc98d2d3ca142eb7fb61b7161c8d384d6676bff5a32a65  docs/pdf/MANUAL_USUARIO.pdf
677372a85da0fb110053d80359dfd48418af47ed868b09c92817a353870c094a  docs/pdf/MEMORIA_DESCRIPTIVA.pdf
144f9c4caa7b37c0d52032ab2668a2569d75306c4fab8828b8583fab46b048c2  deliverables/HuellitasEnCasa-v1.0.0-source.zip
```

(El SHA-256 del APK no aparece porque el APK no se generó — ver §8.)

---

## 8. APK

**No generado.** `assembleDebug` no pudo ejecutarse (§1). No existe
`app/build/outputs/apk/debug/app-debug.apk` ni, por tanto, ningún archivo en
`deliverables/HuellitasEnCasa-v1.0.0.apk`. Cualquier persona con Android Studio (o CI con
acceso a `google()`/`services.gradle.org`, como `.github/workflows/build-apk.yml`) puede
generarlo siguiendo `README.md`.

---

## 9. Estadísticas del proyecto

- 52 archivos Kotlin en `app/src/main` + 8 en `app/src/test` = 60 archivos, ~5.500 líneas.
- 20 entidades Room del listado BASE + 1 adicional documentada = 21 tablas.
- 12 pantallas Compose principales + onboarding.
- 54 tests unitarios (43 verificados en este sandbox, 11 pendientes de Gradle).
- 55 recursos vectoriales propios.
- Contenido semilla: 5 especies, 30 definiciones de necesidad, 50 tarjetas de
  alimentos/situaciones, 30 escenarios de bienestar, 30 misiones, 10 insignias, 8
  decoraciones, 8 objetos del hogar, 8 lecciones de Academia.

---

## 10. Limitaciones conocidas (documentadas, no ocultadas)

1. **Compilación Android no verificada** en esta sesión (§1). El código fue escrito siguiendo
   estrictamente las APIs de las versiones fijadas (AGP 8.5.2 / Kotlin 2.0.21 / Compose BOM
   2024.09.00 / Room 2.6.1), pero solo una compilación real con Android Studio puede confirmar
   que no hay errores específicos del SDK de Android (recursos, manifest merger, etc.).
2. Los 11 tests basados en Robolectric no se ejecutaron en el sandbox (§3.2).
3. El planificador de rutinas valida colocación por franja permitida (no exige un único orden
   rígido); está documentado así intencionalmente en `PlannerValidator.kt`, no es una
   simplificación oculta.
4. El drag & drop se implementó con `pointerInput`/`detectDragGestures` propio (no una librería
   externa), por ser la opción más simple y confiable sin dependencias adicionales; su
   comportamiento visual final solo puede confirmarse en un dispositivo/emulador real.
5. No se realizó ningún `git push`; `.github/workflows/build-apk.yml` existe pero nunca se ha
   ejecutado (instrucción explícita de la especificación).
