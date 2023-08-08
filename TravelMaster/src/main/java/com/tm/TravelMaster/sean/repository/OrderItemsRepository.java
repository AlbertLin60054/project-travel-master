package com.tm.TravelMaster.sean.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tm.TravelMaster.sean.model.OrderItemsBean;

public interface OrderItemsRepository extends JpaRepository<OrderItemsBean, Integer> {
}
