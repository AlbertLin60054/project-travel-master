package com.tm.TravelMaster.leo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import com.tm.TravelMaster.chih.dao.MemberService;
import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.leo.model.Playone;
import com.tm.TravelMaster.leo.model.PlayoneImg;
import com.tm.TravelMaster.leo.service.PlayoneService;

@Controller
public class PlayoneController {

	@Autowired
	private PlayoneService pService;
	@Autowired
	private MemberService mService;
	
	@GetMapping("/playone/main")
	public String mainPage() {
		return "leo/background/MainPage";
	}
	

//----------------------------------------新增功能------------------------------------------	
	
	@GetMapping("/playone/new")
	public String newPlayone() {
		return "leo/background/insertPlayone";
	}

	@PostMapping("/playone/post")
	protected String PostPlayone(@RequestParam("playoneNick") String pNick, @RequestParam("playoneName") String pName,
			@RequestParam("playoneSex") String pSex, @RequestParam("playoneBirth") String pBirth,
			@RequestParam("playoneInterest") String pInterest, @RequestParam("playoneIntroduce") String pIntroduce,
			@RequestParam(value = "deletedImages", required = false) String deletedImages,
			@RequestParam("playonePhoto") MultipartFile[] files, Model model) throws IOException {

		// 從請求中獲取名為"deletedImages"的參數值，並將其按逗號分隔為數組。如果參數為空，則創建一個空的數組列表。
		List<String> deletedImagesList = deletedImages != null ? Arrays.asList(deletedImages.split(","))
				: new ArrayList<>();
		// 創建一個空的PlayoneImgBean列表，用於存儲玩家圖片數據。
		List<PlayoneImg> playoneImgBeans = new ArrayList<>();

		// 遍歷請求中的每個Part對象，Part對象代表著上傳的文件。
		for (MultipartFile file : files) {
			String filename = file.getOriginalFilename();

			// 獲取Part對象的文件名，如果文件名不為空且長度大於0，則執行以下操作：
			if (filename != null && filename.length() > 0) {
				// 將文件數據轉換為Base64格式的字符串。
				InputStream fileContent = file.getInputStream();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead;
				byte[] data = new byte[10240];
				while ((nRead = fileContent.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				byte[] imgByte = buffer.toByteArray();

				// 創建一個PlayoneImgBean對象，並將圖片數據設置為字節數組。
				String base64ImageContent = Base64.getEncoder().encodeToString(imgByte);
				PlayoneImg playoneImgBean = new PlayoneImg();
				playoneImgBean.setPlayonePhoto(imgByte);

				// 檢查該文件是否在已刪除的圖片列表中，並根據結果設置PlayoneImgBean的fixedValue屬性。
				if (deletedImagesList.contains(base64ImageContent)) {
					playoneImgBean.setFixedValue(0);
					// 在此處添加一行來顯示哪些部分被標記為已刪除
					System.out.println("Marking part " + " as deleted");
				} else {
					playoneImgBean.setFixedValue(1);
					// 在此處添加一行來顯示哪些部分未被標記為已刪除
					System.out.println("Marking part " + " as not deleted");
				}

				// 將PlayoneImgBean對象添加到playoneImgBeans列表中
				playoneImgBeans.add(playoneImgBean);

			}
		}
		Member mb = mService.findById(1);
		System.out.println(mb.getMemberSeq());
		Playone playone = new Playone(pNick, pName, pSex, Integer.parseInt(pBirth), pInterest, pIntroduce,
				playoneImgBeans, 1,0,null);
		pService.insertPlayone(playone);

		Playone playone1 = pService.findById(playone.getPlayoneId());
		model.addAttribute("playone", playone1);

		return "leo/background/newPlayone";
	}

//----------------------------------------刪除功能------------------------------------------	

	@PutMapping("/playone/delete")
	public String deletePost(@RequestParam("playoneId") Playone p, Model m) {
		pService.deleteById(p.getPlayoneId());
		List<Playone> playone = pService.findAll();
		m.addAttribute("playone", playone);
		return "leo/background/allPlayoneForDelete";
	}
	@PostMapping("/playone/deleteAjax")
	public ResponseEntity<String> deletePost(@RequestParam("playoneId") Playone p) {
		 try {
		pService.deleteById(p.getPlayoneId());
		return ResponseEntity.ok("success") ;
	        } catch(NumberFormatException e) {
	            System.out.println("Invalid format for playoneid.");
	            e.printStackTrace();
	            return  ResponseEntity.ok("error") ;
	        } catch(Exception e) {
	            System.out.println("Error in deletion process.");
	            e.printStackTrace();
	            return ResponseEntity.ok("error") ;
	        }
	    }
	
	@GetMapping("/playone/deleteRegisteredAjax")
	public ResponseEntity<String> deleteRegistered(@RequestParam("playoneId") Integer playoneId) {
		try {
			pService.deleteRegisteredById(playoneId);
			return ResponseEntity.ok("success") ;
		} catch(NumberFormatException e) {
			System.out.println("Invalid format for playoneid.");
			e.printStackTrace();
			return  ResponseEntity.ok("error") ;
		} catch(Exception e) {
			System.out.println("Error in deletion process.");
			e.printStackTrace();
			return ResponseEntity.ok("error") ;
		}
	}
	
//----------------------------------------搜尋功能------------------------------------------	
	
	@GetMapping("/playone/findall")
	public String findAllPlayone(Model m) {
		return "leo/background/findAll";
	}
	@GetMapping("/playone/all")
	public String getAllPlayone(Model m) {
		List<Playone> playone = pService.findAll();
		m.addAttribute("playone", playone);
		return "leo/background/allPlayone";
	}

	@GetMapping("/playone/findone")
	public String findPlayone(Model m) {
		return "leo/background/findOne";
	}

	@GetMapping("/playone/findid")
	public String getPlayoneById(@RequestParam("id") Integer playoneId, Model m) {
	    Playone playone = pService.findById(playoneId);
	    m.addAttribute("playone", playone);
	    return "leo/background/allPlayoneForSearch";
	}
	
	@GetMapping("/playone/audit")
	public String getPlayoneByRegistered(Model m) {
		List<Playone> playone = pService.findByRegistered();
		m.addAttribute("playone", playone);
		return "leo/background/allRegisteredPlayone";
	}

	@GetMapping("/playone/findname")
	public String getPlayonesByName(@RequestParam("name") String playoneName, Model m) {
	    List<Playone> playones = pService.findByName(playoneName);
	    m.addAttribute("playone", playones);
	    return "leo/background/allPlayone";
	}

	//資料分析
	@GetMapping("/playone/Analysis")
	public String getPlayoneAnalysis(Model m) {
	    List<Playone> malePlayones = pService.findBySex("男");
	    List<Playone> femalePlayones = pService.findBySex("女");
	    m.addAttribute("maleCount", malePlayones.size());
	    m.addAttribute("femaleCount", femalePlayones.size());

	    List<Playone> ageGroup1Playones = pService.findByAgeRange(18, 20);
	    List<Playone> ageGroup2Playones = pService.findByAgeRange(21, 25);
	    List<Playone> ageGroup3Playones = pService.findByAgeRange(26, 30);
	    List<Playone> ageGroup4Playones = pService.findByAgeRange(31, 40);
	    List<Playone> ageGroup5Playones = pService.findByAgeRange(41, 50);
	    List<Playone> ageGroup6Playones = pService.findByAgeRange(51, Integer.MAX_VALUE);
	    
	    m.addAttribute("ageGroup1", ageGroup1Playones.size());
	    m.addAttribute("ageGroup2", ageGroup2Playones.size());
	    m.addAttribute("ageGroup3", ageGroup3Playones.size());
	    m.addAttribute("ageGroup4", ageGroup4Playones.size());
	    m.addAttribute("ageGroup5", ageGroup5Playones.size());
	    m.addAttribute("ageGroup6", ageGroup6Playones.size());

	    return "leo/background/sqlAnalysis";
	}


	
//----------------------------------------修改功能------------------------------------------	
	
	@PutMapping("/playone/edit")
	public String editPost(@RequestParam("id") String playoneId,
	        @RequestParam("nic") String pNick,
	        @RequestParam("name") String pName,
	        @RequestParam("sex") String pSex,
	        @RequestParam("age") String pBirth,
	        @RequestParam("ins") String pInterest,
	        @RequestParam("int") String pIntroduce,
	        Model m) throws IOException {
		
		
	    Logger logger = LoggerFactory.getLogger(PlayoneController.class);
	    logger.info("id: " + playoneId);
	    logger.info("nic: " + pNick);
	    logger.info("name: " + pName);
	    logger.info("sex: " + pSex);
	    logger.info("age: " + pBirth);
	    logger.info("ins: " + pInterest);
	    logger.info("int: " + pIntroduce);
		
		    
	    Playone updatedPlayone = pService.updatePlayoneById(playoneId, pNick, pName, pSex, pBirth, pInterest, pIntroduce);

	    if (updatedPlayone == null) {
	        System.out.println("No Playone with id: " + playoneId);
	        m.addAttribute("message", "No Playone with id: " + playoneId);
	    } else {
	        m.addAttribute("playone", updatedPlayone);
	    }
		    
		List<Playone> playone = pService.findAll();
		m.addAttribute("playone", playone); 
		return "leo/background/allPlayoneForUpdate";
	}
	
	//審核旅伴
	@PutMapping("/playone/ok")
	public String okPost(@RequestParam("id") String playoneId,
			Model m) throws IOException {
		Playone updatedPlayone = pService.updateRegisteredById(playoneId);
		
		if (updatedPlayone == null) {
			System.out.println("No Playone with id: " + playoneId);
			m.addAttribute("message", "No Playone with id: " + playoneId);
		} else {
            Member member = updatedPlayone.getMember();

            member.setPlayoneLevel(1); 

            mService.saveMember(member);
			m.addAttribute("playone", updatedPlayone);
		}
		
		List<Playone> playone = pService.findByRegistered();
		m.addAttribute("playone", playone); 
		return "redirect:/playone/audit";
	}


//----------------------------------------其他功能------------------------------------------	
	
	@PostMapping(value = "/CNController", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> handlePostRequest(@RequestParam("playoneNick") String playoneNick) {
        boolean exists = pService.checkIfplayoneNickExist(playoneNick);

        if (exists) {
            return ResponseEntity.ok("1");
        } else {
            return ResponseEntity.ok("0");
        }
    }
	

	@GetMapping("/downloadImg/{id}")
	public ResponseEntity<byte[]> downloadImage(@PathVariable Integer playoneId) {
		List<PlayoneImg> photos = pService.getPhotosByPlayoneId(playoneId);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		try {
			for (PlayoneImg photo : photos) {
				ZipEntry entry = new ZipEntry(photo.getPlayoneimgId() + ".jpg");
				zos.putNextEntry(entry);
				zos.write(photo.getPlayonePhoto());
				zos.closeEntry();
			}
		} catch (IOException e) {
			// handle exception
		} finally {
			try {
				zos.close();
			} catch (IOException e) {
				// handle exception
			}
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
	}

//----------------------------------------CSV功能------------------------------------------		
	
	@GetMapping("/playone/all/csv")
	public ResponseEntity<String> getAllPlayoneCsv() {
	    List<Playone> playone = pService.findAll();
	    String csvContent = playone.stream()
	            .map(this::convertToCsv)
	            .collect(Collectors.joining(System.lineSeparator()));
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentDispositionFormData("attachment", "playone.csv");
	    return ResponseEntity.ok()
	            .headers(headers)
	            .body(csvContent);
	}

	private String convertToCsv(Playone playone) {
	    return playone.getPlayoneId() + ","
	            + playone.getPlayoneNick() + ","
	            + playone.getPlayoneName() + ","
	            + playone.getPlayoneSex() + ","
	            + playone.getPlayoneBirth() + ","
	            + playone.getPlayoneInterest() + ","
	            + playone.getPlayoneIntroduce() + ","
	            + playone.getFixedValue() + ","
	            + playone.getRegistered();
	}

	
}
