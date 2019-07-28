package com.shuzhi.common.basemapper;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zgk
 * @description 通用service实现类
 * @date 2019-04-29 11:52
 */
public class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    private MyBaseMapper<T> baseMapper;

    @Override
    public int deleteByPrimaryKey(Object key) {
        return baseMapper.deleteByPrimaryKey(key);
    }

    @Override
    public int delete(T record) {
        return baseMapper.delete(record);
    }

    @Override
    public int insert(T record) {
        return baseMapper.insert(record);
    }

    @Override
    public int insertSelective(T record) {
        return baseMapper.insertSelective(record);
    }

    @Override
    public boolean existsWithPrimaryKey(Object key) {
        return baseMapper.existsWithPrimaryKey(key);
    }

    @Override
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    @Override
    public T selectByPrimaryKey(Object key) {
        return baseMapper.selectByPrimaryKey(key);
    }

    @Override
    public int selectCount(T record) {
        return baseMapper.selectCount(record);
    }

    @Override
    public List<T> select(T record) {
        return baseMapper.select(record);
    }

    @Override
    public T selectOne(T record) {
        return baseMapper.selectOne(record);
    }

    @Override
    public int updateByPrimaryKey(T record) {
        return baseMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        return baseMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByCondition(Object condition) {
        return baseMapper.deleteByCondition(condition);
    }

    @Override
    public List<T> selectByCondition(Object condition) {
        return baseMapper.selectByCondition(condition);
    }

    @Override
    public int selectCountByCondition(Object condition) {
        return baseMapper.selectCountByCondition(condition);
    }

    @Override
    public int updateByCondition(T record, Object condition) {
        return baseMapper.updateByCondition(record,condition);
    }

    @Override
    public int updateByConditionSelective(T record, Object condition) {
        return baseMapper.updateByConditionSelective(record,condition);
    }

    @Override
    public int deleteByExample(Object example) {
        return baseMapper.deleteByExample(example);
    }

    @Override
    public List<T> selectByExample(Object example) {
        return baseMapper.selectByExample(example);
    }

    @Override
    public int selectCountByExample(Object example) {
        return baseMapper.selectCountByExample(example);
    }

    @Override
    public T selectOneByExample(Object example) {
        return baseMapper.selectOneByExample(example);
    }

    @Override
    public int updateByExample(T record, Object example) {
        return baseMapper.updateByExample(record,example);
    }

    @Override
    public int updateByExampleSelective(T record, Object example) {
        return baseMapper.updateByExampleSelective(record,example);
    }

    @Override
    public int deleteByIds(String ids) {
        return baseMapper.deleteByIds(ids);
    }

    @Override
    public List<T> selectByIds(String ids) {
        return baseMapper.selectByIds(ids);
    }

    @Override
    public int insertList(List<? extends T> recordList) {
        return baseMapper.insertList(recordList);
    }

    @Override
    public int insertUseGeneratedKeys(T record) {
        return baseMapper.insertUseGeneratedKeys(record);
    }
}
