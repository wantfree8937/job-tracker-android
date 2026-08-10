package com.wantfree.jobtracker.domain.repository

import com.wantfree.jobtracker.data.model.job.JobPostingResponse

/**
 * AI 면접 저장소 인터페이스 (클린 아키텍처 — domain 계층)
 * 구현체는 data 계층(AiRepositoryImpl)에 있다.
 */
interface AiRepository {

    suspend fun getInterviewQuestions(job: JobPostingResponse): Result<List<String>>
}
