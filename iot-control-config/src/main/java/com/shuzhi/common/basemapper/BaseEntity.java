package com.shuzhi.common.basemapper;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author zgk
 * @description 实体类通用属性
 * @date 2019-04-29 10:20
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询时between的开始时间
     */
    @Transient
    private String startTime;

    /**
     * 查询时between的结束时间
     */
    @Transient
    private String endTime;

    /**
     * 分页信息 第几页
     */
    @Transient
    private Integer pageNum;

    /**
     * 分页信息 多少条
     */
    @Transient
    private Integer pageSize;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
