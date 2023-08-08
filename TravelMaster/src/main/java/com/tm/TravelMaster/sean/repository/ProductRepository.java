package com.tm.TravelMaster.sean.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tm.TravelMaster.sean.model.ProductBean;

public interface ProductRepository extends JpaRepository<ProductBean, Integer>, JpaSpecificationExecutor<ProductBean> {

	// 查詢三筆
	@Query(value = "SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY productId ASC) AS rowNum, p.* FROM product p) AS sub WHERE rowNum > :rowNum AND rowNum <= :top", nativeQuery = true)
	List<ProductBean> findProductsByRange(@Param("rowNum") int rowNum, @Param("top") int top);

	// 計算商品數量
	@Query(value = "SELECT COUNT(*) FROM product", nativeQuery = true)
	int countAllProducts();

	// 模糊查詢
	List<ProductBean> findAll(Specification<ProductBean> spec, Sort sort);

}
