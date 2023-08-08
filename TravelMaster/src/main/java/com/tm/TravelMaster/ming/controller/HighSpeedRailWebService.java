package com.tm.TravelMaster.ming.controller;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.tm.TravelMaster.ming.db.service.HighSpeedRailService;
import com.tm.TravelMaster.ming.db.service.TicketInfoService;
import com.tm.TravelMaster.ming.model.dto.BookingGoForm;
import com.tm.TravelMaster.ming.model.dto.HighSpeedRailTicket;
import com.tm.TravelMaster.ming.model.dto.TrainTimeInfo;
import com.tm.TravelMaster.ming.model.entity.StationInfo;
import com.tm.TravelMaster.ming.model.entity.TicketInfo;
import com.tm.TravelMaster.ming.model.entity.TranInfo;
import com.tm.TravelMaster.ming.model.entity.TicketInfoGroup;

@Controller
@RequestMapping("/services")
public class HighSpeedRailWebService {

	@Autowired
	private TicketInfoService ticketsService;

	@Autowired
	private HighSpeedRailService highSpeedRailService;

	// 使用者頁面 查詢時刻表區間 (使用Page做分頁)
	@GetMapping(value = "/GetTranInfo", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String GetTranInfo(@RequestParam("departureST") String departureST,
			@RequestParam("destinationST") String destinationST, @RequestParam("departureTime") String departureTime,
			@RequestParam(name = "p", defaultValue = "1") Integer pageNumber, Model model) {

		Page<TrainTimeInfo> trainTimeInfos = highSpeedRailService.findByPage(departureST, destinationST, departureTime,
				pageNumber);

		String json = new Gson().toJson(trainTimeInfos);
		return json;
	}

	// 管理者後台 查看消費者之訂票紀錄
	@GetMapping("/GetAllTicketInfo")
	@ResponseBody
	public String GetAllTicketInfo() {
		List<HighSpeedRailTicket> tks = highSpeedRailService.getAllBookingTk();
		Map<String, List<List<String>>> inputMap = new HashMap<String, List<List<String>>>();
		List<List<String>> dataList = new ArrayList<List<String>>();

		for (HighSpeedRailTicket tk : tks) {
			List<String> tkDataLst = new ArrayList<String>();
			tkDataLst.add(Integer.toString(tk.getTicketID()));
			tkDataLst.add(tk.getTranNo());
			tkDataLst.add(tk.getSeat());
			tkDataLst.add(tk.getDepartureST());
			tkDataLst.add(tk.getDestinationST());
			tkDataLst.add(tk.getDeparturedate());
			tkDataLst.add(tk.getDeparturetime());
			tkDataLst.add(tk.getArrivaltime());
			tkDataLst.add(Integer.toString(tk.getPrice()));
			tkDataLst.add(tk.getBookingdate());
			dataList.add(tkDataLst);
		}
		inputMap.put("data", dataList);

		String json = new Gson().toJson(inputMap);
		return json;
	}

	// 依據訂票紀錄的(抵達站)作為熱門目的的資料分析
	@GetMapping("/GetHotSpotChartData")
	@ResponseBody
	public String GetHotSpotChartData() {
		Map<String, List<String>> inputMap = new HashMap<String, List<String>>();
		
		List<StationInfo> stations = highSpeedRailService.findAllStationInfo();
		List<String> stationNames = new ArrayList<>(); // x軸:['x','南港','台北','板橋','桃園',...,'台南','左營']
		stationNames.add("x");
		for (StationInfo station : stations) {
			stationNames.add(station.getStationName());
		}

		// 計算各站(DestinationST)總共有幾個
		List<TicketInfo> ticketInfos = ticketsService.findAllTicketInfo();
		Map<Integer, Integer> stationsCountMap = new HashMap<>();
		for (TicketInfo ticketInfo : ticketInfos) { 
			int stationID = Integer.parseInt(ticketInfo.getDestinationST());
			if (stationsCountMap.containsKey(stationID)) {
				int tmp = stationsCountMap.get(stationID);
				tmp++;
				stationsCountMap.put(stationID, tmp);
			} else {
				stationsCountMap.put(stationID, 1);
			}
		}
		
		List<String> stationCountNames = new ArrayList<>(); // y軸:['TopStation','2','0', '0',...,'0','0']
		stationCountNames.add("熱門目的地");
		for (StationInfo station : stations) {
			if (stationsCountMap.containsKey(station.getStationID())) {
				stationCountNames.add(Integer.toString(stationsCountMap.get(station.getStationID())));
			} else {
				stationCountNames.add("0");
			}
		}

		inputMap.put("x", stationNames);
		inputMap.put("TopStation", stationCountNames);
		/*
		 * json: { "x": [南港, 台北, 板橋 ... 左營], "各站統計" : [200, 130, 90 ... 220] // 數目 }
		 */
		String json = new Gson().toJson(inputMap);
		return json;
	}

	private final String[] monthsStr = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
	// 依據消費者訂票紀錄的作為資料分析
	@GetMapping("/AnalysisTicketSales")
	@ResponseBody
	public String GetTicketSalesData() {
		// 將訂票日期格式化只取月份
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

		Map<String, List<String>> inputMap = new HashMap<String, List<String>>();

		List<TicketInfo> ticketInfos = ticketsService.findAllTicketInfo();

		Date tmpDate;
		long leastDate = Long.MAX_VALUE;
		for (TicketInfo ticketInfo : ticketInfos) {
			try {
				tmpDate = dateFormat.parse(ticketInfo.getDeparturedate());
				if (leastDate > tmpDate.getTime()) {
					leastDate = tmpDate.getTime(); // 找出最早的一天(找最小年度)
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		tmpDate = new Date(leastDate);
		int leastYear = Integer.parseInt(yearFormat.format(tmpDate)); // 最小年度
		int currentYear = Integer.parseInt(yearFormat.format(new Date())); // 當前年度

		List<String> departureDates_allYear = new ArrayList<>(); // All years ["x","2020-01-01","2021-01-01","2022-01-01","2023-01-01"]
		List<String> departureDates_3Year = new ArrayList<>(); // 3 years ["x","2021-01-01",..,"2021-12-01","2022-01-01",..,"2022-12-01","2023-01-01",..,"2023-12-01"]
		List<String> departureDates_1Year = new ArrayList<>(); // 1 years ["x","2023-01-01","2023-02-01",...,"2023-12-01"]
		departureDates_allYear.add("x");
		departureDates_3Year.add("x");
		departureDates_1Year.add("x");
		for (int i = leastYear; i <= currentYear; i++) {
			departureDates_allYear.add(String.format("%d-01-01", i)); // All years
			// 把每一年的每個月分 丟到List 裡面
			if ((currentYear - i) <= 3) { // 3 years
				for (String monthStr : monthsStr) {
					departureDates_3Year.add(String.format("%d-%s-01", i, monthStr));
				}
			}
			if (currentYear == i) {
				for (String monthStr : monthsStr) { // 1 years
					departureDates_1Year.add(String.format("%d-%s-01", i, monthStr));
				}
			}
		}

		// yearMonthFormat
		Map<String, Integer> allYearIncomeMap = new HashMap<>(); // 全年度收益
		Map<String, Integer> Last3YearIncomeMap = new HashMap<>(); // 最近三年月份收益
		Map<String, Integer> ThisYearIncomeMap = new HashMap<>(); // 當年月份收益
		for (int i = 1; i < departureDates_allYear.size(); i++) { // 跳過第一個(因為是 "x")
			allYearIncomeMap.put(departureDates_allYear.get(i), 0); // 全部先初始化成0
		}
		for (int i = 1; i < departureDates_3Year.size(); i++) { // 跳過第一個(因為是 "x")
			Last3YearIncomeMap.put(departureDates_3Year.get(i), 0); // 全部先初始化成0
		}
		for (int i = 1; i < departureDates_1Year.size(); i++) { // 跳過第一個(因為是 "x")
			ThisYearIncomeMap.put(departureDates_1Year.get(i), 0); // 全部先初始化成0
		}
		
		for (TicketInfo ticketInfo : ticketInfos) { // 把 "以日為單位" 的資料 統整進 "以月為單位"
			try {
				Date departureDate = dateFormat.parse(ticketInfo.getDeparturedate());
				String[] yearMonthStrArr = (yearMonthFormat.format(departureDate) + "-01").split("-");
				int currentIncome = 0;
				String mapKey= yearMonthStrArr[0] + "-01-01";
				if (allYearIncomeMap.containsKey(mapKey)) { // 看年
					currentIncome = allYearIncomeMap.get(mapKey);
					currentIncome += ticketInfo.getPrice();
					allYearIncomeMap.put(mapKey, currentIncome);
				}
				mapKey= yearMonthStrArr[0] + "-" + yearMonthStrArr[1] + "-01";
				if (Last3YearIncomeMap.containsKey(mapKey)) { // 看年+月
					currentIncome = Last3YearIncomeMap.get(mapKey);
					currentIncome += ticketInfo.getPrice();
					Last3YearIncomeMap.put(mapKey, currentIncome);
				}
				if (ThisYearIncomeMap.containsKey(mapKey)) { // 看年+月
					currentIncome = ThisYearIncomeMap.get(mapKey);
					currentIncome += ticketInfo.getPrice();
					ThisYearIncomeMap.put(mapKey, currentIncome);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<String> allYearIncome = new ArrayList<>();
		List<String> Last3YearIncome = new ArrayList<>();
		List<String> thisYearIncome = new ArrayList<>();
		
		allYearIncome.add("全年度訂票收益");
		for (int i = 1; i < departureDates_allYear.size(); i++) { // 跳過第一個(因為是 "x")
			allYearIncome.add(Integer.toString(allYearIncomeMap.get(departureDates_allYear.get(i))));
		}
		Last3YearIncome.add("近三年訂票收益");
		for (int i = 1; i < departureDates_3Year.size(); i++) { // 跳過第一個(因為是 "x")
			Last3YearIncome.add(Integer.toString(Last3YearIncomeMap.get(departureDates_3Year.get(i))));
		}
		thisYearIncome.add("本年度訂票收益");
		for (int i = 1; i < departureDates_1Year.size(); i++) { // 跳過第一個(因為是 "x")
			thisYearIncome.add(Integer.toString(ThisYearIncomeMap.get(departureDates_1Year.get(i))));
		}
		inputMap.put("x_all", departureDates_allYear);
		inputMap.put("Income_all", allYearIncome);
		
		inputMap.put("x_3y", departureDates_3Year);
		inputMap.put("Income_3y", Last3YearIncome);
		
		inputMap.put("x_1y", departureDates_1Year);
		inputMap.put("Income_1y", thisYearIncome);

		/*
		 * json: { "x": [01,02,03,...,12], "各月份收益統計" : [200, 130, 90 ... 220] }
		 */

		String json = new Gson().toJson(inputMap);
		return json;
	}

	// 刪除 使用者消費紀錄
	@GetMapping(value = "/DeleteTicketInfo", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String DeleteTicketInfo(@RequestParam("id") String id) {
		boolean isSucceed = ticketsService.deleteTickerInfoById(Integer.parseInt(id));
		String msg = Boolean.toString(isSucceed);
		String json = String.format("{\"id\":\"%s\", \"result\":%s}", id, msg);
		return json;
	}

	// 批次上傳班次時刻表
	@PostMapping(value = "/BatchUploadTrainInfo", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String BatchUploadTrainInfo(@RequestParam("file") MultipartFile file) throws IOException {
		String fileContent = new String(file.getBytes());
		boolean uploadResult = true;
		String resultErrMsg = "";

//		另一種方法 這樣loop滾一次就夠了
//		try {
//			uploadResult = highSpeedRailService.insertTranInfoByCSV(fileContent);
//		} catch (SQLException e) {
//			uploadResult = false;
//			resultErrMsg = e.getMessage();
//		}
//		String json = String.format("{\"result\":%s, \"msg\":\"%s\"}", uploadResult ? "true" : "false",
//				uploadResult ? "批次新增成功" : String.format("批次新增失敗(%s)", resultErrMsg));
//		System.out.println(json);
//		return json;

		String[] fileContentList = fileContent.split(System.lineSeparator());
		String TranNo, StationID, TrainArrivalTime;
		TranInfo tInfo;
		List<TranInfo> tInfos = new ArrayList<>();
		for (String fileContentRow : fileContentList) {
			String[] datas = fileContentRow.split(",");
			// 驗證檔案格式
			if (datas.length != 3) {
				uploadResult = false;
				resultErrMsg = "檔案格式錯誤";
				break;
			}
			TranNo = datas[0].trim();
			StationID = datas[1].trim();
			TrainArrivalTime = datas[2].trim();
			// 驗證資料格式(excel轉成csv需先以記事本另存檔案轉UTF-8，避免產生髒資料)
			if (!TranNo.matches("[0-9]+")) {// -> 至少一個數字
				uploadResult = false;
				resultErrMsg = "TranNo格式錯誤";
				break;
			}
			if (!StationID.matches("[0-9]+")) { // -> 至少一個數字
				uploadResult = false;
				resultErrMsg = "StationID格式錯誤";
				break;
			}
			if (!TrainArrivalTime.matches("[0-9]{1,2}:[0-9]{1,2}")) { // -> 兩個數字 + ":" + 兩個數字
				uploadResult = false;
				resultErrMsg = "TrainArrivalTime格式錯誤";
				break;
			}

			// 0: TranNo
			// 1: StationID
			// 2: TrainArrivalTime
			tInfo = new TranInfo();
			tInfo.setTranNo(TranNo);
			tInfo.setStationID(StationID);
			tInfo.setTrainArrvialTime(TrainArrivalTime);
			tInfos.add(tInfo);
		}
		// 所有資料都要對 才可以開始 insert table, 但是這樣會多滾一次loop, 所以可以用另一種寫法 XD 給你參考
		if (uploadResult) {
			try {
				highSpeedRailService.insertTranInfos(tInfos);
			} catch (SQLException e) {
				uploadResult = false;
				resultErrMsg = e.getMessage();
			}
		}

		String json = String.format("{\"result\":%s, \"msg\":\"%s\"}", uploadResult ? "true" : "false",
				uploadResult ? "批次新增成功" : String.format("批次新增失敗(%s)", resultErrMsg));
		System.out.println(json);
		return json;
	}

	// 將訂票紀錄以DTO方式傳入DB的TicketInfo Table
	@PostMapping(value = "/bookingGo", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String bookingGo(@RequestBody BookingGoForm bookingGoForm, Model model) {
		Date today = new Date();
		String resultErrMsg = "";
		boolean result = true;
		if (bookingGoForm.getFormInputVal_selectedSeats() == null
				|| bookingGoForm.getFormInputVal_selectedSeats().isEmpty()) {
			result = false;
			resultErrMsg = "前端資料獲取失敗";
		}
		TicketInfoGroup shoppingCart = null;
		if (result) {
			String[] selectedSeats = bookingGoForm.getFormInputVal_selectedSeats().split(",");
			List<TicketInfo> ticketInfos = new ArrayList<>();
			TicketInfo ticketInfo;

			for (String selectedSeat : selectedSeats) {
				ticketInfo = new TicketInfo();
				ticketInfo.setTranNo(bookingGoForm.getFormInputVal_TranNo());
				ticketInfo.setSeat(selectedSeat);
				ticketInfo.setDepartureST(bookingGoForm.getFormInputVal_DepartureStation());
				ticketInfo.setDestinationST(bookingGoForm.getFormInputVal_ArrivalStation());
				ticketInfo.setDeparturedate(bookingGoForm.getFormInputVal_DepartureDate());
				ticketInfo.setDeparturetime(bookingGoForm.getFormInputVal_DepartureTime());
				ticketInfo.setArrivaltime(bookingGoForm.getFormInputVal_ArrivalTime());
				ticketInfo.setPrice(Integer.parseInt(bookingGoForm.getFormInputVal_price()));
				ticketInfo.setBookingdate(today);
				ticketInfos.add(ticketInfo);
			}
			shoppingCart = new TicketInfoGroup();
			shoppingCart.setTicketInfos(ticketInfos);
			shoppingCart.setMember_id(bookingGoForm.getFormInputVal_memberId());
			shoppingCart.setStatus(0);
			try {
				// 這裡的目的要是拿到剛剛insert的Cart_Id是多少， 因為你的ID 是讓DB自己去長的
				shoppingCart = ticketsService.insertShoppingCart(shoppingCart);
			} catch (SQLException e) {
				resultErrMsg = e.getMessage();
			}
		}
		String json = String.format("{\"result\":%s, \"msg\":\"%s\", \"ticketCartId\":\"%s\"}",
				result ? "true" : "false", result ? "資料儲存成功" : String.format("資料儲存失敗(%s)", resultErrMsg),
				shoppingCart == null ? "-1" : shoppingCart.getCart_Id()); // 把Cart_Id 丟回AJAX
		System.out.println(json);
		return json;
	}

}
