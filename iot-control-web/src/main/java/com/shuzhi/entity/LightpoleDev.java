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

@Table(name = "t_lightpole_dev")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightpoleDev extends BaseEntity implements Serializable{
private static final long serialVersionUID=1L;

    
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
        
    /**
     * 灯杆id t_light_pole中的lamppostid
     */
    @Column(name = "lamppostid")
    private Integer lamppostid;
        
    /**
     * 设备id
     */
    @Column(name = "device_id")
    private Integer deviceId;
        
    /**
     * 设备类型
     */
    @Column(name = "device_type")
    private Integer deviceType;
    
}
