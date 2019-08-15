package com.shuzhi.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ControllerLights  implements Serializable {

    private Integer id;
    private String name;
    private  Integer state ;
   private Integer onoff;
}
