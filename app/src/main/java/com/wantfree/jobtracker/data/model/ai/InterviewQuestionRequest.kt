package com.wantfree.jobtracker.data.model.ai

import kotlinx.serialization.Serializable

/** AI 면접 질문 생성 요청 DTO — 공고 정보를 딥시크 프록시에 전달 */
@Serializable
data class InterviewQuestionRequest(
    val companyName: String,
    val position: String,
    val region: String? = null,
    val experience: String? = null,
    val industry: String? = null,
    val memo: String? = null,
)
