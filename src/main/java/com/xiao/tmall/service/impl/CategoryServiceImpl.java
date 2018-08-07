package com.xiao.tmall.service.impl;

import com.xiao.tmall.mapper.CategoryMapper;
import com.xiao.tmall.pojo.Category;
import com.xiao.tmall.pojo.CategoryExample;
import com.xiao.tmall.service.CategoryService;
import com.xiao.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    CategoryMapper categoryMapper;
    @Override
    public List<Category> list() {
        CategoryExample example =new CategoryExample();
        example.setOrderByClause("id desc");
        return categoryMapper.selectByExample(example);
    }
/*    public List<Category> list(Page page){
        return categoryMapper.list(page);
    }*/

  //  @Override
/*    public int total(){
        return categoryMapper.total();
    }*/

    @Override
    public void add(Category category){
        categoryMapper.insert(category);
    }

    @Override
    public void delete(int id){
        categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Category get(int id){
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Category category){
        categoryMapper.updateByPrimaryKeySelective(category);
    }
}
