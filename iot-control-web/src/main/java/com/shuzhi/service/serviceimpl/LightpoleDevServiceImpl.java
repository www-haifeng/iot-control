package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.LightpoleDev;
import com.shuzhi.service.LightpoleDevService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class LightpoleDevServiceImpl extends BaseServiceImpl<LightpoleDev> implements LightpoleDevService {

}