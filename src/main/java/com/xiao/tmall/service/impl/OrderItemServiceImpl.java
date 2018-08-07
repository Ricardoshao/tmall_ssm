package com.xiao.tmall.service.impl;

import com.xiao.tmall.mapper.OrderItemMapper;
import com.xiao.tmall.pojo.Order;
import com.xiao.tmall.pojo.OrderItem;
import com.xiao.tmall.pojo.OrderItemExample;
import com.xiao.tmall.pojo.Product;
import com.xiao.tmall.service.OrderItemService;
import com.xiao.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ProductService productService;

    @Override
    public void add(OrderItem orderItem){
        orderItemMapper.insert(orderItem);
    }

    @Override
    public void delete(int id){
        orderItemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(OrderItem orderItem){
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
    }

    @Override
    public OrderItem get(int id){
        OrderItem result = orderItemMapper.selectByPrimaryKey(id);
        setProduct(result);
        return result;
    }

    @Override
    public List<OrderItem> list(){
        OrderItemExample example = new OrderItemExample();
        example.setOrderByClause("id desc");
        return orderItemMapper.selectByExample(example);
    }

    @Override
    public void fill(List<Order> orders){
        for(Order o : orders){
            fill(o);
        }
    }

    @Override
    public void fill(Order order){
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOidEqualTo(order.getId());
        example.setOrderByClause("id desc");
        List<OrderItem> ois = orderItemMapper.selectByExample(example);
        setProduct(ois);
        float total = 0;
        int totalNumber = 0;
        for(OrderItem oi : ois){
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
        }
        order.setTotal(total);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(ois);
    }

    public void setProduct(List<OrderItem> ois){
        for (OrderItem oi: ois) {
            setProduct(oi);
        }
    }

    private void setProduct(OrderItem oi) {
        Product p = productService.get(oi.getPid());
        oi.setProduct(p);
    }

    @Override
    public int getSaleCount(int pid) {
        OrderItemExample example =new OrderItemExample();
        example.createCriteria().andPidEqualTo(pid);
        List<OrderItem> ois =orderItemMapper.selectByExample(example);
        int result =0;
        for (OrderItem oi : ois) {
            result+=oi.getNumber();
        }
        return result;
    }

    @Override
    public List<OrderItem> listByUser(int uid){
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andUidEqualTo(uid).andOidIsNull();;
        example.setOrderByClause("id desc");
        List<OrderItem> result = orderItemMapper.selectByExample(example);
        setProduct(result);
        return result;
    }


}
