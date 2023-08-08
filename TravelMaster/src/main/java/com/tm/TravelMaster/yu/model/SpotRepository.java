package com.tm.TravelMaster.yu.model;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpotRepository extends JpaRepository<Spot, Integer>, JpaSpecificationExecutor<Spot> {

	@Query("SELECT DISTINCT s.cityRegion FROM Spot s")
	public List<String> findAllCityRegions();

	@Query("SELECT DISTINCT s.cityName FROM Spot s")
	public List<String> findAllCityNames();

	@Query("SELECT DISTINCT s.spotType FROM Spot s")
	public List<String> findAllSpotTypes();

	public List<Spot> findAll(Specification<Spot> spec, Sort sort);

	@Query("SELECT s FROM Spot s WHERE s.cityName = :cityName AND s.spotNo != :spotNo")
    List<Spot> findRelatedSpotsByCityNameAndSpotNoNot(@Param("cityName") String cityName, @Param("spotNo") Integer spotNo);
}
