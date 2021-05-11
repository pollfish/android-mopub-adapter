# Pollfish Android MoPub Mediation Adapter

MoPub Mediation Adapter for Android apps looking to load and show Rewarded Surveys from Pollfish in the same waterfall with other Rewarded Ads.

> **Note:** A detailed step by step guide is provided on how to integrate can be found [here](https://www.pollfish.com/docs/android-mopub-adapter) 

<br/>

## Step 1: Add Pollfish MoPub Adapter to your project

Import Pollfish MoPub adapter **.aar** file as it can be found in the **pollfish-mopub-aar** folder, to your project libraries  

If you are using Android Studio, right click on your project and select New Module. Then select Import .JAR or .AAR Package option and from the file browser locate Pollfish MoPub Adapter aar file. Right click again on your project and in the Module Dependencies tab choose to add Pollfish module that you recently added, as a dependency.

**OR**

#### **Retrieve Pollfish MoPub Adapter through maven()**

Retrieve Pollfish through **maven()** with gradle by adding the following line in your project **build.gradle** (not the top level one, the one under 'app') in  dependencies section:  

```groovy
dependencies {
    implementation 'com.pollfish.mediation:pollfish-mopub:6.1.0.0'
}
```

<br/>

## Step 2: Request for a RewardedAd

Import `com.mopub.common` and `com.mopub.mobileads` packages

<span style="text-decoration:underline">Kotlin</span>

```kotlin
import com.mopub.common.*
import com.mopub.mobileads.*
```

<span style="text-decoration:underline">Java</span>

```java
import com.mopub.common.*;
import com.mopub.mobileads.*;
```

Implement `SdkInitializationListener` interface to listen for MoPub SDK intialisation completion

<span style="text-decoration:underline">Kotlin</span>

```kotlin
class MainActivity : AppCompatActivity(), SdkInitializationListener {
    
     override fun onInitializationFinished() {}

}
```

<span style="text-decoration:underline">Java</span>

```java
public class MainActivity extends AppCompatActivity implements SdkInitializationListener {
    
    @Override
    public void onInitializationFinished() {}

}
```

Initialize MoPub SDK and pass `PollfishAdapterConfiguration` class name on the initialisation configuration.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
val configuration = SdkConfiguration.Builder("AD_UNIT_ID")
    .withAdditionalNetwork(PollfishAdapterConfiguration::class.java.name)
    .withMediatedNetworkConfiguration(
        PollfishAdapterConfiguration::class.java.name, emptyMap()
    )
    .build()

MoPub.initializeSdk(this, configuration, this)
```

<span style="text-decoration:underline">Java</span>

```java
SdkConfiguration configuration = new SdkConfiguration.Builder("AD_UNIT_ID")
    .withAdditionalNetwork(PollfishAdapterConfiguration.class.getName())
    .withMediatedNetworkConfiguration(
        PollfishAdapterConfiguration.class.getName(), new HashMap<>())
    .build();

MoPub.initializeSdk(this, configuration, this);
```

Request a RewardedAd from MoPub using the Pollfish configuration params that you provided on MoPub's Web UI. If no configuration is provided or if you want to override any of those params provided in the Web UI please see step 3.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
MoPubRewardedAds.setRewardedAdListener(this)
MoPubRewardedAds.loadRewardedAd("AD_UNIT_ID")
```

<span style="text-decoration:underline">Java</span>

```java
MoPubRewardedAds.setRewardedAdListener(this);
MoPubRewardedAds.loadRewardedAd("AD_UNIT_ID");
```

Implement `MoPubRewardedAdListener` to get notified when the rewarded ad is ready to be shown

<span style="text-decoration:underline">Kotlin</span>

```kotlin
class MainActivity : AppCompatActivity(), 
    ...,
    MoPubRewardedAdListener {

    override fun onRewardedAdClicked(adUnitId: String) {}

    override fun onRewardedAdClosed(adUnitId: String) {}

    override fun onRewardedAdCompleted(adUnitIds: Set<String?>, reward: MoPubReward) {}

    override fun onRewardedAdLoadFailure(adUnitId: String, errorCode: MoPubErrorCode) {}

    override fun onRewardedAdLoadSuccess(adUnitId: String) {}

    override fun onRewardedAdShowError(adUnitId: String, errorCode: MoPubErrorCode) {}

    override fun onRewardedAdStarted(adUnitId: String) {}

}
```

<span style="text-decoration:underline">Java</span>

```java
public class MainActivity extends AppCompatActivity implements MoPubRewardedAdListener {

    @Override
    public void onRewardedAdClicked(String s) {}

    @Override
    public void onRewardedAdClosed(String s) {}

    @Override
    public void onRewardedAdCompleted(Set<String> set, MoPubReward reward) {}

    @Override
    public void onRewardedAdLoadFailure(String s, MoPubErrorCode moPubErrorCode) {}

    @Override
    public void onRewardedAdLoadSuccess(String s) {}

    @Override
    public void onRewardedAdShowError(String s, MoPubErrorCode moPubErrorCode) {}

    @Override
    public void onRewardedAdStarted(String s) {}

}
```

When the Rewarded Ad is ready present the ad by invoking `showRewardedAd`

<span style="text-decoration:underline">Kotlin</span>

```kotlin
MoPubRewardedAds.showRewardedAd("AD_UNIT_ID")
```

<span style="text-decoration:underline">Java</span>

```java
MoPubRewardedAds.showRewardedAd("AD_UNIT_ID");
```

## Step 3: Use and control Pollfish MoPub Adapter in your Rewarded Ad Unit 

Pollfish MoPub Adapter provides different options that you can use to control the behaviour of Pollfish SDK.

<br/>

Below you can see how to initialise **`PollfishMoPubMediationSettings`**  that is used to configure the behaviour of Pollfish SDK.

<br/>

```kotlin
PollfishMoPubAdapter.PollfishMoPubMediationSettings
    .create(apiKey: "API_KEY", 
        requestUUID: "REQUEST_UUID", 
        releaseMode: true,
        offerwallMode: true)
```

No | Description
------------ | -------------
3.1 | **`apikey`** <br/> Sets Pollfish SDK API key as provided by Pollfish
3.2 | **`requestUUID`** <br/> Sets a unique id to identify a user and be passed through server-to-server callbacks
3.3 | **`releaseMode`** <br/> Sets Pollfish SDK to Developer or Release mode
3.4 | **`offerwallMode`** <br/> Sets Pollfish SDK to Offerwall Mode

<br/>

### 3.1 `apiKey`

Pollfish API Key as provided by Pollfish on  [Pollfish Dashboard](https://www.pollfish.com/publisher/) after you sign up to the platform. If you have already specified Pollfish API Key on MoPub's UI, this param will override the one defined on Web UI.

### 3.2 `requestUUID`

Sets a unique id to identify a user and be passed through server-to-server callbacks on survey completion. 

In order to register for such callbacks you can set up your server URL on your app's page on Pollfish Developer Dashboard and then pass your requestUUID through ParamsBuilder object during initialization. On each survey completion you will receive a callback to your server including the requestUUID param passed.

If you would like to read more on Pollfish s2s callbacks you can read the documentation [here](https://www.pollfish.com/docs/s2s)

### 3.3 `releaseMode`

Sets Pollfish SDK to Developer or Release mode.

*   **Developer mode** is used to show to the developer how Pollfish surveys will be shown through an app (useful during development and testing).
*   **Release mode** is the mode to be used for a released app in any app store (start receiving paid surveys).

Pollfish MoPub Adapter runs Pollfish SDK in release mode by default. If you would like to test with Test survey, you should set release mode to fasle.

### 3.4 `offerwall_mode`

Enables offerwall mode. If not set, one single survey is shown each time.

<br/>

Below you can see an example on how you can pass info to Pollfish MoPub Adapter connfiguration:

<span style="text-decoration:underline">Kotlin</span>

```kotlin
val configuration = SdkConfiguration.Builder(adUnitId)
    ...
    .withMediationSettings(
        PollfishMoPubAdapter.PollfishMoPubMediationSettings
            .create(
                apiKey = "YOUR_POLLFISH_API_KEY",
                requestUUID = "REQUEST_UUID",
                releaseMode = true,
                offerwallMode = false
            )
    )
    .build()

MoPub.initializeSdk(this, configuration, this)
```

<span style="text-decoration:underline">Java</span>

```java
SdkConfiguration configuration = new SdkConfiguration.Builder(adUnitId)
    ...
    .withMediationSettings(
        PollfishMoPubAdapter.PollfishMoPubMediationSettings
            .create("YOUR_POLLFISH_API_KEY", "REQUEST_UUID", true, false)
    )
    .build()

MoPub.initializeSdk(this, configuration, this);
```

### Step 4: Publish 

If you everything worked fine during the previous steps, you should turn Pollfish to release mode and publish your app.

> **Note:** After you take your app live, you should request your account to get verified through Pollfish Dashboard in the App Settings area.

> **Note:** There is an option to show **Standalone Demographic Questions** needed for Pollfish to target users with surveys even when no actually surveys are available. Those surveys do not deliver any revenue to the publisher (but they can increase fill rate) and therefore if you do not want to show such surveys in the Waterfall you should visit your **App Settings** are and disable that option.
