package com.shuzhi.entity;

import com.shuzhi.common.basemapper.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@Table(name = "t_light_pole")
@Data
@EqualsAndHashCode(callSuper = true)
public class Lighpole extends BaseEntity implements Serializable{
private static final long serialVersionUID=1L;

    
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
        
    /**
     * 灯杆id
     */
    @Column(name = "lamppostid")
    private Integer lamppostid;
        
    /**
     * 灯杆名称
     */
    @Column(name = "lamppostname")
    private String lamppostname;
        
    /**
     * 经度
     */
    @Column(name = "longitude")
    private Double longitude;
        
    /**
     * 纬度
     */
    @Column(name = "latitude")
    private Double latitude;


    /**
     * 高度
     */
    @Column(name = "height")
    private Double height;

}
