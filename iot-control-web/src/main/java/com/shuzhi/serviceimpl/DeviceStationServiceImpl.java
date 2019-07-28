package com.shuzhi.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.DeviceStation;
import com.shuzhi.service.DeviceStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author shuzhi
 * @date 2019-07-23 11:31:25
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceStationServiceImpl extends BaseServiceImpl<DeviceStation> implements DeviceStationService {

}