package com.tm.TravelMaster.leo.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayoneImgRepository extends JpaRepository<PlayoneImg, Integer> {
	List<PlayoneImg> findByPlayone_PlayoneId(Integer playoneId);
	List<PlayoneImg> findByPlayone_PlayoneIdAndFixedValue(Integer playoneId, Integer fixedValue);
	Optional<PlayoneImg> findByPlayoneimgId(Integer playoneimgId);
	@Modifying
	@Query("delete from PlayoneImg p where p.playone.id = :id")
	void deleteByPlayoneId(@Param("id") Integer id);

}
