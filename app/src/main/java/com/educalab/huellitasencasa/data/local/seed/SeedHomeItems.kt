package com.educalab.huellitasencasa.data.local.seed

import com.educalab.huellitasencasa.data.local.entity.HomeItemEntity

/** Objetos disponibles para preparar el hogar de la mascota (drag & drop). */
object SeedHomeItems {

    private const val ALL = "PERRO,GATO,CONEJO,HAMSTER,AVE"

    val items = listOf(
        HomeItemEntity(
            category = "CUENCO_COMIDA", name = "Cuenco de comida",
            iconRes = "item_cuenco_comida",
            description = "Debe estar limpio y con la ración adecuada para cada especie.",
            compatibleSpeciesCsv = ALL
        ),
        HomeItemEntity(
            category = "CUENCO_AGUA", name = "Cuenco de agua fresca",
            iconRes = "item_cuenco_agua",
            description = "El agua limpia debe estar siempre disponible, cerca pero no encima de la comida.",
            compatibleSpeciesCsv = ALL
        ),
        HomeItemEntity(
            category = "CAMA", name = "Cama o nido acogedor",
            iconRes = "item_cama",
            description = "Un lugar suave y tranquilo donde la mascota pueda descansar sin ser molestada.",
            compatibleSpeciesCsv = "PERRO,GATO,CONEJO"
        ),
        HomeItemEntity(
            category = "JUGUETE", name = "Pelota de juego",
            iconRes = "item_juguete_pelota",
            description = "Estimula el juego activo y ayuda a gastar energía de forma divertida.",
            compatibleSpeciesCsv = "PERRO,GATO"
        ),
        HomeItemEntity(
            category = "HIGIENE", name = "Cepillo de aseo",
            iconRes = "item_cepillo",
            description = "Ayuda a mantener el pelaje limpio y sin nudos; también fortalece el vínculo.",
            compatibleSpeciesCsv = "PERRO,GATO,CONEJO"
        ),
        HomeItemEntity(
            category = "ENTORNO", name = "Correa para paseo",
            iconRes = "item_correa",
            description = "Necesaria para pasear de forma segura fuera de casa.",
            compatibleSpeciesCsv = "PERRO"
        ),
        HomeItemEntity(
            category = "ENTORNO", name = "Planta segura de interior",
            iconRes = "item_planta_interior",
            description = "Decora el espacio; siempre debe verificarse que no sea tóxica para la mascota.",
            compatibleSpeciesCsv = "GATO,CONEJO,AVE"
        ),
        HomeItemEntity(
            category = "ENTORNO", name = "Ventana con luz natural",
            iconRes = "item_ventana_luz",
            description = "La luz natural regula el ritmo de sueño y actividad de la mascota.",
            compatibleSpeciesCsv = ALL
        )
    )
}
