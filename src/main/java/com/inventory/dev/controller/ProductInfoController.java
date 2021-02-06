package com.inventory.dev.controller;

import com.inventory.dev.entity.CategoryEntity;
import com.inventory.dev.entity.Paging;
import com.inventory.dev.entity.ProductInfoEntity;
import com.inventory.dev.service.ProductService;
import com.inventory.dev.util.Constant;
import com.inventory.dev.validate.ProductInfoValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductInfoController {
    static final Logger log = Logger.getLogger(ProductInfoController.class);
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductInfoValidator productInfoValidator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        if (binder.getTarget() == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        if (binder.getTarget().getClass() == ProductInfoEntity.class) {
            binder.setValidator(productInfoValidator);
        }
    }

    @RequestMapping(value = {"/product-info/list", "/product-info/list/"})
    public String redirect() {
        return "redirect:/product-info/list/1";
    }

    @RequestMapping(value = "/product-info/list/{page}")
    public String showProductInfoList(Model model, HttpSession session, @ModelAttribute("searchForm") ProductInfoEntity productInfo, @PathVariable("page") int page) {
        Paging paging = new Paging(5);
        paging.setIndexPage(page);
        List<ProductInfoEntity> products = productService.getAllProductInfo(productInfo, paging);
        if (session.getAttribute(Constant.MSG_SUCCESS) != null) {
            model.addAttribute(Constant.MSG_SUCCESS, session.getAttribute(Constant.MSG_SUCCESS));
            session.removeAttribute(Constant.MSG_SUCCESS);
        }
        if (session.getAttribute(Constant.MSG_ERROR) != null) {
            model.addAttribute(Constant.MSG_ERROR, session.getAttribute(Constant.MSG_ERROR));
            session.removeAttribute(Constant.MSG_ERROR);
        }
        model.addAttribute("pageInfo", paging);
        model.addAttribute("products", products);
        return "productInfo-list";

    }

    @GetMapping("/product-info/add")
    public String add(Model model) {
        model.addAttribute("titlePage", "Add ProductInfo");
        model.addAttribute("modelForm", new ProductInfoEntity());
        List<CategoryEntity> categories = productService.getAllCategory(null, null);
        Map<String, String> mapCategory = new HashMap<>();
        for (CategoryEntity category : categories) {
            mapCategory.put(String.valueOf(category.getId()), category.getName());
        }
        model.addAttribute("mapCategory", mapCategory);
        model.addAttribute("mapCategory", mapCategory);
        model.addAttribute("viewOnly", false);
        return "productInfo-action";
    }

    @GetMapping("/product-info/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        log.info("Edit productInfo with id=" + id);
        ProductInfoEntity productInfo = productService.findByIdProductInfo(id);
        if (productInfo != null) {
            List<CategoryEntity> categories = productService.getAllCategory(null, null);
            Map<String, String> mapCategory = new HashMap<>();
            for (CategoryEntity category : categories) {
                mapCategory.put(String.valueOf(category.getId()), category.getName());
            }
//            productInfo.setCategory(productInfo.getCategory().getId());
            model.addAttribute("mapCategory", mapCategory);
            model.addAttribute("titlePage", "Edit ProductInfo");
            model.addAttribute("modelForm", productInfo);
            model.addAttribute("viewOnly", false);
            return "productInfo-action";
        }
        return "redirect:/product-info/list";
    }

    @GetMapping("/product-info/view/{id}")
    public String view(Model model, @PathVariable("id") int id) {
        log.info("View productInfo with id=" + id);
        ProductInfoEntity productInfo = productService.findByIdProductInfo(id);
        if (productInfo != null) {
            model.addAttribute("titlePage", "View ProductInfo");
            model.addAttribute("modelForm", productInfo);
            model.addAttribute("viewOnly", true);
            return "productInfo-action";
        }
        return "redirect:/product-info/list";
    }

    @PostMapping("/product-info/save")
    public String save(Model model, @ModelAttribute("modelForm") @Validated ProductInfoEntity productInfo, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            if (productInfo.getId() != null) {
                model.addAttribute("titlePage", "Edit ProductInfo");
            } else {
                model.addAttribute("titlePage", "Add ProductInfo");
            }
            List<CategoryEntity> categories = productService.getAllCategory(null, null);
            Map<String, String> mapCategory = new HashMap<>();
            for (CategoryEntity category : categories) {
                mapCategory.put(String.valueOf(category.getId()), category.getName());
            }
            model.addAttribute("mapCategory", mapCategory);
            model.addAttribute("modelForm", productInfo);
            model.addAttribute("viewOnly", false);
            return "productInfo-action";

        }
        CategoryEntity category = new CategoryEntity();
//        category.setId(productInfo.getCateId());
        productInfo.setCategories(category);
        if (productInfo.getId() != null && productInfo.getId() != 0) {
            try {

                productService.updateProductInfo(productInfo);
                session.setAttribute(Constant.MSG_SUCCESS, "Update success!!!");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error(e.getMessage());
                session.setAttribute(Constant.MSG_ERROR, "Update has error");
            }

        } else {
            try {
                productService.saveProductInfo(productInfo);
                session.setAttribute(Constant.MSG_SUCCESS, "Insert success!!!");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                session.setAttribute(Constant.MSG_ERROR, "Insert has error!!!");
            }
        }
        return "redirect:/product-info/list";

    }

    @GetMapping("/product-info/delete/{id}")
    public String delete(Model model, @PathVariable("id") int id, HttpSession session) {
        log.info("Delete productInfo with id=" + id);
        ProductInfoEntity productInfo = productService.findByIdProductInfo(id);
        if (productInfo != null) {
            try {
                productService.deleteProductInfo(productInfo);
                session.setAttribute(Constant.MSG_SUCCESS, "Delete success!!!");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                session.setAttribute(Constant.MSG_ERROR, "Delete has error!!!");
            }
        }
        return "redirect:/product-info/list";
    }
}
