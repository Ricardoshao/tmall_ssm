package com.xiao.tmall.service.impl;

import com.xiao.tmall.mapper.OrderItemMapper;
import com.xiao.tmall.mapper.OrderMapper;
import com.xiao.tmall.pojo.*;
import com.xiao.tmall.service.OrderItemService;
import com.xiao.tmall.service.OrderService;
import com.xiao.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserService userService;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public void add(Order order){
        orderMapper.insert(order);
    }

    @Override
    public void delete(int id){
        orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public Order get(int id){
        return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Order> list(){
        OrderExample example = new OrderExample();
        example.setOrderByClause("id desc");
        List<Order> result =orderMapper.selectByExample(example);
        setUser(result);
        return result;
    }

    public void setUser(List<Order> os){
        for(Order o : os){
            setUser(o);
        }
    }

    public void setUser(Order order){
        int uid = order.getUid();
        User user = userService.get(uid);
        order.setUser(user);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public float add(Order o, List<OrderItem> ois) {
        float total = 0;
        add(o);

        if(false) {
            throw new RuntimeException();
        }

        for (OrderItem oi: ois) {
            oi.setOid(o.getId());
            orderItemService.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return total;
    }

    @Override
    public List<Order> list(int uid, String excludedStatus){
        OrderExample example = new OrderExample();
        example.createCriteria().andUidEqualTo(uid).andStatusNotEqualTo(excludedStatus);
        example.setOrderByClause("id desc");
        return orderMapper.selectByExample(example);
    }


}
