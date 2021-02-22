package com.pollfish.mediation.mopub.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mopub.common.MoPub
import com.mopub.common.MoPubReward
import com.mopub.common.SdkConfiguration
import com.mopub.common.SdkInitializationListener
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.*
import com.pollfish.mediation.mopub.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SdkInitializationListener, MoPubRewardedAdListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showRewardedAdButton.setOnClickListener {
            MoPubRewardedAds.showRewardedAd("1db01b491c1746a18d290ed77084ab14")
        }

        val pollfishMediatedNetworkConfiguration = mutableMapOf<String, String>()
        val vungleAdapterConfiguration = mutableMapOf<String, String>()

        pollfishMediatedNetworkConfiguration["api_key"] = "01b4e6c2-77ca-4cbc-ad40-d66af5423ea4"

        val configuration = SdkConfiguration.Builder("1db01b491c1746a18d290ed77084ab14")
            .withAdditionalNetwork(PollfishAdapterConfiguration::class.java.name)
            .withMediationSettings(GooglePlayServicesRewardedVideo.GooglePlayServicesMediationSettings())
            .withMediatedNetworkConfiguration(
                PollfishAdapterConfiguration::class.java.name,
                pollfishMediatedNetworkConfiguration
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
        MoPubRewardedAds.loadRewardedAd("1db01b491c1746a18d290ed77084ab14")
    }

    override fun onRewardedAdClicked(adUnitId: String) {

    }

    override fun onRewardedAdClosed(adUnitId: String) {

    }

    override fun onRewardedAdCompleted(adUnitIds: Set<String?>, reward: MoPubReward) {

    }

    override fun onRewardedAdLoadFailure(adUnitId: String, errorCode: MoPubErrorCode) {
        binding.showRewardedAdButton.isEnabled = false
    }

    override fun onRewardedAdLoadSuccess(adUnitId: String) {
        binding.showRewardedAdButton.isEnabled = true
    }

    override fun onRewardedAdShowError(adUnitId: String, errorCode: MoPubErrorCode) {
        Log.d(this::class.java.name, "onRewardedAdShowError: $errorCode")
    }

    override fun onRewardedAdStarted(adUnitId: String) {

    }
}