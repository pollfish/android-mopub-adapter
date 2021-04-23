package com.mopub.mobileads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.mopub.common.LifecycleListener
import com.mopub.common.MediationSettings
import com.mopub.common.MoPubReward
import com.mopub.common.logging.MoPubLog
import com.pollfish.Pollfish
import com.pollfish.builder.*;
import com.pollfish.callback.*

class PollfishMoPubAdapter : BaseAd() {

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
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.d(TAG, "Pollfish surveys will not run on targets lower than 21");
            mLoadListener?.onAdLoadFailed(MoPubErrorCode.CANCELLED)
            return
        }

        if (Pollfish.isPollfishPanelOpen()) {
            MoPubLog.log(
                MoPubLog.AdapterLogEvent.LOAD_FAILED,
                TAG,
                "Pollfish Survey Panel already visible"
            )
            mLoadListener?.onAdLoadFailed(MoPubErrorCode.AD_SHOW_ERROR)
            return
        }

        if (context !is Activity) {
            MoPubLog.log(
                MoPubLog.AdapterLogEvent.LOAD_FAILED,
                TAG,
                "Context is not an Activity. Pollfish requires an Activity context to load surveys"
            )
            mLoadListener?.onAdLoadFailed(MoPubErrorCode.CANCELLED)
            return
        }

        if (adData.extras.isEmpty()) {
            MoPubLog.log(
                MoPubLog.AdapterLogEvent.LOAD_FAILED,
                TAG,
                "Pollfish MoPub Adapter initialized with empty data"
            )
            mLoadListener?.onAdLoadFailed(MoPubErrorCode.CANCELLED)
            return
        }

        val apiKey = PollfishMoPubMediationSettings.apiKey
            ?: adData.extras[PollfishConstants.POLLFSIH_API_KEY_EXTRA_KEY]

        val requestUUID = PollfishMoPubMediationSettings.requestUUID
            ?: adData.extras[PollfishConstants.POLLFISH_REQUEST_UUID_EXTRA_KEY]

        val offerwallMode = PollfishMoPubMediationSettings.offerwallMode ?: run {
            adData.extras[PollfishConstants.POLLFISH_OFFERWALL_MODE_EXTRA_KEY].toBoolean()
        }

        val releaseMode = PollfishMoPubMediationSettings.releaseMode ?: run {
            adData.extras[PollfishConstants.POLLFISH_RELEASE_MODE_EXTRA_KEY]?.let {
                adData.extras[PollfishConstants.POLLFISH_RELEASE_MODE_EXTRA_KEY].toBoolean()
            } ?: true
        }

        if (apiKey == null) {
            MoPubLog.log(
                MoPubLog.AdapterLogEvent.LOAD_FAILED,
                TAG,
                "Pollfish SDK Failed: Missing Pollfish API key"
            )
            mLoadListener?.onAdLoadFailed(MoPubErrorCode.CANCELLED)
            return
        }

        val params = Params.Builder(apiKey)
            .releaseMode(releaseMode)
            .offerwallMode(offerwallMode)
            .apply {
                (requestUUID)?.let {
                    this.requestUUID(it)
                }
            }
            .pollfishOpenedListener(object : PollfishOpenedListener {
                override fun onPollfishOpened() {
                    MoPubLog.log(MoPubLog.AdapterLogEvent.DID_APPEAR, TAG)
                    mInteractionListener?.onAdShown()
                    mInteractionListener?.onAdImpression()
                }
            })
            .pollfishClosedListener(object : PollfishClosedListener {
                override fun onPollfishClosed() {
                    MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR, TAG)
                    mInteractionListener?.onAdDismissed()
                }
            })
            .pollfishUserNotEligibleListener(object : PollfishUserNotEligibleListener {
                override fun onUserNotEligible() {
                    MoPubLog.log(
                        MoPubLog.AdapterLogEvent.CUSTOM,
                        TAG,
                        "Pollfish Surveys Not Available"
                    )
                    mInteractionListener?.onAdFailed(MoPubErrorCode.AD_SHOW_ERROR)
                }
            })
            .pollfishUserRejectedSurveyListener(object : PollfishUserRejectedSurveyListener {
                override fun onUserRejectedSurvey() {
                    MoPubLog.log(
                        MoPubLog.AdapterLogEvent.CUSTOM,
                        TAG,
                        "User Rejected Survey"
                    )
                    mInteractionListener?.onAdDismissed()
                }
            })
            .pollfishSurveyNotAvailableListener(object : PollfishSurveyNotAvailableListener {
                override fun onPollfishSurveyNotAvailable() {
                    MoPubLog.log(
                        MoPubLog.AdapterLogEvent.LOAD_FAILED,
                        TAG,
                        "Pollfish Surveys Not Available"
                    )
                    mLoadListener?.onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL)
                }
            })
            .pollfishSurveyReceivedListener(object : PollfishSurveyReceivedListener {
                override fun onPollfishSurveyReceived(surveyInfo: SurveyInfo?) {
                    MoPubLog.log(
                        MoPubLog.AdapterLogEvent.LOAD_SUCCESS,
                        TAG,
                        "Pollfish Survey Received"
                    )
                    mLoadListener?.onAdLoaded()
                }
            })
            .pollfishSurveyCompletedListener(object : PollfishSurveyCompletedListener {
                override fun onPollfishSurveyCompleted(surveyInfo: SurveyInfo) {
                    MoPubLog.log(
                        MoPubLog.AdapterLogEvent.SHOULD_REWARD,
                        TAG,
                        "Pollfish Survey Completed"
                    )

                    mInteractionListener?.onAdComplete(
                        MoPubReward.success(
                            surveyInfo.rewardName ?: "",
                            surveyInfo.rewardValue ?: 0
                        )
                    )
                }
            })
            .rewardMode(true)
            .platform(Platform.MOPUB)
            .build()

        Pollfish.initWith(context, params)
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG)
    }

    override fun show() {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.d(TAG, "Pollfish surveys will not run on targets lower than 21");
            mLoadListener?.onAdLoadFailed(MoPubErrorCode.CANCELLED)
            return
        }

        Pollfish.show()
    }

    object PollfishMoPubMediationSettings : MediationSettings {

        @JvmStatic
        var apiKey: String? = null
            private set

        @JvmStatic
        var requestUUID: String? = null
            private set

        @JvmStatic
        var releaseMode: Boolean? = null
            private set

        @JvmStatic
        var offerwallMode: Boolean? = null
            private set

        @JvmStatic
        fun create(
            apiKey: String? = null,
            requestUUID: String? = null,
            releaseMode: Boolean? = null,
            offerwallMode: Boolean? = null
        ): PollfishMoPubMediationSettings {
            PollfishMoPubMediationSettings.apiKey = apiKey
            PollfishMoPubMediationSettings.requestUUID = requestUUID
            PollfishMoPubMediationSettings.releaseMode = releaseMode
            PollfishMoPubMediationSettings.offerwallMode = offerwallMode
            return this
        }

    }

}