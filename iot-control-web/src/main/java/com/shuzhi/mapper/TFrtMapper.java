package com.shuzhi.mapper;

import com.shuzhi.entity.Group;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author huliang
 * @date 2019-07-23 11:31:25
 */

@Repository
public interface TFrtMapper {
    Group findById(@Param("frtid") Integer frtid);

    List<Group> findAll();
}
