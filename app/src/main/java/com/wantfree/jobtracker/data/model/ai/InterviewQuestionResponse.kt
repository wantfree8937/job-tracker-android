package com.wantfree.jobtracker.data.model.ai

import kotlinx.serialization.Serializable

/** AI 면접 질문 생성 응답 DTO */
@Serializable
data class InterviewQuestionResponse(
    val questions: List<String>,
    val usedResume: Boolean? = null,
)
