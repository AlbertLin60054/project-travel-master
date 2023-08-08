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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tm.TravelMaster.chih.dao.MemberService;
import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.leo.model.Playone;
import com.tm.TravelMaster.leo.model.PlayoneImg;
import com.tm.TravelMaster.leo.service.PlayoneImgService;
import com.tm.TravelMaster.leo.service.PlayoneService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ClientController {

	@Autowired
	private PlayoneService pService;
	@Autowired
	private PlayoneImgService piService;
	@Autowired
	private MemberService mService;
	
	@GetMapping("/playone")
	public String homePage(Model m,HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");
		int playoneLevel = 0;  
	    if (member != null) {
	        playoneLevel = member.getPlayoneLevel();
	    }
	    
		List<Playone> playone = pService.findRandomEightByFixedValue();

	    for (Playone p : playone) {
	        if (p.getPlayoneImgs().isEmpty()) {
	            System.out.println("Playone with ID: " + p.getPlayoneId() + " has no images.");
	        } else {
	            System.out.println("Playone with ID: " + p.getPlayoneId() + " has " + p.getPlayoneImgs().size() + " image(s).");
	        }
	    }
	    m.addAttribute("playone", playone);
	    if (playoneLevel == 1) {
	        return "leo/client/HomePage2";
	    }else {
	    return "leo/client/HomePage";
	    }
	}

	@GetMapping("/playoneRegistered")
	public String playoneRegistrationForm(HttpServletRequest request, HttpSession session) {
	    if (session.getAttribute("mbsession") == null) {
	        String referrer = request.getRequestURL().toString();
	        session.setAttribute("url_prior_login", referrer);
	        return "chih/loginSystem";
	    }
	    Member member = (Member) session.getAttribute("mbsession");
	    String memberNum = member.getMemberNum();
	    return "leo/client/playoneRegistered";
	}


//----------------------------------------新增功能------------------------------------------	
	
	@PostMapping("/playone/registered")
	protected String PostPlayone(@RequestParam("playoneNick") String pNick, @RequestParam("playoneName") String pName,
			@RequestParam("playoneSex") String pSex, @RequestParam("playoneBirth") String pBirth,
			@RequestParam("playoneInterest") String pInterest, @RequestParam("playoneIntroduce") String pIntroduce,
			@RequestParam(value = "deletedImages", required = false) String deletedImages,@RequestParam("memberSeq") String memberSeq,
			@RequestParam("playonePhoto") MultipartFile[] files, Model m,HttpSession session) throws IOException {

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
		Member mb = mService.findById(Integer.parseInt(memberSeq));
		System.out.println(mb.getMemberSeq());
		Playone playone = new Playone(pNick, pName, pSex, Integer.parseInt(pBirth), pInterest, pIntroduce,
				playoneImgBeans, 0,1,mb);

		Member member = (Member) session.getAttribute("mbsession");
		int playoneLevel = 0;  
	    if (member != null) {
	        playoneLevel = member.getPlayoneLevel();
	    }
		pService.insertPlayone(playone);
		List<Playone> playone1 = pService.findRandomEightByFixedValue();

	    for (Playone p : playone1) {
	        if (p.getPlayoneImgs().isEmpty()) {
	            System.out.println("Playone with ID: " + p.getPlayoneId() + " has no images.");
	        } else {
	            System.out.println("Playone with ID: " + p.getPlayoneId() + " has " + p.getPlayoneImgs().size() + " image(s).");
	        }
	    }

	    m.addAttribute("playone", playone1);
	    if (playoneLevel == 1) {
	        return "leo/client/HomePage2";
	    }else {
	    return "leo/client/HomePage";
	    }
	}

//----------------------------------------購物車功能------------------------------------------	

//	@PostMapping(value = "/sean/addPlayoneToCart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//	@ResponseBody
//	public ResponseEntity<String> addPlayoneToCart(@ModelAttribute("playone") Playone playone, HttpSession session) {
//		
//	    Member member = (Member) session.getAttribute("mbsession");
//
//	    if (member == null) {
//	        return new ResponseEntity<>("請先登入", HttpStatus.UNAUTHORIZED);
//	    }
//
//	    String memberNum = member.getMemberNum();
//
//	    // 加載購物車資訊
//	    List<Playone> cart = pService.loadPlayoneCartData(memberNum);
//
//	    // 在 Service 層進行產品 ID 判定
//	    boolean isDuplicate = pService.isPlayoneInCart(cart, playone.getPlayoneId());
//
//	    if (isDuplicate) {
//	        return new ResponseEntity<>("商品已存在於購物車中", HttpStatus.OK);
//	    } else {
//	        // 添加產品到購物車
//	        cart.add(playone);
//
//	        // 保存購物車資訊至本地端 JSON
//	        pService.savePlayoneCartData(memberNum, cart);
//
//	        return new ResponseEntity<>("已成功加入商品", HttpStatus.OK);
//	    }
//	}

	
//----------------------------------------搜尋功能------------------------------------------	
	
	@GetMapping("/playone/more")
	public String ajaxMix(@RequestParam(name = "p",defaultValue = "1")Integer pageNumber,Model m,HttpSession session){
		Member member = (Member) session.getAttribute("mbsession");
		int playoneLevel = 0;  
	    if (member != null) {
	        playoneLevel = member.getPlayoneLevel();
	    }
		Page<Playone> page = pService.findByPage(pageNumber);
		m.addAttribute("page",page);
		if (playoneLevel == 1) {
	        return "leo/client/morePage2";
	    }else {
	    return "leo/client/morePage";
	    }
	}

	@GetMapping("/playone/moreFragment")
	public String ajaxMoreFragment(@RequestParam(name = "p", defaultValue = "1") Integer pageNumber, Model m) {
	    Page<Playone> page = pService.findByPage(pageNumber);
	    m.addAttribute("page", page);
	    return "leo/client/morePageFragment"; 
	}
	
	@GetMapping("/playone/byNick")
	public String getPlayonesByNick(@RequestParam("nick") String playoneNick, Model m) {
	    List<Playone> playones = pService.findByNick(playoneNick);
	    m.addAttribute("playone", playones);
	    return "leo/client/byNickPage"; 
	}
	@GetMapping("/playone/bySex")
	public String getPlayonesBySex(@RequestParam(name = "p",defaultValue = "1")Integer pageNumber, @RequestParam(name = "sex") String playoneSex, Model m) {
	    Page<Playone> page = pService.sexfindByPage(playoneSex, pageNumber);
	    m.addAttribute("page", page);
	    return "leo/client/sexFindPage"; 
	}
	
	@GetMapping("/playone/byId")
	public String getPlayoneById(@RequestParam("id") Integer playoneId, Model m,HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");
		int playoneLevel = 0;  
	    if (member != null) {
	        playoneLevel = member.getPlayoneLevel();
	    }
		Playone playone = pService.findById(playoneId);
	    m.addAttribute("playone", playone);
	    if (playoneLevel == 1) {
	        return "leo/client/personalPage2";
	    }else {
	    return "leo/client/personalPage";
	    }
	}

	@GetMapping("/playone/byHistory")
	public String getPlayonesByHistory(@RequestParam("ids") List<Integer> playoneIds, Model m) {
	    List<Playone> playones = pService.findByIds(playoneIds);
	    m.addAttribute("playone", playones);
	    return "leo/client/history";
	}

	@GetMapping("/playone/byHot")
	public String hotPage(Model m) {
		List<Playone> playone = pService.findRandomEightByFixedValue();

	    for (Playone p : playone) {
	        if (p.getPlayoneImgs().isEmpty()) {
	            System.out.println("Playone with ID: " + p.getPlayoneId() + " has no images.");
	        } else {
	            System.out.println("Playone with ID: " + p.getPlayoneId() + " has " + p.getPlayoneImgs().size() + " image(s).");
	        }
	    }

	    m.addAttribute("playone", playone);
	    return "leo/client/hotPage";
	}
//----------------------------------------修改功能------------------------------------------	

	@GetMapping("/playone/editById")
	public String editPlayoneById(@RequestParam("id") Integer playoneId, Model m) {
		Playone playone = pService.findById(playoneId);
		m.addAttribute("playone", playone);
		return "leo/client/personalEditPage";
	}

	@PutMapping("/playone/clienteditAjax")
	public ResponseEntity<String> editClientPost(@RequestParam("id") String playoneId,
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
		
	    try {    
		    pService.updatePlayoneById(playoneId, pNick, pName, pSex, pBirth, pInterest, pIntroduce);
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
	
	@PostMapping("/playone/clientimgedit")
	public String playoneImgEdit(@RequestParam("id") Integer playoneId, Model m) {
		List<PlayoneImg> playoneImgList =piService.findImgById(playoneId);
		 m.addAttribute("playoneImgList", playoneImgList);
		return "leo/client/clientPlayoneImg";
	}
	
	@PostMapping("/playone/clientimgnew")
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

	    return "redirect:/playone/editById?id=" + playoneId;

	}
	
//----------------------------------------其他功能------------------------------------------	
	

}
