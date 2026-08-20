package com.educalab.huellitasencasa

import android.app.Application
import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.seed.SeedProvider
import com.educalab.huellitasencasa.data.repository.CareLogRepository
import com.educalab.huellitasencasa.data.repository.ContentRepository
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProfileRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HuellitasApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: HuellitasDatabase by lazy { HuellitasDatabase.getInstance(this) }

    val profileRepository: ProfileRepository by lazy { ProfileRepository(database) }
    val petRepository: PetRepository by lazy { PetRepository(database) }
    val contentRepository: ContentRepository by lazy { ContentRepository(database) }
    val careLogRepository: CareLogRepository by lazy { CareLogRepository(database) }
    val progressRepository: ProgressRepository by lazy { ProgressRepository(database) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            SeedProvider.seedIfNeeded(database)
        }
    }
}
