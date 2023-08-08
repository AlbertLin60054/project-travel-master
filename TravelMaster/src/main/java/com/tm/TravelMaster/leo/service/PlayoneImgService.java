package com.tm.TravelMaster.leo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tm.TravelMaster.leo.model.Playone;
import com.tm.TravelMaster.leo.model.PlayoneImg;
import com.tm.TravelMaster.leo.model.PlayoneImgRepository;
//import com.tm.TravelMaster.leo.model.PlayoneRepository;

import jakarta.transaction.Transactional;

@Service
public class PlayoneImgService {
	
//	@Autowired
//	private PlayoneRepository pRepo;
	
	@Autowired
	private PlayoneImgRepository piRepo;

//----------------------------------------新增功能------------------------------------------	
	
	public PlayoneImg insertPlayonePhoto(byte[] imgData, Playone playone) {
	    // 創建一個新的 PlayoneImg 對象
	    PlayoneImg playoneImg = new PlayoneImg();

	    // 將圖片數據和 playone 設置到 PlayoneImg 對象中
	    playoneImg.setPlayonePhoto(imgData);
	    playoneImg.setPlayone(playone);
	    playoneImg.setFixedValue(1);
	    

	    // 使用 Repository 保存 PlayoneImg 對象
	    return piRepo.save(playoneImg);
	}


//----------------------------------------刪除功能------------------------------------------	
	
	@Transactional
	public PlayoneImg deleteById(Integer playoneimgId) {
		Optional<PlayoneImg> optional = piRepo.findByPlayoneimgId(playoneimgId);
		if (optional.isPresent()) {
			PlayoneImg p = optional.get();
			p.setFixedValue(0);
			piRepo.save(p);
			return p;
		}
		System.out.println("No Playone with id: " + playoneimgId);
		return null;
	}
	
	
	
//	public void deleteById(Integer playoneimgId) {
//		piRepo.deleteById(playoneimgId);
//	}
	
	public List<PlayoneImg> findAll() {
		return piRepo.findAll();
	}
	
	public List<PlayoneImg> findImgById(Integer playoneId) {
		return piRepo.findByPlayone_PlayoneIdAndFixedValue(playoneId,1);
	}
}
