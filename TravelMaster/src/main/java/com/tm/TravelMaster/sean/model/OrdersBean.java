package com.tm.TravelMaster.sean.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "orders")
@Data
public class OrdersBean {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "memberSeq")
	private int memberSeq;

	@Column(name = "TotalPrice")
	private int totalPrice;

	@Column(name = "transactionId")
	private String transactionId;

	@Column(name = "paymentStatus")
	private String paymentStatus;

	@Column(name = "createdAt")
	private LocalDateTime createdAt;

	@Column(name = "paidAt")
	private LocalDateTime paidAt;

	@OneToMany(mappedBy = "orders")
	private List<OrderItemsBean> items;

}