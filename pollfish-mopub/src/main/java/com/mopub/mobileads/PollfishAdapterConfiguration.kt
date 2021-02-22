package com.mopub.mobileads

import android.content.Context
import com.mopub.common.BaseAdapterConfiguration
import com.mopub.common.OnNetworkInitializationFinishedListener

class PollfishAdapterConfiguration(): BaseAdapterConfiguration() {

    override fun getAdapterVersion(): String {
        return "5.5.5.1"
    }

    override fun getBiddingToken(context: Context): String? {
        return null
    }

    override fun getMoPubNetworkName(): String {
        return "pollfish"
    }

    override fun getNetworkSdkVersion(): String {
        return "5.5.5"
    }

    override fun initializeNetwork(
        context: Context,
        configuration: MutableMap<String, String>?,
        listener: OnNetworkInitializationFinishedListener
    ) {
        // NO-OP
    }
}