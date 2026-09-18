package com.example.data.crypto

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private const val SALT = "FieldnoteSecureZeroKnowledgeSalt2026"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256

    private var masterPassphrase: String = "FieldnotePrivacyVaultKey2026"

    fun setMasterPassphrase(passphrase: String) {
        if (passphrase.isNotBlank()) {
            masterPassphrase = passphrase
        }
    }

    fun getMasterPassphrasePreview(): String {
        return "••••••••" + masterPassphrase.takeLast(2)
    }

    private fun getSecretKey(): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(masterPassphrase.toCharArray(), SALT.toByteArray(), ITERATIONS, KEY_LENGTH)
        val secretKey = factory.generateSecret(spec)
        return SecretKeySpec(secretKey.encoded, "AES")
    }

    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return ""
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val iv = ByteArray(IV_LENGTH_BYTE)
            SecureRandom().nextBytes(iv)
            val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec)
            val cipherBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            val ivBase64 = Base64.getEncoder().encodeToString(iv)
            val cipherBase64 = Base64.getEncoder().encodeToString(cipherBytes)
            return "ENC:$ivBase64:$cipherBase64"
        } catch (e: Exception) {
            return plainText
        }
    }

    fun decrypt(cipherText: String): String {
        if (!cipherText.startsWith("ENC:")) return cipherText
        try {
            val parts = cipherText.removePrefix("ENC:").split(":")
            if (parts.size != 2) return cipherText
            val iv = Base64.getDecoder().decode(parts[0])
            val cipherBytes = Base64.getDecoder().decode(parts[1])

            val cipher = Cipher.getInstance(ALGORITHM)
            val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            val decryptedBytes = cipher.doFinal(cipherBytes)
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            return "[Encrypted - Decryption Failed]"
        }
    }

    fun isEncrypted(text: String): Boolean {
        return text.startsWith("ENC:")
    }
}
