package com.tm.TravelMaster.sean.model;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orderItems")
@Data
public class OrderItemsBean {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "orderId")
	private OrdersBean orders;

	// ---------行程-----------
	@Column(name = "productId")
	private Integer productId;
	@Column(name = "productName")
	private String productName;
	@Column(name = "productPrice")
	private Integer productPrice;

	// ---------旅伴-----------
	@Column(name = "playoneId")
	private int playoneId;
	@Column(name = "playoneNick")
	private String playoneNick;
	@Column(name = "playonePrice")
	private Integer playonePrice;

	// ---------訂票-----------
	@Column(name = "ticketID")
	private int ticketID;
	@Column(name = "departuredate")
	private String departuredate;
	@Column(name = "departuretime")
	private String departuretime;
	@Column(name = "departureST")
	private String departureST;
	@Column(name = "destinationST")
	private String destinationST;
	@Column(name = "ticketPrice")
	private Integer ticketPrice;
	@Column(name = "seat")
	private String seat;

	// ---------共用-----------
	@Column(name = "quantity")
	private Integer quantity;
	@Column(name = "subTotal")
	private Integer subTotal;

}