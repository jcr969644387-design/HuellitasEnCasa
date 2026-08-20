package com.educalab.huellitasencasa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.educalab.huellitasencasa.data.local.dao.AcademyLessonProgressDao
import com.educalab.huellitasencasa.data.local.dao.BadgeDao
import com.educalab.huellitasencasa.data.local.dao.CareActionDao
import com.educalab.huellitasencasa.data.local.dao.CareSessionDao
import com.educalab.huellitasencasa.data.local.dao.DailyCarePlanDao
import com.educalab.huellitasencasa.data.local.dao.DecorationDao
import com.educalab.huellitasencasa.data.local.dao.FoodAttemptDao
import com.educalab.huellitasencasa.data.local.dao.FoodItemDao
import com.educalab.huellitasencasa.data.local.dao.HomeChallengeDao
import com.educalab.huellitasencasa.data.local.dao.HomeItemDao
import com.educalab.huellitasencasa.data.local.dao.MissionCompletionDao
import com.educalab.huellitasencasa.data.local.dao.MissionDao
import com.educalab.huellitasencasa.data.local.dao.PetNeedDefinitionDao
import com.educalab.huellitasencasa.data.local.dao.PetSpeciesDao
import com.educalab.huellitasencasa.data.local.dao.ScenarioAttemptDao
import com.educalab.huellitasencasa.data.local.dao.UnlockedDecorationDao
import com.educalab.huellitasencasa.data.local.dao.UserBadgeDao
import com.educalab.huellitasencasa.data.local.dao.UserProfileDao
import com.educalab.huellitasencasa.data.local.dao.VirtualPetDao
import com.educalab.huellitasencasa.data.local.dao.WellbeingScenarioDao
import com.educalab.huellitasencasa.data.local.entity.BadgeEntity
import com.educalab.huellitasencasa.data.local.entity.AcademyLessonProgressEntity
import com.educalab.huellitasencasa.data.local.entity.CareActionEntity
import com.educalab.huellitasencasa.data.local.entity.CareSessionEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanItemEntity
import com.educalab.huellitasencasa.data.local.entity.DecorationEntity
import com.educalab.huellitasencasa.data.local.entity.FoodAttemptEntity
import com.educalab.huellitasencasa.data.local.entity.FoodItemEntity
import com.educalab.huellitasencasa.data.local.entity.HomeChallengeEntity
import com.educalab.huellitasencasa.data.local.entity.HomeItemEntity
import com.educalab.huellitasencasa.data.local.entity.MissionCompletionEntity
import com.educalab.huellitasencasa.data.local.entity.MissionEntity
import com.educalab.huellitasencasa.data.local.entity.PetNeedDefinitionEntity
import com.educalab.huellitasencasa.data.local.entity.PetSpeciesEntity
import com.educalab.huellitasencasa.data.local.entity.ScenarioAttemptEntity
import com.educalab.huellitasencasa.data.local.entity.UnlockedDecorationEntity
import com.educalab.huellitasencasa.data.local.entity.UserBadgeEntity
import com.educalab.huellitasencasa.data.local.entity.UserProfileEntity
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.local.entity.WellbeingScenarioEntity

/**
 * Base de datos Room de HuellitasEnCasa. 100% local: no hay ninguna sincronización remota.
 * Contiene las 20 tablas descritas en la especificación (perfiles, especies, mascota virtual,
 * historial de cuidado, contenido educativo semilla y progreso real del jugador).
 */
@Database(
    entities = [
        UserProfileEntity::class,
        PetSpeciesEntity::class,
        PetNeedDefinitionEntity::class,
        VirtualPetEntity::class,
        CareActionEntity::class,
        CareSessionEntity::class,
        FoodItemEntity::class,
        FoodAttemptEntity::class,
        HomeItemEntity::class,
        HomeChallengeEntity::class,
        WellbeingScenarioEntity::class,
        ScenarioAttemptEntity::class,
        DailyCarePlanEntity::class,
        DailyCarePlanItemEntity::class,
        MissionEntity::class,
        MissionCompletionEntity::class,
        DecorationEntity::class,
        UnlockedDecorationEntity::class,
        BadgeEntity::class,
        UserBadgeEntity::class,
        AcademyLessonProgressEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HuellitasDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun petSpeciesDao(): PetSpeciesDao
    abstract fun petNeedDefinitionDao(): PetNeedDefinitionDao
    abstract fun virtualPetDao(): VirtualPetDao

    abstract fun careActionDao(): CareActionDao
    abstract fun careSessionDao(): CareSessionDao
    abstract fun dailyCarePlanDao(): DailyCarePlanDao

    abstract fun foodItemDao(): FoodItemDao
    abstract fun foodAttemptDao(): FoodAttemptDao
    abstract fun homeItemDao(): HomeItemDao
    abstract fun homeChallengeDao(): HomeChallengeDao
    abstract fun wellbeingScenarioDao(): WellbeingScenarioDao
    abstract fun scenarioAttemptDao(): ScenarioAttemptDao

    abstract fun missionDao(): MissionDao
    abstract fun missionCompletionDao(): MissionCompletionDao
    abstract fun badgeDao(): BadgeDao
    abstract fun userBadgeDao(): UserBadgeDao
    abstract fun decorationDao(): DecorationDao
    abstract fun unlockedDecorationDao(): UnlockedDecorationDao
    abstract fun academyLessonProgressDao(): AcademyLessonProgressDao

    companion object {
        private const val DB_NAME = "huellitas_en_casa.db"

        @Volatile
        private var INSTANCE: HuellitasDatabase? = null

        fun getInstance(context: Context): HuellitasDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HuellitasDatabase::class.java,
                    DB_NAME
                )
                    // No usamos destructive migrations en producción; para v1 no hace falta migrar.
                    .build()
                    .also { INSTANCE = it }
            }
        }

        /** Usada únicamente por tests: construye una base de datos en memoria. */
        fun inMemory(context: Context): HuellitasDatabase =
            Room.inMemoryDatabaseBuilder(context, HuellitasDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }
}
