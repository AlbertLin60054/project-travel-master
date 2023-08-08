package com.tm.TravelMaster.yu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import com.tm.TravelMaster.yu.model.Spot;
import com.tm.TravelMaster.yu.model.SpotRepository;

@Service
public class SpotService {

	@Autowired
	private SpotRepository sRepo;

	public Spot insert(Spot spot) {
		return sRepo.save(spot);
	}

	public List<String> getAllCityRegions() {
		return sRepo.findAllCityRegions();
	}

	public List<String> getAllCityNames() {
		return sRepo.findAllCityNames();
	}

	public List<String> getAllSpotTypes() {
		return sRepo.findAllSpotTypes();
	}

	public Spot findById(Integer spotNo) {
		Optional<Spot> optional = sRepo.findById(spotNo);

		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public List<Spot> findAll() {
		return sRepo.findAll();
	}

	public List<Spot> findTopNSpots(int n) {
		PageRequest pageRequest = PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "clickCount"));
		return sRepo.findAll(pageRequest).getContent();
	}

	public List<Spot> findTopNSpotsBySpotNoDesc(int n) {
		PageRequest pageRequest = PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "spotNo"));
		return sRepo.findAll(pageRequest).getContent();
	}

	public List<Spot> findRelatedSpots(Integer spotNo, String cityName) {
        return sRepo.findRelatedSpotsByCityNameAndSpotNoNot(cityName, spotNo);
    }

	public List<Spot> fuzzySearch(String cityRegion, String cityName, String spotType, String txt) {
		Specification<Spot> spec = Specification.where(null);
		if (cityRegion != null && !cityRegion.isEmpty()) {
			spec = spec
					.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cityRegion"), cityRegion));
		}

		if (cityName != null && !cityName.isEmpty()) {
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cityName"), cityName));
		}

		if (spotType != null && !spotType.isEmpty()) {
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("spotType"), spotType));
		}

		if (txt != null && !txt.isEmpty()) {
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
					criteriaBuilder.like(root.get("spotName"), "%" + txt + "%"),
					criteriaBuilder.like(root.get("spotInfo"), "%" + txt + "%")));
		}
		return sRepo.findAll(spec);
	}

	@Transactional
	public Spot updateClickCount(Integer spotNo) {
	    Spot spot = sRepo.findById(spotNo).orElse(null);
	    if (spot != null) {
	        spot.setClickCount(spot.getClickCount() + 1);
	        return sRepo.save(spot);
	    }
	    return null;
	}
	
	

	@Transactional
	public Spot update(Spot spot) {
		Optional<Spot> optional = sRepo.findById(spot.getSpotNo());

		if (optional.isPresent()) {
			Spot newSpot = optional.get();
			newSpot.setSpotName(spot.getSpotName());
			newSpot.setCityRegion(spot.getCityRegion());
			newSpot.setCityName(spot.getCityName());
			newSpot.setSpotType(spot.getSpotType());
			newSpot.setSpotInfo(spot.getSpotInfo());
			newSpot.setSpotPic(spot.getSpotPic());
			return newSpot;
		}
		return null;
	}

	public void deleteById(Integer spotNo) {
		sRepo.deleteById(spotNo);
	}

}
