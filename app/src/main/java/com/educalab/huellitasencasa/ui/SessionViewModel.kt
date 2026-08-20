package com.educalab.huellitasencasa.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Estado de sesión compartido entre pantallas mientras la app está abierta (perfil activo y
 * mascota activa). Vive mientras dure la Activity; se recalcula desde Room al reabrir la app.
 */
class SessionViewModel : ViewModel() {
    private val _currentProfileId = MutableStateFlow<Long?>(null)
    val currentProfileId: StateFlow<Long?> = _currentProfileId

    private val _currentPetId = MutableStateFlow<Long?>(null)
    val currentPetId: StateFlow<Long?> = _currentPetId

    fun setProfile(id: Long) { _currentProfileId.value = id }
    fun setActivePet(id: Long) { _currentPetId.value = id }
}
