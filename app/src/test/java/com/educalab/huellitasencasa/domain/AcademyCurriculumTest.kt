package com.educalab.huellitasencasa.domain

import com.educalab.huellitasencasa.domain.model.AcademyCurriculum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AcademyCurriculumTest {

    @Test
    fun `curriculum has exactly eight lessons`() {
        assertEquals(8, AcademyCurriculum.lessons.size)
    }

    @Test
    fun `every lesson code is unique`() {
        val codes = AcademyCurriculum.lessons.map { it.code }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `every lesson has at least two content paragraphs`() {
        assertTrue(AcademyCurriculum.lessons.all { it.bodyParagraphs.size >= 2 })
    }

    @Test
    fun `every lesson has a non-blank title and summary`() {
        assertTrue(AcademyCurriculum.lessons.all { it.title.isNotBlank() && it.summary.isNotBlank() })
    }
}
