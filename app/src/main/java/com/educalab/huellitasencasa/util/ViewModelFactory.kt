package com.educalab.huellitasencasa.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.educalab.huellitasencasa.HuellitasApplication

/** Fábrica genérica que construye cualquier ViewModel a partir de la [HuellitasApplication]. */
class AppViewModelFactory(
    private val app: HuellitasApplication,
    private val creator: (HuellitasApplication) -> ViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        creator(app) as T
}
