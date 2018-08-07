package com.xiao.tmall.service.impl;

import com.sun.org.apache.regexp.internal.RE;
import com.xiao.tmall.mapper.ReviewMapper;
import com.xiao.tmall.pojo.Review;
import com.xiao.tmall.pojo.ReviewExample;
import com.xiao.tmall.pojo.User;
import com.xiao.tmall.service.ReviewService;
import com.xiao.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewMapper reviewMapper;
    @Autowired
    UserService userService;

    @Override
    public void add(Review review){
        reviewMapper.insert(review);
    }

    @Override
    public void delete(int id){
        reviewMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Review review){
        reviewMapper.updateByPrimaryKeySelective(review);
    }

    @Override
    public Review get(int id){
        return reviewMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Review> list(int pid){
        ReviewExample example = new ReviewExample();
        example.createCriteria().andPidEqualTo(pid);
        example.setOrderByClause("id desc");
        List<Review> result = reviewMapper.selectByExample(example);
        setUser(result);
        return result;
    }

    @Override
    public int getCount(int pid){
        return list(pid).size();
    }

    public void setUser(List<Review> reviews){
        for (Review review : reviews) {
            setUser(review);
        }
    }

    private void setUser(Review review) {
        int uid = review.getUid();
        User user =userService.get(uid);
        review.setUser(user);
    }

}
