package com.shuzhi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
* @Program: IotControlWeb
* @Description:
* @Author: YuJQ
* @Create: 2019/7/22 16:58
**/
@SpringBootApplication
@MapperScan("com.shuzhi.mapper")
@EnableFeignClients(basePackages= {"com.shuzhi"})
@EnableEurekaClient
@EnableScheduling
public class IotControlWeb {
    public static void main(String[] args)
    {
        SpringApplication.run(IotControlWeb.class, args);
    }

}
