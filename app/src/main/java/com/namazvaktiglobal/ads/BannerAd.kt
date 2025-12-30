package com.namazvaktiglobal.ads

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier, adUnitId: String) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                val displayMetrics = context.resources.displayMetrics
                val adWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    adWidth
                )
                setAdSize(adSize)
                this.adUnitId = adUnitId
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
