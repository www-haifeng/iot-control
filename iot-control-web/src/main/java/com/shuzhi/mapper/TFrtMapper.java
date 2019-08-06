package com.shuzhi.mapper;

import com.shuzhi.common.basemapper.MyBaseMapper;
import com.shuzhi.entity.Station;
import com.shuzhi.websocket.socketvo.Groups;
import org.springframework.stereotype.Repository;

/**
 * @author huliang
 * @date 2019-07-23 11:31:25
 */

@Repository
public interface TFrtMapper extends MyBaseMapper<Station> {
    Groups findById(Integer frtid);
}
