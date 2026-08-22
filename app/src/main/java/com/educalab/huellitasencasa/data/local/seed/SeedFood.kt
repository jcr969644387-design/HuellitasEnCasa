package com.educalab.huellitasencasa.data.local.seed

import com.educalab.huellitasencasa.data.local.entity.FoodItemEntity

/**
 * 50 tarjetas para el minijuego "Clasifica y Cuida": alimentos aptos, alimentos peligrosos
 * y situaciones de cuidado correctas/incorrectas. Contenido educativo real, no relleno.
 */
object SeedFood {

    private data class Food(
        val name: String,
        val category: String,
        val iconRes: String,
        val isAppropriate: Boolean,
        val explanation: String,
        val speciesCode: String?
    )

    private val raw = listOf(
    Food("Croquetas para perro", "ALIMENTO_BUENO", "food_pienso", true, "El pienso balanceado aporta todos los nutrientes que un perro adulto necesita.", "PERRO"),
    Food("Zanahoria en trozos", "ALIMENTO_BUENO", "food_fruta_verdura", true, "La zanahoria es un snack crujiente, bajo en calorías y rico en fibra para perros y conejos.", null),
    Food("Pollo cocido sin hueso", "ALIMENTO_BUENO", "food_carne_pescado", true, "El pollo cocido, sin piel ni huesos, es una fuente segura de proteína para perros y gatos.", null),
    Food("Croquetas para gato", "ALIMENTO_BUENO", "food_pienso", true, "El pienso específico para gatos contiene taurina, un nutriente esencial que ellos necesitan.", "GATO"),
    Food("Atún en agua (poca cantidad)", "ALIMENTO_BUENO", "food_carne_pescado", true, "En cantidades pequeñas y ocasionales, el atún natural puede ser un premio para gatos.", "GATO"),
    Food("Heno fresco", "ALIMENTO_BUENO", "food_fruta_verdura", true, "El heno debe estar siempre disponible: ayuda a la digestión y desgasta los dientes del conejo.", "CONEJO"),
    Food("Hojas de lechuga romana", "ALIMENTO_BUENO", "food_fruta_verdura", true, "En pequeñas cantidades, la lechuga romana es segura y refrescante para el conejo.", "CONEJO"),
    Food("Mezcla de semillas para hámster", "ALIMENTO_BUENO", "food_pienso", true, "Una mezcla equilibrada de semillas es la base de la dieta de un hámster.", "HAMSTER"),
    Food("Trocito de manzana sin semillas", "ALIMENTO_BUENO", "food_fruta_verdura", true, "La manzana sin semillas es un premio ocasional seguro para hámsters y aves.", null),
    Food("Mezcla de semillas para ave", "ALIMENTO_BUENO", "food_pienso", true, "La mezcla de semillas específica cubre las necesidades energéticas del ave.", "AVE"),
    Food("Brócoli al vapor en trocitos", "ALIMENTO_BUENO", "food_fruta_verdura", true, "El brócoli cocido y en trozos pequeños aporta vitaminas sin dañar al ave.", "AVE"),
    Food("Agua fresca y limpia", "ALIMENTO_BUENO", "food_agua_fresca", true, "El agua limpia y renovada a diario es esencial para todas las especies.", null),
    Food("Pepino en rodajas", "ALIMENTO_BUENO", "food_fruta_verdura", true, "El pepino hidrata y es un snack ligero apto para conejos y cobayas.", "CONEJO"),
    Food("Calabacín cocido", "ALIMENTO_BUENO", "food_fruta_verdura", true, "El calabacín cocido en trozos pequeños es suave para el estómago del perro.", "PERRO"),
    Food("Arándanos frescos", "ALIMENTO_BUENO", "food_fruta_verdura", true, "Los arándanos son un premio saludable y antioxidante para perros y aves.", null),
    Food("Sardina cocida sin sal", "ALIMENTO_BUENO", "food_carne_pescado", true, "El pescado azul sin sal aporta ácidos grasos beneficiosos para el gato.", "GATO"),
    Food("Pipas de girasol (con moderación)", "ALIMENTO_BUENO", "food_pienso", true, "En pequeñas cantidades, las pipas sin sal son un premio energético para el hámster.", "HAMSTER"),
    Food("Espinaca cruda en poca cantidad", "ALIMENTO_BUENO", "food_fruta_verdura", true, "Ofrecida con moderación, la espinaca aporta hierro al conejo.", "CONEJO"),
    Food("Huevo cocido (poca cantidad)", "ALIMENTO_BUENO", "food_carne_pescado", true, "El huevo bien cocido, sin sal, es una fuente ocasional de proteína para el perro.", "PERRO"),
    Food("Fresas en trozos pequeños", "ALIMENTO_BUENO", "food_fruta_verdura", true, "Las fresas son un premio dulce y natural, seguro con moderación.", null),
    Food("Chocolate", "ALIMENTO_MALO", "food_dulce_prohibido", false, "El chocolate contiene teobromina, una sustancia tóxica y peligrosa para perros y gatos.", null),
    Food("Uvas y pasas", "ALIMENTO_MALO", "food_fruta_verdura", false, "Las uvas y pasas pueden dañar gravemente los riñones de un perro, aunque parezcan inofensivas.", null),
    Food("Cebolla y ajo", "ALIMENTO_MALO", "food_planta_toxica", false, "La cebolla y el ajo dañan los glóbulos rojos de perros y gatos, incluso cocinados.", null),
    Food("Huesos de pollo cocidos", "ALIMENTO_MALO", "food_hueso_prohibido", false, "Los huesos cocidos se astillan fácilmente y pueden herir el interior del perro.", null),
    Food("Leche de vaca", "ALIMENTO_MALO", "food_lacteo", false, "Muchos gatos y perros adultos no digieren bien la lactosa; puede causarles malestar.", null),
    Food("Aguacate", "ALIMENTO_MALO", "food_planta_toxica", false, "El aguacate contiene persina, una sustancia tóxica sobre todo para aves.", "AVE"),
    Food("Caramelos y golosinas", "ALIMENTO_MALO", "food_dulce_prohibido", false, "El azúcar y los edulcorantes artificiales pueden ser tóxicos y dañar los dientes.", null),
    Food("Café o bebidas con cafeína", "ALIMENTO_MALO", "food_dulce_prohibido", false, "La cafeína acelera peligrosamente el corazón de perros y gatos, incluso en pequeñas dosis.", null),
    Food("Lechuga iceberg en exceso", "ALIMENTO_MALO", "food_fruta_verdura", false, "En exceso aporta poca fibra y puede causar diarrea en el conejo.", "CONEJO"),
    Food("Comida frita o con mucha sal", "ALIMENTO_MALO", "food_dulce_prohibido", false, "La comida grasa o salada puede dañar el estómago y el corazón de la mascota.", null),
    Food("Ruibarbo y sus hojas", "ALIMENTO_MALO", "food_planta_toxica", false, "Las hojas de ruibarbo son tóxicas para conejos y otros pequeños mamíferos.", "CONEJO"),
    Food("Semillas de manzana", "ALIMENTO_MALO", "food_planta_toxica", false, "Las semillas de manzana contienen una sustancia tóxica; solo la pulpa es segura.", null),
    Food("Cítricos (naranja, limón)", "ALIMENTO_MALO", "food_fruta_verdura", false, "Los cítricos pueden irritar el estómago del hámster y de las aves.", "HAMSTER"),
    Food("Xilitol (chicles sin azúcar)", "ALIMENTO_MALO", "food_dulce_prohibido", false, "El xilitol es un edulcorante muy tóxico incluso en cantidades mínimas para el perro.", "PERRO"),
    Food("Perejil en grandes cantidades", "ALIMENTO_MALO", "food_fruta_verdura", false, "En grandes cantidades puede afectar los riñones del conejo; solo en pequeñas porciones.", "CONEJO"),
    Food("Dejar agua estancada varios días", "SITUACION", "food_agua_fresca", false, "El agua debe cambiarse todos los días; el agua estancada acumula bacterias.", null),
    Food("Cambiar el agua todos los días", "SITUACION", "food_agua_fresca", true, "Renovar el agua a diario mantiene a la mascota hidratada y sana.", null),
    Food("Bañar al hámster con agua", "SITUACION", "food_agua_fresca", false, "Los hámsters se asean solos; bañarlos con agua puede enfermarlos por el frío.", "HAMSTER"),
    Food("Ofrecer un baño de arena al hámster", "SITUACION", "food_pienso", true, "Un baño de arena especial permite al hámster limpiarse de forma natural y segura.", "HAMSTER"),
    Food("Dejar al perro sin paseo varios días", "SITUACION", "food_hueso_prohibido", false, "El perro necesita salir a pasear a diario para su salud física y mental.", "PERRO"),
    Food("Sacar a pasear al perro cada día", "SITUACION", "food_hueso_prohibido", true, "El paseo diario cubre su necesidad de actividad y de explorar el entorno.", "PERRO"),
    Food("Limpiar la jaula solo una vez al mes", "SITUACION", "food_pienso", false, "La jaula debe limpiarse con frecuencia para evitar malos olores y enfermedades.", null),
    Food("Limpiar la jaula o el espacio cada semana", "SITUACION", "food_pienso", true, "Una limpieza regular mantiene el espacio de la mascota sano y cómodo.", null),
    Food("Dejar comida sin terminar varios días en el plato", "SITUACION", "food_pienso", false, "La comida que se echa a perder debe retirarse para que no enferme a la mascota.", null),
    Food("Ofrecer heno fresco cada día al conejo", "SITUACION", "food_fruta_verdura", true, "Renovar el heno a diario asegura que el conejo siempre tenga fibra fresca.", "CONEJO"),
    Food("Dejar al ave sin salir nunca de la jaula", "SITUACION", "food_pienso", false, "El ave necesita tiempo fuera de la jaula, en un espacio seguro, para volar y estirar las alas.", "AVE"),
    Food("Dejar salir al ave a un espacio seguro y supervisado", "SITUACION", "food_pienso", true, "El tiempo de vuelo supervisado es clave para el bienestar físico del ave.", "AVE"),
    Food("Cepillar al perro de pelo largo cada semana", "SITUACION", "food_pienso", true, "El cepillado regular evita nudos y ayuda a detectar heridas o parásitos a tiempo.", "PERRO"),
    Food("No cambiar nunca el sustrato del hámster", "SITUACION", "food_pienso", false, "Un sustrato sucio genera humedad y malos olores que afectan la salud del hámster.", "HAMSTER"),
    Food("Chocolate con leche como premio", "ALIMENTO_MALO", "food_dulce_prohibido", false, "Ningún tipo de chocolate es seguro para mascotas, ni siquiera en pequeñas cantidades.", null),
    Food("Croquetas específicas para conejo", "ALIMENTO_BUENO", "food_pienso", true, "El pienso para conejo aporta la fibra y los nutrientes que necesita a diario.", "CONEJO"),
    Food("Brócoli en trocitos pequeños", "ALIMENTO_BUENO", "food_fruta_verdura", true, "En pequeñas cantidades, el brócoli aporta vitaminas al conejo y al hámster.", null),
    Food("Mijo en espiga", "ALIMENTO_BUENO", "food_pienso", true, "El mijo en espiga es un premio natural que las aves disfrutan picotear.", "AVE"),
    Food("Cáscara de huevo triturada", "ALIMENTO_BUENO", "food_carne_pescado", true, "Aporta calcio extra al ave, útil sobre todo para hembras ponedoras.", "AVE"),
    Food("Piña sin cáscara en trozos", "ALIMENTO_BUENO", "food_fruta_verdura", true, "La piña sin cáscara es un premio jugoso y seguro con moderación.", null),
    Food("Chocolate blanco", "ALIMENTO_MALO", "food_dulce_prohibido", false, "Aunque tiene menos teobromina, sigue siendo un dulce que no aporta nada bueno y puede engordar en exceso.", null),
    Food("Huesos crudos grandes sin supervisión", "ALIMENTO_MALO", "food_hueso_prohibido", false, "Incluso crudos, los huesos grandes pueden romper piezas dentales o causar atragantamiento.", "PERRO"),
    Food("Restos de comida muy condimentada", "ALIMENTO_MALO", "food_dulce_prohibido", false, "Las especias y condimentos irritan el estómago de la mayoría de las mascotas.", null),
    Food("Pesar a la mascota una vez al mes", "SITUACION", "food_pienso", true, "Controlar el peso con regularidad ayuda a detectar cambios de salud a tiempo.", null),
    Food("Dejar plantas tóxicas al alcance de la mascota", "SITUACION", "food_planta_toxica", false, "Algunas plantas comunes de interior son tóxicas; deben quedar fuera de su alcance.", null)
    )

    /** Construye las entidades resolviendo el código de especie a su ID real ya insertado. */
    fun buildEntities(speciesIdByCode: Map<String, Long>): List<FoodItemEntity> =
        raw.map { f ->
            FoodItemEntity(
                speciesId = f.speciesCode?.let { speciesIdByCode[it] },
                name = f.name,
                category = f.category,
                iconRes = f.iconRes,
                isAppropriate = f.isAppropriate,
                explanation = f.explanation
            )
        }

    val size: Int get() = raw.size
}
