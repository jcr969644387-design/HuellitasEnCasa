package com.educalab.huellitasencasa.data

import androidx.test.core.app.ApplicationProvider
import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.entity.UserProfileEntity
import com.educalab.huellitasencasa.data.local.seed.SeedProvider
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Verifica que completar una misión real desbloquea de verdad su insignia/decoración asociada
 * y que la base de datos persiste ese desbloqueo (no una lista en memoria). Requiere
 * Robolectric + Android SDK; no se pudo ejecutar en este sandbox (ver nota en SeedProviderTest).
 */
@RunWith(RobolectricTestRunner::class)
class MissionRewardFlowTest {

    private lateinit var db: HuellitasDatabase
    private lateinit var repo: ProgressRepository
    private var profileId: Long = 0

    @Before
    fun setUp() = runTest {
        db = HuellitasDatabase.inMemory(ApplicationProvider.getApplicationContext())
        SeedProvider.seedIfNeeded(db)
        repo = ProgressRepository(db)
        profileId = db.userProfileDao().insert(
            UserProfileEntity(alias = "Tester", avatarId = 0, createdAt = 0L)
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `registering the adoption event completes the first-adoption mission and grants its badge`() = runTest {
        val rewards = repo.registerEvent(profileId, "ADOPCION", 1)
        assertTrue(rewards.newlyCompletedMissions.any { it.code == "M_ADOPTAR_PRIMERA" })
        assertTrue(rewards.newBadges.any { it.code == "PRIMERA_ADOPCION" })
        assertEquals(1, repo.countUserBadges(profileId))
    }

    @Test
    fun `progress below target does not complete the mission nor grant a badge`() = runTest {
        val rewards = repo.registerEvent(profileId, "HIGIENE", 4) // objetivo mínimo de higiene es 5
        assertTrue(rewards.newlyCompletedMissions.none { it.code == "M_HIGIENE_5" })
        assertEquals(0, repo.countUserBadges(profileId))
    }

    @Test
    fun `repeated events accumulate progress until the mission completes exactly once`() = runTest {
        repeat(4) { repo.registerEvent(profileId, "HIGIENE", 1) }
        val finalRewards = repo.registerEvent(profileId, "HIGIENE", 1)
        assertTrue(finalRewards.newlyCompletedMissions.any { it.code == "M_HIGIENE_5" })
        // Un evento adicional no debe volver a completar ni duplicar recompensas.
        val extraRewards = repo.registerEvent(profileId, "HIGIENE", 1)
        assertTrue(extraRewards.newlyCompletedMissions.none { it.code == "M_HIGIENE_5" })
    }
}
