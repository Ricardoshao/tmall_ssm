package com.xiao.tmall.service.impl;

import com.xiao.tmall.mapper.PropertyValueMapper;
import com.xiao.tmall.pojo.*;
import com.xiao.tmall.service.PropertyService;
import com.xiao.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyValueServiceImpl implements PropertyValueService {
    @Autowired
    PropertyValueMapper propertyValueMapper;
    @Autowired
    PropertyService propertyService;

    @Override
    public void init(Product product){
     //   Category category = product.getCategory();
        List<Property> pts = propertyService.list(product.getCid());
        for(Property pt : pts ){
            PropertyValue pv = get(pt.getId(), product.getId());
            if(pv == null){
                pv = new PropertyValue();
                pv.setPid(product.getId());
                pv.setPtid(pt.getId());
                propertyValueMapper.insert(pv);
            }
        }

    }

    @Override
    public void update(PropertyValue pv){
        propertyValueMapper.updateByPrimaryKey(pv);
    }

    @Override
    public PropertyValue get(int ptid,int pid){
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria()
                .andPtidEqualTo(ptid)
                .andPidEqualTo(pid);
        List<PropertyValue> pvs = propertyValueMapper.selectByExample(example);
        if(pvs.isEmpty()){
            return null;
        }
        return pvs.get(0);
    }

    @Override
    public List<PropertyValue> list(int pid){
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria()
                .andPidEqualTo(pid);
        List<PropertyValue> result = propertyValueMapper.selectByExample(example);
        for(PropertyValue pv : result ){
            Property property = propertyService.get(pv.getPtid());
            pv.setProperty(property);
        }
        return result;

    }
}
