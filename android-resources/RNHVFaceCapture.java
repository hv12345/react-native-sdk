package com.awesomenewwrappers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import co.hyperverge.hypersnapsdk.activities.HVFaceActivity;
import co.hyperverge.hypersnapsdk.listeners.FaceCaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.objects.HVError;
import co.hyperverge.hypersnapsdk.objects.HVFaceConfig;
import co.hyperverge.hypersnapsdk.objects.HVResponse;


public class RNHVFaceCapture extends ReactContextBaseJavaModule {
    public String liveness;
    HVFaceConfig faceConfig;
    boolean hasBeenCalled;

    public RNHVFaceCapture(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "RNHVFaceCapture";
    }

    @ReactMethod
    public void setLivenessEndpoint(String livenessEndpoint) {
        getFaceConfig().setLivenessEndpoint(livenessEndpoint);
    }

    public HVFaceConfig getFaceConfig() {
        if (this.faceConfig == null) {
            this.faceConfig = new HVFaceConfig();
        }
        return this.faceConfig;

    }

    @ReactMethod
    public void setShouldReturnFullImageUri(Boolean shouldReturnFullImageUrl) {
        getFaceConfig().setShouldReturnFullImageUrl(shouldReturnFullImageUrl.booleanValue());
    }

    @ReactMethod
    public void setClientID(String clientID) {
        getFaceConfig().setClientID(clientID);

    }

    @ReactMethod
    public void setUIStrings(String uiStrings) {
        try {
            JSONObject uiStringsJsonObject = new JSONObject(uiStrings);
            JSONObject customUIStrings = new JSONObject();

            if (uiStringsJsonObject.has("faceCaptureTitle")) {
                getFaceConfig().setFaceCaptureTitle(uiStringsJsonObject.getString("faceCaptureTitle"));
            }

            if (uiStringsJsonObject.has("faceCaptureMoveCloser")) {
                customUIStrings.put("moveCloser", uiStringsJsonObject.getString("faceCaptureMoveCloser"));
            }

            if (uiStringsJsonObject.has("faceCaptureFaceFound")) {
                customUIStrings.put("faceCaptureFaceFound", uiStringsJsonObject.getString("faceCaptureFaceFound"));
            }

            if (uiStringsJsonObject.has("faceCaptureFaceNotFound")) {
                customUIStrings.put("faceCaptureFaceNotFound", uiStringsJsonObject.getString("faceCaptureFaceNotFound"));
            }

            if (uiStringsJsonObject.has("faceCaptureMoveAway")) {
                customUIStrings.put("faceCaptureMoveAway", uiStringsJsonObject.getString("faceCaptureMoveAway"));
            }

            if (uiStringsJsonObject.has("faceCaptureMultipleFaces")) {
                customUIStrings.put("faceCaptureMultipleFaces", uiStringsJsonObject.getString("faceCaptureMultipleFaces"));
            }

            if (uiStringsJsonObject.has("faceRetakeTitleText")) {
                getFaceConfig().setErrorReviewTitle(uiStringsJsonObject.getString("faceRetakeTitleText"));
            }

            if (uiStringsJsonObject.has("faceRetakeButtonText")) {
                getFaceConfig().setErrorReviewRetakeButton(uiStringsJsonObject.getString("faceRetakeButtonText"));
            }

            getFaceConfig().setCustomUIStrings(customUIStrings);

        } catch (JSONException e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }

    @ReactMethod
    public void setCustomUIStrings(String customStrings) {
        try {
            JSONObject stringObj = new JSONObject();
            if (customStrings == null && !customStrings.trim().isEmpty())
                stringObj = new JSONObject(customStrings);
            getFaceConfig().setCustomUIStrings(stringObj);
        } catch (JSONException e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }

    @ReactMethod
    public void setShouldShowCameraSwitchButton(Boolean shouldShow) {
        getFaceConfig().setShouldShowCameraSwitchButton(shouldShow.booleanValue());
    }


    @ReactMethod
    public void setShouldShowInstructionPage(Boolean shouldShowInstructionPage) {
        getFaceConfig().setShouldShowInstructionPage(shouldShowInstructionPage.booleanValue());
    }


    @ReactMethod
    public void setLivenessMode(String livenessValue) {
        try {
            liveness = livenessValue.split("\\.")[1];
            HVFaceConfig.LivenessMode livenessMode = HVFaceConfig.LivenessMode.valueOf(liveness);
            getFaceConfig().setLivenessMode(livenessMode);
        } catch (Exception exp) {
            Log.e(getName(), Objects.requireNonNull(exp.getMessage()));
        }
    }

    @ReactMethod
    public void setFaceCaptureTitle(String faceCaptureTitle) {
        getFaceConfig().setFaceCaptureTitle(faceCaptureTitle);
    }

    @ReactMethod
    public void setShouldUseBackCamera(Boolean shouldUseBackCamera) {
        getFaceConfig().setShouldUseBackCamera(shouldUseBackCamera.booleanValue());
    }

    @ReactMethod
    public void setShouldEnableDataLogging(Boolean dataLogging) {
        getFaceConfig().setShouldEnableDataLogging(dataLogging.booleanValue());
    }

    @ReactMethod
    public void setShouldEnablePadding(Boolean shouldSetPadding) {
        getFaceConfig().setShouldEnablePadding(shouldSetPadding.booleanValue());

    }

    @ReactMethod
    public void setPadding(Number leftPadding, Number rightPadding, Number topPadding, Number bottomPadding) {
        getFaceConfig().setPadding((float) (leftPadding), (float) rightPadding, (float) topPadding, (float) bottomPadding);
    }

    @ReactMethod
    public void setLivenessAPIParameters(String params) {
        try {
            getFaceConfig().setLivenessAPIParameters(new JSONObject(params));
        } catch (JSONException e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }

    @ReactMethod
    public void setLivenessAPIHeaders(String headers) {
        try {
            getFaceConfig().setLivenessAPIHeaders(new JSONObject(headers));
        } catch (JSONException e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }


    @ReactMethod
    public void start(final Callback resultCallback) {
        hasBeenCalled = false;

        HVFaceActivity.start(getCurrentActivity(), getFaceConfig(), new FaceCaptureCompletionHandler() {
            @Override
            public void onResult(HVError error, HVResponse hvResponse) {

                try {

                    WritableMap errorObj = Arguments.createMap();
                    WritableMap resultsObj = Arguments.createMap();

                    if (error != null) {
                        errorObj.putInt("errorCode", error.getErrorCode());
                        errorObj.putString("errorMessage", error.getErrorMessage());
                        if (!hasBeenCalled) {
                            hasBeenCalled = true;
                            resultCallback.invoke(errorObj, null);
                        }
                    } else {
                        JSONObject apiResult = hvResponse.getApiResult();
                        JSONObject apiHeaders = hvResponse.getApiHeaders();

                        String imageURI = hvResponse.getImageURI();
                        String fullImageUri = hvResponse.getFullImageURI();
                        String action = hvResponse.getAction();
                        String retakeMessage = hvResponse.getRetakeMessage();

                        if (apiResult != null) {
                            try {
                                resultsObj.putMap("apiResult", RNHVNetworkHelper.convertJsonToMap(apiResult));
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
                        if (apiHeaders != null) {
                            try {
                                resultsObj.putMap("apiHeaders",
                                        RNHVNetworkHelper.convertJsonToMap(apiHeaders));
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
