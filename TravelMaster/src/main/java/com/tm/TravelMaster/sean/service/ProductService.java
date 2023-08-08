package com.tm.TravelMaster.sean.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tm.TravelMaster.sean.model.ProductBean;
import com.tm.TravelMaster.sean.repository.*;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductService {

	// 導入JPA
	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	// 查詢所有商品
	public List<ProductBean> getAllProducts() {
		return productRepository.findAll();
	}

	// 模糊查詢
	public List<ProductBean> searchProducts(String productType, Date productStartDate, String sort,
			String productStatus, String search) {
		Specification<ProductBean> spec = Specification.where(null);
		if (productType != null && !productType.isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("productType"), productType));
		}
		if (productStartDate != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("productStartDate"), productStartDate));
		}
		if (productStatus != null && !productStatus.isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("productStatus"), productStatus));
		}
		if (search != null && !search.isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.or(cb.like(root.get("productName"), "%" + search + "%"),
					cb.like(root.get("productDescription"), "%" + search + "%")));
		}

		Sort sortCondition = null;
		if (sort != null && !sort.isEmpty()) {
			sortCondition = Sort.by("productPrice");
			if ("desc".equals(sort)) {
				sortCondition = sortCondition.descending();
			} else {
				sortCondition = sortCondition.ascending();
			}
		}

		if (sortCondition != null) {
			return productRepository.findAll(spec, sortCondition);
		} else {
			return productRepository.findAll(spec);
		}
	}

	// 指定查詢幾筆
	public List<ProductBean> getProductsByRange(int startIndex, int pageSize) {
		return productRepository.findProductsByRange(startIndex, startIndex + pageSize);
	}

	// 往下顯示更多判斷是否還有商品
	public boolean hasMoreProducts(int loadedProducts) {
		int totalProducts = productRepository.countAllProducts();
		return loadedProducts < totalProducts;
	}

	// 計算總商品數量
	public int countAllProducts() {
		int totalProducts = productRepository.countAllProducts();
		return totalProducts;
	}

	// 取商品id
	public ProductBean getProductById(Integer productId) {
		if (productId == null) {
			return null;
		}
		return productRepository.findById(productId).orElse(null);
	}

	// 儲存或更新
	public void saveOrUpdateProduct(ProductBean product) {
		productRepository.save(product);
	}

}