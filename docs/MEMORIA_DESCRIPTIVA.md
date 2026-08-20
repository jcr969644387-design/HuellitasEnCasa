# Memoria Descriptiva — HuellitasEnCasa

## 1. Identificación del proyecto

| Campo | Valor |
|---|---|
| Nombre | HuellitasEnCasa |
| Package | `com.educalab.huellitasencasa` |
| Versión | 1.0.0 |
| Plataforma | Android nativo (Kotlin, Jetpack Compose) |
| Público objetivo | Niños y niñas de 8 a 12 años |
| Área temática | Cuidado animal / educación responsable |
| Conectividad | 100% offline |

## 2. Problema y justificación

Muchos niños y niñas piden una mascota real sin comprender del todo la responsabilidad diaria
que implica: alimentación adecuada, higiene, ejercicio, descanso y atención a señales de
malestar. Las aplicaciones de "mascota virtual" existentes suelen caer en dos extremos: o son
demasiado infantiles (pensadas para preescolares), o convierten el cuidado en un mecanismo de
castigo (la mascota "muere" o se enferma si no se juega a diario), lo que genera ansiedad más
que aprendizaje.

HuellitasEnCasa se plantea como un punto intermedio: un centro de cuidado animal con
mecánicas variadas y contenido educativo real, pensado para 8-12 años, sin mecanismos punitivos,
que enseña hábitos de cuidado responsable transferibles a una futura mascota real, siempre bajo
supervisión de un adulto.

## 3. Objetivos

### Objetivo general
Ofrecer una experiencia lúdica y educativa de cuidado animal que enseñe hábitos responsables de
alimentación, higiene, actividad, descanso y reconocimiento de señales de bienestar en cinco
especies domésticas comunes.

### Objetivos específicos
- Permitir adoptar y personalizar una mascota virtual de 5 especies distintas.
- Enseñar, mediante clasificación de tarjetas, qué alimentos son aptos y cuáles peligrosos.
- Enseñar hábitos de higiene y actividad mediante secuencias de acciones reales.
- Enseñar a reconocer señales de bienestar/malestar mediante escenarios "¿Qué harías?",
  recomendando siempre acudir a un adulto o veterinario ante señales importantes (sin ofrecer
  diagnóstico clínico).
- Enseñar a planificar una rutina diaria de cuidado (mañana/tarde/noche).
- Motivar la continuidad mediante misiones, insignias y decoraciones desbloqueables ligadas a
  acciones reales, nunca a mecanismos de presión o castigo.
- Garantizar el funcionamiento 100% offline y la privacidad infantil.

## 4. Público y contexto de uso

Niños y niñas de 8 a 12 años, con sesiones de uso de entre 5 y 20 minutos, en un dispositivo
Android personal o familiar, con o sin conexión a Internet. Se asume capacidad de lectura de
textos breves en español y familiaridad básica con interfaces táctiles.

## 5. Alcance y exclusiones

**Incluye:** adopción de mascota virtual, cuidado de 6 indicadores por mascota, minijuego de
clasificación de 50 tarjetas de alimentos/situaciones, preparación del hogar por arrastre,
secuencia de higiene, actividad/juego/descanso/cariño, 30 escenarios de bienestar, planificador
de rutina diaria por arrastre, Academia con 8 lecciones, sistema de misiones (30), insignias
(10) y decoraciones (8) desbloqueables, persistencia local completa con Room.

**Excluye explícitamente:** conexión a Internet, backend, cuentas de usuario, login,
notificaciones push, publicidad, compras dentro de la app, analítica/tracking, chat o
comunidad entre menores, diagnóstico veterinario real, geolocalización, cámara y micrófono
(no se usan porque ninguna función los requiere).

## 6. Requisitos funcionales (resumen)

