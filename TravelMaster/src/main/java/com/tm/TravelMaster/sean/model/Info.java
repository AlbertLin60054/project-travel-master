package com.tm.TravelMaster.sean.model;

import lombok.Data;

@Data
public class Info {
	private PaymentUrl paymentUrl;
	private Long transactionId;
	private String paymentAccessToken;
}