package com.shuzhi.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zgk
 * @description 封装返回结果
 */
@Data
@AllArgsConstructor
class SimpleResponse {

    /**
     * 封装返回信息
     */
    private Boolean isAdmin;


}
