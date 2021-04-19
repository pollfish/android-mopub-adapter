package com.mopub.mobileads

import android.content.Context
import com.mopub.common.BaseAdapterConfiguration
import com.mopub.common.OnNetworkInitializationFinishedListener

class PollfishAdapterConfiguration: BaseAdapterConfiguration() {

    override fun getAdapterVersion(): String {
        return PollfishConstants.POLLFISH_ADAPTER_VERSION
    }

    override fun getBiddingToken(context: Context): String = ""

    override fun getMoPubNetworkName(): String {
        return PollfishConstants.POLLFISH_NETWORK_NAME
    }

    override fun getNetworkSdkVersion(): String {
        return PollfishConstants.POLLFISH_SDK_VERSION
    }

    override fun initializeNetwork(
        context: Context,
        configuration: MutableMap<String, String>?,
        listener: OnNetworkInitializationFinishedListener
    ) {}

}