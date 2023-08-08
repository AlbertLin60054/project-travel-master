package com.tm.TravelMaster.ming.controller;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.google.gson.Gson;
import com.tm.TravelMaster.ming.db.service.HighSpeedRailService;
import com.tm.TravelMaster.ming.model.dto.TrainTimeInfo;

@Controller
public class SelectController {

	@Autowired
	private HighSpeedRailService highSpeedRailService;
	
	@GetMapping("/select")
	public String selectPage(Model model) {
		model.addAttribute("stationList", highSpeedRailService.findAllStationInfo());
		model.addAttribute("priceInfos", GetAllPriceInfo());
		return "ming/SelectPage";
	}

	private String GetAllPriceInfo() {
		String json = new Gson().toJson(highSpeedRailService.findAllPriceInfo());
		return json;
	}

	@PostMapping("/choose")
	public String choosePage(TrainTimeInfo trainTimeInfo, Model model) {
		Map<Set<String>, Integer> priceInfoMap = highSpeedRailService.getPriceInfoMap();

		for (Entry<Set<String>, Integer> entry : priceInfoMap.entrySet()) {
			Set<String> s = entry.getKey();
			if (s.contains(trainTimeInfo.getDepartureST()) && s.contains(trainTimeInfo.getDestinationST())) {
				model.addAttribute("singlePrice", entry.getValue());
				break;
			}
		}

		model.addAttribute("DepartureST_ID", trainTimeInfo.getDepartureST()); // 這裡還是ID
		model.addAttribute("DestinationST_ID", trainTimeInfo.getDestinationST());// 這裡還是ID
		
		Map<Integer, String> stationMap = highSpeedRailService.getStationInfoMap();
		trainTimeInfo.setDepartureST(stationMap.get(Integer.parseInt(trainTimeInfo.getDepartureST())));// 換成站名了
		trainTimeInfo.setDestinationST(stationMap.get(Integer.parseInt(trainTimeInfo.getDestinationST())));// 換成站名了
		
		model.addAttribute("trainTimeInfo", trainTimeInfo);
		return "ming/ChoosePage";
	}

	@GetMapping("/bookingRecord")
	public String BookingRecord() {
		return "ming/BookingRecord";
	}
}
