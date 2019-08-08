package com.shuzhi.mapper;

import com.shuzhi.entity.TLightPole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-07-14 15:15:36
 */

@Repository
public interface TLightPoleMapper{

    List<TLightPole> findByTlightPole(@Param("strings") List<Integer> strings);

    TLightPole findByTlightPoles(@Param("strings") Integer strings);

    List<TLightPole> findByFrtId(@Param("id") Integer id);
}
