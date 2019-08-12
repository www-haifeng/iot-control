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

@Table(name = "t_group")
@Data
@EqualsAndHashCode(callSuper = true)
public class Group extends BaseEntity implements Serializable{
private static final long serialVersionUID=1L;

    
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
        
    /**
     * 组名称
     */
    @Column(name = "group_name")
    private String groupName;
        
    /**
     * 设备类型
1.灯杆 2.led 3.lcd 4.集中控制器 5环测 6广播(跟设备回路一致)
     */
    @Column(name = "device_type")
    private Integer deviceType;
        
    /**
     * 组描述
     */
    @Column(name = "describe")
    private String describe;
    
}