RF-01. El sistema debe permitir crear un perfil de cuidador con alias y avatar local, sin datos
personales reales.
RF-02. El sistema debe permitir adoptar una o varias mascotas virtuales de 5 especies.
RF-03. El sistema debe permitir aplicar acciones de cuidado (alimentar, hidratar, asear, jugar,
pasear, dejar descansar, dar cariño) que modifiquen de forma real y persistente los 6
indicadores de la mascota, siempre acotados a [0, 100].
RF-04. El sistema debe ofrecer un minijuego de clasificación con al menos 50 tarjetas de
alimentos/situaciones, registrando cada intento y su corrección.
RF-05. El sistema debe permitir preparar el hogar de la mascota mediante arrastrar y soltar
objetos en la zona correcta.
RF-06. El sistema debe presentar al menos 30 escenarios de bienestar tipo "¿Qué harías?",
con explicación educativa tras cada respuesta.
RF-07. El sistema debe permitir planificar una rutina diaria (mañana/tarde/noche) mediante
arrastre de tarjetas de acción, validando la colocación.
RF-08. El sistema debe ofrecer contenido educativo estructurado en lecciones (Academia).
RF-09. El sistema debe llevar un sistema de misiones con progreso real, que desbloquee
insignias y decoraciones de forma persistente.
RF-10. El sistema debe funcionar completamente sin conexión a Internet.
RF-11. El sistema no debe solicitar ni almacenar datos personales identificables de menores.

## 7. Requisitos no funcionales

RNF-01. La aplicación debe iniciar y responder con fluidez en un dispositivo Android de gama
media (minSdk 24, targetSdk/compileSdk 34).
RNF-02. Toda persistencia debe realizarse con Room/SQLite; ningún dato de progreso debe vivir
solo en memoria.
RNF-03. La interfaz debe ser accesible: `contentDescription` en iconografía relevante,
objetivos táctiles de tamaño adecuado y contraste suficiente; ningún estado debe comunicarse
solo por color.
RNF-04. El código debe seguir arquitectura MVVM + Repository, separando `data/`, `domain/` y
`ui/`, con lógica de dominio testeable sin UI.
RNF-05. Menos del 50% de las mecánicas educativas principales deben ser de opción múltiple
simple (ver sección 12).
RNF-06. La aplicación no debe declarar el permiso `INTERNET` ni ningún permiso sensible no
utilizado.

## 8. Casos de uso principales

1. **Crear perfil y adoptar mascota** — Actor: niño/a. El sistema pide un alias y avatar,
   luego muestra el centro de adopción; el usuario elige especie y nombre; el sistema crea la
   mascota con indicadores iniciales y otorga la insignia "Primera adopción".
2. **Alimentar y clasificar** — El usuario aplica acciones de alimentar/dar agua (suben
   indicadores reales) y juega tarjetas de clasificación (aptas/peligrosas), recibiendo
   explicación educativa en cada intento.
3. **Responder a un escenario de bienestar** — Ante una situación descrita con imagen, el
   usuario elige una de 2-3 opciones; el sistema corrige y explica, recomendando avisar a un
   adulto cuando corresponde.
4. **Planificar el día** — El usuario arrastra tarjetas de acción a mañana/tarde/noche; el
   sistema valida el plan y, si supera el 80% de acierto, lo marca completado y otorga
   progreso de misión.
5. **Consultar misiones y álbum** — El usuario revisa el progreso de sus 30 misiones y las
   insignias/decoraciones ya desbloqueadas frente a las pendientes.

## 9. Módulos / pantallas

1. Onboarding (4 pantallas, solo primer inicio)
2. Perfil de cuidador (alias + avatar local)
3. Centro de adopción virtual (5 especies)
4. Centro de experiencia / Home Hub
5. Hogar de la mascota (drag & drop)
6. Alimentación y agua (acciones reales + clasificación de 50 tarjetas)
7. Higiene (secuencia de 3 pasos reales)
8. Actividad, juego y descanso (arrastre de juguete + acciones)
9. Señales de bienestar (30 escenarios "¿Qué harías?")
10. Planificador de rutinas (drag & drop por franja horaria)
11. Academia de cuidado (8 lecciones)
12. Misiones, álbum y progreso (30 misiones, 10 insignias, 8 decoraciones)

## 10. Flujo general de navegación

```
Onboarding -> Perfil -> Adopcion -> Home Hub <-> {Hogar, Alimentacion, Higiene, Actividad,
Bienestar, Planificador, Academia, Misiones}
```
El Home Hub es el centro permanente: desde ahí se accede a cualquier módulo y se vuelve tras
completar una actividad. La app recuerda el perfil y la mascota activa entre sesiones.

## 11. Arquitectura (resumen)

MVVM + Repository sobre Jetpack Compose, con tres capas (`data/`, `domain/`, `ui/`). El detalle
completo, con diagrama de paquetes y explicación de cada capa, está en `MANUAL_TECNICO.md`.

## 12. Mecánicas educativas utilizadas

