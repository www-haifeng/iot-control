package com.shuzhi.common.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * The class Wrap mapper.
 *
 * @author tianzehua
 */
public class WrapMapper {

	/**
	 * Instantiates a new wrap mapper.
	 */
	public WrapMapper() {
	}

	/**
	 * Wrap.
	 *
	 * @param <E>     the element type
	 * @param code    the code
	 * @param message the message
	 * @param o       the o
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> wrap(int code, String message, E o) {
		return new Wrapper<>(code, message, o);
	}

	/**
	 * Wrap.
	 *
	 * @param <E>     the element type
	 * @param code    the code
	 * @param message the message
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> wrap(int code, String message) {
		return wrap(code, message, null);
	}

	/**
	 * Wrap.
	 *
	 * @param <E>  the element type
	 * @param code the code
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> wrap(int code) {
		return wrap(code, null);
	}

	/**
	 * Wrap.
	 *
	 * @param <E> the element type
	 * @param e   the e
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> wrap(Exception e) {
		return new Wrapper<>(Wrapper.ERROR_CODE, e.getMessage());
	}

	/**
	 * Un wrapper.
	 *
	 * @param <E>     the element type
	 * @param wrapper the wrapper
	 *
	 * @return the e
	 */
	public static <E> E unWrap(Wrapper<E> wrapper) {
		return wrapper.getResult();
	}

	/**
	 * Wrap ERROR. code=100
	 *
	 * @param <E> the element type
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> illegalArgument() {
		return wrap(Wrapper.ILLEGAL_ARGUMENT_CODE_, Wrapper.ILLEGAL_ARGUMENT_MESSAGE);
	}

	/**
	 * Wrap ERROR. code=500
	 *
	 * @param <E> the element type
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> error() {
		return wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE);
	}


	/**
	 * Error wrapper.
	 *
	 * @param <E>     the type parameter
	 * @param message the message
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> error(String message) {
		return wrap(Wrapper.ERROR_CODE, StringUtils.isBlank(message) ? Wrapper.ERROR_MESSAGE : message);
	}

	/**
	 * Wrap SUCCESS. code=200
	 *
	 * @param <E> the element type
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> ok() {
		return new Wrapper<>();
	}

	/**
	 * Ok wrapper.
	 *
	 * @param <E> the type parameter
	 * @param o   the o
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> ok(E o) {
		return new Wrapper<>(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, o);
	}

	public static <E> Wrapper<E> loginok(E o) {
		return new Wrapper<>(Wrapper.SUCCESS_CODE, "登录成功", o);
	}

	/**
	 * Handle result wrapper.
	 *
	 * @param <T>    the type parameter
	 * @param result the result
	 *
	 * @return the wrapper
	 */
	public static <T> Wrapper<T> handleResult(T result) {
		boolean flag = isFlag(result);

		if (flag) {
			return WrapMapper.wrap(Wrapper.SUCCESS_CODE, "操作成功", result);
		} else {
			return WrapMapper.wrap(Wrapper.ERROR_CODE, "操作失败", result);
		}
	}


	/**
	 * Handle result wrapper.
	 *
	 * @param <E>      the type parameter
	 * @param result   the result
	 * @param errorMsg the error msg
	 *
	 * @return the wrapper
	 */
	public static <E> Wrapper<E> handleResult(E result, String errorMsg) {
		boolean flag = isFlag(result);

		if (flag) {
			return WrapMapper.wrap(Wrapper.SUCCESS_CODE, "操作成功", result);
		} else {
			return WrapMapper.wrap(Wrapper.ERROR_CODE, errorMsg, result);
		}
	}

	private static boolean isFlag(Object result) {
		boolean flag;
		if (result instanceof Integer) {
			flag = (Integer) result > 0;
		} else if (result instanceof Boolean) {
			flag = (Boolean) result;
		} else {
			flag = PublicUtil.isNotEmpty(result);
		}
		return flag;
	}
}
