package com.shuzhi.entity;

import lombok.Data;

import java.io.Serializable;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@Data
public class Lighpoles implements Serializable{

    private Long id;
        
    /**
     * 灯杆id
     */
    private Integer lamppostid;
        
    /**
     * 灯杆名称
     */
    private String lamppostname;
        
    /**
     * 经度
     */
    private Double longitude;
        
    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 设备类型
     */
    private String deviceType;
        
}
