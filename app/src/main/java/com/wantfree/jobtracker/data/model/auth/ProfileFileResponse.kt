package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class ProfileFileResponse(
    val fileName: String? = null,
    val fileType: String? = null,
    val text: String? = null,
)
