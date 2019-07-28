package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.DeviceLoop;
import com.shuzhi.service.DeviceLoopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author shuzhi
 * @date 2019-07-14 15:15:36
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceLoopServiceImpl extends BaseServiceImpl<DeviceLoop> implements DeviceLoopService {

}