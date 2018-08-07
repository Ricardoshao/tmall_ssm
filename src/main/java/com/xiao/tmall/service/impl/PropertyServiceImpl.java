package com.xiao.tmall.service.impl;


import com.xiao.tmall.mapper.PropertyMapper;
import com.xiao.tmall.pojo.Property;
import com.xiao.tmall.pojo.PropertyExample;
import com.xiao.tmall.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    PropertyMapper propertyMapper;
    @Override
    public void add(Property p){
        propertyMapper.insert(p);
    }

    @Override
    public void update(Property p){
        propertyMapper.updateByPrimaryKeySelective(p);
    }

    @Override
    public void delete(int id){
        propertyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Property> list(int cid){
        PropertyExample example = new PropertyExample();
        example.createCriteria().andCidEqualTo(cid);
        example.setOrderByClause("id desc");
        return propertyMapper.selectByExample(example);
    }

    @Override
    public Property get(int id){
        return propertyMapper.selectByPrimaryKey(id);
    }
}
