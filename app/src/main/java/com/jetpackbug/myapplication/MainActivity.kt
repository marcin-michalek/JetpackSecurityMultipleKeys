package com.jetpackbug.myapplication

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File

class MainActivity : AppCompatActivity() {

    private val firstMasterKey by lazy {
        MasterKeys.getOrCreate(provideKeyGenParameters("aliasFirst"))
    }

    private val secondMasterKey by lazy {
        MasterKeys.getOrCreate(provideKeyGenParameters("aliasSecond"))
    }

    private val firstEncryptedFile by lazy {
        EncryptedFile.Builder(
            File(filesDir, "firstFile"),
            this,
            firstMasterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        )
            .build()
    }

    private val secondEncryptedFile by lazy {
        EncryptedFile.Builder(
            File(filesDir, "secondFile"),
            this,
            secondMasterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        )
            .build()
    }

    private fun provideKeyGenParameters(alias: String) =
        KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveFiles()
    }

    private fun saveFiles() {
        firstEncryptedFile.openFileOutput().use {
            it.write(ByteArray(1) { Byte.MIN_VALUE })
        }

        // if I comment this save it works fine
        // as soon as I uncomment it the app crashes
        secondEncryptedFile.openFileOutput().use {
            it.write(ByteArray(1) { Byte.MIN_VALUE })
        }
    }
}
