package com.tm.TravelMaster.yu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.chih.model.MemberRepository;
import com.tm.TravelMaster.yu.model.Spot;
import com.tm.TravelMaster.yu.model.SpotRepository;
import com.tm.TravelMaster.yu.service.SpotService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SpotFrontController {

	@Autowired
	private SpotService spService;

//	@Autowired
//	private SavedSpotService saService;

//	@Autowired
//	private MemberRepository mRepo;
//
//	@Autowired
//	private SpotRepository spRepo;

	@GetMapping("/toSpot")
	public String toSpot(Model model) {
		List<Spot> topClickedSpots = spService.findTopNSpots(4);
		List<Spot> descSpots = spService.findTopNSpotsBySpotNoDesc(4);

		model.addAttribute("topClickedSpots", topClickedSpots);
		model.addAttribute("descSpots", descSpots);
		return "yu/index";
	}

	@GetMapping("/allSpot")
	public String allSpot(Model model) {
		List<Spot> spots = spService.findAll();
		model.addAttribute("spots", spots);
		return "yu/allSpot";
	}

	@GetMapping("/searchSpots")
	public String fuzzySearchSpots(@RequestParam(value = "cityRegion", required = false) String cityRegion,
			@RequestParam(value = "cityName", required = false) String cityName,
			@RequestParam(value = "spotType", required = false) String spotType,
			@RequestParam(value = "txtQuery", required = false) String txt, Model model) {
		List<Spot> spots = spService.fuzzySearch(cityRegion, cityName, spotType, txt);
		model.addAttribute("spots", spots);
		return "yu/allSpot";
	}

	@GetMapping("/spotDetails/{spotNo}")
	public String spotDetails(@PathVariable("spotNo") Integer spotNo, Model model) {
		Spot spot = spService.findById(spotNo);
		spService.updateClickCount(spotNo);
		List<Spot> relatedSpots = spService.findRelatedSpots(spotNo, spot.getCityName());
		List<Spot> mostClickedSpots = spService.findTopNSpots(4);

		Double longitude = spot.getLongitude() != null ? spot.getLongitude() : 0.0;
		Double latitude = spot.getLatitude() != null ? spot.getLatitude() : 0.0;

		model.addAttribute("spot", spot);
		model.addAttribute("relatedSpots", relatedSpots);
		model.addAttribute("mostClickedSpots", mostClickedSpots);
		model.addAttribute("longitude", longitude);
		model.addAttribute("latitude", latitude);

		return "yu/spotDetails";
	}

//	@PostMapping("/savedSpot")
//	public ResponseEntity<?> doSave(HttpSession session, @RequestParam("spotNo") Integer spotNo) {
//		Member member = (Member) session.getAttribute("mbsession");
//
//		if (member == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
//		} else {
//			SavedSpot list = saService.findByMemberMemberSeqAndspotNo(member.getMemberSeq(), spotNo);
//			if (list == null) {
//				Spot spot = spService.findById(spotNo);
//				SavedSpot newList = new SavedSpot();
//				newList.setSpot(spot);
//				newList.setMember(member);
//				saService.savedSpots(newList);
//				return ResponseEntity.ok("收藏成功");
//			} else {
//				saService.removedSpots(list);
//				return ResponseEntity.ok("取消收藏");
//			}
//		}
//	}

	@GetMapping("/likedSpot")
	public String likedSpot() {
		return "yu/likedSpot";
	}



//	@PostMapping("/{spotNo}/like")
//	public ResponseEntity<String> likeSpot(@PathVariable("spotNo") Integer spotNo, @RequestBody Member member) {
//		Spot spot = spRepo.findById(spotNo).orElse(null);
//		if (spot == null) {
//			return ResponseEntity.notFound().build();
//		}
//
//		Member savedMember = mRepo.findById(member.getMemberSeq()).orElse(null);
//		if (savedMember == null) {
//			return ResponseEntity.badRequest().body("Invalid member");
//		}
//
//		saService.saveSpot(savedMember, spot);
//		return ResponseEntity.ok("Spot liked successfully");
//	}
}