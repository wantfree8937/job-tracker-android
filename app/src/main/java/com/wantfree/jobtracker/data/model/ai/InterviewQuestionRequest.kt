package com.wantfree.jobtracker.data.model.ai

import kotlinx.serialization.Serializable

/** AI 면접 질문 생성 요청 DTO — 공고 정보를 딥시크 프록시에 전달 (공고 미선택 시 필드 전부 null 가능) */
@Serializable
data class InterviewQuestionRequest(
    val companyName: String? = null,
    val position: String? = null,
    val region: String? = null,
    val experience: String? = null,
    val industry: String? = null,
    val memo: String? = null,
    val url: String? = null,
    val topic: String? = null,
    val difficulty: String? = null,
)
