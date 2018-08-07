package com.xiao.tmall.service;

import com.xiao.tmall.pojo.Product;
import com.xiao.tmall.pojo.PropertyValue;

import java.util.List;

public interface PropertyValueService {

    void update(PropertyValue propertyValue);
    void init(Product product);
    PropertyValue get(int ptid, int pid);
    List<PropertyValue> list(int pid);

}
