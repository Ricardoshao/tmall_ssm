package com.xiao.tmall.controller;


import com.xiao.tmall.pojo.Product;
import com.xiao.tmall.pojo.PropertyValue;
import com.xiao.tmall.service.ProductService;
import com.xiao.tmall.service.PropertyService;
import com.xiao.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @RequestMapping("admin_propertyValue_edit")
    public String edit(Model model, int pid){
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> pvs = propertyValueService.list(product.getId());
        model.addAttribute("p", product);
        model.addAttribute("pvs", pvs);
        return "admin/editPropertyValue";
    }

    @RequestMapping("admin_propertyValue_update")
    @ResponseBody
    public String update(PropertyValue pv){
        propertyValueService.update(pv);
        return "success";
    }
}
