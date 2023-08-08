package com.tm.TravelMaster.sean.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.leo.model.Playone;
import com.tm.TravelMaster.ming.db.service.HighSpeedRailService;
import com.tm.TravelMaster.ming.db.service.TicketInfoService;
import com.tm.TravelMaster.ming.model.entity.TicketInfoGroup;
import com.tm.TravelMaster.ming.model.entity.TicketInfo;
import com.tm.TravelMaster.sean.model.ChartData;
import com.tm.TravelMaster.sean.model.LinePayRequest;
import com.tm.TravelMaster.sean.model.LinePayResponse;
import com.tm.TravelMaster.sean.model.LinePayStatusResponse;
import com.tm.TravelMaster.sean.model.OrdersBean;
import com.tm.TravelMaster.sean.model.ProductBean;
import com.tm.TravelMaster.sean.service.LinePayService;
import com.tm.TravelMaster.sean.service.ShoppingService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ShoppingCartController {

	@Autowired
	private TicketInfoService ticketsService;
	@Autowired
	private HighSpeedRailService highSpeedRailService;

	private final ShoppingService shoppingService;
	private final LinePayService linePayService;

	public ShoppingCartController(ShoppingService shoppingService, LinePayService linePayService) {
		this.shoppingService = shoppingService;
		this.linePayService = linePayService;
	}

	// 購物車-查詢
	@GetMapping("/sean/CartLoginStatus")
	public String handleCartLoginStatus(HttpSession session, Model model) {
		Member member = (Member) session.getAttribute("mbsession");

		if (member != null) {
			String memberNum = member.getMemberNum();

			// 加載購物車資訊
			List<ProductBean> productCart = shoppingService.loadProductCartData(memberNum);
			List<Playone> playoneCart = shoppingService.loadPlayoneCartData(memberNum);
			List<TicketInfoGroup> ShoppingCart = shoppingService.loadTicketCartData(memberNum);

			// 將購物車資料添加到 Model 中
			model.addAttribute("products", productCart);
			model.addAttribute("playones", playoneCart);
			model.addAttribute("tickets", ShoppingCart);

			// 判斷購物車是否為空
			boolean isCartEmpty = productCart.isEmpty() && playoneCart.isEmpty()
					&& ShoppingCart.stream().allMatch(cart -> cart.getTicketInfos().isEmpty());

			// 將判斷結果添加到 Model 中
			model.addAttribute("isCartEmpty", isCartEmpty);

			return "sean/ShoppingCart";
		} else {
			return "redirect:/login.controller";
		}
	}

	// 行程-加入購物車
	@PostMapping(value = "/sean/addToCart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ResponseEntity<String> addToCart(@ModelAttribute("product") ProductBean product, HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");

		if (member == null) {
			return new ResponseEntity<>("請先登入", HttpStatus.UNAUTHORIZED);
		}

		String memberNum = member.getMemberNum();

		// 加載購物車資訊
		List<ProductBean> cart = shoppingService.loadProductCartData(memberNum);

		// 在 Service 層進行產品 ID 判定
		boolean isDuplicate = shoppingService.isProductInCart(cart, product.getProductId());

		if (isDuplicate) {
			return new ResponseEntity<>("商品已存在於購物車中", HttpStatus.OK);
		} else {
			// 添加產品到購物車
			cart.add(product);

			// 保存購物車資訊至本地端 JSON
			shoppingService.saveCartData(memberNum, cart);

			return new ResponseEntity<>("已成功加入商品", HttpStatus.OK);
		}
	}

	// 旅伴-加入購物車
	@PostMapping(value = "/sean/addPlayoneToCart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ResponseEntity<String> addPlayoneToCart(@ModelAttribute("playone") Playone playone, HttpSession session) {

		Member member = (Member) session.getAttribute("mbsession");

		if (member == null) {
			return new ResponseEntity<>("請先登入", HttpStatus.UNAUTHORIZED);
		}

		String memberNum = member.getMemberNum();

		// 加載購物車資訊
		List<Playone> cart = shoppingService.loadPlayoneCartData(memberNum);

		// 在 Service 層進行產品 ID 判定
		boolean isDuplicate = shoppingService.isPlayoneInCart(cart, playone.getPlayoneId());

		if (isDuplicate) {
			return new ResponseEntity<>("商品已存在於購物車中", HttpStatus.OK);
		} else {
			// 添加產品到購物車
			cart.add(playone);

			// 保存購物車資訊至本地端 JSON
			shoppingService.savePlayoneCartData(memberNum, cart);

			return new ResponseEntity<>("已成功加入商品", HttpStatus.OK);
		}
	}

	// 訂票-加入購物車
	@GetMapping(value = "/sean/addTicketToCart")
	@ResponseBody
	public ResponseEntity<String> addaddTicketToCart(@RequestParam("ticketCartId") int ticketCartId,
			HttpSession session) {

		Member member = (Member) session.getAttribute("mbsession");
		if (member == null) {
			return new ResponseEntity<>("請先登入", HttpStatus.UNAUTHORIZED);
		}

		String memberNum = member.getMemberNum();

		TicketInfoGroup lastTicketInfoGroup = ticketsService.findShoppingCartById(ticketCartId); // 最新一筆購物車紀錄
		System.out.println("lastShoppingCart在這: " + lastTicketInfoGroup);
		Map<Integer, String> Id2StationMap = highSpeedRailService.getStationInfoMap(); // 站 ID-名稱 對應表
		Map<String, Integer> Station2IdMap = new HashMap<>(); // 站 ID-名稱 對應表
		for (Entry<Integer, String> entry : Id2StationMap.entrySet()) {
			Station2IdMap.put(entry.getValue(), entry.getKey());
		}
		List<TicketInfo> ticketInfos = lastTicketInfoGroup.getTicketInfos(); // 全部的票
		System.out.println("ticketInfos在這: " + ticketInfos);
		for (TicketInfo ticketInfo : ticketInfos) {
			ticketInfo.setDepartureST(Id2StationMap.get(Integer.parseInt(ticketInfo.getDepartureST())));
			ticketInfo.setDestinationST(Id2StationMap.get(Integer.parseInt(ticketInfo.getDestinationST())));
		}
		// 加載購物車資訊(這邊會對DB資料進行update)
		List<TicketInfoGroup> currentCarts = shoppingService.loadTicketCartData(memberNum);
		if (shoppingService.isCartExists(currentCarts, lastTicketInfoGroup.getCart_Id())) {
			return new ResponseEntity<>("訂票已存在於購物車中", HttpStatus.OK);
		} else {
			// 添加訂票到購物車
			currentCarts.add(lastTicketInfoGroup);
		}
		// 因DB資料被修改，所以要重新修改回去
		shoppingService.saveTicketCartData(memberNum, currentCarts);
		for (TicketInfo ticketInfo : ticketInfos) {
			ticketInfo.setDepartureST(Integer.toString(Station2IdMap.get(ticketInfo.getDepartureST())));
			ticketInfo.setDestinationST(Integer.toString(Station2IdMap.get(ticketInfo.getDestinationST())));
			ticketsService.updateTicketInfo(ticketInfo);
		}
		return new ResponseEntity<>("已成功加入訂票", HttpStatus.OK);
	}

	// 行程-更新報名人數
	@PutMapping("/sean/updateRegistrations")
	public ResponseEntity<Void> updateRegistrations(@RequestParam("productId") Integer productId,
			@RequestParam("productRegistrations") int productRegistrations, HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");
		String memberNum = member.getMemberNum();

		// 加載購物車資訊
		List<ProductBean> cart = shoppingService.loadProductCartData(memberNum);

		// 更新指定商品的數量
		shoppingService.updateCartQuantity(memberNum, cart, productId, productRegistrations);

		// 儲存更新後的購物車資訊到本地 JSON 檔案
		shoppingService.saveCartData(memberNum, cart);

		return ResponseEntity.ok().build();
	}

	// 旅伴-更新天數
	@PutMapping("/sean/playOneDays")
	public ResponseEntity<Void> updateplayOneDays(@RequestParam("playoneId") int playoneId,
			@RequestParam("playoneDays") int playoneDays, HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");
		String memberNum = member.getMemberNum();

		// 加載購物車資訊
		List<Playone> cart = shoppingService.loadPlayoneCartData(memberNum);

		// 更新指定商品的數量
		shoppingService.updatePlayOneCartQuantity(memberNum, cart, playoneId, playoneDays);

		// 儲存更新後的購物車資訊到本地 JSON 檔案
		shoppingService.savePlayoneCartData(memberNum, cart);

		return ResponseEntity.ok().build();
	}

	// 行程-刪除購物車行程
	@PostMapping("/sean/RemoveCartItem")
	public String removeCartItem(@RequestParam("productId") Integer productId, HttpSession session) {
		try {
			Member member = (Member) session.getAttribute("mbsession");
			String memberNum = member != null ? member.getMemberNum() : "";

			List<ProductBean> cart = shoppingService.loadProductCartData(memberNum);

			if (cart != null) {
				Iterator<ProductBean> iterator = cart.iterator();
				while (iterator.hasNext()) {
					ProductBean item = iterator.next();
					if (item.getProductId() == productId) {
						iterator.remove();
						break;
					}
				}

				shoppingService.saveCartData(memberNum, cart);
			}

			return "redirect:/sean/CartLoginStatus";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/sean/CartLoginStatus?error=發生錯誤";
		}
	}

	// 旅伴-刪除購物車旅伴
	@PostMapping("/sean/RemoveCartPlayoneItem")
	public String removeCartPlayoneItem(@RequestParam("playoneId") int playoneId, HttpSession session) {
		try {
			Member member = (Member) session.getAttribute("mbsession");
			String memberNum = member != null ? member.getMemberNum() : "";

			List<Playone> cart = shoppingService.loadPlayoneCartData(memberNum);

			if (cart != null) {
				Iterator<Playone> iterator = cart.iterator();
				while (iterator.hasNext()) {
					Playone item = iterator.next();
					if (item.getPlayoneId() == playoneId) {
						iterator.remove();
						break;
					}
				}

				shoppingService.savePlayoneCartData(memberNum, cart);
			}

			return "redirect:/sean/CartLoginStatus";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/sean/CartLoginStatus?error=發生錯誤";
		}
	}

	// 訂票-刪除購物車旅伴
	@PostMapping("/sean/RemoveCartTicketItem")
	public String removeCartTicketItem(@RequestParam("ticketId") int ticketId, HttpSession session) {
		try {
			Member member = (Member) session.getAttribute("mbsession");
			String memberNum = member != null ? member.getMemberNum() : "";
			// load這邊會update DB的資料
			List<TicketInfoGroup> cart = shoppingService.loadTicketCartData(memberNum);

			// 加載購物車資訊
			if (cart != null) {
				List<TicketInfo> ticketInfos = null;
				Iterator<TicketInfoGroup> iterator = cart.iterator();
				while (iterator.hasNext()) {
					TicketInfoGroup item = iterator.next();
					List<TicketInfo> tickets = item.getTicketInfos();
					for (TicketInfo ticket : tickets) {
						if (ticket.getTicketID() == ticketId) {
							tickets.remove(ticket);
							ticketsService.deleteTicketInfo(ticket);
							if (tickets.size() == 0) {
								ticketsService.deleteShoppingCart(item);
							}
							ticketInfos = tickets;
							break;
						}
					}

				}
				shoppingService.saveTicketCartData(memberNum, cart);

				Map<Integer, String> Id2StationMap = highSpeedRailService.getStationInfoMap(); // 站 ID-名稱 對應表
				Map<String, Integer> Station2IdMap = new HashMap<>(); // 站 ID-名稱 對應表
				for (Entry<Integer, String> entry : Id2StationMap.entrySet()) {
					Station2IdMap.put(entry.getValue(), entry.getKey());
				}
				// 因DB資料被修改，所以要重新修改回去
				for (TicketInfo ticketInfo : ticketInfos) {
					ticketInfo.setDepartureST(Integer.toString(Station2IdMap.get(ticketInfo.getDepartureST())));
					ticketInfo.setDestinationST(Integer.toString(Station2IdMap.get(ticketInfo.getDestinationST())));
					ticketsService.updateTicketInfo(ticketInfo);
				}
			}

			return "redirect:/sean/CartLoginStatus";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/sean/CartLoginStatus?error=發生錯誤";
		}
	}

	// 購物車-結單
	@PostMapping("/sean/checkOut")
	public ResponseEntity<Map<String, String>> checkOut(HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");
		String memberNum = member.getMemberNum();
		if (memberNum != null) {
			List<ProductBean> productCart = shoppingService.loadProductCartData(memberNum);
			List<Playone> playoneCart = shoppingService.loadPlayoneCartData(memberNum);
			List<TicketInfoGroup> tickets = shoppingService.loadTicketCartData(memberNum);

			if ((productCart != null && !productCart.isEmpty()) || (playoneCart != null && !playoneCart.isEmpty())
					|| (tickets != null && !tickets.isEmpty())) {
				// line pay API
				String transactionId = null;
				String paymentStatus = "尚未付款";

				OrdersBean orders = shoppingService.createOrder(member, productCart, playoneCart, tickets,
						transactionId, paymentStatus);

				if (orders != null) {
					// 結單後清除購物車
					productCart.clear();
					playoneCart.clear();
					tickets.clear();
					shoppingService.saveCartData(memberNum, productCart);
					shoppingService.savePlayoneCartData(memberNum, playoneCart);
					shoppingService.saveTicketCartData(memberNum, tickets);

					return new ResponseEntity<>(Collections.singletonMap("message", "Checkout completed successfully."),
							HttpStatus.OK);
				} else {
					return new ResponseEntity<>(
							Collections.singletonMap("message", "Checkout failed. Please try again."),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				return new ResponseEntity<>(Collections.singletonMap("message", "Cart is empty."), HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(Collections.singletonMap("message", "Checkout failed. Please try again."),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// 訂單-付款
	@PostMapping("/sean/payMoney")
	public ResponseEntity<Map<String, String>> payMoney(@RequestBody Map<String, UUID> payload, Model model,
			HttpSession session) {
		UUID orderId = payload.get("orderId");
		// 獲取會員及訂單資訊
		Member member = (Member) session.getAttribute("mbsession");
		int memberSeq = member.getMemberSeq();
		OrdersBean order = shoppingService.getOrderByIdAndMemberSeq(orderId, memberSeq);

		// 將訂單的 ID 和 totalPrice 和 currency 儲存在 session 中
		session.setAttribute("currentOrderId", order.getId());
		session.setAttribute("totalPrice", order.getTotalPrice());
		session.setAttribute("currency", "TWD");

		// 設置傳遞line pay參數
		LinePayRequest request = new LinePayRequest();
		request.setAmount(order.getTotalPrice());
		request.setCurrency("TWD");
		request.setOrderId(order.getId());
		request.setConfirmUrl("http://localhost:8080/TM/sean/confirmURL"); // 設置 confirmUrl
		request.setProductName("付款測試");

		// 印出確定有無取到值
		System.out.println("訂單id: " + order.getId());
		System.out.println("總價totalprice:" + order.getTotalPrice());

		LinePayResponse response = linePayService.payMoney(request);
		System.out.println("request: " + request.toString());
		System.out.println("response: " + response.toString());

		if (response != null && response.getInfo() != null && response.getInfo().getPaymentUrl() != null) {
			String paymentUrl = response.getInfo().getPaymentUrl().getWeb();
			return new ResponseEntity<>(Collections.singletonMap("paymentUrl", paymentUrl), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(Collections.singletonMap("error", "No payment URL found"),
					HttpStatus.BAD_REQUEST);
		}
	}

	// 交易驗證
	@GetMapping("/sean/confirmURL")
	public String confirmURL(@RequestParam String transactionId, HttpSession session,
			RedirectAttributes redirectAttrs) {

		// 從 session 中取出訂單的 totalPrice
		int totalPrice = (int) session.getAttribute("totalPrice");

		System.out.println("totalPrice session: " + totalPrice);

		// 調用 Line Pay API 進行交易驗證
		LinePayStatusResponse paymentStatus = linePayService.checkPaymentStatus(transactionId, totalPrice, "TWD");

		// 根據付款狀態碼進行相應的處理
		if ("0000".equals(paymentStatus.getReturnCode())) {
			// 付款成功，更新訂單狀態
			// 從 session 中取出當前訂單的 ID
			UUID orderId = (UUID) session.getAttribute("currentOrderId");
			OrdersBean order = shoppingService.getOrderById(orderId);
			System.out.println("當前訂單id: " + orderId);
			order.setPaymentStatus("付款成功"); // 將訂單設為已付款
			order.setTransactionId(transactionId);
			order.setPaidAt(LocalDateTime.now());
			shoppingService.updateOrder(order); // 更新訂單狀態

			session.setAttribute("paymentStatus", "Checkout completed!");

			// 返回成功的視圖
			return "redirect:/sean/getPersonalOrder";
		} else {
			// 付款失敗
			session.setAttribute("paymentStatus", "Checkout failed with status: " + paymentStatus.getReturnCode());

			// 返回失敗的視圖
			return "redirect:/sean/paymentError";
		}
	}

	// 全部訂單查詢
	@GetMapping("/sean/getAllOrder")
	public String getOrder(Model model, HttpSession session) {
		List<OrdersBean> orders = shoppingService.getAllOrders();
		model.addAttribute("orders", orders);

		Member member = (Member) session.getAttribute("mbsession");
		// 有登入
		if (member != null) {
			return "sean/checkoutConfirmationBackStage";

			// 沒登入
		} else {
			return "redirect:/login.controller";
		}
	}

	// 個人訂單查詢
	@GetMapping("/sean/getPersonalOrder")
	public String getPersonalOrder(Model model, HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");

		// 有登入
		if (member != null) {
			int memberSeq = member.getMemberSeq();
			List<OrdersBean> orders = shoppingService.getMemberOrders(memberSeq);
			model.addAttribute("orders", orders);
			return "sean/checkoutConfirmation";
		} else {
			// 沒登入
			return "redirect:/login.controller";
		}
	}

	// 取消訂單
	@DeleteMapping("/sean/deleteOrder/{orderId}")
	public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId, HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");

		// 有登入
		if (member != null) {
			OrdersBean order = shoppingService.getOrderById(orderId);

			if (order != null) {
				String paymentStatus = order.getPaymentStatus();
				if (paymentStatus.equals("付款成功")) {
					shoppingService.deleteOrder(orderId);
					return ResponseEntity.ok().body(paymentStatus);
				} else {
					shoppingService.deleteOrder(orderId);
					return ResponseEntity.ok().body("Order deleted successfully");
				}
			} else {
				throw new IllegalArgumentException("Invalid order Id: " + orderId);
			}
		} else {
			// 沒登入
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login to delete orders");
		}
	}

	// 搜尋訂單
	@GetMapping("/sean/searchOrder")
	public String searchOrder(@RequestParam("keyword") String keyword, Model model) {
		List<OrdersBean> orders = shoppingService.searchOrders(keyword);
		model.addAttribute("orders", orders);
		return "sean/checkoutConfirmation";
	}

	// 圖表分析
	@GetMapping("/sean/graphAnalysis")
	public String getGraphAnalysis(Model model) {
		ChartData chartData = shoppingService.getChartData();
		Gson gson = new Gson();
		String json = gson.toJson(chartData);
		model.addAttribute("chartData", json);
		return "sean/graphAnalysis";
	}

	// 輸出CSV
	@GetMapping("/sean/exportCSV")
	public void exportCSV(HttpServletResponse response) throws IOException {
		// 設定CSV格式
		response.setContentType("text/csv");
		response.setCharacterEncoding("MS950");
		response.setHeader("Content-Disposition", "attachment; filename=\"report.csv\"");

		// 獲取圖表資訊
		ChartData chartData = shoppingService.getChartData();

		// 寫成CSV至本地端
		try (CSVWriter writer = new CSVWriter(response.getWriter())) {
			// 設置header
			String[] header = { "Product", "Quantity" };
			writer.writeNext(header);

			// 迭代輸出
			for (int i = 0; i < chartData.getLabels().length; i++) {
				String[] line = { chartData.getLabels()[i], Integer.toString(chartData.getValues()[i]) };
				writer.writeNext(line);
			}
		}
	}
}