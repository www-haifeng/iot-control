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

@Table(name = "t_group_device")
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupDevice extends BaseEntity implements Serializable{
private static final long serialVersionUID=1L;

    
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
        
    /**
     * 组id t_groupid
     */
    @Column(name = "group_id")
    private Long groupId;
        
    /**
     * 设备id
     */
    @Column(name = "device_id")
    private Integer deviceId;
    
}
