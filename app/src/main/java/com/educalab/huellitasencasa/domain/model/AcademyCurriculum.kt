package com.educalab.huellitasencasa.domain.model

/**
 * Contenido curricular fijo de la Academia de cuidado. No es progreso de usuario
 * (eso vive en AcademyLessonProgressEntity/Room); es la definición de las 8 lecciones.
 */
data class AcademyLesson(
    val code: String,
    val title: String,
    val summary: String,
    val bodyParagraphs: List<String>,
    val iconRes: String
)

object AcademyCurriculum {
    val lessons = listOf(
        AcademyLesson(
            code = "L1_ALIMENTACION",
            title = "La base de una buena alimentación",
            summary = "Qué necesita cada especie para comer bien y qué alimentos evitar siempre.",
            bodyParagraphs = listOf(
                "Cada especie tiene una dieta distinta: el heno es la base del conejo, mientras que el perro necesita una mezcla balanceada de proteína y cereales.",
                "Algunos alimentos humanos, como el chocolate, la cebolla o las uvas, son peligrosos para muchas mascotas aunque a nosotros nos parezcan inofensivos.",
                "Ofrecer siempre agua fresca y limpia es tan importante como la comida sólida."
            ),
            iconRes = "ic_mod_alimentacion"
        ),
        AcademyLesson(
            code = "L2_HIGIENE",
            title = "Higiene sin estrés",
            summary = "Cómo mantener limpio a tu compañero sin agobiarlo.",
            bodyParagraphs = listOf(
                "El cepillado regular no solo mantiene el pelaje limpio: también permite detectar heridas, parásitos o bultos a tiempo.",
                "Cada especie se asea de forma distinta: los gatos se limpian solos, mientras que los hámsters usan baños de arena en vez de agua.",
                "Un espacio limpio (jaula, cama, arenero) evita malos olores y previene enfermedades."
            ),
            iconRes = "ic_mod_higiene"
        ),
        AcademyLesson(
            code = "L3_ACTIVIDAD",
            title = "Jugar también es cuidar",
            summary = "Por qué el ejercicio y el juego son parte esencial del bienestar.",
            bodyParagraphs = listOf(
                "El ejercicio físico ayuda a mantener un peso saludable y libera energía acumulada.",
                "El juego mental (buscar comida escondida, resolver un juguete) es tan importante como el ejercicio físico.",
                "Cada especie necesita un tipo de actividad distinto: paseos para el perro, vuelo supervisado para el ave, rueda para el hámster."
            ),
            iconRes = "ic_mod_actividad"
        ),
        AcademyLesson(
            code = "L4_DESCANSO",
            title = "El valor del descanso",
            summary = "Cómo respetar el ritmo de sueño de cada mascota.",
            bodyParagraphs = listOf(
                "Los hámsters son nocturnos: dormir de día es completamente normal para ellos, no una señal de enfermedad.",
                "Un lugar tranquilo, alejado del ruido, ayuda a que la mascota descanse mejor.",
                "Interrumpir el descanso constantemente puede generar estrés, igual que nos pasaría a nosotros."
            ),
            iconRes = "ic_mod_planificador"
        ),
        AcademyLesson(
            code = "L5_SENALES",
            title = "Aprender a leer las señales",
            summary = "Cómo reconocer si tu mascota está contenta, asustada o incómoda.",
            bodyParagraphs = listOf(
                "El lenguaje corporal cuenta mucho: una cola que se mueve, unas orejas hacia atrás o un cuerpo encogido dicen cómo se siente la mascota.",
                "Rascarse mucho, esconderse más de lo habitual o dejar de comer son señales que merecen atención.",
                "Ante cualquier duda importante, lo correcto es avisar a un adulto o a un veterinario; nosotros no diagnosticamos, solo observamos y pedimos ayuda."
            ),
            iconRes = "ic_mod_bienestar"
        ),
        AcademyLesson(
            code = "L6_HOGAR",
            title = "Preparar un buen hogar",
            summary = "Qué elementos no pueden faltar en el espacio de tu mascota.",
            bodyParagraphs = listOf(
                "Cada mascota necesita comida, agua, un lugar para descansar y un espacio seguro para moverse.",
                "Los objetos decorativos también importan: deben ser seguros y adecuados para la especie.",
                "Revisar el hogar regularmente evita accidentes y mantiene todo en buen estado."
            ),
            iconRes = "ic_mod_hogar"
        ),
        AcademyLesson(
            code = "L7_RUTINAS",
            title = "Rutinas que dan seguridad",
            summary = "Por qué planificar el día ayuda a tu mascota a sentirse segura.",
            bodyParagraphs = listOf(
                "Las mascotas se sienten más tranquilas cuando la comida, el juego y el descanso ocurren en momentos parecidos cada día.",
                "Planificar mañana, tarde y noche ayuda a no olvidar ninguna necesidad importante.",
                "Una rutina no tiene que ser rígida: lo importante es cubrir todas las necesidades a lo largo del día."
            ),
            iconRes = "ic_mod_planificador"
        ),
        AcademyLesson(
            code = "L8_RESPONSABILIDAD",
            title = "Cuidar es una responsabilidad compartida",
            summary = "Por qué siempre debe haber un adulto involucrado en el cuidado real.",
            bodyParagraphs = listOf(
                "Cuidar una mascota real es una tarea compartida entre el niño o niña y las personas adultas de la casa.",
                "Ante cualquier señal de enfermedad o peligro, lo correcto es avisar rápido a un adulto o acudir al veterinario.",
                "Practicar en HuellitasEnCasa ayuda a aprender hábitos que luego se aplican, siempre con supervisión, en la vida real."
            ),
            iconRes = "ic_mod_academia"
        )
    )
}
