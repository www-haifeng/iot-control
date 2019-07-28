package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.MqMessage;
import com.shuzhi.service.MqMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author shuzhi
 * @date 2019-07-07 14:32:21
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class MqMessageServiceImpl extends BaseServiceImpl<MqMessage> implements MqMessageService {

}