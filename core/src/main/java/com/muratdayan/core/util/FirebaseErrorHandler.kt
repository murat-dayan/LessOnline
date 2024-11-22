package com.muratdayan.core.util

import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.muratdayan.core.R
import java.io.IOException

object FirebaseErrorHandler {

    fun getErrorMessage(context: Context, exception: Throwable): String {
        return when (exception) {
            is FirebaseAuthException -> handleAuthException(context, exception)
            is FirebaseFirestoreException -> handleFirestoreException(context, exception)
            is FirebaseException -> context.getString(R.string.error_firebase_unexpected)
            is IOException -> context.getString(R.string.error_network_issue)
            else -> context.getString(R.string.error_unknown, exception.message)
        }
    }

    private fun handleAuthException(context: Context, exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> context.getString(R.string.error_invalid_email)
            "ERROR_USER_NOT_FOUND" -> context.getString(R.string.error_user_not_found)
            "ERROR_WRONG_PASSWORD" -> context.getString(R.string.error_wrong_password)
            "ERROR_EMAIL_ALREADY_IN_USE" -> context.getString(R.string.error_email_in_use)
            "ERROR_WEAK_PASSWORD" -> context.getString(R.string.error_weak_password)
            "ERROR_OPERATION_NOT_ALLOWED" -> context.getString(R.string.error_operation_not_allowed)
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> context.getString(R.string.error_account_exists_with_different_credential)
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> context.getString(R.string.error_credential_already_in_use)
            "ERROR_REQUIRES_RECENT_LOGIN" -> context.getString(R.string.error_requires_recent_login)
            "ERROR_PROVIDER_ALREADY_LINKED" -> context.getString(R.string.error_provider_already_linked)
            "ERROR_NO_SUCH_PROVIDER" -> context.getString(R.string.error_no_such_provider)
            "ERROR_INVALID_USER_TOKEN" -> context.getString(R.string.error_invalid_user_token)
            "ERROR_USER_TOKEN_EXPIRED" -> context.getString(R.string.error_user_token_expired)
            "ERROR_TOO_MANY_REQUESTS" -> context.getString(R.string.error_too_many_requests)
            "ERROR_NETWORK_REQUEST_FAILED" -> context.getString(R.string.error_network_request_failed)
            "ERROR_INTERNAL_ERROR" -> context.getString(R.string.error_internal_error)
            "ERROR_USER_DISABLED" -> context.getString(R.string.error_user_disabled)
            "ERROR_INVALID_CREDENTIAL" -> context.getString(R.string.error_invalid_credential)
            else -> context.getString(R.string.error_auth_generic, exception.message)
        }
    }

    private fun handleFirestoreException(context: Context, exception: FirebaseFirestoreException): String {
        return when (exception.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> context.getString(R.string.error_firestore_permission_denied)
            FirebaseFirestoreException.Code.UNAVAILABLE -> context.getString(R.string.error_firestore_unavailable)
            FirebaseFirestoreException.Code.ABORTED -> context.getString(R.string.error_firestore_aborted)
            else -> context.getString(R.string.error_firestore_generic, exception.message)
        }
    }


}