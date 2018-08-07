package com.xiao.tmall.service.impl;

import com.xiao.tmall.mapper.ProductMapper;
import com.xiao.tmall.pojo.Category;
import com.xiao.tmall.pojo.Product;
import com.xiao.tmall.pojo.ProductExample;
import com.xiao.tmall.pojo.ProductImage;
import com.xiao.tmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @Override
    public void add(Product product){
        productMapper.insert(product);
    }

    @Override
    public void delete(int id){
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Product get(int id){
        Product p = productMapper.selectByPrimaryKey(id);
        setFirstProductImage(p);
        setCategory(p);
        return p;
    }

    @Override
    public void update(Product product){
        productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public List<Product> list(int cid){
        ProductExample example = new ProductExample();
        example.createCriteria().andCidEqualTo(cid);
        example.setOrderByClause("id desc");
        List result = productMapper.selectByExample(example);
        setCategory(result);
        setFirstProductImage(result);
        return result;

    }

    public void setCategory(List<Product> ps){
        for (Product p : ps) {
            setCategory(p);
        }
    }
    public void setCategory(Product p){
        int cid = p.getCid();
        Category c = categoryService.get(cid);
        p.setCategory(c);
    }

    @Override
    public void setFirstProductImage(Product p){
        List<ProductImage> pis = productImageService.list(p.getId(), ProductImageService.type_single);
        if(!pis.isEmpty()){
            ProductImage pi = pis.get(0);
            p.setFirstProductImage(pi);
        }
    }

    public void setFirstProductImage(List<Product> ps) {
        for (Product p : ps) {
            setFirstProductImage(p);
        }
    }

    @Override
    public void fill(List<Category> cs){
        for(Category c : cs){
            fill(c);
        }
    }

    @Override
    public void fill(Category c){
        List<Product> ps = list(c.getId());
        c.setProducts(ps);
    }

    @Override
    public void fillByRow(List<Category> cs){
        int productNumberEachRow = 8;
        for(Category c : cs){
            List<Product> products = c.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for(int i = 0; i < products.size(); i += productNumberEachRow){
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            c.setProductsByRow(productsByRow);
        }
    }

    @Override
    public void setSaleAndReviewNumber(Product product){
        int saleCount = orderItemService.getSaleCount(product.getId());
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product.getId());
        product.setReviewCount(reviewCount);
    }

    @Override
    public void setSaleAndReviewNumber(List<Product> ps) {
        for (Product p : ps) {
            setSaleAndReviewNumber(p);
        }
    }

    @Override
    public List<Product> search(String keyword){
        ProductExample example = new ProductExample();
        example.createCriteria().andNameLike("%" + keyword + "%");
        example.setOrderByClause("id desc");
        List<Product> result = productMapper.selectByExample(example);
        setFirstProductImage(result);
        setCategory(result);
        return result;
    }
}
