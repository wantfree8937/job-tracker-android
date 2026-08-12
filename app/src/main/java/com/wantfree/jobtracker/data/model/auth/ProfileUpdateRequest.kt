package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdateRequest(
    val profileText: String,
)
