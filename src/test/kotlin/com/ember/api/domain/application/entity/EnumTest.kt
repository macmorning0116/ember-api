package com.ember.api.domain.application.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EnumTest {
    @Test
    @DisplayName("ApplicationStatus 한글 라벨 변환")
    fun applicationStatusFromLabel() {
        assertEquals(ApplicationStatus.PENDING, ApplicationStatus.fromLabel("지원 예정"))
        assertEquals(ApplicationStatus.CODING_TEST, ApplicationStatus.fromLabel("코테 기간"))
        assertEquals(ApplicationStatus.ACCEPTED, ApplicationStatus.fromLabel("최종 합격"))
        assertEquals(ApplicationStatus.REJECTED_INTERVIEW, ApplicationStatus.fromLabel("면접 탈락"))
    }

    @Test
    @DisplayName("ApplicationStatus 잘못된 라벨 예외")
    fun applicationStatusInvalidLabel() {
        assertThrows<IllegalArgumentException> {
            ApplicationStatus.fromLabel("없는상태")
        }
    }

    @Test
    @DisplayName("CompanySize 한글 라벨 변환")
    fun companySizeFromLabel() {
        assertEquals(CompanySize.LARGE, CompanySize.fromLabel("대기업"))
        assertEquals(CompanySize.MID_LARGE, CompanySize.fromLabel("중견기업"))
        assertEquals(CompanySize.MID, CompanySize.fromLabel("중소기업"))
        assertEquals(CompanySize.STARTUP, CompanySize.fromLabel("스타트업"))
    }

    @Test
    @DisplayName("CompanySize 잘못된 라벨 예외")
    fun companySizeInvalidLabel() {
        assertThrows<IllegalArgumentException> {
            CompanySize.fromLabel("없는규모")
        }
    }

    @Test
    @DisplayName("CoverLetterType 한글 라벨 변환")
    fun coverLetterTypeFromLabel() {
        assertEquals(CoverLetterType.MOTIVATION, CoverLetterType.fromLabel("지원 동기"))
        assertEquals(CoverLetterType.GROWTH, CoverLetterType.fromLabel("성장 과정"))
        assertEquals(CoverLetterType.OTHER, CoverLetterType.fromLabel("기타"))
    }

    @Test
    @DisplayName("CoverLetterType 잘못된 라벨 예외")
    fun coverLetterTypeInvalidLabel() {
        assertThrows<IllegalArgumentException> {
            CoverLetterType.fromLabel("없는유형")
        }
    }

    @Test
    @DisplayName("ApplicationStatus label 값 확인")
    fun applicationStatusLabel() {
        assertEquals("지원 예정", ApplicationStatus.PENDING.label)
        assertEquals("코테 기간", ApplicationStatus.CODING_TEST.label)
        assertEquals("면접 기간", ApplicationStatus.INTERVIEW.label)
        assertEquals("지원 완료", ApplicationStatus.APPLIED.label)
        assertEquals("최종 합격", ApplicationStatus.ACCEPTED.label)
        assertEquals("서류 탈락", ApplicationStatus.REJECTED_DOCS.label)
        assertEquals("코테 탈락", ApplicationStatus.REJECTED_CODING.label)
        assertEquals("면접 탈락", ApplicationStatus.REJECTED_INTERVIEW.label)
    }
}
