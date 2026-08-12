package com.wantfree.jobtracker.data.repository

import com.wantfree.jobtracker.data.api.AuthService
import com.wantfree.jobtracker.data.model.auth.ProfileFileResponse
import com.wantfree.jobtracker.data.model.auth.ProfileUpdateRequest
import com.wantfree.jobtracker.domain.repository.ProfileRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/** 저장된 이력서/파일이 없으면 백엔드가 404를 주므로 null로 변환해서 UI가 분기 없이 쓰게 한다 */
@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val authService: AuthService,
) : ProfileRepository {

    override suspend fun getProfile(): Result<String?> = runCatching {
        authService.getProfile().profileText
    }.recoverNotFound(null)

    override suspend fun saveProfile(text: String): Result<Unit> = runCatching {
        authService.updateProfile(ProfileUpdateRequest(text))
        Unit
    }

    override suspend fun uploadFile(bytes: ByteArray, fileName: String, mimeType: String): Result<ProfileFileResponse> =
        runCatching {
            val body = bytes.toRequestBody(mimeType.toMediaType())
            val part = MultipartBody.Part.createFormData("file", fileName, body)
            authService.uploadProfileFile(part)
        }

    override suspend fun getFileInfo(): Result<ProfileFileResponse?> = runCatching {
        authService.getProfileFile()
    }.recoverNotFound(null)

    override suspend fun deleteFile(): Result<Unit> = runCatching {
        authService.deleteProfileFile()
        Unit
    }

    private fun <T> Result<T>.recoverNotFound(default: T): Result<T> = recoverCatching { e ->
        if (e is HttpException && e.code() == 404) default else throw e
    }
}
