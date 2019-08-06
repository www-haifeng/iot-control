package com.shuzhi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author huliang
 * @date 2019/7/31 15:56
 */
@NoArgsConstructor
@Data
@Accessors(chain=true)
public class TLightPole {
    /**
     * 灯杆主键id
     */
    private Integer id;
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
    private Float longitude;
    /**
     * 纬度
     */
    private Float latitude;

    private Integer frtid;
}
