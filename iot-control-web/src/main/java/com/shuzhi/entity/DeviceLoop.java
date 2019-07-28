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
 * @date 2019-07-14 15:15:36
 */

@Table(name = "t_device_loop")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceLoop extends BaseEntity implements Serializable{
private static final long serialVersionUID=1L;

    
    @Id
    private Integer id;
        
    /**
     * 类型编码
     */
    @Column(name = "typecode")
    private String typecode;
        
    /**
     * 设备did
     */
    @Column(name = "device_did")
    private String deviceDid;
        
    /**
     * 设备名称
     */
    @Column(name = "device_name")
    private String deviceName;
        
    /**
     * 回路号
     */
    @Column(name = "loop")
    private Integer loop;
        
    /**
     * 网关设备did
     */
    @Column(name = "gateway_did")
    private String gatewayDid;

    public DeviceLoop(Integer id) {
        this.loop = id;
    }
    public DeviceLoop() {

    }

    public DeviceLoop(String did) {
        this.deviceDid = did;
    }
}
