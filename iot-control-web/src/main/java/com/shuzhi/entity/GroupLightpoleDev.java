package com.shuzhi.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
* @Program: GroupLightpoleDev
* @Description:
* @Author: YuJQ
* @Create: 2019/8/15 9:51
**/
@Data
public class GroupLightpoleDev {
    private  Integer groupid;

    private String groupname;

    List<LightpoleDevs> lampposts = new ArrayList<>();

}