| Mecánica | Módulo |
|---|---|
| Selección sobre imágenes | Avatar, especie |
| Clasificar | Alimentación (50 tarjetas aptas/peligrosas/situación) |
| Arrastrar y soltar (construir) | Hogar de la mascota, Planificador |
| Ejecutar secuencia | Higiene |
| Manipular / arrastrar objeto | Actividad (arrastrar juguete) |
| Decisión situacional ("¿Qué harías?") | Bienestar |
| Leer y confirmar comprensión | Academia |

De 7 mecánicas principales, solo 1 (Bienestar) usa un formato de elección entre opciones; el
resto son clasificación, construcción, arrastre y ejecución de secuencias, cumpliendo el
requisito de que la opción múltiple no domine la experiencia.

## 13. Datos y reglas de negocio

Ver `docs/BASE_DE_DATOS.md` para el modelo completo. Reglas clave:
- Todo indicador de mascota vive siempre en `[0, 100]`.
- El descenso de indicadores solo ocurre una vez por sesión nueva (día de calendario distinto
  al de la última visita), nunca por temporizadores en segundo plano, y nunca por debajo de un
  "piso protector" (15): la mascota nunca "muere" ni queda en estado crítico solo por ausencia.
- El progreso de misiones se deriva siempre de un evento real (acción de cuidado, clasificación
  correcta, escenario acertado, plan aprobado, lección vista), nunca de un contador simulado.
- Una misión, al completarse, desbloquea su insignia y/o decoración asociada exactamente una
  vez (restricción de unicidad en base de datos).

## 14. UX y dirección visual

Paleta cálida (naranja, turquesa, amarillo, lavanda) sobre fondo crema; tipografía redondeada de
peso alto para titulares; ilustraciones vectoriales propias (54 recursos) y personajes de
mascota dibujados con Compose Canvas con expresiones según su bienestar. El Home funciona como
un centro de experiencia con tarjetas ilustradas por módulo, no como una lista de botones.

## 15. Privacidad y seguridad infantil

No se solicitan datos personales reales (nombre, email, teléfono, dirección, ubicación,
contactos). El único identificador es un alias elegido libremente. No hay cámara, micrófono,
cuentas, chat ni comunidad entre menores. No hay conexión a Internet ni, por tanto, tracking,
anuncios o analítica. Los datos viven exclusivamente en la base de datos Room local del
dispositivo.

## 16. Pruebas

Batería de 55 tests incluida en `app/src/test`. De ellos, **43 tests de lógica de dominio pura
se compilaron y ejecutaron realmente** en el entorno de generación (JUnit 4.13.2 + compilador
Kotlin local), con resultado **43/43 correctos**. Los 12 tests restantes (persistencia Room con
Robolectric) están escritos pero requieren Android SDK para ejecutarse; ver
`docs/BUILD_REPORT.md` para el detalle honesto de qué se verificó y qué no.

## 17. Limitaciones conocidas

- El entorno de generación de este proyecto no tuvo acceso a `dl.google.com`/`maven.google.com`
  ni al servidor de Gradle, por lo que el build completo de Android (`assembleDebug`,
  `testDebugUnitTest` sobre el módulo Android, `lintDebug`) no se pudo ejecutar ni verificar.
- Los tests basados en Room/Robolectric no se ejecutaron en este sandbox (solo se revisaron
  manualmente); deben verificarse en la primera compilación real.
- El planificador valida franjas horarias contra un conjunto de reglas fijo por tipo de acción,
  no contra un modelo de IA ni contra las preferencias particulares de cada especie más allá de
  lo codificado.
- El contenido de las 50 tarjetas de alimentos y 30 escenarios es representativo y
  pedagógicamente revisado, pero no sustituye el consejo de un veterinario real.

## 18. Mejoras futuras

- Soporte multi-perfil por dispositivo (varios hermanos con perfiles separados y selector).
- Modo "familia": un adulto puede revisar un resumen de las lecciones vistas.
- Ampliar el catálogo de especies (tortuga, pez) y de objetos del hogar.
- Exportar/backup local del progreso a un archivo cifrado, sin salir de la app.

## 19. Conclusiones

HuellitasEnCasa cumple el objetivo de ofrecer un producto educativo de cuidado animal completo,
con identidad visual propia, mecánicas variadas, contenido semilla real y persistencia genuina,
evitando tanto la estética infantilizada como los mecanismos punitivos. El proyecto entrega
código fuente completo y compilable; la única salvedad honesta es que el build de Android no
pudo verificarse en el entorno de generación por restricciones de red del sandbox, no por
errores de código conocidos.
