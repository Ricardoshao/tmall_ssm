package com.xiao.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiao.tmall.pojo.Category;
import com.xiao.tmall.pojo.Property;
import com.xiao.tmall.service.CategoryService;
import com.xiao.tmall.service.PropertyService;
import com.xiao.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class PropertyController {
    @Autowired
    PropertyService propertyService;
    @Autowired
    CategoryService categoryService;

    @RequestMapping("admin_property_list")
    public String list(int cid, Page page, Model model){
        Category c = categoryService.get(cid);

        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Property> ps = propertyService.list(cid);

        int total = (int) new PageInfo<>(ps).getTotal();
        page.setTotal(total);
        page.setParam("&cid=" + c.getId());

        model.addAttribute("c",c);
        model.addAttribute("ps", ps);
        model.addAttribute("page",page);
        return "admin/listProperty";
    }

    @RequestMapping("admin_property_add")
    public String add(Property property){
        propertyService.add(property);
        return "redirect:/admin_property_list?cid=" + property.getCid();

    }

    @RequestMapping("admin_property_delete")
    public String delete(int id){
        Property p = propertyService.get(id);
        propertyService.delete(id);
        return "redirect:/admin_property_list?cid=" + p.getCid();
    }

    @RequestMapping("admin_property_edit")
    public String edit(int id, Model model){
        Property p = propertyService.get(id);
        Category c = categoryService.get(p.getCid());
        p.setCategory(c);
        model.addAttribute("p", p);
        return "admin/editProperty";
    }

    @RequestMapping("admin_property_update")
    public String update(Property property){
        propertyService.update(property);
        return "redirect:/admin_property_list?cid=" + property.getCid();
    }


}
