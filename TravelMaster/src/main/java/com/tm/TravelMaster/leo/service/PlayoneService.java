package com.tm.TravelMaster.leo.service;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;

import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.chih.model.MemberRepository;
import com.tm.TravelMaster.leo.model.Playone;
import com.tm.TravelMaster.leo.model.PlayoneImg;
import com.tm.TravelMaster.leo.model.PlayoneImgRepository;
import com.tm.TravelMaster.leo.model.PlayoneRepository;

import jakarta.transaction.Transactional;

@Service
public class PlayoneService {
	
	@Autowired
	private PlayoneRepository pRepo;
	
	@Autowired
	private PlayoneImgRepository piRepo;

	@Autowired
	private MemberRepository memberRepository;
//----------------------------------------新增功能------------------------------------------	

	public Playone insertPlayone(Playone po) {
		return pRepo.save(po);
	}

//----------------------------------------修改功能------------------------------------------	

	@Transactional
	public Playone updatePlayoneById( String playoneId, String pNick, String pName, String pSex, String pBirth, String pInterest, String pIntroduce) {
		Optional<Playone> optional = pRepo.findById(Integer.parseInt(playoneId));
		if (optional.isPresent()) {
			Playone playone = optional.get();
			playone.setPlayoneNick(pNick);
			playone.setPlayoneName(pName);
			playone.setPlayoneSex(pSex);
			playone.setPlayoneBirth(Integer.parseInt(pBirth));
			playone.setPlayoneInterest(pInterest);
			playone.setPlayoneIntroduce(pIntroduce);
			playone.setFixedValue(1);
			
			
			pRepo.save(playone);
			return playone;
		}
		System.out.println("No Playone with id: " + playoneId);
		return null;
	}
	
	@Transactional
	public Playone updateRegisteredById( String playoneId) {
		Optional<Playone> optional = pRepo.findById(Integer.parseInt(playoneId));
		if (optional.isPresent()) {
			Playone playone = optional.get();
			playone.setFixedValue(1);
			playone.setRegistered(0);
			pRepo.save(playone);
			return playone;
		}
		return null;
	}

	
//----------------------------------------刪除功能------------------------------------------	
	
	@Transactional
	public Playone deleteById(Integer id) {
		Optional<Playone> optional = pRepo.findById(id);
		if (optional.isPresent()) {
			Playone p = optional.get();
			p.setFixedValue(0);
			
			for (PlayoneImg img : p.getPlayoneImgs()) {
				img.setFixedValue(0);
				piRepo.save(img);
			}
			
			pRepo.save(p);
			return p;
		}
		System.out.println("No Playone with id: " + id);
		return null;
	}
	
	@Transactional
	public void deleteRegisteredById(Integer id) {
	    Optional<Playone> optionalPlayone = pRepo.findById(id);
	    if (optionalPlayone.isPresent()) {
	        Playone playone = optionalPlayone.get();

	        // Unlink the Playone from the associated Member
	        playone.setMember(null);
	        pRepo.save(playone);  // Save the Playone with the removed association

	        // Then try to delete the Playone
	        pRepo.deleteByPlayoneId(id);

	        // Also delete the associated PlayoneImg entities
	        piRepo.deleteByPlayoneId(id);
	    } else {
	        System.out.println("No Playone with id: " + id);
	    }
	}

	
//----------------------------------------搜尋功能------------------------------------------	
	
	public List<Playone> findAll() {
	    List<Playone> playones = pRepo.findByFixedValue(1);
	    for (Playone playone : playones) {
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }
	    return playones;
	}

	
	public List<Playone> findRandomSixByFixedValue() {
		List<Playone> playones = pRepo.findRandomSixByFixedValue();
		for (Playone playone : playones) {
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }
	    return playones;
	}

	public List<Playone> findRandomEightByFixedValue() {
	    List<Playone> playones = pRepo.findRandomEightByFixedValue();
		for (Playone playone : playones) {
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }
	    return playones;
	}

	public Page<Playone> findByPage(Integer pageNumber) {
	    Pageable pgb = PageRequest.of(pageNumber - 1, 8, Sort.Direction.ASC, "playoneId");
	    Page<Playone> page = pRepo.findByFixedValue(1, pgb);
	    List<Playone> playones = new ArrayList<>(page.getContent());
	    for (Playone playone : playones) {
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }
	    return new PageImpl<>(playones, page.getPageable(), page.getTotalElements());
	}

	public Page<Playone> sexfindByPage(String playoneSex,Integer pageNumber) {
		Pageable pgb = PageRequest.of(pageNumber-1,8, Sort.Direction.ASC,"playoneId");
		Page<Playone> page = pRepo.findByPlayoneSexContainingAndFixedValue(playoneSex,1, pgb);
		List<Playone> playones = new ArrayList<>(page.getContent());
	    for (Playone playone : playones) {
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }
	    return new PageImpl<>(playones, page.getPageable(), page.getTotalElements());
	}
	
	
	public List<PlayoneImg> getPhotosByPlayoneId(Integer playoneId) {
	    return piRepo.findByPlayone_PlayoneId(playoneId);
	}
	
