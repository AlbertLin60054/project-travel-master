package com.tm.TravelMaster.sean.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tm.TravelMaster.sean.model.LinePayRequest;
import com.tm.TravelMaster.sean.model.LinePayResponse;
import com.tm.TravelMaster.sean.model.LinePayStatusResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;

@Service
public class LinePayService {

	// 設置參數
	private final RestTemplate restTemplate;
	private final String linePayUrl = "https://sandbox-api-pay.line.me/v2/payments/request"; // 測試環境使用這個URL
	private final String channelId = "1661550356";
	private final String channelSecret = "5c5a58ed986b89bf64769f8ad17664f0";

	public LinePayService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	// 付款API
	public LinePayResponse payMoney(LinePayRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-LINE-ChannelId", channelId);
		headers.set("X-LINE-ChannelSecret", channelSecret);

		HttpEntity<LinePayRequest> entity = new HttpEntity<>(request, headers);

		return restTemplate.postForObject(linePayUrl, entity, LinePayResponse.class);
	}

	// 交易驗證API
	public LinePayStatusResponse checkPaymentStatus(String transactionId, int totalPrice, String currency) {
		String paymentStatusUrl = "https://sandbox-api-pay.line.me/v2/payments/" + transactionId + "/confirm";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-LINE-ChannelId", channelId);
		headers.set("X-LINE-ChannelSecret", channelSecret);

		// 創建包含金額和貨幣的請求體
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("amount", totalPrice);
		requestBody.put("currency", currency);

		// 將請求體轉換為JSON格式，並設置到HttpEntity中
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<LinePayStatusResponse> responseEntity = restTemplate.exchange(paymentStatusUrl, HttpMethod.POST,
				entity, LinePayStatusResponse.class);

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		} else {
			// 處理錯誤
			return null;
		}
	}

}
