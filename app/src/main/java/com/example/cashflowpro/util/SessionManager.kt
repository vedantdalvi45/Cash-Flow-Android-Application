package com.example.cashflowpro.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import java.util.Date
import java.util.concurrent.TimeUnit

private const val TOKEN_EXPIRATION_BUFFER_SECONDS = 30

class SessionManager(context: Context) {

    private val prefs: SharedPreferences

    companion object {
        const val AUTH_TOKEN = "auth_token"
        private const val PREFS_FILENAME = "secure_app_prefs"
    }

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(AUTH_TOKEN)
        editor.apply()
    }

    fun isTokenExpired(): Boolean {
        val token = fetchAuthToken() ?: return true

        return try {
            val jwt: Jwt<*, Claims> = Jwts.parser().parseClaimsJwt(token.substring(0, token.lastIndexOf('.') + 1))
            val claims: Claims = jwt.body
            val expirationDate: Date = claims.expiration
            val now = Date()
            val buffer = TimeUnit.SECONDS.toMillis(TOKEN_EXPIRATION_BUFFER_SECONDS.toLong())
            val expirationBuffer = Date(expirationDate.time - buffer)
            expirationBuffer.before(now)
        }catch (e: Exception) {
//            Log.d("SessionManager", "isTokenExpired: $e")
            true
        }
    }
}