//package com.tm.TravelMaster.yu.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import com.tm.TravelMaster.chih.model.Member;
//import com.tm.TravelMaster.yu.model.SavedSpot;
//import com.tm.TravelMaster.yu.model.SavedSpotRepository;
//import com.tm.TravelMaster.yu.model.Spot;
//
//@Service
//public class SavedSpotService {
//
//	@Autowired
//	private SavedSpotRepository saRepo;
//
//	// 儲存收藏
//	public void savedSpots(SavedSpot spot) {
//		saRepo.save(spot);
//	}
//
//	// 刪除收藏
//	public void removedSpots(SavedSpot spot) {
//		saRepo.delete(spot);
//	}
//
//	// 查詢會員收藏的紀錄
//	public SavedSpot findByMemberMemberSeqAndspotNo(int memberSeq, Integer spotNo) {
//		return saRepo.findByMemberMemberSeqAndSpotSpotNo(memberSeq, spotNo);
//	}
//
//	// 透過會員編號查詢該會員所有收藏
//	public Page<SavedSpot> findAllByMemberMemberSeq(int memberSeq, Integer pageNum) {
//		PageRequest pgb = PageRequest.of(pageNum - 1, 3, Sort.Direction.DESC, "savedSpotId");
//		return saRepo.findAllByMemberMemberSeq(memberSeq, pgb);
//	}
//
//	public SavedSpot saveSpot(Member member, Spot spot) {
//		SavedSpot savedSpot = new SavedSpot();
//		savedSpot.setMember(member);
//		savedSpot.setSpot(spot);
//		return saRepo.save(savedSpot);
//	}
//}
//
////    public void removeSpot(Member member, Spot spot) {
////        savedSpotRepository.deleteByMemberAndSpot(member, spot);
////    }
////
////    public List<Spot> getSavedSpots(Member member) {
////        List<SavedSpot> savedSpots = savedSpotRepository.findByMember(member);
////        List<Spot> spots = new ArrayList<>();
////        for (SavedSpot savedSpot : savedSpots) {
////            spots.add(savedSpot.getSpot());
////        }
////        return spots;
////    }
//
////}
