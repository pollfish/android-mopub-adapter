package com.mopub.mobileads

import android.app.Activity
import android.content.Context
import com.mopub.common.LifecycleListener
import com.mopub.common.MoPub
import com.mopub.common.MoPubReward
import com.mopub.common.logging.MoPubDefaultLogger
import com.mopub.common.logging.MoPubLog
import com.pollfish.main.PollFish

class PollfishMoPubAdapter(): BaseAd() {

    val TAG = this.javaClass.simpleName

    override fun onInvalidate() {}

    override fun getLifecycleListener(): LifecycleListener? {
        return null
    }

    override fun getAdNetworkId(): String {
        return ""
    }

    override fun checkAndInitializeSdk(launcherActivity: Activity, adData: AdData): Boolean {
        return false
    }

    override fun load(context: Context, adData: AdData) {

        if (PollFish.isPollfishPanelOpen()) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG, "Pollfish Survey Panel already visible")
            mInteractionListener?.onAdFailed(MoPubErrorCode.AD_SHOW_ERROR)
            return
        }

        if (context !is Activity) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG, "Context is not an Activity. Pollfish requires an Activity context to load surveys")
            mInteractionListener?.onAdFailed(MoPubErrorCode.AD_SHOW_ERROR)
            return
        }

        if (adData.extras.isEmpty()) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG, "Pollfish MoPub Adapter initialized with empty data")
            mInteractionListener?.onAdFailed(MoPubErrorCode.AD_SHOW_ERROR)
            return
        }

        val apiKey = adData.extras["api_key"]
        val requestUUID = adData.extras["request_uuid"]
        val offerwallMode = adData.extras["offerwall"].toBoolean()
        val releaseMode = adData.extras["release"].toBoolean()

        if (apiKey == null) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG, "Pollfish SDK Failed: Missing Pollfish API key")
            mInteractionListener?.onAdFailed(MoPubErrorCode.AD_SHOW_ERROR)
            return
        }

        val params = PollFish.ParamsBuilder(apiKey)
                .releaseMode(releaseMode)
                .offerWallMode(offerwallMode)
                .apply {
                    (requestUUID)?.let {
                        this.requestUUID(it)
                    }
                }
                .pollfishOpenedListener {
                    MoPubLog.log(MoPubLog.AdapterLogEvent.DID_APPEAR)
                    mInteractionListener?.onAdShown()
                    mInteractionListener?.onAdImpression()
                }
                .pollfishClosedListener {
                    MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR)
                    mInteractionListener?.onAdDismissed()
                }
                .pollfishUserNotEligibleListener {
                    // TODO: Log
                    MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Pollfish Surveys Not Available")
                }
                .pollfishUserRejectedSurveyListener {
                    // TODO: Log
                }
                .pollfishSurveyNotAvailableListener {
                    MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_FAILED, TAG, "Pollfish Surveys Not Available")
                    mInteractionListener?.onAdFailed(MoPubErrorCode.AD_NOT_AVAILABLE)
                }
                .pollfishReceivedSurveyListener {
//                    mInteractionListener.onAdLo
                    MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, TAG, "Pollfish Survey Received")
                }
                .pollfishCompletedSurveyListener {
                    MoPubLog.log(MoPubLog.AdapterLogEvent.SHOULD_REWARD, TAG, "Pollfish Survey Completed")
                    val reward = MoPubReward.success(it?.rewardName ?: "", it?.rewardValue ?: 0)
                    mInteractionListener?.onAdComplete(reward)
                }
                .build()

        PollFish.initWith(context, params)

        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG)
    }

    override fun show() {
        PollFish.show()
    }
}