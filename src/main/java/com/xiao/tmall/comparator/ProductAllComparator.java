package com.xiao.tmall.comparator;

import com.xiao.tmall.pojo.Product;

import java.util.Comparator;

public class ProductAllComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount() * p2.getReviewCount() - p1.getSaleCount() * p1.getReviewCount();
    }
}
