package com.namazvaktiglobal.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.namazvaktiglobal.ads.AdManager
import com.namazvaktiglobal.ads.ConsentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    val adManager: AdManager,
    val consentManager: ConsentManager
) : ViewModel()
