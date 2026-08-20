package com.educalab.huellitasencasa.ui.navigation

object Destinations {
    const val ONBOARDING = "onboarding"
    const val PROFILE_SETUP = "profile_setup"
    const val HUB = "hub"
    const val ADOPTION = "adoption"
    const val HOME_SETUP = "home_setup"
    const val FEEDING = "feeding"
    const val HYGIENE = "hygiene"
    const val ACTIVITY = "activity"
    const val WELLBEING = "wellbeing"
    const val PLANNER = "planner"
    const val ACADEMY = "academy"
    const val ACADEMY_LESSON = "academy_lesson/{lessonCode}"
    const val MISSIONS = "missions"
    const val PROFILE_EDIT = "profile_edit"

    fun academyLesson(code: String) = "academy_lesson/$code"
}
