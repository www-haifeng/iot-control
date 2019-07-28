package com.shuzhi.entity;

import com.shuzhi.common.basemapper.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * @author shuzhi
 * @date 2019-07-23 11:31:25
 */

@Table(name = "t_station")
@Data
@EqualsAndHashCode(callSuper = true)
public class Station extends BaseEntity implements Serializable{
private static final long serialVersionUID=1L;

    
    @Id
    private Integer id;
        
    /**
     * 站台名称
     */
    @Column(name = "station_name")
    private String stationName;
        
    /**
     * 站台经度
     */
    @Column(name = "station_lon")
    private String stationLon;
        
    /**
     * 站台纬度
     */
    @Column(name = "station_lat")
    private String stationLat;
        
    /**
     * 站台描述
     */
    @Column(name = "describe")
    private String describe;
    
}
