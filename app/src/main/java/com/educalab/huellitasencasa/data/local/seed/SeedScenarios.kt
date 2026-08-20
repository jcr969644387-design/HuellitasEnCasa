package com.educalab.huellitasencasa.data.local.seed

import com.educalab.huellitasencasa.data.local.entity.WellbeingScenarioEntity

/**
 * 30 escenarios "¿Qué harías?" (6 por especie) sobre señales de bienestar animal.
 * Cuando corresponde, la explicación recomienda avisar a un adulto o veterinario,
 * sin ofrecer ningún diagnóstico clínico: solo orientación de cuándo pedir ayuda.
 */
object SeedScenarios {

    private data class Scenario(
        val speciesCode: String,
        val situationText: String,
        val optionsCsv: String,
        val correctIndex: Int,
        val explanation: String,
        val recommendAskAdult: Boolean
    )

    private val raw = listOf(
    Scenario("PERRO", "Tu perro se rasca mucho la oreja y la sacude todo el tiempo.", "Ignorarlo, seguro se le pasa solo|Revisar con cuidado y avisar a un adulto o al veterinario|Ponerle un perfume para que huela mejor", 1, "Rascarse mucho la oreja puede indicar una infección o un objeto molestándole; conviene que un adulto lo revise.", true),
    Scenario("PERRO", "Después de pasear, tu perro jadea mucho y busca la sombra.", "Dejarlo descansar y ofrecerle agua fresca|Obligarlo a seguir jugando|Darle chocolate para animarlo", 0, "Jadear y buscar sombra es normal tras el ejercicio: necesita descanso y agua fresca.", false),
    Scenario("PERRO", "Tu perro no ha tocado su comida en todo el día y está decaído.", "Esperar una semana a ver qué pasa|Registrar la situación y comentarlo con un adulto o veterinario|Cambiarle la comida por chocolate", 1, "No comer durante un día entero junto con decaimiento merece atención de un adulto o veterinario.", true),
    Scenario("PERRO", "Ves que tu perro mueve la cola y trae su juguete favorito.", "Jugar un rato con él|Ignorarlo por completo|Regañarlo sin motivo", 0, "Traer el juguete y mover la cola son señales de que quiere jugar; responder fortalece el vínculo.", false),
    Scenario("PERRO", "Hace mucho calor y vas a dejar a tu perro en el jardín todo el día.", "Dejarlo sin sombra ni agua|Asegurarte de que tenga sombra y agua fresca disponible|Ponerle un abrigo", 1, "En días de calor, la sombra y el agua fresca son esenciales para evitar un golpe de calor.", false),
    Scenario("PERRO", "Tu perro tiembla, se esconde y no quiere salir durante una tormenta.", "Forzarlo a salir igualmente|Dejarle un lugar tranquilo y acompañarlo con calma|Dejarlo solo y cerrar la puerta", 1, "El miedo a los truenos es común; ofrecer calma y un refugio tranquilo ayuda a que se sienta seguro.", false),
    Scenario("GATO", "Tu gato se esconde debajo de la cama y no quiere salir en todo el día.", "Sacarlo a la fuerza|Dejarle espacio y observar si vuelve a comer y jugar con normalidad|Ignorarlo una semana entera", 1, "Esconderse un rato es normal en gatos, pero si persiste conviene observarlo y, si sigue, avisar a un adulto.", false),
    Scenario("GATO", "Notas que tu gato vomita una bola de pelo de vez en cuando.", "Es normal ocasionalmente; puedes cepillarlo más seguido|Alarmarse y dejar de alimentarlo|Bañarlo inmediatamente", 0, "Las bolas de pelo ocasionales son comunes; cepillarlo con más frecuencia reduce su formación.", false),
    Scenario("GATO", "Tu gato araña el sofá en vez del rascador.", "Regañarlo fuerte y encerrarlo|Colocar el rascador cerca del sofá y premiarlo cuando lo use|Quitarle todas las uñas", 1, "Redirigir el comportamiento hacia el rascador con paciencia y premios funciona mejor que el castigo.", false),
    Scenario("GATO", "Tu gato maúlla mucho y no se acerca al plato de agua.", "Cambiar el agua de lugar, lejos de la comida, y ofrecer agua fresca|No hacer nada|Darle solo leche de vaca", 0, "Muchos gatos prefieren el agua lejos de la comida; renovarla puede animarlo a beber más.", false),
    Scenario("GATO", "Tu gato deja de usar la caja de arena y hace sus necesidades fuera.", "Castigarlo|Revisar que la arena esté limpia y, si continúa, avisar a un adulto|Cambiarlo de casa", 1, "Este cambio de conducta puede indicar estrés o un problema de salud; conviene revisarlo con un adulto.", true),
    Scenario("GATO", "Ves que tu gato duerme casi todo el día.", "Preocuparte, es raro que duerma tanto|Es normal: los gatos duermen muchas horas al día|Despertarlo cada hora", 1, "Dormir gran parte del día es completamente normal en la conducta felina.", false),
    Scenario("CONEJO", "Notas que tu conejo no ha comido heno en todo el día.", "Esperar sin hacer nada|Avisar a un adulto: en conejos, dejar de comer es una señal urgente|Darle solo golosinas dulces", 1, "En conejos, dejar de comer incluso pocas horas puede ser grave y requiere atención rápida de un adulto.", true),
    Scenario("CONEJO", "Tu conejo golpea el suelo con la pata trasera de repente.", "Ignorarlo|Revisar el entorno: puede estar advirtiendo de algo que le asusta|Regañarlo", 1, "Golpear el suelo es una señal de alerta o alarma; conviene revisar si algo lo está asustando.", false),
    Scenario("CONEJO", "Ves que tu conejo se estira relajado y mordisquea despacio el heno.", "Dejarlo tranquilo, está cómodo y relajado|Molestarlo para que se mueva|Sacarlo de su espacio a la fuerza", 0, "Un conejo relajado que come con calma está mostrando bienestar; lo mejor es no interrumpirlo.", false),
    Scenario("CONEJO", "Su jaula lleva dos semanas sin limpiarse y huele fuerte.", "Dejarla así, no pasa nada|Limpiarla esta misma semana con ayuda de un adulto|Rociar perfume para tapar el olor", 1, "Un espacio limpio y seco es esencial para evitar enfermedades respiratorias y de piel.", false),
    Scenario("CONEJO", "Tu conejo se deja acariciar la cabeza pero se aparta si tocas su lomo.", "Respetar su límite y acariciar solo donde está cómodo|Forzarlo a que se deje tocar en todas partes|Dejar de acariciarlo para siempre", 0, "Respetar las zonas donde el conejo se siente cómodo construye confianza poco a poco.", false),
    Scenario("CONEJO", "Hace mucho calor y el conejo está tumbado sin moverse, respirando rápido.", "Dejarlo al sol para que se caliente más|Llevarlo a la sombra, ofrecer agua fresca y avisar a un adulto|No hacer nada", 1, "Los conejos son muy sensibles al calor; respirar rápido y estar inmóvil puede indicar un golpe de calor.", true),
    Scenario("HAMSTER", "Tu hámster está muy quieto durante el día y no se mueve.", "Despertarlo para jugar|Dejarlo dormir: es un animal nocturno y de día suele descansar|Sacarlo de la jaula y sacudirlo", 1, "Los hámsters son nocturnos; estar quietos de día es su comportamiento normal, no una señal de alarma.", false),
    Scenario("HAMSTER", "Ves que tu hámster guarda comida en las mejillas y la esconde en un rincón.", "Es un comportamiento normal de acumulación, no hace falta hacer nada especial|Regañarlo por no comer|Quitarle toda la comida guardada", 0, "Guardar comida en los carrillos y acumularla es un comportamiento natural del hámster.", false),
    Scenario("HAMSTER", "La rueda de ejercicio de tu hámster hace un ruido chirriante constante.", "Ignorarlo, no afecta al hámster|Revisarla y limpiarla o engrasarla con ayuda de un adulto|Quitarle la rueda para siempre", 1, "Una rueda que chirría puede indicar que necesita mantenimiento; además el ruido puede estresarlo.", false),
    Scenario("HAMSTER", "Notas que tu hámster ha perdido pelo en una zona y se rasca mucho ahí.", "No darle importancia|Comentarlo con un adulto para que revise si necesita veterinario|Cortarle el pelo restante", 1, "La pérdida de pelo localizada junto con rascado puede indicar un problema de piel a revisar.", true),
    Scenario("HAMSTER", "Vas a manipular a tu hámster por primera vez en el día.", "Agarrarlo rápido y por la cola|Dejar que huela tu mano primero y luego tomarlo con ambas manos, con calma|Levantarlo de golpe mientras duerme", 1, "Acercarse con calma y dejar que reconozca tu mano evita sustos y mordiscos.", false),
    Scenario("HAMSTER", "El bebedero de tu hámster está vacío desde ayer.", "Rellenarlo cuanto antes con agua limpia|Esperar al día siguiente|Darle solo fruta en su lugar", 0, "El agua no debe faltar nunca; hay que rellenarla en cuanto se detecta que está vacía.", false),
    Scenario("AVE", "Tu ave se arranca plumas del pecho repetidamente.", "Ignorarlo, las plumas vuelven a crecer solas siempre|Comentarlo con un adulto: puede ser estrés o aburrimiento|Cubrir la jaula todo el día", 1, "Arrancarse plumas suele indicar estrés, aburrimiento o un problema de salud; conviene consultarlo.", true),
    Scenario("AVE", "Ves que tu ave canta y salta activamente en su percha por la mañana.", "Es una señal de que está contenta y en buen estado|Es preocupante y hay que llevarla al veterinario ya|Hay que hacerla callar", 0, "Cantar y moverse con energía por la mañana es una señal típica de bienestar en muchas aves.", false),
    Scenario("AVE", "Tu ave está erizada, con los ojos entrecerrados y no come.", "Esperar unos días a ver si mejora sola|Avisar a un adulto pronto: puede ser un signo de enfermedad|Darle solo agua con azúcar", 1, "Un ave enferma suele esconder los síntomas; estar erizada y sin comer es señal de alarma que requiere atención pronto.", true),
    Scenario("AVE", "Vas a limpiar el bebedero y el comedero del ave.", "Lavarlos con agua y dejar todo limpio y seco antes de rellenar|Rellenar directamente sin lavar|Usar detergente fuerte sin enjuagar", 0, "Lavar bien los recipientes evita bacterias y mantiene la comida y el agua en buen estado.", false),
    Scenario("AVE", "El ave lleva todo el día en la jaula sin salir a volar.", "Está bien así siempre|Ofrecerle un rato de vuelo supervisado en un espacio seguro|Dejarla encerrada una semana más", 1, "El tiempo de vuelo supervisado es importante para su salud física y mental.", false),
    Scenario("AVE", "Notas que el ave se acerca a los barrotes cuando te ve llegar.", "Ignorarla y alejarte|Saludarla con calma y ofrecerle un momento de atención|Golpear la jaula para jugar", 1, "Acercarse a saludar es una señal de vínculo positivo; responder con calma refuerza esa confianza.", false),    )

    fun buildEntities(speciesIdByCode: Map<String, Long>): List<WellbeingScenarioEntity> =
        raw.map { s ->
            WellbeingScenarioEntity(
                speciesId = speciesIdByCode[s.speciesCode],
                situationText = s.situationText,
                iconRes = "ic_mod_bienestar",
                optionsCsv = s.optionsCsv,
                correctOptionIndex = s.correctIndex,
                explanation = s.explanation,
                recommendAskAdult = s.recommendAskAdult
            )
        }

    val size: Int get() = raw.size
}
