package com.wantfree.jobtracker.data.api

import com.wantfree.jobtracker.data.model.ai.InterviewQuestionRequest
import com.wantfree.jobtracker.data.model.ai.InterviewQuestionResponse
import retrofit2.http.Body
import retrofit2.http.POST

/** AI 면접 API — 백엔드 딥시크 프록시와 1:1 대응 */
interface AiService {

    @POST("api/ai/interview/questions")
    suspend fun getInterviewQuestions(@Body request: InterviewQuestionRequest): InterviewQuestionResponse
}
