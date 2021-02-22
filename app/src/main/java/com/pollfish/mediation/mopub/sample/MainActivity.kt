package com.pollfish.mediation.mopub.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mopub.common.*
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.*
import com.pollfish.mediation.mopub.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SdkInitializationListener, MoPubRewardedAdListener {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adUnitId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adUnitId = getString(R.string.rewarded_video_unit_id)

        initializeMoPub()
    }

    private fun initializeMoPub() {
        val vungleAdapterConfiguration = mutableMapOf<String, String>()

        val configuration = SdkConfiguration.Builder(adUnitId)
            .withAdditionalNetwork(PollfishAdapterConfiguration::class.java.name)
            .withMediationSettings(GooglePlayServicesRewardedVideo.GooglePlayServicesMediationSettings())
            .withMediatedNetworkConfiguration(
                PollfishAdapterConfiguration::class.java.name, emptyMap()
            )
            .withMediatedNetworkConfiguration(
                VungleAdapterConfiguration::class.java.name,
                vungleAdapterConfiguration
            )
            .withLogLevel(MoPubLog.LogLevel.DEBUG)
            .build()

        MoPub.initializeSdk(this, configuration, this)
    }

    override fun onInitializationFinished() {
        MoPubRewardedAds.setRewardedAdListener(this)
        MoPubRewardedAds.loadRewardedAd(adUnitId)
        Log.d(TAG, "onInitializationFinished")
    }

    override fun onRewardedAdClicked(adUnitId: String) {
        Log.d(TAG, "onRewardedAdClicked")
    }

    override fun onRewardedAdClosed(adUnitId: String) {
        Log.d(TAG, "onRewardedAdClosed")
    }

    override fun onRewardedAdCompleted(adUnitIds: Set<String?>, reward: MoPubReward) {
        Log.d(TAG, "onRewardedAdCompleted")
    }

    override fun onRewardedAdLoadFailure(adUnitId: String, errorCode: MoPubErrorCode) {
        binding.showRewardedAdButton.isEnabled = false
        Log.d(TAG, "onRewardedAdLoadFailure")
    }

    override fun onRewardedAdLoadSuccess(adUnitId: String) {
        binding.showRewardedAdButton.isEnabled = true
        Log.d(TAG, "onRewardedAdLoadSuccess")
    }

    override fun onRewardedAdShowError(adUnitId: String, errorCode: MoPubErrorCode) {
        Log.d(TAG, "onRewardedAdShowError: $errorCode")
    }

    override fun onRewardedAdStarted(adUnitId: String) {
        Log.d(TAG, "onRewardedAdStarted")
    }

    fun onShowRewardedAd(view: View) {
        MoPubRewardedAds.showRewardedAd(adUnitId)
    }

}