	public PlayoneImg getPhotoById(Integer id) {
		Optional<PlayoneImg> optional = piRepo.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public Playone findById(Integer playoneId) {
	    Playone playone = pRepo.findByPlayoneIdAndFixedValue(playoneId, 1);
	    if (playone != null) {
	        List<PlayoneImg> images = piRepo.findByPlayone_PlayoneIdAndFixedValue(playoneId, 1);
	        playone.setPlayoneImgs(images);
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	        return playone;
	    }
	    return null;
	}
	

	public Playone findBySeq(Integer memberSeq) {
	    Member member = memberRepository.findById(memberSeq).orElse(null);
	    if (member == null) {
	    }
	    Playone playone = pRepo.findByMember(member);
	    return playone;
	}

	
	public List<Playone> findByRegistered() {
		List<Playone> playones = pRepo.findByRegistered(1);
		 for (Playone playone : playones) {
		        playone.setAge(calculateAge(playone.getPlayoneBirth()));
		        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
		    }
		    return playones;
	}

	
	public List<Playone> findByName(String playoneName) {
	    List<Playone> playones = pRepo.findByPlayoneNameAndFixedValue(playoneName,1);
	    for (Playone playone : playones) {
	    	List<PlayoneImg> images = piRepo.findByPlayone_PlayoneIdAndFixedValue(playone.getPlayoneId(), 1);
	        playone.setPlayoneImgs(images);
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }
	    return playones;
	}

	public List<Playone> findByNick(String playoneNick) {
	    List<Playone> playones = pRepo.findByPlayoneNickContainingAndFixedValue(playoneNick,1);
	    for (Playone playone : playones) {
	    	List<PlayoneImg> images = piRepo.findByPlayone_PlayoneIdAndFixedValue(playone.getPlayoneId(), 1);
	        playone.setPlayoneImgs(images);
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }
	    return playones;
	}

	public List<Playone> findBySex(String playoneSex) {
		List<Playone> playones = pRepo.findByPlayoneSexContainingAndFixedValue(playoneSex,1);
		for (Playone playone : playones) {
			List<PlayoneImg> images = piRepo.findByPlayone_PlayoneIdAndFixedValue(playone.getPlayoneId(), 1);
			playone.setPlayoneImgs(images);
			playone.setAge(calculateAge(playone.getPlayoneBirth()));
			playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
		}
		return playones;
	}

	public List<Playone> findByIds(List<Integer> playoneIds) {
	    List<Playone> playones = pRepo.findAllById(playoneIds);
	    for (Playone playone : playones) {
	        playone.setAge(calculateAge(playone.getPlayoneBirth()));
	        playone.setZodiac(getZodiac(playone.getPlayoneBirth()));
	    }

	    playones.sort(Comparator.comparingInt(playone -> playoneIds.indexOf(playone.getPlayoneId())));
	    return playones;
	}

//----------------------------------------購物車功能------------------------------------------	

//	// JSON儲存路徑
//		private static final String CART_DATA_DIRECTORY = "C:\\TravelMaster\\workspace\\TravelMaster\\src\\data\\cart_data";
//	
//	// 載入購物車資訊-JSON
//	public List<Playone> loadPlayoneCartData(String memberNum) {
//	    List<Playone> cart = new ArrayList<>();
//	    ObjectMapper objectMapper = new ObjectMapper();
//
//	    try {
//	        File file = new File(CART_DATA_DIRECTORY + File.separator + memberNum + "_playone.json");
//	        if (file.exists()) {
//	            cart = objectMapper.readValue(file, new TypeReference<List<Playone>>() {});
//	        }
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	    return cart;
//	}
//
//	// 保存購物車資訊-JSON
//	public void savePlayoneCartData(String memberNum, List<Playone> cart) {
//	    ObjectMapper objectMapper = new ObjectMapper();
//	    File file = new File(CART_DATA_DIRECTORY + File.separator + memberNum + "_playone.json");
//	    try {
//	        objectMapper.writeValue(file, cart);
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	}
//
//	// 檢查Playone是否在購物車中
//	public boolean isPlayoneInCart(List<Playone> cart, int playoneId) {
//	    return cart.stream().anyMatch(playone -> playone.getPlayoneId() == playoneId);
//	}

	
//----------------------------------------其他功能------------------------------------------	
	
	public boolean checkIfplayoneNickExist(String playoneNick) {
		Playone dbUser = pRepo.findByPlayoneNick(playoneNick);
		if (dbUser!=null) {
			return true;
		}else {
			return false;
		}
	}
	
	private int calculateAge(int birthDate) {
	    int birthYear = birthDate / 10000;
	    int currentYear = Year.now().getValue();
	    return currentYear - birthYear;
	}
	
	public List<Playone> findByAgeRange(int ageStart, int ageEnd) {
	    List<Playone> allPlayones = pRepo.findByFixedValue(1);
	    List<Playone> playonesInAgeRange = new ArrayList<>();
	    int currentYear = Year.now().getValue();

	    for (Playone playone : allPlayones) {
	        int birthYear = playone.getPlayoneBirth() / 10000;
	        int age = currentYear - birthYear;
	        if (age >= ageStart && age <= ageEnd) {
	            playonesInAgeRange.add(playone);
	        }
	    }

	    return playonesInAgeRange;
	}


	
	public String getZodiac(int birthDate) {
		int month = (birthDate / 100) % 100;
		int day = birthDate % 100;
	    if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) {
	        return "♈️牡羊座";
	    } else if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) {
	        return "♉️金牛座";
	    } else if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) {
	        return "♊️雙子座";
	    } else if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) {
	        return "♋️巨蟹座";
	    } else if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) {
	        return "♌️獅子座";
	    } else if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) {
	        return "♍️處女座";
	    } else if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) {
	        return "♎️天秤座";
	    } else if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) {
	        return "♏️天蠍座";
	    } else if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) {
	        return "♐️射手座";
	    } else if ((month == 12 && day >= 22) || (month == 1 && day <= 19)) {
	        return "♑️魔羯座";
	    } else if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) {
	        return "♒️水瓶座";
	    } else if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) {
	        return "♓️雙鱼座";
	    } else {
	        return "❌未知";
	    }
	}

}
