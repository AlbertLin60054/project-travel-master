package com.tm.TravelMaster.sean.model;

import java.util.UUID;

import lombok.Data;

@Data
public class LinePayRequest {
	private UUID orderId;
	private int amount;
	private String currency;
	private String confirmUrl;
	private String ProductName;
}
