package com.namazvaktiglobal.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.concurrent.TimeUnit
import kotlin.math.max

class AdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var lastInterstitialShownAt = 0L
    private var lastInterstitialLoadedAt = 0L

    fun bannerAdUnitId(): String = if (BuildConfig.DEBUG) {
        TEST_BANNER_ID
    } else {
        PROD_BANNER_ID
    }

    fun interstitialAdUnitId(): String = if (BuildConfig.DEBUG) {
        TEST_INTERSTITIAL_ID
    } else {
        PROD_INTERSTITIAL_ID
    }

    fun rewardedAdUnitId(): String = if (BuildConfig.DEBUG) {
        TEST_REWARDED_ID
    } else {
        PROD_REWARDED_ID
    }

    fun loadInterstitial() {
        if (interstitialAd != null && !isInterstitialExpired()) {
            return
        }
        InterstitialAd.load(
            context,
            interstitialAdUnitId(),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    lastInterstitialLoadedAt = System.currentTimeMillis()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, minIntervalMinutes: Long, onDismissed: () -> Unit) {
        val now = System.currentTimeMillis()
        val minIntervalMs = TimeUnit.MINUTES.toMillis(max(1, minIntervalMinutes))
        if (now - lastInterstitialShownAt < minIntervalMs) {
            onDismissed()
            return
        }
        val ad = interstitialAd
        if (ad == null) {
            onDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                lastInterstitialShownAt = System.currentTimeMillis()
                interstitialAd = null
                onDismissed()
                loadInterstitial()
            }
        }
        ad.show(activity)
    }

    fun loadRewarded() {
        RewardedAd.load(
            context,
            rewardedAdUnitId(),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    fun showRewarded(activity: Activity, onReward: (RewardItem) -> Unit, onDismissed: () -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            onDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                onDismissed()
                loadRewarded()
            }
        }
        ad.show(activity) { reward ->
            onReward(reward)
        }
    }

    private fun isInterstitialExpired(): Boolean {
        return System.currentTimeMillis() - lastInterstitialLoadedAt > TimeUnit.HOURS.toMillis(1)
    }

    companion object {
        const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"

        const val PROD_BANNER_ID = "REPLACE_WITH_PROD_BANNER"
        const val PROD_INTERSTITIAL_ID = "REPLACE_WITH_PROD_INTERSTITIAL"
        const val PROD_REWARDED_ID = "REPLACE_WITH_PROD_REWARDED"
    }
}
