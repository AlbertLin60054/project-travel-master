package com.tm.TravelMaster.ming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.tm.TravelMaster.ming.db.service.HighSpeedRailService;
import com.tm.TravelMaster.ming.db.service.TicketInfoService;
import com.tm.TravelMaster.ming.model.entity.TicketInfo;

@Controller
@RequestMapping("/highSpeedRail")
public class HighSpeedRailController {

	@Autowired
	private TicketInfoService ticketsService;

	@Autowired
	private HighSpeedRailService highSpeedRailService;

	@GetMapping("")
	public String index(Model model) {
		return "ming/BookingAdmin";
	}

	@GetMapping("/insert")
	public String insert(Model model) {
		model.addAttribute("stationList", highSpeedRailService.findAllStationInfo());
		model.addAttribute("priceInfos", GetAllPriceInfo());
		return "ming/BookingAdminDataPage";
	}

	@GetMapping("/update")
	public String update(Model model, @RequestParam("id") String id) {
		TicketInfo ticketDto = ticketsService.findTicketInfoById(Integer.parseInt(id));
		model.addAttribute("stationList", highSpeedRailService.findAllStationInfo());
		model.addAttribute("priceInfos", GetAllPriceInfo());
		model.addAttribute("ticketDto", ticketDto);
		return "ming/BookingAdminDataPage";
	}

	private String GetAllPriceInfo() {
		String json = new Gson().toJson(highSpeedRailService.findAllPriceInfo());
		return json;
	}

	@PostMapping("/doAction")
	public String doAction(@ModelAttribute("ticketDto") TicketInfo ticketDto, @RequestParam("action") String action) {
		if (action.equals("doInsert")) {
			ticketsService.insertTicketInfo(ticketDto);
		} else if (action.equals("doUpdate")) {
			ticketsService.updateTicketInfo(ticketDto);
		}
		return "redirect:/highSpeedRail";
	}

}
