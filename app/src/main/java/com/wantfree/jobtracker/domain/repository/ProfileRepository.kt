package com.wantfree.jobtracker.domain.repository

import com.wantfree.jobtracker.data.model.auth.ProfileFileResponse

/**
 * 이력서(프로필) 저장소 인터페이스 — 텍스트 조회 없음(404)은 null, 파일은 목록(최대 3개)으로 표현한다
 */
interface ProfileRepository {

    /** 저장된 이력서 텍스트 조회 (없으면 profileText가 null) */
    suspend fun getProfile(): Result<String?>

    /** 이력서 텍스트 저장 */
    suspend fun saveProfile(text: String): Result<Unit>

    /** PDF/PPT/PPTX 업로드 (텍스트 추출은 백엔드가 수행) */
    suspend fun uploadFile(bytes: ByteArray, fileName: String, mimeType: String): Result<ProfileFileResponse>

    /** 저장된 파일 목록 조회 (최대 3개, 없으면 빈 목록) */
    suspend fun getFiles(): Result<List<ProfileFileResponse>>

    /** 저장된 파일 개별 삭제 */
    suspend fun deleteFile(fileId: Long): Result<Unit>
}
