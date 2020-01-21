# EveryPay Android SDK
* [Overview](https://github.com/UnifiedPaymentSolutions/everypay-android3#overview)
* [Integrating the SDK](https://github.com/UnifiedPaymentSolutions/everypay-android3#integrating_the_sdk)
  * [Add the SDK to your Android Studio project](https://github.com/UnifiedPaymentSolutions/everypay-android3#add-the-sdk-to-your-android-studio-project)
  * [Configure the SDK parameters](https://github.com/UnifiedPaymentSolutions/everypay-android3#configure-the-sdk-parameters)
  * [How to integrate Payment Flow](https://github.com/UnifiedPaymentSolutions/everypay-android3#how_to_integrate_payment_flow)
* [Required Android permissions](https://github.com/UnifiedPaymentSolutions/everypay-android3#required-android-permissions)
* [Theming the card input form](https://github.com/UnifiedPaymentSolutions/everypay-android3/blob/master/README.md#theming-the-card-input-form)

## Overview
 EveryPay SDK has two payment flows:

1. Alternative Payment Method (APM) Flow<br/>
On Card Payment's screen, a full-screen WebView is opened and the associated payment_link is opened inside this WebView<br/>
2. Card Payment Flow<br/>
APM screen presents a preconfigured Card Details form and user have to enter the information of user card to perform payment.


## Integrating the SDK

### Add the SDK to your Android Studio project

Add the following line to your `app/build.gradle` file:

```groovy
dependencies {
    ... Other dependencies ...
   compile 'com.everypay.sdk:android-sdk:3.0.0'
}
```
**NB! SDK minSdkVersion is 19 so it's supporting Android 4.4+**


Note that it goes into the app module build file (`app/build.gradle` or similar), NOT the project-wide build file (`./build.gradle`). If there is `apply plugin: 'com.android.application'` at the top of the file, it's probably the right one.

If you wish to download a copy of the SDK to add it to your project manually, then you can find the .aar library files at https://bintray.com/everypay/maven/android-sdk/view#files

### Configure the SDK parameters

**NOTE: EveryPay object initialization changed as of version 3.0.0. First you need to call initv3(API_USERNAME, EVERYPAY_HOST_URL, AMOUNT, CURRENCY )**

Create a new EveryPay object, for example in your payment activity onCreate():

```java
EveryPay.getInstance(CURRENT_ACTIVITY).initv3(API_USERNAME, EVERYPAY_HOST_URL, AMOUNT, CURRENCY);
```
CURRENT_ACTIVITY is the current activity which you want to call to EVERYPAY SDK.<br/>
API_NAME is the username of the Merchant.<br/>
EVERYPAY_HOST_URL is the host of Everypay GW i.e igw-demo.every-pay.com/ <br/>
AMOUNT is the transaction amount, use decimal number with 2 digit precision, e.g. 10.55 <br/>
CURRENCY is the currency of processing account <br/>

### How to integrate Payment Flow

When the user is ready to start and already configure SDK through initv3 method, we can start payment flow by:
```java
handlePayment(String LINK, String METHOD_SOURCE, String MOBILE_ACCESS_TOKEN)
```
This method will call SDK's activity ```PaymentActivity``` from your current activity by ```startActivityForResult```, where:<br/>
LINK is the payment link.<br/>
METHOD_SOURCE is type of payment flow.<br/>
MOBILE_ACCESS_TOKEN is the token only use in Card Payment Flow.<br/>
This SDK has two payment flows:

1. ```Card Payment Flow``` if method_source is ```card```.
```java
EveryPay.getInstance(getActivity()).handlePayment(paymentLink,  "card", mobileAccessToken);
```
2. ```Alternative Payment Method (APM) Flow``` if method_source is not ```card```, e.g: bank

```java
EveryPay.getInstance(getActivity()).handlePayment(paymentLink, "bank", null);
```



In the same activity, override and handle `onActivityResult()`, which is called after PaymentActivity finishes. Note you need to handle `onActivityResult()` in fragment if you called `handlePayment` method to start payment in fragment, not from current activity.

```java
 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EveryPay.REQUEST_CODE) {
            if (resultCode == EveryPay.RESULT_OK) {
                //TODO: Handle payment success
                ...
            } else if (resultCode == EveryPay.RESULT_ERROR){
                //TODO: Hadle payment failure
                ...
            }
        }
    }
```

## Required Android permissions

The SDK requires `<uses-permission android:name="android.permission.INTERNET" />` for internet access.

## Theming the Card Details form

To override the text, color and styles used in the card input form, define resources matching the identifiers in your `colors.xml`, `strings.xml` and `styles.xml` files. The EveryPay SDK resources start with the `ep_` prefix.

For an example, see https://github.com/UnifiedPaymentSolutions/everypay-android3/blob/master/app/src/main/res/values/strings.xml

For more substantial theming, overriding the `layout/ep_fragment_card_detail.xml` with your own layout is also a possibility.

Addtionally you can keep the layout,but use your own theme, by overriding PaymentActivity in your manifest and specifing your theme as activity theme, like so :
```xml
 <activity android:name="com.everypay.sdk.activity.PaymentActivity"
            android:theme="@style/YourCustomTheme"/>
```
PS! This only works if you are using PaymentActivity.
