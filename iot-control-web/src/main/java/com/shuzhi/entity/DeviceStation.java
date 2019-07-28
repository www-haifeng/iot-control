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

@Table(name = "t_device_station")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceStation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    private Integer stationid;

    /**
     * 设备类型编码 1.顶棚照明 2.logo照明 3.站台照明 4.led 5.lcd 6.集中控制器
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

    public DeviceStation(String id) {

        this.deviceDid = id;

    }

    public DeviceStation() {


    }
}
