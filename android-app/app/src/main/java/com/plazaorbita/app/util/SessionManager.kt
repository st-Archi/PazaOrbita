package com.plazaorbita.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Antes esta clase guardaba el token JWT en DataStore/SharedPreferences en texto plano:
 * cualquiera con acceso al dispositivo (root, backup, ADB en debug) podía leerlo.
 * Ahora usa EncryptedSharedPreferences, que cifra clave y valor con una llave que vive
 * en el Android Keystore (nunca sale del hardware/OS del dispositivo).
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "plaza_orbita_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val TOKEN_KEY = "token"
        private const val ROLE_KEY = "role"
        private const val NAME_KEY = "name"
        private const val USER_ID_KEY = "userId"
    }

    // Se mantienen como suspend + Dispatchers.IO para no romper el resto del código
    // (ViewModels, pantallas) que ya llama a estas funciones desde una coroutine.
    suspend fun saveSession(token: String, userId: Long, name: String, role: String) =
        withContext(Dispatchers.IO) {
            prefs.edit()
                .putString(TOKEN_KEY, token)
                .putLong(USER_ID_KEY, userId)
                .putString(NAME_KEY, name)
                .putString(ROLE_KEY, role)
                .apply()
        }

    suspend fun getRole(): String? = withContext(Dispatchers.IO) { prefs.getString(ROLE_KEY, null) }

    suspend fun getUserId(): Long? = withContext(Dispatchers.IO) {
        val id = prefs.getLong(USER_ID_KEY, -1L)
        if (id == -1L) null else id
    }

    suspend fun getToken(): String? = withContext(Dispatchers.IO) { prefs.getString(TOKEN_KEY, null) }

    suspend fun clear() = withContext(Dispatchers.IO) { prefs.edit().clear().apply() }
}
