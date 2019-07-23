package com.shuzhi.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

/**
 * The class Public util.
 *
 * @author tianzehua
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PublicUtil {

	/**
	 * 判断对象是否Empty(null或元素为0)
	 * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
	 *
	 * @param pObj 待检查对象
	 *
	 * @return boolean 返回的布尔值
	 */
	public static boolean isEmpty(Object pObj) {
		if (pObj == null) {
			return true;
		}
		if (pObj == "") {
			return true;
		}
		if (pObj instanceof String) {
			return ((String) pObj).length() == 0;
		} else if (pObj instanceof Collection) {
			return ((Collection) pObj).isEmpty();
		} else if (pObj instanceof Map) {
			return ((Map) pObj).size() == 0;
		}
		return false;
	}

	/**
	 * 判断对象是否为NotEmpty(!null或元素大于0)
	 * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
	 *
	 * @param pObj 待检查对象
	 *
	 * @return boolean 返回的布尔值
	 */
	public static boolean isNotEmpty(Object pObj) {
		if (pObj == null) {
			return false;
		}
		if (pObj == "") {
			return false;
		}
		if (pObj instanceof String) {
			return ((String) pObj).length() != 0;
		} else if (pObj instanceof Collection) {
			return !((Collection) pObj).isEmpty();
		} else if (pObj instanceof Map) {
			return ((Map) pObj).size() != 0;
		}
		return true;
	}

	/**
	 * 把字符串百分比类型转成double小数
	 * @param percent 百分比
	 *
	 * @return double
	 */
	public static double percentToDouble(String percent) throws ParseException {
		NumberFormat nf = NumberFormat.getPercentInstance();
		//将百分数转换成Number类型
		Number m = nf.parse(percent);
		//通过调用nubmer类默认方法直接转换成double
		return m.doubleValue();
	}

	/**
	 * 把空间位置信息 gis 切割成经度和纬度 例如 传入"POINT(108.9498710632 34.2588125935)"
	 * 返回 108.9498710632 34.2588125935
	 * @param gis 空间位置信息
	 * @return String[] 长度是2的数组 索引0是 lng 索引1是lat
	 */
	public static String[] spiltGis(String gis) {
		String[] arr = gis.substring(gis.indexOf("(")+1, gis.indexOf(")")).split(" ");
		return arr;
	}
}
