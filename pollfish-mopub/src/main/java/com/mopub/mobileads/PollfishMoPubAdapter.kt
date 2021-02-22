package com.mopub.mobileads

import android.app.Activity
import android.content.Context
import com.mopub.common.LifecycleListener
import com.mopub.common.MediationSettings
import com.mopub.common.MoPubReward
import com.mopub.common.logging.MoPubLog
import com.pollfish.main.PollFish

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
        if (PollFish.isPollfishPanelOpen()) {
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
            adData.extras[PollfishConstants.POLLFISH_RELEASE_MODE_EXTRA_KEY].toBoolean()
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

        val params = PollFish.ParamsBuilder(apiKey)
            .releaseMode(releaseMode)
            .offerWallMode(offerwallMode)
            .apply {
                (requestUUID)?.let {
                    this.requestUUID(it)
                }
            }
            .pollfishOpenedListener {
                MoPubLog.log(MoPubLog.AdapterLogEvent.DID_APPEAR, TAG)
                mInteractionListener?.onAdShown()
                mInteractionListener?.onAdImpression()
            }
            .pollfishClosedListener {
                MoPubLog.log(MoPubLog.AdapterLogEvent.DID_DISAPPEAR, TAG)
                mInteractionListener?.onAdDismissed()
            }
            .pollfishUserNotEligibleListener {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Pollfish Surveys Not Available")
                mInteractionListener?.onAdFailed(MoPubErrorCode.NETWORK_NO_FILL)
            }
            .pollfishUserRejectedSurveyListener {
                MoPubLog.log(
                    MoPubLog.AdapterLogEvent.CUSTOM,
                    TAG,
                    "User Rejected Survey"
                )
                mInteractionListener?.onAdDismissed()
            }
            .pollfishSurveyNotAvailableListener {
                MoPubLog.log(
                    MoPubLog.AdapterLogEvent.LOAD_FAILED,
                    TAG,
                    "Pollfish Surveys Not Available"
                )
                mLoadListener?.onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL)
            }
            .pollfishReceivedSurveyListener {
                MoPubLog.log(
                    MoPubLog.AdapterLogEvent.LOAD_SUCCESS,
                    TAG,
                    "Pollfish Survey Received"
                )
                mLoadListener?.onAdLoaded()
            }
            .pollfishCompletedSurveyListener {
                MoPubLog.log(
                    MoPubLog.AdapterLogEvent.SHOULD_REWARD,
                    TAG,
                    "Pollfish Survey Completed"
                )

                mInteractionListener?.onAdComplete(
                    it?.let {
                        MoPubReward.success(
                            it.rewardName,
                            it.rewardValue
                        )
                    })
            }
            .rewardMode(true)
            .build()

        PollFish.initWith(context, params)
        MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED, TAG)
    }

    override fun show() {
        PollFish.show()
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