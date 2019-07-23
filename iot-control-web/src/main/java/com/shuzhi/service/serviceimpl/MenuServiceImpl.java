package com.shuzhi.service.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl {

}