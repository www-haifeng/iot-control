package com.shuzhi.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LightpoleDevs {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 灯杆id
     */
    private Integer lamppostid;

    /**
     * 灯杆名称
     */
    private String lamppostname;

    List<ControllerLights> linghts = new ArrayList<>();
}
