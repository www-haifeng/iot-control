package com.shuzhi.service.serviceimpl;

import com.shuzhi.common.basemapper.BaseServiceImpl;
import com.shuzhi.entity.Group;
import com.shuzhi.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class GroupServiceImpl extends BaseServiceImpl<Group> implements GroupService {

}