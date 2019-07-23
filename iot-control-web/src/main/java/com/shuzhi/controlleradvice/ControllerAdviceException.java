package com.shuzhi.controlleradvice;

import com.shuzhi.common.utils.WrapMapper;
import com.shuzhi.common.utils.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * @author zgk
 * @description 全局异常处理
 * @date 2019-05-20 16:16
 */
@Slf4j
@ControllerAdvice
public class ControllerAdviceException {

    /**
     * @description 处理全局异常信息
     * @param e 错误信息
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Wrapper controllerAdvice(Exception e , HttpServletRequest httpRequest){
        log.error("服务器异常,请联系管理员 {} {} :", new Date(),e.getMessage());
        //返回全局异常处理
        e.printStackTrace();
        return WrapMapper.wrap(500,"服务器异常,请联系管理员",e.getMessage());
    }
}
