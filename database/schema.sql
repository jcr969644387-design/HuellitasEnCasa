-- ============================================================================
-- HuellitasEnCasa — schema.sql
-- SQLite (motor real de Room 2.6.1). Refleja EXACTAMENTE las entidades Kotlin
-- de app/src/main/java/.../data/local/entity/*.kt (nombres de tabla y columna
-- tomados literalmente de las anotaciones @Entity/@ColumnInfo).
-- Versión de esquema Room: 1
-- ============================================================================

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------------------------------
-- 1) TABLAS DE REFERENCIA / CONTENIDO SEMILLA (pobladas una vez por SeedProvider)
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS pet_species (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    code          TEXT NOT NULL,
    display_name  TEXT NOT NULL,
    description   TEXT NOT NULL,
    icon_res      TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS pet_need_definitions (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    species_id     INTEGER NOT NULL,
    need_type      TEXT NOT NULL,
    session_decay  INTEGER NOT NULL,
    care_tip       TEXT NOT NULL,
    FOREIGN KEY (species_id) REFERENCES pet_species(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_pet_need_definitions_species_id_need_type
    ON pet_need_definitions (species_id, need_type);

CREATE TABLE IF NOT EXISTS food_items (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    species_id      INTEGER,                 -- NULL = aplica a cualquier especie
    name            TEXT NOT NULL,
    category        TEXT NOT NULL,           -- ALIMENTO_BUENO | ALIMENTO_MALO | SITUACION
    icon_res        TEXT NOT NULL,
    is_appropriate  INTEGER NOT NULL,        -- 0/1
    explanation     TEXT NOT NULL
);
CREATE INDEX IF NOT EXISTS index_food_items_species_id ON food_items (species_id);

CREATE TABLE IF NOT EXISTS home_items (
    id                       INTEGER PRIMARY KEY AUTOINCREMENT,
    category                 TEXT NOT NULL,  -- CUENCO_COMIDA|CUENCO_AGUA|CAMA|JUGUETE|HIGIENE|ENTORNO
    name                     TEXT NOT NULL,
    icon_res                 TEXT NOT NULL,
    description              TEXT NOT NULL,
    compatible_species_csv   TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS wellbeing_scenarios (
    id                     INTEGER PRIMARY KEY AUTOINCREMENT,
    species_id             INTEGER,
    situation_text         TEXT NOT NULL,
    icon_res               TEXT NOT NULL,
    options_csv            TEXT NOT NULL,    -- 2-4 opciones separadas por "|"
    correct_option_index   INTEGER NOT NULL,
    explanation            TEXT NOT NULL,
    recommend_ask_adult    INTEGER NOT NULL, -- 0/1
    FOREIGN KEY (species_id) REFERENCES pet_species(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_wellbeing_scenarios_species_id ON wellbeing_scenarios (species_id);

CREATE TABLE IF NOT EXISTS badges (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    code         TEXT NOT NULL,
    name         TEXT NOT NULL,
    description  TEXT NOT NULL,
    icon_res     TEXT NOT NULL,
    tier         INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS decorations (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    code       TEXT NOT NULL,
    name       TEXT NOT NULL,
    icon_res   TEXT NOT NULL,
    category   TEXT NOT NULL   -- HOGAR | JARDIN | JUGUETE
);

CREATE TABLE IF NOT EXISTS missions (
    id                     INTEGER PRIMARY KEY AUTOINCREMENT,
    code                   TEXT NOT NULL,
    title                  TEXT NOT NULL,
    description            TEXT NOT NULL,
    type                   TEXT NOT NULL,   -- 8 MissionType
    target_count           INTEGER NOT NULL,
    reward_badge_id        INTEGER,
    reward_decoration_id   INTEGER,
    order_index            INTEGER NOT NULL,
    icon_res               TEXT NOT NULL,
    FOREIGN KEY (reward_badge_id) REFERENCES badges(id) ON DELETE SET NULL,
    FOREIGN KEY (reward_decoration_id) REFERENCES decorations(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS index_missions_reward_badge_id ON missions (reward_badge_id);
CREATE INDEX IF NOT EXISTS index_missions_reward_decoration_id ON missions (reward_decoration_id);

-- ---------------------------------------------------------------------------
-- 2) ESTADO DEL CUIDADOR Y SU(S) MASCOTA(S)
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS user_profiles (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    alias            TEXT NOT NULL,
    avatar_id        INTEGER NOT NULL,
    created_at       INTEGER NOT NULL,
    sound_enabled    INTEGER NOT NULL DEFAULT 1,
    haptic_enabled   INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS virtual_pets (
    id                        INTEGER PRIMARY KEY AUTOINCREMENT,
    user_profile_id           INTEGER NOT NULL,
    species_id                INTEGER NOT NULL,
    name                      TEXT NOT NULL,
    avatar_variant            INTEGER NOT NULL,
    adopted_at                INTEGER NOT NULL,
    feeding                   INTEGER NOT NULL DEFAULT 80,
    hydration                 INTEGER NOT NULL DEFAULT 80,
    hygiene                   INTEGER NOT NULL DEFAULT 80,
    activity_level            INTEGER NOT NULL DEFAULT 80,
    rest                      INTEGER NOT NULL DEFAULT 80,
    affection                 INTEGER NOT NULL DEFAULT 80,
    last_session_epoch_day    INTEGER NOT NULL,
    is_active                 INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (species_id) REFERENCES pet_species(id) ON DELETE CASCADE,
    CHECK (feeding BETWEEN 0 AND 100),
    CHECK (hydration BETWEEN 0 AND 100),
    CHECK (hygiene BETWEEN 0 AND 100),
    CHECK (activity_level BETWEEN 0 AND 100),
    CHECK (rest BETWEEN 0 AND 100),
    CHECK (affection BETWEEN 0 AND 100)
);
CREATE INDEX IF NOT EXISTS index_virtual_pets_user_profile_id ON virtual_pets (user_profile_id);
CREATE INDEX IF NOT EXISTS index_virtual_pets_species_id ON virtual_pets (species_id);

-- ---------------------------------------------------------------------------
-- 3) HISTORIAL Y PLANIFICACIÓN
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS care_actions (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    virtual_pet_id   INTEGER NOT NULL,
    action_type      TEXT NOT NULL,
    need_type        TEXT NOT NULL,
    delta            INTEGER NOT NULL,
    timestamp        INTEGER NOT NULL,
    FOREIGN KEY (virtual_pet_id) REFERENCES virtual_pets(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_care_actions_virtual_pet_id ON care_actions (virtual_pet_id);
CREATE INDEX IF NOT EXISTS index_care_actions_timestamp ON care_actions (timestamp);

CREATE TABLE IF NOT EXISTS care_sessions (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    virtual_pet_id      INTEGER NOT NULL,
    started_at          INTEGER NOT NULL,
    ended_at            INTEGER,
    actions_count       INTEGER NOT NULL,
    wellbeing_at_end    INTEGER NOT NULL,
    FOREIGN KEY (virtual_pet_id) REFERENCES virtual_pets(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_care_sessions_virtual_pet_id ON care_sessions (virtual_pet_id);

CREATE TABLE IF NOT EXISTS food_attempts (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    user_profile_id  INTEGER NOT NULL,
    food_item_id     INTEGER NOT NULL,
    virtual_pet_id   INTEGER,
    was_correct      INTEGER NOT NULL,
    timestamp        INTEGER NOT NULL,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_food_attempts_user_profile_id ON food_attempts (user_profile_id);
CREATE INDEX IF NOT EXISTS index_food_attempts_food_item_id ON food_attempts (food_item_id);

CREATE TABLE IF NOT EXISTS home_challenges (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    virtual_pet_id     INTEGER NOT NULL,
    home_item_id       INTEGER NOT NULL,
    placed_correctly   INTEGER NOT NULL,
    timestamp          INTEGER NOT NULL,
    FOREIGN KEY (virtual_pet_id) REFERENCES virtual_pets(id) ON DELETE CASCADE,
    FOREIGN KEY (home_item_id) REFERENCES home_items(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_home_challenges_virtual_pet_id ON home_challenges (virtual_pet_id);
CREATE INDEX IF NOT EXISTS index_home_challenges_home_item_id ON home_challenges (home_item_id);

CREATE TABLE IF NOT EXISTS scenario_attempts (
    id                     INTEGER PRIMARY KEY AUTOINCREMENT,
    user_profile_id        INTEGER NOT NULL,
    scenario_id            INTEGER NOT NULL,
    virtual_pet_id         INTEGER,
    chosen_option_index    INTEGER NOT NULL,
    was_correct            INTEGER NOT NULL,
    timestamp              INTEGER NOT NULL,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (scenario_id) REFERENCES wellbeing_scenarios(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_scenario_attempts_user_profile_id ON scenario_attempts (user_profile_id);
CREATE INDEX IF NOT EXISTS index_scenario_attempts_scenario_id ON scenario_attempts (scenario_id);

CREATE TABLE IF NOT EXISTS daily_care_plans (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    virtual_pet_id     INTEGER NOT NULL,
    date_epoch_day     INTEGER NOT NULL,
    completed          INTEGER NOT NULL,
    created_at         INTEGER NOT NULL,
    FOREIGN KEY (virtual_pet_id) REFERENCES virtual_pets(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_daily_care_plans_virtual_pet_id ON daily_care_plans (virtual_pet_id);
CREATE UNIQUE INDEX IF NOT EXISTS index_daily_care_plans_virtual_pet_id_date_epoch_day
    ON daily_care_plans (virtual_pet_id, date_epoch_day);

CREATE TABLE IF NOT EXISTS daily_care_plan_items (
    id                     INTEGER PRIMARY KEY AUTOINCREMENT,
    daily_care_plan_id     INTEGER NOT NULL,
    slot                   TEXT NOT NULL,   -- MANANA | TARDE | NOCHE
    care_action_type       TEXT NOT NULL,
    order_index            INTEGER NOT NULL,
    is_correct_placement   INTEGER NOT NULL,
    FOREIGN KEY (daily_care_plan_id) REFERENCES daily_care_plans(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_daily_care_plan_items_daily_care_plan_id
    ON daily_care_plan_items (daily_care_plan_id);

CREATE TABLE IF NOT EXISTS mission_completions (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    user_profile_id    INTEGER NOT NULL,
    mission_id         INTEGER NOT NULL,
    progress_count     INTEGER NOT NULL,
    completed          INTEGER NOT NULL,
    completed_at       INTEGER,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_mission_completions_user_profile_id_mission_id
    ON mission_completions (user_profile_id, mission_id);

CREATE TABLE IF NOT EXISTS unlocked_decorations (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    user_profile_id    INTEGER NOT NULL,
    decoration_id      INTEGER NOT NULL,
    unlocked_at        INTEGER NOT NULL,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (decoration_id) REFERENCES decorations(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_unlocked_decorations_user_profile_id_decoration_id
    ON unlocked_decorations (user_profile_id, decoration_id);

CREATE TABLE IF NOT EXISTS user_badges (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    user_profile_id    INTEGER NOT NULL,
    badge_id           INTEGER NOT NULL,
    earned_at          INTEGER NOT NULL,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_user_badges_user_profile_id_badge_id
    ON user_badges (user_profile_id, badge_id);

-- Tabla adicional respecto al listado BASE mínimo (documentada en BASE_DE_DATOS.md):
-- progreso real de la Academia de cuidado, necesaria para no simular el estado de las lecciones.
CREATE TABLE IF NOT EXISTS academy_lesson_progress (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    user_profile_id    INTEGER NOT NULL,
    lesson_code        TEXT NOT NULL,
    viewed_count       INTEGER NOT NULL,
    completed          INTEGER NOT NULL,
    completed_at       INTEGER,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_academy_lesson_progress_user_profile_id_lesson_code
    ON academy_lesson_progress (user_profile_id, lesson_code);
