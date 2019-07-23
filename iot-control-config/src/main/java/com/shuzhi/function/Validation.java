package com.shuzhi.function;

import com.shuzhi.common.utils.Wrapper;

/**
 * @author zgk
 * @description 验证参数的接口
 * @date 2019-07-04 15:53
 */
@FunctionalInterface
public interface Validation<T> {

    /**
     * 验证参数
     *
     * @param t 要验证的数据
     * @return 验证结果
     */
    Wrapper check(T t);
}
