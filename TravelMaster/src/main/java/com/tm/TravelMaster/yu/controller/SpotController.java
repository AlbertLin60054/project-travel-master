package com.tm.TravelMaster.yu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tm.TravelMaster.yu.model.Spot;
import com.tm.TravelMaster.yu.service.SpotService;

@Controller
@RequestMapping("/spotList")
public class SpotController {
	
	@Autowired
	private SpotService spService;
	
	@GetMapping("")
    public String query(Model model) {
        List<Spot> spots = spService.findAll();
        model.addAttribute("spots", spots);
        return "yu/spotAdmin";
    }

    @GetMapping("/insert")
    public String insert(Model model) {
    	model.addAttribute("cityRegions", spService.getAllCityRegions());
	    model.addAttribute("cityNames", spService.getAllCityNames());
	    model.addAttribute("spotTypes", spService.getAllSpotTypes());
        return "yu/updateSpotAdmin";
    }
 
    @GetMapping("/update")
    public String update(@RequestParam("id") String id, Model model) {
        Spot spots = spService.findById(Integer.parseInt(id));
        model.addAttribute("cityRegions", spService.getAllCityRegions());
	    model.addAttribute("cityNames", spService.getAllCityNames());
	    model.addAttribute("spotTypes", spService.getAllSpotTypes());
        model.addAttribute("spots", spots);
        return "yu/updateSpotAdmin";
    }
    
    @PostMapping("/doAction")
    public String doAction(@ModelAttribute("spots") Spot spots, @RequestParam("action") String action) {
    	if(action.equals("doInsert")) {
    		spService.insert(spots);
    	}else if(action.equals("doUpdate")) {
    		spService.update(spots);
    	}
    	return "redirect:/spotList";
    }
 
    @GetMapping("/delete")
    public String delete(@RequestParam("spotNo") Integer spotNo) {
        spService.deleteById(spotNo);
        return "redirect:/spotList";
    }
    
    @GetMapping("/chart")
	public String chart() {
		return "yu/spotChart";
	}
    
}

