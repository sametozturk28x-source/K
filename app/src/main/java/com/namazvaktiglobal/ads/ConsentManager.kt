package com.namazvaktiglobal.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ConsentManager(private val context: Context) {
    private val consentInformation: ConsentInformation = UserMessagingPlatform.getConsentInformation(context)

    suspend fun requestConsent(activity: Activity): Boolean {
        val params = ConsentRequestParameters.Builder().build()
        return suspendCancellableCoroutine { continuation ->
            consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        activity
                    ) { formError ->
                        continuation.resume(formError == null)
                    }
                },
                { continuation.resume(false) }
            )
        }
    }

    fun canRequestAds(): Boolean = consentInformation.canRequestAds()
}
