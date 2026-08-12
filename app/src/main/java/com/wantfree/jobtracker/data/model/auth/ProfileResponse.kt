package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val profileText: String? = null,
)
