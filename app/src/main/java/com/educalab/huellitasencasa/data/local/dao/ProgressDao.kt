package com.educalab.huellitasencasa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.educalab.huellitasencasa.data.local.entity.AcademyLessonProgressEntity
import com.educalab.huellitasencasa.data.local.entity.BadgeEntity
import com.educalab.huellitasencasa.data.local.entity.DecorationEntity
import com.educalab.huellitasencasa.data.local.entity.MissionCompletionEntity
import com.educalab.huellitasencasa.data.local.entity.MissionEntity
import com.educalab.huellitasencasa.data.local.entity.UnlockedDecorationEntity
import com.educalab.huellitasencasa.data.local.entity.UserBadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(missions: List<MissionEntity>)

    @Query("SELECT * FROM missions ORDER BY order_index ASC")
    fun observeAll(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions WHERE type = :type ORDER BY order_index ASC")
    suspend fun getByType(type: String): List<MissionEntity>

    @Query("SELECT * FROM missions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MissionEntity?

    @Query("SELECT COUNT(*) FROM missions")
    suspend fun count(): Int
}

@Dao
interface MissionCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(completion: MissionCompletionEntity): Long

    @Update
    suspend fun update(completion: MissionCompletionEntity)

    @Query("SELECT * FROM mission_completions WHERE user_profile_id = :userProfileId AND mission_id = :missionId LIMIT 1")
    suspend fun getFor(userProfileId: Long, missionId: Long): MissionCompletionEntity?

    @Query("SELECT * FROM mission_completions WHERE user_profile_id = :userProfileId")
    fun observeForUser(userProfileId: Long): Flow<List<MissionCompletionEntity>>

    @Query("SELECT COUNT(*) FROM mission_completions WHERE user_profile_id = :userProfileId AND completed = 1")
    suspend fun countCompleted(userProfileId: Long): Int
}

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<BadgeEntity>)

    @Query("SELECT * FROM badges ORDER BY tier ASC, id ASC")
    fun observeAll(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges ORDER BY tier ASC, id ASC")
    suspend fun getAll(): List<BadgeEntity>

    @Query("SELECT * FROM badges WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): BadgeEntity?

    @Query("SELECT COUNT(*) FROM badges")
    suspend fun count(): Int
}

@Dao
interface UserBadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userBadge: UserBadgeEntity): Long

    @Query("SELECT * FROM user_badges WHERE user_profile_id = :userProfileId")
    fun observeForUser(userProfileId: Long): Flow<List<UserBadgeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_badges WHERE user_profile_id = :userProfileId AND badge_id = :badgeId)")
    suspend fun hasBadge(userProfileId: Long, badgeId: Long): Boolean

    @Query("SELECT COUNT(*) FROM user_badges WHERE user_profile_id = :userProfileId")
    suspend fun countForUser(userProfileId: Long): Int
}

@Dao
interface AcademyLessonProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: AcademyLessonProgressEntity): Long

    @Query("SELECT * FROM academy_lesson_progress WHERE user_profile_id = :userProfileId")
    fun observeForUser(userProfileId: Long): Flow<List<AcademyLessonProgressEntity>>

    @Query("SELECT * FROM academy_lesson_progress WHERE user_profile_id = :userProfileId AND lesson_code = :lessonCode LIMIT 1")
    suspend fun getFor(userProfileId: Long, lessonCode: String): AcademyLessonProgressEntity?

    @Query("SELECT COUNT(*) FROM academy_lesson_progress WHERE user_profile_id = :userProfileId AND completed = 1")
    suspend fun countCompleted(userProfileId: Long): Int
}

@Dao
interface DecorationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(decorations: List<DecorationEntity>)

    @Query("SELECT * FROM decorations ORDER BY id ASC")
    fun observeAll(): Flow<List<DecorationEntity>>

    @Query("SELECT * FROM decorations ORDER BY id ASC")
    suspend fun getAll(): List<DecorationEntity>

    @Query("SELECT * FROM decorations WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): DecorationEntity?

    @Query("SELECT COUNT(*) FROM decorations")
    suspend fun count(): Int
}

@Dao
interface UnlockedDecorationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(unlocked: UnlockedDecorationEntity): Long

    @Query("SELECT * FROM unlocked_decorations WHERE user_profile_id = :userProfileId")
    fun observeForUser(userProfileId: Long): Flow<List<UnlockedDecorationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_decorations WHERE user_profile_id = :userProfileId AND decoration_id = :decorationId)")
    suspend fun isUnlocked(userProfileId: Long, decorationId: Long): Boolean

    @Query("SELECT COUNT(*) FROM unlocked_decorations WHERE user_profile_id = :userProfileId")
    suspend fun countForUser(userProfileId: Long): Int
}
