package com.tm.TravelMaster.leo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tm.TravelMaster.leo.model.Playone;
import com.tm.TravelMaster.leo.model.PlayoneImg;
import com.tm.TravelMaster.leo.service.PlayoneImgService;
import com.tm.TravelMaster.leo.service.PlayoneService;

@Controller
public class PlayoneImgController {

	@Autowired
	private PlayoneService pService;
	
	@Autowired
	private PlayoneImgService piService;
	
	@PostMapping("/playone/imgedit")
	public String playoneImgEdit(@RequestParam("updateimg") Integer playoneId, Model m) {
		List<PlayoneImg> playoneImgList =piService.findImgById(playoneId);
		 m.addAttribute("playoneImgList", playoneImgList);
		return "leo/background/updatePlayoneImg";
	}
	
//----------------------------------------新增功能------------------------------------------	

	@PostMapping("/playone/imgnew")
	public String handlePostRequest(@RequestParam("playoneId") String playoneId,
	                                @RequestParam("newPlayoneImg") MultipartFile newPlayoneImg,
	                                Model m) throws IOException {

	    byte[] imgbyte = newPlayoneImg.getBytes();

	    //如果回傳空值就不新增
	    if (imgbyte != null && imgbyte.length > 0) {
	        Playone playone = pService.findById(Integer.parseInt(playoneId));
	        if(playone != null) {
	            piService.insertPlayonePhoto(imgbyte, playone);
	        }
	    }

	    Playone playone = pService.findById(Integer.parseInt(playoneId));
	    m.addAttribute("playone", playone);

	    return "redirect:/playone/findid?id=" + playoneId;

	}



//----------------------------------------刪除功能------------------------------------------	

	@PostMapping("/playone/imgdelete")
    public ResponseEntity<String> deleteImage(@RequestParam("playoneimgid") Integer playoneImgId) {
        try {
        	piService.deleteById(playoneImgId);
            return ResponseEntity.ok("success") ;
        } catch(NumberFormatException e) {
            System.out.println("Invalid format for playoneimgid.");
            e.printStackTrace();
            return  ResponseEntity.ok("error") ;
        } catch(Exception e) {
            System.out.println("Error in deletion process.");
            e.printStackTrace();
            return ResponseEntity.ok("error") ;
        }
    }
	


//----------------------------------------其他功能------------------------------------------	
	// 圖片
		@GetMapping("/leo/getImageData/{playoneId}")
		public ResponseEntity<byte[]> getImageData(@PathVariable int playoneId) {
			// 根據 playoneId 查詢圖片數據
			List<PlayoneImg> playoneImgList = piService.findImgById(playoneId);
			if (!playoneImgList.isEmpty()) {
				PlayoneImg playoneImg = playoneImgList.get(0);
				byte[] imageData = playoneImg.getPlayonePhoto(); // 假設在 PlayoneImg 類中有名為 getPlayonePhoto() 的方法來獲取圖片數據
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.IMAGE_JPEG);
				return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
}
