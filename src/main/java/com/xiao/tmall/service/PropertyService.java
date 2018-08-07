package com.xiao.tmall.service;

import com.xiao.tmall.pojo.Property;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PropertyService {
    void add(Property p);
    void update(Property p);
    void delete(int id);
    List<Property> list(int cid);
    Property get(int id);
}
