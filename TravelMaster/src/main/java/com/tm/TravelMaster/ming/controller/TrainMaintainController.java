package com.tm.TravelMaster.ming.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tm.TravelMaster.ming.db.service.HighSpeedRailService;
import com.tm.TravelMaster.ming.model.entity.StationInfo;
import com.tm.TravelMaster.ming.model.entity.TranInfo;

@Controller
public class TrainMaintainController {
 
	@Autowired
	private HighSpeedRailService highSpeedRailService;

	@GetMapping("trainMaintain")
	public String index(Model model) {
		List<TranInfo> list = highSpeedRailService.findAllTranInfo();

		Map<String, String> trainInfoMap = new HashMap<>(); // [102,1 -> 12:00],[102,2 -> 12:15]
		for (TranInfo info : list) {
			String key = info.getTranNo() + "," + info.getStationID();
			String value = info.getTrainArrvialTime();
			trainInfoMap.put(key, value);
		}

		Set<String> trainNoSet = new HashSet<>();
		
		for (TranInfo info : list) {
			trainNoSet.add(info.getTranNo());
		}

		List<StationInfo> stationList = highSpeedRailService.findAllStationInfo();

		// --- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ dataTable
		List<List<String>> dataTableContentList = new ArrayList<List<String>>();

		for (Object trainNo : trainNoSet.toArray()) {
			List<String> dataLst = new ArrayList<String>();
			dataLst.add((String) trainNo);
			for (StationInfo stInfo : stationList) {
				String key = (String) trainNo + "," + stInfo.getStationID();
				dataLst.add(trainInfoMap.get(key));  // [102,1 -> 12:00],[102,2 -> 12:15]
			}
			String update = "<button class=\"btn btn-light\"  data-bs-toggle=\"modal\" data-bs-target=\"#EditField\" onclick=\"updateTarget("
					+ trainNo + ")\"><i class=\"fa-solid fa-pen-to-square\"></i> </button>";
			dataLst.add(update);
			String delete = "<button class=\"btn btn-light\" onclick=\"deleteTarget(" + trainNo
					+ ")\"><i class=\"fa-solid fa-trash-can\"></i> </button>";
			dataLst.add(delete);
			dataTableContentList.add(dataLst);
		}

		String dataTableContent = new Gson().toJson(dataTableContentList);
		model.addAttribute("dataTableContent", dataTableContent);
		// --- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
		model.addAttribute("trainNoSet", trainNoSet);
		model.addAttribute("stationList", stationList);
		return "ming/TrainMaintain";
	}

	@GetMapping(value = "DeleteTranInfo", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String DeleteTicketInfo(@RequestParam("tranNo") String tranNo) {
		boolean isSucceed = highSpeedRailService.deleteTrainInfoByTranNo(tranNo);
		String msg = Boolean.toString(isSucceed);
		String json = String.format("{\"id\":\"%s\", \"result\":%s}", tranNo, msg);
		return json;
	}

	@PostMapping("trainMaintain-onSubmit")
	public String UpdateOrInsert(@RequestParam("reqBody") String reqBody, Model model) {
		System.out.println(reqBody);
		JsonObject reqBodyJsonObj = JsonParser.parseString(reqBody).getAsJsonObject();
		int action = reqBodyJsonObj.get("action").getAsInt();
		JsonArray TranInfoList = reqBodyJsonObj.get("TranInfoList").getAsJsonArray();
		for (JsonElement TranInfo : TranInfoList) {
			TranInfo tif = new TranInfo(TranInfo.getAsJsonObject());
			switch (action) {
			case 1: { // insert
				highSpeedRailService.insertTranInfo(tif);
				break;
			}
			case 2: { // update
				highSpeedRailService.updateByTranNoAndStationID(tif);
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + action);
			}
		}

		return "redirect:/trainMaintain";

	}

}
