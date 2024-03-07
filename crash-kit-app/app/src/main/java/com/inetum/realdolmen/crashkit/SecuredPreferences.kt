package com.inetum.realdolmen.crashkit

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

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

    fun putString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun removeString(key: String){
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }

    fun getJwtToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun putJwtToken (value: String){
        with(sharedPreferences.edit()){
            putString("jwt_token", value)
                .commit()
        }
    }

    fun deleteJwtToken (){
        with(sharedPreferences.edit()){
            remove("jwt_token")
                .apply()
        }
    }

    fun rememberLogin(){
        with(sharedPreferences.edit()){
            putBoolean("remember", true)
            apply()
        }
    }

    fun isLoginRemembered(): Boolean {
        return sharedPreferences.getBoolean("remember", false)
    }

    fun loggedAsGuest(){
        with(sharedPreferences.edit()){
            putBoolean("guest", true)
            apply()
        }
    }

    fun loggedAsUser(){
        with(sharedPreferences.edit()){
            putBoolean("guest", false)
            apply()
        }
    }

    fun isGuest(): Boolean {
        return sharedPreferences.getBoolean("guest", false)
    }

}
