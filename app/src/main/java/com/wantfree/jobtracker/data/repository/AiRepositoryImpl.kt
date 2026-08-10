package com.wantfree.jobtracker.data.repository

import com.wantfree.jobtracker.data.api.AiService
import com.wantfree.jobtracker.data.model.ai.InterviewQuestionRequest
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.domain.repository.AiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val aiService: AiService,
) : AiRepository {

    override suspend fun getInterviewQuestions(job: JobPostingResponse): Result<List<String>> = runCatching {
        aiService.getInterviewQuestions(
            InterviewQuestionRequest(
                companyName = job.companyName,
                position = job.position,
                region = job.region,
                experience = job.experience,
                industry = job.industry,
                memo = job.memo,
            )
        ).questions
    }
}
