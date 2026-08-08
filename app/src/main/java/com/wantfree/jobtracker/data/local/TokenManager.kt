package com.wantfree.jobtracker.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * JWT 토큰을 DataStore에 저장/조회한다.
 * 로그인 상태는 이 토큰 유무로 판단한다.
 */
@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
    }

    /** 저장된 토큰 (없으면 null) — UI에서 로그인 상태 관찰용 */
    val accessToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
        }
    }
}
