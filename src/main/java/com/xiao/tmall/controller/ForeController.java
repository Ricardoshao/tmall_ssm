package com.xiao.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.xiao.tmall.comparator.*;
import com.xiao.tmall.pojo.*;
import com.xiao.tmall.service.*;
import com.xiao.tmall.util.Page;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ForeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @RequestMapping("forehome")
    public String home(Model model){
        List<Category> cs = categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        model.addAttribute("cs", cs);
        return "fore/home";

    }

    @RequestMapping("foreregister")
    public String register(Model model, User user){
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        boolean userIsExist = userService.isExist(name);
        if(userIsExist){
            String msg = "此用户名已存在";
            model.addAttribute("msg",msg);
            model.addAttribute("user", null);
            return "fore/register";
        }

        userService.add(user);
        return "redirect:registerSuccessPage";

    }

    @RequestMapping("forelogin")
    public String login(@RequestParam("name") String name, @RequestParam("password") String password, Model model, HttpSession session){
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if(null == user){
            model.addAttribute("msg", "账号或密码错误");
            return "fore/login";
        }
        session.setAttribute("user", user);
        return "redirect:forehome";
    }

    @RequestMapping("forelogout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:forehome";

    }

    @RequestMapping("foreproduct")
    public String product(int pid, Model model){
        Product product = productService.get(pid);
        List<ProductImage> productSingleImages = productImageService.list(product.getId(), ProductImageService.type_single);
        List<ProductImage> productDetailImages = productImageService.list(product.getId(), ProductImageService.type_detail);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        List<PropertyValue> pvs = propertyValueService.list(product.getId());
        List<Review> reviews= reviewService.list(product.getId());

        productService.setSaleAndReviewNumber(product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("p", product);
        model.addAttribute("pvs",pvs);
        return "fore/product";
    }

    @RequestMapping("forecheckLogin")
    @ResponseBody
    public String checkLogin(HttpSession session){
        User user = (User)session.getAttribute("user");
        if(null != user){
            return "success";
        }
        return "fail";
    }

    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session){
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if(null == user){
            return "fail";
        }
        session.setAttribute("user", user);
        return "success";
    }

    @RequestMapping("forecategory")
    public String category(int cid, String sort, Model model){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        if(null != sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        model.addAttribute("c",category);
        return "fore/category";
    }

    @RequestMapping("foresearch")
    public String serch(String keyword, Model model){
        PageHelper.offsetPage(0,20);
        List<Product> ps = productService.search(keyword);
        productService.setSaleAndReviewNumber(ps);
        model.addAttribute("ps",ps);
        return "fore/searchResult";
    }

    @RequestMapping("forebuyone")
    public String buyone(int pid, int num, HttpSession session, Model model){
        Product product = productService.get(pid);
        User user = (User) session.getAttribute("user");
        int oiid = 0;
        boolean found = false;

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for(OrderItem oi : ois){
            if(oi.getProduct().getId().intValue() == product.getId().intValue()){
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                oiid = oi.getId();
                found = true;
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setPid(pid);
            oi.setNumber(num);
            oi.setUid(user.getId());
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return "redirect:forebuy?oiid="+oiid;
    }

    @RequestMapping("forebuy")
    public String buy( Model model,String[] oiid,HttpSession session){
        List<OrderItem> ois = new ArrayList<>();
        float total = 0;
        for (String strid : oiid){
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
         //   total = 1;
            ois.add(oi);
        }

        session.setAttribute("ois",ois);
        model.addAttribute("total",total);
        return "fore/buy";
    }

    @RequestMapping("foreaddCart")
    @ResponseBody
    public String addCart(int pid, int num, Model model,HttpSession session) {
        Product product = productService.get(pid);
        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for(OrderItem oi : ois){
            if(oi.getProduct().getId().intValue() == product.getId().intValue()){
                oi.setNumber(num + oi.getNumber());
                orderItemService.update(oi);
                found = true;
                break;
            }
        }
        if(!found){
            OrderItem oi = new OrderItem();
            oi.setNumber(num);
            oi.setPid(pid);
            oi.setUid(user.getId());
            orderItemService.add(oi);
        }

        return "success";
    }

    @RequestMapping("forecart")
    public String cart( Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        model.addAttribute("ois", ois);
        return  "fore/cart";
    }

    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(HttpSession session, int pid, int number){
        User user = (User) session.getAttribute("user");
        if (null == user){
            return "fail";
        }
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for (OrderItem oi : ois){
            if (oi.getProduct().getId().intValue() == pid){
                oi.setNumber(number);
                orderItemService.update(oi);
                break;
            }
        }

        return "success";
    }

    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(HttpSession session,int oiid){
        User user = (User) session.getAttribute("user");
        if (null == user){
            return "fail";
        }
        orderItemService.delete(oiid);
        return "success";
    }

    @RequestMapping("forecreateOrder")
    public String createOrder( Model model,Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUid(user.getId());
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");

        float total =orderService.add(order,ois);
        return "redirect:forealipay?oid="+order.getId() +"&total="+total;
    }

    @RequestMapping("forepayed")
    public String payed(int oid, float total, Model model) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        model.addAttribute("o",order);
        return "fore/payed";
    }

    @RequestMapping("forebought")
    public String bought( Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Order> os = orderService.list(user.getId(), OrderService.delete);
        orderItemService.fill(os);
        model.addAttribute("os",os);
        return "fore/bought";
    }

    @RequestMapping("foreconfirmPay")
    public String confirmPay( Model model,int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        model.addAttribute("o",order);
        return "fore/confirmPay";
    }

    @RequestMapping("foreorderConfirmed")
    public String orderConfirmed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setCreateDate(new Date());
        orderService.update(order);
        return "fore/orderConfirmed";
    }

    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder(int oid){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return "success";
    }

    @RequestMapping("forereview")
    public String review( Model model,int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(product.getId());
        productService.setSaleAndReviewNumber(product);
        model.addAttribute("p", product);
        model.addAttribute("o", order);
        model.addAttribute("reviews", reviews);
        return "fore/review";
    }

    @RequestMapping("foredoreview")
    public String doreview( Model model,HttpSession session,@RequestParam("oid") int oid,@RequestParam("pid") int pid,String content) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);
        Product product = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);
        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setPid(pid);
        review.setCreateDate(new Date());
        review.setUid(user.getId());
        reviewService.add(review);
        return "redirect:forereview?oid="+oid+"&showonly=true";

    }
}
