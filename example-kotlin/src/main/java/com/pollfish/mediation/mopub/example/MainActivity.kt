package com.pollfish.mediation.mopub.example

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mopub.common.*
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.*
import com.pollfish.mediation.mopub.example.databinding.ActivityMainBinding

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
        val configuration = SdkConfiguration.Builder(adUnitId)
            .withAdditionalNetwork(PollfishAdapterConfiguration::class.java.name)
            .withMediationSettings(
                PollfishMoPubAdapter.PollfishMoPubMediationSettings.create(releaseMode = false)
            )
            .withMediatedNetworkConfiguration(
                PollfishAdapterConfiguration::class.java.name, emptyMap()
            )
            .withLogLevel(MoPubLog.LogLevel.DEBUG)
            .build()

        MoPub.initializeSdk(this, configuration, this)
    }

    private fun requestAd() {
        binding.showRewardedAdButton.isEnabled = false
        MoPubRewardedAds.setRewardedAdListener(this)
        MoPubRewardedAds.loadRewardedAd(adUnitId)
    }

    override fun onInitializationFinished() {
        Log.d(TAG, "onInitializationFinished")
        requestAd()
    }

    override fun onRewardedAdClicked(adUnitId: String) {
        Log.d(TAG, "onRewardedAdClicked")
    }

    override fun onRewardedAdClosed(adUnitId: String) {
        Log.d(TAG, "onRewardedAdClosed")
        requestAd()
    }

    override fun onRewardedAdCompleted(adUnitIds: Set<String?>, reward: MoPubReward) {
        Log.d(TAG, "onRewardedAdCompleted - Reward: ${reward.amount} ${reward.label}")
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