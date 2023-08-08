package com.tm.TravelMaster.sean.model;

import lombok.Data;

@Data
public class LinePayResponse {
	private String returnCode;
	private String returnMessage;
	private Info info;
}