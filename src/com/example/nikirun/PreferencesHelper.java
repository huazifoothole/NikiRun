package com.example.nikirun;

import java.util.List;

import com.google.gson.Gson;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferencesHelper {
	
	private static final String LIST_TAG = ".LIST";
	private static SharedPreferences sharedPreferences;
	private static Gson gson;

	public PreferencesHelper() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 使用之前初始化，可在Application中调用
	 * @param context 请传入ApplicationContext避免内存泄漏
	 */
	public static void init(Context context) {
		sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		gson = new Gson();
	}
	
	private static void checkInit() {
		if(sharedPreferences == null || gson == null){
			throw new IllegalArgumentException("Please call init(context) first");
		}
	}
	
	/**
	 * 保存对象数据至SharedPreference,key默认为类名，如
	 * <pre>
	 * preferencesHelper.saveData();
	 * </pre>
	 * @param data  不带泛型的任意数据类型实例
	 */
	
	public static <T> void saveData(T data) {
		saveData(data.getClass().getName(),data);
	}
	
	/**
	 * 根据key保存对象数据至SharePreferences，如
	 * <pre>
	 * PreferencesHelper.saveData(key,saveUser);
	 * </pre>
	 * @param data 不带泛型的任意数据类型实例
	 */
	
	public static <T> void saveData(String key,T data) {
		checkInit();
		if(data == null)
			throw new IllegalArgumentException("data should not be null.");
	
		sharedPreferences.edit().putString(key, gson.toJson(data)).apply();
	}
	
	/**
	 * 保存List集合数据至SharedPreferences，请确保List至少含有一个元素
	 * <pre>
     * PreferencesHelper.saveData(users);
     * </pre>
     * @param data List类型实例
	 */
	public  static <T> void saveData(List<T> data) {
		checkInit();
		if(data == null || data.size() <= 0)
			throw new IllegalArgumentException(
					"List should not be null or at least contains one element.");
			
		Class returnType = data.get(0).getClass();
		sharedPreferences.edit().putString(returnType.getName()+LIST_TAG, gson.toJson(data)).apply();
		
	}
	
	/**
     * 将数据从SharedPreferences中取出, key默认为类名, 如
     * <pre>
     * User user = PreferencesHelper.getData(User.class)
     * </pre>
     */
	
	public static <T> T getData(Class<T> clz) {
		return getData(clz.getName(),clz);
	}
	
	
	/**
     * 根据key将数据从SharedPreferences中取出, 如
     * <pre>
     * User user = PreferencesHelper.getData(key,User.class)
     * </pre>
     */
	
	public static <T> T getData(String key,Class<T> clz) {
		checkInit();
		String json = sharedPreferences.getString(key, "");
		return gson.fromJson(json, clz);
	}
	
	/**
     * 将数据从SharedPreferences中取出, 如
     * <pre>List<User> users = PreferencesHelper.getData(List.class, User.class)</pre>
     */
	
//	public static <T> List<T> getData(Class<List> clz, Class<T> gClz) {
//        checkInit();
//        String json = sharedPreferences.getString(gClz.getName() + LIST_TAG, "");
//        return gson.fromJson(json,ParameterizedTypeImpl);
//    }
	
	/**
     * 简易字符串保存, 仅支持字符串
     */
    public static void saveData(String key, String data) {
        sharedPreferences.edit().putString(key, data).apply();
    }

    /**
     * 简易字符串获取, 仅支持字符串
     */
    public static String getData(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**
     * 删除保存的对象
     */
    public static void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    /**
     * 删除保存的对象
     */
    public static void remove(Class clz) {
        remove(clz.getName());
    }

    /**
     * 删除保存的数组
     */
    public static void removeList(Class clz) {
        sharedPreferences.edit().remove(clz.getName() + LIST_TAG).apply();
    }
	
}
