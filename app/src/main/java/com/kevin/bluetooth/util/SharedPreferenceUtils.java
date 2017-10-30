package com.kevin.bluetooth.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Kevin-Tu on 2017/10/26 0026.
 */

public class SharedPreferenceUtils {

    /**
     * 向SharedPreference中存值
     * @param context
     * @param name
     * @param key
     * @param value
     * @return
     */
    public static boolean putValue(Context context, String name, String key, Object value) {
        return putValue(context, name, key, value, false);
    }

    public static boolean putValue(Context context, String name, String key, Object value, boolean apply) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            editor.putString(key, (String) value);
        }
        if (apply) {
            editor.apply();
            return true;
        } else {
            return editor.commit();
        }
    }

    /**
     * 从SharedPreference中取值
     * @param context
     * @param name
     * @param key
     * @param vClass
     * @return
     */
    public static Object getValue(Context context, String name, String key, Class<?> vClass) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        Object value;
        if (vClass == Long.class) {
            value = sp.getLong(key, 0L);
        } else if (vClass == Float.class) {
            value = sp.getFloat(key, 0.0f);
        } else if (vClass == Integer.class) {
            value = sp.getInt(key, 0);
        } else if (vClass == Boolean.class) {
            try {
                value = sp.getBoolean(key, false);
            } catch (Exception e) {
                value = false;
            }
        } else {
            value = sp.getString(key, "");
        }
        return value;
    }

    /**
     * 从配置文件name中移除key
     *
     * @param context
     * @param name
     * @param key
     * @return
     */
    public static boolean removeKey(Context context, String name, String key) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }
}
