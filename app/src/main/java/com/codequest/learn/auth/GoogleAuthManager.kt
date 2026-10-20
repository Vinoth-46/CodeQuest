package com.codequest.learn.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GoogleUserData(
    val idToken: String?,
    val googleId: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String
)

class GoogleAuthManager {

    suspend fun signInWithGoogle(
        activity: Activity,
        serverClientId: String
    ): Result<GoogleUserData> = withContext(Dispatchers.Main) {
        try {
            if (serverClientId.isBlank()) {
                return@withContext Result.failure(
                    Exception("Please provide a Google Web Client ID in Settings to enable Google Sign-In, or use Quick Player Sign-In.")
                )
            }

            val credentialManager = CredentialManager.create(activity)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId.trim())
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            handleSignInResult(result)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Google Sign-In canceled or failed: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse): Result<GoogleUserData> {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                return Result.success(
                    GoogleUserData(
                        idToken = googleIdTokenCredential.idToken,
                        googleId = googleIdTokenCredential.id,
                        email = googleIdTokenCredential.id,
                        displayName = googleIdTokenCredential.displayName ?: "CodeQuest Challenger",
                        avatarUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: ""
                    )
                )
            } catch (e: GoogleIdTokenParsingException) {
                return Result.failure(Exception("Failed to parse Google ID token: ${e.message}"))
            }
        }
        return Result.failure(Exception("Unexpected credential type: ${credential.type}"))
    }
}
