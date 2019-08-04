package com.shuzhi.mapper;

import com.shuzhi.entity.DeviceLoop;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.shuzhi.common.basemapper.MyBaseMapper;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-07-14 15:15:36
 */

@Repository
public interface DeviceLoopMapper extends MyBaseMapper<DeviceLoop> {


    /**
     * 通过公交站id查询该站下所有的信息
     *
     * @param stationId 公交站id
     * @return 查询结果
     */
    List<DeviceLoop> findByStationId(@Param("stationId") Integer stationId);

    List<Integer> findByLamppostId(@Param("did") String did,@Param("name") String name);
}
