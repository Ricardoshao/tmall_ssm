package com.xiao.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiao.tmall.pojo.User;
import com.xiao.tmall.service.UserService;
import com.xiao.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("admin_user_list")
    public String list(Model model, Page page){
        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<User> user = userService.list();
        int total = (int) new PageInfo<>(user).getTotal();
        page.setTotal(total);
        model.addAttribute("us", user);
        model.addAttribute("page",page);
        return "admin/listUser";
    }
}
