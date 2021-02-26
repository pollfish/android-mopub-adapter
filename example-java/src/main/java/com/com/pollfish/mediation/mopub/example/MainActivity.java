package com.com.pollfish.mediation.mopub.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedAdListener;
import com.mopub.mobileads.MoPubRewardedAds;
import com.mopub.mobileads.PollfishAdapterConfiguration;
import com.mopub.mobileads.PollfishMoPubAdapter;

import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SdkInitializationListener, MoPubRewardedAdListener {

    private static final String TAG = "MainActivity";
    private Button showAdButton;
    private String adUnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showAdButton = findViewById(R.id.showRewardedAdButton);

        adUnitId = getString(R.string.rewarded_video_unit_id);

        initializeMoPub();
    }

    private void initializeMoPub() {
        SdkConfiguration configuration = new SdkConfiguration.Builder(adUnitId)
                .withAdditionalNetwork(PollfishAdapterConfiguration.class.getName())
                .withMediationSettings(
                        PollfishMoPubAdapter.PollfishMoPubMediationSettings.create(null, null, false, null)
                )
                .withMediatedNetworkConfiguration(PollfishAdapterConfiguration.class.getName(), new HashMap<>())
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .build();

        MoPub.initializeSdk(this, configuration, this);
    }

    private void requestAd() {
        showAdButton.setEnabled(false);
        MoPubRewardedAds.setRewardedAdListener(this);
        MoPubRewardedAds.loadRewardedAd(adUnitId);
    }

    @Override
    public void onInitializationFinished() {
        Log.d(TAG, "onInitializationFinished");
        requestAd();
    }

    @Override
    public void onRewardedAdClicked(@NonNull String s) {
        Log.d(TAG, "onRewardedAdClicked");
    }

    @Override
    public void onRewardedAdClosed(@NonNull String s) {
        Log.d(TAG, "onRewardedAdClosed");
        requestAd();
    }

    @Override
    public void onRewardedAdCompleted(@NonNull Set<String> set, MoPubReward reward) {
        Log.d(TAG, "onRewardedAdCompleted - Reward: " + reward.getAmount() + " " + reward.getLabel());
    }

    @Override
    public void onRewardedAdLoadFailure(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
        showAdButton.setEnabled(false);
        Log.d(TAG, "onRewardedAdLoadFailure");
    }

    @Override
    public void onRewardedAdLoadSuccess(@NonNull String s) {
        showAdButton.setEnabled(true);
        Log.d(TAG, "onRewardedAdLoadSuccess");
    }

    @Override
    public void onRewardedAdShowError(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
        Log.d(TAG, "onRewardedAdShowError: " + moPubErrorCode);
    }

    @Override
    public void onRewardedAdStarted(@NonNull String s) {
        Log.d(TAG, "onRewardedAdStarted");
    }

    public void onShowRewardedAd(View view) {
        MoPubRewardedAds.showRewardedAd(adUnitId);
    }

}