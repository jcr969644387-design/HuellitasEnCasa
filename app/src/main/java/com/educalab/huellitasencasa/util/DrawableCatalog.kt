package com.educalab.huellitasencasa.util

import androidx.annotation.DrawableRes
import com.educalab.huellitasencasa.R

/**
 * El contenido semilla guarda el nombre del drawable como String (para no acoplar la capa de
 * datos a recursos de Android). Este objeto traduce ese nombre a un @DrawableRes real,
 * comprobado por el compilador (evita el uso de getIdentifier en tiempo de ejecución).
 */
object DrawableCatalog {

    @DrawableRes
    fun resolve(name: String): Int = when (name) {
        // Módulos
        "ic_mod_adopcion" -> R.drawable.ic_mod_adopcion
        "ic_mod_hogar" -> R.drawable.ic_mod_hogar
        "ic_mod_alimentacion" -> R.drawable.ic_mod_alimentacion
        "ic_mod_higiene" -> R.drawable.ic_mod_higiene
        "ic_mod_actividad" -> R.drawable.ic_mod_actividad
        "ic_mod_bienestar" -> R.drawable.ic_mod_bienestar
        "ic_mod_planificador" -> R.drawable.ic_mod_planificador
        "ic_mod_academia" -> R.drawable.ic_mod_academia
        "ic_mod_misiones" -> R.drawable.ic_mod_misiones
        "ic_mod_perfil" -> R.drawable.ic_mod_perfil
        // Insignias
        "badge_primera_adopcion" -> R.drawable.badge_primera_adopcion
        "badge_maestro_comidas" -> R.drawable.badge_maestro_comidas
        "badge_higiene_experta" -> R.drawable.badge_higiene_experta
        "badge_energia_activa" -> R.drawable.badge_energia_activa
        "badge_guardian_bienestar" -> R.drawable.badge_guardian_bienestar
        "badge_planificador" -> R.drawable.badge_planificador
        "badge_academia_oro" -> R.drawable.badge_academia_oro
        "badge_amigo_fiel" -> R.drawable.badge_amigo_fiel
        "badge_veterinario_junior" -> R.drawable.badge_veterinario_junior
        "badge_decorador" -> R.drawable.badge_decorador
        // Decoraciones
        "decor_maceta_flores" -> R.drawable.decor_maceta_flores
        "decor_cojin_estrella" -> R.drawable.decor_cojin_estrella
        "decor_lampara_luna" -> R.drawable.decor_lampara_luna
        "decor_alfombra_rayas" -> R.drawable.decor_alfombra_rayas
        "decor_cuadro_paisaje" -> R.drawable.decor_cuadro_paisaje
        "decor_manta_cuadros" -> R.drawable.decor_manta_cuadros
        "decor_estanteria_libros" -> R.drawable.decor_estanteria_libros
        "decor_farolillo" -> R.drawable.decor_farolillo
        // Objetos del hogar
        "item_cuenco_comida" -> R.drawable.item_cuenco_comida
        "item_cuenco_agua" -> R.drawable.item_cuenco_agua
        "item_cama" -> R.drawable.item_cama
        "item_juguete_pelota" -> R.drawable.item_juguete_pelota
        "item_cepillo" -> R.drawable.item_cepillo
        "item_correa" -> R.drawable.item_correa
        "item_planta_interior" -> R.drawable.item_planta_interior
        "item_ventana_luz" -> R.drawable.item_ventana_luz
        // Categorías de alimentos
        "food_fruta_verdura" -> R.drawable.food_fruta_verdura
        "food_carne_pescado" -> R.drawable.food_carne_pescado
        "food_pienso" -> R.drawable.food_pienso
        "food_lacteo" -> R.drawable.food_lacteo
        "food_dulce_prohibido" -> R.drawable.food_dulce_prohibido
        "food_hueso_prohibido" -> R.drawable.food_hueso_prohibido
        "food_agua_fresca" -> R.drawable.food_agua_fresca
        "food_planta_toxica" -> R.drawable.food_planta_toxica
        // Estados
        "estado_bloqueado" -> R.drawable.estado_bloqueado
        "estado_disponible" -> R.drawable.estado_disponible
        "estado_iniciado" -> R.drawable.estado_iniciado
        "estado_completado" -> R.drawable.estado_completado
        "estado_dominado" -> R.drawable.estado_dominado
        // Otros
        "logo_huellitas" -> R.drawable.logo_huellitas
        "motif_paw_single" -> R.drawable.motif_paw_single
        "motif_cloud" -> R.drawable.motif_cloud
        "motif_grass" -> R.drawable.motif_grass
        else -> R.drawable.motif_paw_single
    }
}
