package com.shuzhi.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.Station;
import com.shuzhi.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author shuzhi
 * @date 2019-07-23 11:31:25
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class StationServiceImpl extends BaseServiceImpl<Station> implements StationService {

}