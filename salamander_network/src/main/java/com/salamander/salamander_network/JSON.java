package com.salamander.salamander_network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSON {

    public static String getStringOrNull(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key)) {
                if (!jsonObject.getString(Key).trim().toLowerCase().equals("null"))
                    return jsonObject.getString(Key).trim();
            }
        } catch (Exception e) {
            Log.e("JSON->getStringOrNull", e.toString());
        }
        return null;
    }

    public static String getString(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key)) {
                if (!jsonObject.getString(Key).trim().toLowerCase().equals("null"))
                    return jsonObject.getString(Key).trim();
            }
        } catch (Exception e) {
            Log.e("JSON->getString", e.toString());
        }
        return null;
    }

    public static String getString(JSONObject jsonObject, String Key, String defaultValue) {
        try {
            if (jsonObject.has(Key)) {
                if (!jsonObject.getString(Key).trim().toLowerCase().equals("null"))
                    return jsonObject.getString(Key).trim();
            }
        } catch (Exception e) {
            Log.e("JSON->getString", e.toString());
        }
        return defaultValue;
    }

    public static int getInt(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key))
                return jsonObject.getInt(Key);
        } catch (Exception e) {
            Log.e("JSON->getInt", e.toString());
        }
        return 0;
    }

    public static double getDouble(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key))
                return jsonObject.getDouble(Key);
        } catch (Exception e) {
            Log.e("JSON->getDouble", e.toString());
        }
        return 0;
    }

    public static float getFloat(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key))
                return Float.valueOf(String.valueOf(jsonObject.getDouble(Key)));
        } catch (Exception e) {
            Log.e("JSON->getFloat", e.toString());
        }
        return 0;
    }

    public static boolean getBoolean(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key))
                return jsonObject.getBoolean(Key);
        } catch (Exception e) {
            Log.e("JSON->getBoolean", e.toString());
        }
        return false;
    }

    public static JSONObject getJSONObject(String json, String Key) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(Key))
                return jsonObject.getJSONObject(Key);
        } catch (Exception e) {
            Log.e("JSON->toJSONObject", e.toString());
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key))
                return jsonObject.getJSONObject(Key);
        } catch (Exception e) {
            Log.e("JSON->toJSONObject", e.toString());
        }
        return null;
    }

    public static JSONArray getJSONArray(String json, String Key) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(Key))
                return jsonObject.getJSONArray(Key);
        } catch (Exception e) {
            Log.e("JSON->toJSONObject", e.toString());
        }
        return null;
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String Key) {
        try {
            if (jsonObject.has(Key))
                return jsonObject.getJSONArray(Key);
        } catch (Exception e) {
            Log.e("JSON->toJSONObject", e.toString());
        }
        return null;
    }

    public static JSONObject toJSONObject(String json) {
        try {
            return new JSONObject(json);
        } catch (Exception e) {
            Log.e("JSON->toJSONObject", e.toString());
        }
        return null;
    }

    public static JSONArray toJSONArray(String json) {
        try {
            return new JSONArray(json);
        } catch (Exception e) {
            Log.e("JSON->toJSONArray", e.toString());
        }
        return null;
    }
}
