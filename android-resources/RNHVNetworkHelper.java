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
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;

import javax.annotation.Nonnull;

import co.hyperverge.hypersnapsdk.listeners.APICompletionCallback;
import co.hyperverge.hypersnapsdk.network.HVNetworkHelper;
import co.hyperverge.hypersnapsdk.objects.HVError;
import co.hyperverge.hypersnapsdk.objects.HVResponse;

public class RNHVNetworkHelper extends ReactContextBaseJavaModule {
    public RNHVNetworkHelper(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    protected static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    private static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String) {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
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

    @Nonnull
    @Override
    public String getName() {
        return "RNHVNetworkHelper";
    }

    @ReactMethod
    public void makeOCRCall(String endpoint, String documentUri, ReadableMap parameters, ReadableMap requestHeaders, final Callback resultCallback) {

        APICompletionCallback completionCallback = new APICompletionCallback() {
            @Override
            public void onResult(HVError error, HVResponse hvResponse) {

                WritableMap errorObj = Arguments.createMap();
                WritableMap resultsObj = Arguments.createMap();

                if (error != null) {

                    errorObj.putInt("errorCode", error.getErrorCode());
                    errorObj.putString("errorMessage", error.getErrorMessage());
                    resultCallback.invoke(errorObj, null);
                } else if (hvResponse != null) {
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
                    resultCallback.invoke(null, resultsObj);

                }
            }

        };
        try {
            JSONObject params = new JSONObject();
            if (parameters != null) {
                params = convertMapToJson(parameters);
            }

            JSONObject headers2 = new JSONObject();
            if (requestHeaders != null) {
                headers2 = convertMapToJson(requestHeaders);
            }
            HVNetworkHelper.makeOCRCall(getCurrentActivity(), endpoint, documentUri, params, headers2, completionCallback);
        } catch (Exception e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }

    @ReactMethod
    public void makeFaceMatchCall(String endpoint, String faceUri, String documentUri, ReadableMap parameters, ReadableMap requestHeaders, final Callback resultCallback) {
        APICompletionCallback completionCallback = new APICompletionCallback() {
            @Override
            public void onResult(HVError error, HVResponse hvResponse) {

                WritableMap errorObj = Arguments.createMap();
                WritableMap resultsObj = Arguments.createMap();

                if (error != null) {
                    errorObj.putInt("errorCode", error.getErrorCode());
                    errorObj.putString("errorMessage", error.getErrorMessage());
                    resultCallback.invoke(errorObj, null);

                } else if (hvResponse != null) {
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
                    resultCallback.invoke(null, resultsObj);

                }
            }

        };
        try {
            JSONObject params = new JSONObject();
            if (parameters != null) {
                params = convertMapToJson(parameters);
            }

            JSONObject headers2 = new JSONObject();
            if (requestHeaders != null) {
                headers2 = convertMapToJson(requestHeaders);
            }

            HVNetworkHelper.makeFaceMatchCall(getCurrentActivity(), endpoint, faceUri, documentUri, params, headers2, completionCallback);
        } catch (JSONException e) {
            Log.e(getName(), Objects.requireNonNull(e.getMessage()));
        }
    }
}
