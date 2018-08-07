package com.xiao.tmall.service;

import com.xiao.tmall.mapper.ProductMapper;
import com.xiao.tmall.pojo.Category;
import com.xiao.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ProductService {


    void add(Product product);

    void delete(int id);

    Product get(int id);

    void update(Product product);

    List<Product> list(int cid);

    void setFirstProductImage(Product p);
    void fill(List<Category> cs);

    void fill(Category c);

    void fillByRow(List<Category> cs);

    void setSaleAndReviewNumber(Product p);

    void setSaleAndReviewNumber(List<Product> ps);

    List<Product> search(String keyword);

}
