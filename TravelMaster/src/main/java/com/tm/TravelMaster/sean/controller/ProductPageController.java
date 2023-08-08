package com.tm.TravelMaster.sean.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.sean.model.ProductBean;
import com.tm.TravelMaster.sean.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductPageController {

	private final ProductService productService;

	public ProductPageController(ProductService productService) {
		this.productService = productService;
	}

	// 圖片
	@GetMapping("/sean/getImage/{productId}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer productId) {
		ProductBean productBean = productService.getProductById(productId);
		byte[] productsImage = productBean.getProductImage();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(productsImage, header, HttpStatus.OK);
	}

	// 商品頁-初始查詢
	@GetMapping("/sean/productPageByCount")
	public String getProductPageByCount(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");

		// 每頁顯示商品數量
		int pageSize = 3;

		// 計算起始索引
		int startIndex = (page - 1) * pageSize;

		// 查詢指定範圍內商品
		List<ProductBean> products = productService.getProductsByRange(startIndex, pageSize);
		model.addAttribute("products", products);

		// 沒登入或一般使用者
		if (member == null || member.getMemberLevel().equals("normal_user")) {
			return "sean/productPageNormalUser";

			// 超級使用者
		} else if (member.getMemberLevel().equals("super_user")) {
			return "sean/productPage";

			// 錯誤情況
		} else {
			return "errorPage";
		}
	}

	// 商品頁-滾動式查詢
	@GetMapping("/sean/productMore")
	public ResponseEntity<Map<String, Object>> getProductMore(@RequestParam int page, HttpSession session) {
		// 查詢每頁顯示數量
		int pageSize = 3;
		int startIndex = (page - 1) * pageSize;

		// 獲取用戶等級
		Member member = (Member) session.getAttribute("mbsession");

		// 獲取商品列表
		List<ProductBean> products = productService.getProductsByRange(startIndex, pageSize);

		// 獲取已加載商品數量
		int loadedProducts = startIndex + products.size();

		// 獲取總商品數量
		int totalProducts = productService.countAllProducts();

		// 判斷是否還有更多商品
		boolean hasMoreProducts = productService.hasMoreProducts(loadedProducts);

		// 構建響應數據
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("products", products);
		responseData.put("loadedProducts", loadedProducts);
		responseData.put("totalProducts", totalProducts);
		responseData.put("hasMoreProducts", hasMoreProducts);
		responseData.put("memberLevel", member != null ? member.getMemberLevel() : null);

		// 返回響應數據
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("hasMoreProducts", String.valueOf(hasMoreProducts));
		responseHeaders.add("totalProducts", String.valueOf(totalProducts));
		responseHeaders.add("loadedProducts", String.valueOf(loadedProducts));

		return ResponseEntity.ok().headers(responseHeaders).body(responseData);
	}

	// 商品頁-模糊查詢
	@GetMapping("/sean/productQuery")
	public String searchProducts(@RequestParam(value = "productType", required = false) String productType,
			@RequestParam(value = "productStartDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date productStartDate,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "productStatus", required = false) String productStatus,
			@RequestParam(value = "search", required = false) String search, HttpSession session, Model model) {
		Member member = (Member) session.getAttribute("mbsession");

		List<ProductBean> products = productService.searchProducts(productType, productStartDate, sort, productStatus,
				search);
		model.addAttribute("products", products);

		if (member == null || member.getMemberLevel().equals("normal_user")) {
			System.out.println("查詢成功");
			return "sean/productPageNormalUser";
		} else if (member.getMemberLevel().equals("super_user")) {
			System.out.println("查詢成功");
			return "sean/productPage";
		} else {
			return "errorPage";
		}
	}

	// 商品頁-新增商品
	@PostMapping("/sean/addProduct")
	public String addProduct(@RequestParam("productName") String productName,
			@RequestParam("productPrice") int productPrice, @RequestParam("productType") String productType,
			@RequestParam("productQuantity") int productQuantity,
			@RequestParam("productStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date productStartDate,
			@RequestParam("productEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date productEndDate,
			@RequestParam("productDescription") String productDescription,
			@RequestParam("productImage") MultipartFile productImage,
			@RequestParam("productStatus") String productStatus) {

		ProductBean product = new ProductBean();
		product.setProductName(productName);
		product.setProductPrice(productPrice);
		product.setProductQuantity(productQuantity);
		product.setProductType(productType);
		product.setProductStartDate(productStartDate);
		product.setProductEndDate(productEndDate);
		product.setProductDescription(productDescription);
		product.setProductStatus(productStatus);
		product.setProductRegistrations(0);

		try {
			byte[] imageBytes = productImage.getBytes();
			product.setProductImage(imageBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		productService.saveOrUpdateProduct(product);

		return "redirect:/sean/productPageByCount";
	}

	// 商品頁-編輯商品
	@PostMapping("/sean/editProduct")
	public String updateProduct(@RequestParam("productId") Integer productId,
			@RequestParam("productName") String productName, @RequestParam("productPrice") int productPrice,
			@RequestParam("productQuantity") int productQuantity, @RequestParam("productType") String productType,
			@RequestParam("productStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date productStartDate,
			@RequestParam("productEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date productEndDate,
			@RequestParam("productDescription") String productDescription,
			@RequestParam("productImage") MultipartFile productImage) {

		ProductBean existingProduct = productService.getProductById(productId);
		if (existingProduct == null) {
			return "redirect:/sean/productPage";
		}

		existingProduct.setProductName(productName);
		existingProduct.setProductPrice(productPrice);
		existingProduct.setProductQuantity(productQuantity);
		existingProduct.setProductType(productType);
		existingProduct.setProductStartDate(productStartDate);
		existingProduct.setProductEndDate(productEndDate);
		existingProduct.setProductDescription(productDescription);

		try {
			byte[] newImageBytes = productImage.getBytes();
			existingProduct.setProductImage(newImageBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		productService.saveOrUpdateProduct(existingProduct);

		return "redirect:/sean/productPageByCount";
	}

	// 商品頁-上下架修改
	@PostMapping("/sean/toggleStatus/{productId}")
	public String toggleStatus(@PathVariable("productId") Integer productId, Model model) {
		ProductBean product = productService.getProductById(productId);
		String currentStatus = product.getProductStatus();

		if (currentStatus.equals("下架")) {
			product.setProductStatus("上架");

		} else if (currentStatus.equals("上架")) {
			product.setProductStatus("下架");
		}

		productService.saveOrUpdateProduct(product);

		return "redirect:/sean/productPageByCount";
	}

}