package com.tm.TravelMaster.index;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.sean.model.ProductBean;
import com.tm.TravelMaster.sean.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController {

	private final ProductService productService;

	public IndexController(ProductService productService) {
		this.productService = productService;
	}

	// 首頁
	@GetMapping("/layout/index")
	public String getProductPageByCount(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {

		// 每頁顯示商品數量
		int pageSize = 3;

		// 計算起始索引
		int startIndex = (page - 1) * pageSize;

		// 查詢指定範圍內商品
		List<ProductBean> products = productService.getProductsByRange(startIndex, pageSize);
		model.addAttribute("products", products);

		return "layout/index";
	}

	// 會員中心
	@GetMapping("/layout/memberCenter")
	public String getMemberCenter(HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");
		// 有登入
		if (member != null) {
			return "layout/memberCenter";
			// 沒登入
		} else {
			return "redirect:/login.controller";
		}
	}
}