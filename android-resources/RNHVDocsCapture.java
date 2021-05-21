package com.awesomenewwrappers;


import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import co.hyperverge.hypersnapsdk.activities.HVDocsActivity;
import co.hyperverge.hypersnapsdk.listeners.DocCaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.objects.HVDocConfig;
import co.hyperverge.hypersnapsdk.objects.HVError;
import co.hyperverge.hypersnapsdk.objects.HVResponse;

public class RNHVDocsCapture extends ReactContextBaseJavaModule {

    public String docType;
    public Float aspectRatio;
    HVDocConfig docConfig = null;
    HVDocConfig.Document doc;
    boolean hasBeenCalled;

    public RNHVDocsCapture(ReactApplicationContext reactContext) {
        super(reactContext);

    }

    private static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (readableMap.getType(key)) {
                case Null:
                    object.put(key, JSONObject.NULL);
                    break;
                case Boolean:
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    object.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    object.put(key, readableMap.getString(key));
                    break;
                case Map:
                    object.put(key, convertMapToJson(readableMap.getMap(key)));
                    break;
                case Array:
                    object.put(key, convertArrayToJson(readableMap.getArray(key)));
                    break;
            }
        }
        return object;
    }

    private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    array.put(readableArray.getDouble(i));
                    break;
                case String:
                    array.put(readableArray.getString(i));
                    break;
                case Map:
                    array.put(convertMapToJson(readableArray.getMap(i)));
                    break;
                case Array:
                    array.put(convertArrayToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
    }

    private void test() {

    }

    @Override
    public String getName() {
        return "RNHVDocsCapture";
    }

    public HVDocConfig getDocConfig() {
        if (this.docConfig == null) {
            this.docConfig = new HVDocConfig();

        }
        return this.docConfig;

    }

    @ReactMethod
    public HVDocConfig.DocumentSide getFrontDocumentSide() {
        return HVDocConfig.DocumentSide.FRONT;
    }

    @ReactMethod
    public HVDocConfig.DocumentSide getBackDocumentSide() {
        return HVDocConfig.DocumentSide.BACK;
    }

    @ReactMethod
    public void setOCRAPIDetails(String ocrEndpoint, String documentSideString, ReadableMap params,
                                 ReadableMap headers) throws JSONException {
        String[] documentSideParams = documentSideString.split("\\.");
        HVDocConfig.DocumentSide documentSide = getDocumentSide(documentSideParams[2], HVDocConfig.DocumentSide.class);

        JSONObject paramsObj = new JSONObject();
        if (params != null) {
            paramsObj = convertMapToJson(params);
        }

        JSONObject headersObj = new JSONObject();
        if (headers != null) {
            headersObj = convertMapToJson(headers);
        }

        if (documentSide != null) {
            docConfig.setOCRDetails(ocrEndpoint,
                    documentSide,
                    paramsObj.toString(),
                    headersObj.toString());
        }
    }

    private <T> HVDocConfig.DocumentSide getDocumentSide(String documentSide, Class<T> type) {
        switch (documentSide) {
            case "FRONT":
            case "front":
                return HVDocConfig.DocumentSide.FRONT;
            case "BACK":
            case "back":
                return HVDocConfig.DocumentSide.BACK;
            default: {
                return null;
            }
        }
    }

    @ReactMethod
    public void setCustomUIStrings(String customStrings) {
        try {
            JSONObject stringObj = new JSONObject();
            if (customStrings == null && !customStrings.trim().isEmpty())
                stringObj = new JSONObject(customStrings);
            getDocConfig().setCustomUIStrings(stringObj);
        } catch (JSONException e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }

    @ReactMethod
    public void setAspectRatio(Float aspectRatioValue) {
        aspectRatio = aspectRatioValue;
    }

    @ReactMethod
    public void setShouldAddPadding(Boolean shouldSetPadding) {
        getDocConfig().setShouldAddPadding(shouldSetPadding.booleanValue());

    }

    @ReactMethod
    public void setPadding(Number padding) {
        getDocConfig().setPadding((float) padding);

    }

    @ReactMethod
    public void setShouldShowFlashIcon(Boolean shouldShowFlashIcon) {
        getDocConfig().setShouldShowFlashIcon(shouldShowFlashIcon.booleanValue());
    }

    @ReactMethod
    public void setShouldShowReviewScreen(Boolean shouldShowReviewScreen) {
        getDocConfig().setShouldShowReviewScreen(shouldShowReviewScreen.booleanValue());
    }

    @ReactMethod
    public void setDocCaptureSubText(String subText) {
        getDocConfig().setDocCaptureSubText(subText);
    }

    @ReactMethod
    public void setDocCaptureDescription(String description) {
        getDocConfig().setDocCaptureDescription(description);
    }

    @ReactMethod
    public void setShouldShowInstructionPage(Boolean shouldShowInstructionPage) {
        getDocConfig().setShouldShowInstructionPage(shouldShowInstructionPage.booleanValue());
    }

    @ReactMethod
    public HVDocConfig getConfig() {
        return getDocConfig();
    }

    @ReactMethod
    public void setDocumentType(String docTypeValue) {
        try {
            this.docType = docTypeValue.split("\\.")[1];
            if (this.docType == null) {
                this.docType = "Card";
            }

            this.doc = HVDocConfig.Document.valueOf(docType);
            if (aspectRatio != null)
                this.doc.setAspectRatio(this.aspectRatio);

            getDocConfig().setDocumentType(this.doc);
        } catch (Exception exp) {
            Log.e(getName(), Objects.requireNonNull(exp.getMessage()));
        }
    }

    @ReactMethod
    public void setDocCaptureTitle(String titleText) {
        getDocConfig().setDocCaptureTitle(titleText);
    }

    @ReactMethod
    public void setDocReviewTitle(String docReviewTitle) {

        getDocConfig().setDocReviewTitle(docReviewTitle);
    }

    @ReactMethod
    public void setDocReviewDescription(String docReviewDescription) {
        getDocConfig().setDocReviewDescription(docReviewDescription);

    }

    @ReactMethod
    public void setUIStrings(String uiStrings) {
        try {
            JSONObject uiStringsJsonObject = new JSONObject(uiStrings);
            JSONObject customUIStrings = new JSONObject();

            // Document capture screen title
            if (uiStringsJsonObject.has("docCaptureTitle")) {
                getDocConfig().setDocCaptureTitle(uiStringsJsonObject.getString("docCaptureTitle"));
            }

            // Document capture screen description
            if (uiStringsJsonObject.has("docCaptureDescription")) {
                getDocConfig().setDocCaptureDescription(uiStringsJsonObject.getString("docCaptureDescription"));
            }

            // Document capture screen sub text - FRONT/BACK text
            if (uiStringsJsonObject.has("docCaptureSubText")) {
                getDocConfig().setDocCaptureSubText(uiStringsJsonObject.getString("docCaptureSubText"));
            }

            // Review Screen retake button
            if (uiStringsJsonObject.has("docReviewRetakeButton")) {
                customUIStrings.put("docReviewRetakeButton", uiStringsJsonObject.getString(
                        "docReviewRetakeButton"));
            }

            // Review Screen continue button
            if (uiStringsJsonObject.has("docReviewContinueButton")) {
                customUIStrings.put("docReviewContinueButton", uiStringsJsonObject.getString(
                        "docReviewContinueButton"));
            }

            // Review Screen title
            if (uiStringsJsonObject.has("docReviewTitle")) {
                getDocConfig().setDocReviewTitle(uiStringsJsonObject.getString("docReviewTitle"));
            }

            // Review Screen description
            if (uiStringsJsonObject.has("docReviewDescription")) {
                getDocConfig().setDocReviewDescription(uiStringsJsonObject.getString("docReviewDescription"));
            }

            if (uiStringsJsonObject.has("docRetakeTitleText")) {
                getDocConfig().setErrorReviewTitle(uiStringsJsonObject.getString("docRetakeTitleText"));
            }

            if (uiStringsJsonObject.has("docRetakeButtonText")) {
                getDocConfig().setErrorReviewRetakeButton(uiStringsJsonObject.getString("docRetakeButtonText"));
            }

            // Using customUIStrings
            getDocConfig().setCustomUIStrings(customUIStrings);

        } catch (JSONException e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }

    @ReactMethod
    public void start(final Callback resultCallback) {
        hasBeenCalled = false;

        HVDocsActivity.start(getCurrentActivity(), getDocConfig(), new DocCaptureCompletionHandler() {
            @Override
            public void onResult(HVError error, HVResponse hvResponse) {
                try {

                    WritableMap errorObj = Arguments.createMap();
                    WritableMap resultsObj = Arguments.createMap();
                    WritableMap headersObj = Arguments.createMap();

                    if (error != null) {
                        errorObj.putInt("errorCode", error.getErrorCode());
                        errorObj.putString("errorMessage", error.getErrorMessage());
                        if (!hasBeenCalled) {
                            hasBeenCalled = true;
                            resultCallback.invoke(errorObj, null);
                        }
                    } else if (hvResponse != null) {
                        JSONObject result = hvResponse.getApiResult();
                        JSONObject headers = hvResponse.getApiHeaders();

                        String imageURI = hvResponse.getImageURI();
                        String fullImageUri = hvResponse.getFullImageURI();
                        String action = hvResponse.getAction();
                        String retakeMessage = hvResponse.getRetakeMessage();
                        if (result != null) {
                            resultsObj = null;
                            try {
                                resultsObj = RNHVNetworkHelper.convertJsonToMap(result);
                                resultsObj.putString("apiResult", result.toString());
                                resultsObj.putString("imageUri", imageURI);
                                if (fullImageUri != null && !fullImageUri.isEmpty()) {
                                    resultsObj.putString("fullImageUri", fullImageUri);
                                }
                                if (retakeMessage != null && !retakeMessage.isEmpty()) {
                                    resultsObj.putString("retakeMessage", retakeMessage);
                                }
                                if (action != null && !action.isEmpty()) {
                                    resultsObj.putString("action", action);
                                }
                            } catch (Exception e) {
                                Log.e(getName(), Objects.requireNonNull(e.getMessage()));
                            }
                        }
                        if (headers != null) {
                            headersObj = null;
                            try {
                                headersObj = RNHVNetworkHelper.convertJsonToMap(headers);
                                resultsObj.putString("apiHeaders", headers.toString());
                            } catch (Exception e) {
                                Log.e(getName(), Objects.requireNonNull(e.getMessage()));
                            }
                        }
                        if (!hasBeenCalled) {
                            hasBeenCalled = true;
                            resultCallback.invoke(null, resultsObj);
                        }

                    }

                } catch (Exception e) {
                    Log.e(getName(), Objects.requireNonNull(e.getMessage()));
                }
            }
        });

    }

}
