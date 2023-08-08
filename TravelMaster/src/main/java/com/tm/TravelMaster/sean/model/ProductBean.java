package com.tm.TravelMaster.sean.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class ProductBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer productId;

	@Column(name = "productType")
	private String productType;

	@Column(name = "productName")
	private String productName;

	@Lob
	@Column(name = "productImage")
	private byte[] productImage;

	@Column(name = "productPrice")
	private int productPrice;

	@Column(name = "productRegistrations")
	private int productRegistrations;

	@Column(name = "productQuantity")
	private int productQuantity;

	@Column(name = "productDescription")
	private String productDescription;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "productStartDate")
	private Date productStartDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "productEndDate")
	private Date productEndDate;

	@Column(name = "productStatus")
	private String productStatus;

}
