package com.educalab.huellitasencasa.data

import androidx.test.core.app.ApplicationProvider
import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.seed.SeedProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Verifica que el contenido semilla se inserta correctamente en una base de datos Room en
 * memoria y que sembrar dos veces no duplica datos (idempotencia). Requiere Robolectric +
 * Android SDK (no se pudo ejecutar en el sandbox de generación por falta de acceso a
 * dl.google.com; queda pendiente de verificación en un entorno con Android Studio/Gradle).
 */
@RunWith(RobolectricTestRunner::class)
class SeedProviderTest {

    private lateinit var db: HuellitasDatabase

    @Before
    fun setUp() {
        db = HuellitasDatabase.inMemory(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `seeding inserts the five species`() = runTest {
        SeedProvider.seedIfNeeded(db)
        assertEquals(5, db.petSpeciesDao().count())
    }

    @Test
    fun `seeding inserts thirty need definitions (five species times six needs)`() = runTest {
        SeedProvider.seedIfNeeded(db)
        assertEquals(30, db.petNeedDefinitionDao().count())
    }

    @Test
    fun `seeding inserts sixty food items`() = runTest {
        SeedProvider.seedIfNeeded(db)
        assertEquals(60, db.foodItemDao().count())
    }

    @Test
    fun `seeding inserts thirty wellbeing scenarios`() = runTest {
        SeedProvider.seedIfNeeded(db)
        assertEquals(30, db.wellbeingScenarioDao().count())
    }

    @Test
    fun `seeding inserts thirty missions with resolved rewards`() = runTest {
        SeedProvider.seedIfNeeded(db)
        assertEquals(30, db.missionDao().count())
        val missionsWithBadgeReward = db.missionDao().getByType("ALIMENTACION").count { it.rewardBadgeId != null }
        assertTrue("Al menos una misión de alimentación debe otorgar una insignia real", missionsWithBadgeReward >= 1)
    }

    @Test
    fun `seeding ten badges and eight decorations`() = runTest {
        SeedProvider.seedIfNeeded(db)
        assertEquals(10, db.badgeDao().count())
        assertEquals(8, db.decorationDao().count())
    }

    @Test
    fun `seeding seventeen home items`() = runTest {
        SeedProvider.seedIfNeeded(db)
        assertEquals(17, db.homeItemDao().count())
    }

    @Test
    fun `seeding twice does not duplicate data`() = runTest {
        SeedProvider.seedIfNeeded(db)
        SeedProvider.seedIfNeeded(db)
        assertEquals(5, db.petSpeciesDao().count())
        assertEquals(60, db.foodItemDao().count())
        assertEquals(30, db.missionDao().count())
    }
}
