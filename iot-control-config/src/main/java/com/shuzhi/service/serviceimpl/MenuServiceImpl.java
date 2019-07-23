package com.shuzhi.service.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.shuzhi.eum.WebEum.*;


/**
 * @author shuzhi
 * @date 2019-07-04 15:04:42
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl {

}