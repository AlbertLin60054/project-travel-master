package com.tm.TravelMaster.sean.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tm.TravelMaster.sean.model.OrderItemsBean;
import com.tm.TravelMaster.sean.model.OrdersBean;

public interface OrdersRepository extends JpaRepository<OrdersBean, Integer> {

	// 查詢個人訂單
	@Query(value = "SELECT * FROM orders WHERE memberSeq = :memberSeq", nativeQuery = true)
	List<OrdersBean> findByMemberSeq(@Param("memberSeq") int memberSeq);

	// 設置付款-指定會員跟訂單
	OrdersBean findByIdAndMemberSeq(UUID id, int memberSeq);

	// 找單筆ID
	OrdersBean findById(UUID id);

	// 刪除訂單
	@Modifying
	@Query("DELETE FROM OrderItemsBean oi WHERE oi = :orderItem")
	void deleteOrderItem(@Param("orderItem") OrderItemsBean orderItem);

	// 搜尋訂單
	@Query("SELECT o FROM OrdersBean o JOIN o.items item WHERE item.productName LIKE %:keyword%")
	List<OrdersBean> searchOrders(String keyword);
}
