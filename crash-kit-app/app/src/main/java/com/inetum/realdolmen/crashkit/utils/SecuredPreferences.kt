package com.inetum.realdolmen.crashkit.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * `SecuredPreferences` is a class that provides secure storage for key-value pairs using SharedPreferences.
 * It uses Android's EncryptedSharedPreferences for storing data securely.
 *
 * @function getString This function retrieves a stored String value for the given key.
 * @function getJwtToken This function retrieves the stored JWT token.
 * @function putJwtToken This function stores a JWT token.
 * @function deleteJwtToken This function deletes the stored JWT token.
 * @function rememberLogin This function sets a flag to remember the login.
 * @function isLoginRemembered This function checks if the login is remembered.
 * @function loggedAsGuest This function sets a flag indicating the user is logged in as a guest.
 * @function loggedAsUser This function sets a flag indicating the user is logged in as a user.
 * @function isGuest This function checks if the user is logged in as a guest.
 *
 */
class SecuredPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(
                KeyGenParameterSpec.Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                    .build()
            )
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getJwtToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun putJwtToken(value: String) {
        with(sharedPreferences.edit()) {
            putString("jwt_token", value)
                .commit()
        }
    }

    fun deleteJwtToken() {
        with(sharedPreferences.edit()) {
            remove("jwt_token")
                .apply()
        }
    }

    fun rememberLogin() {
        with(sharedPreferences.edit()) {
            putBoolean("remember", true)
            apply()
        }
    }

    fun isLoginRemembered(): Boolean {
        return sharedPreferences.getBoolean("remember", false)
    }

    fun loggedAsGuest() {
        with(sharedPreferences.edit()) {
            putBoolean("guest", true)
            apply()
        }
    }

    fun loggedAsUser() {
        with(sharedPreferences.edit()) {
            putBoolean("guest", false)
            apply()
        }
    }

    fun isGuest(): Boolean {
        return sharedPreferences.getBoolean("guest", false)
    }

}
