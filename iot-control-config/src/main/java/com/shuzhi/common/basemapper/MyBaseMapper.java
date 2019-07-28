package com.shuzhi.common.basemapper;

import tk.mybatis.mapper.common.*;

/**
 * @author zgk
 * @description 通用mapper接口
 * @date 2019-04-29 9:47
 */
public interface MyBaseMapper<T> extends BaseMapper<T>, MySqlMapper<T>, IdsMapper<T>, ConditionMapper<T>, ExampleMapper<T> {


}
