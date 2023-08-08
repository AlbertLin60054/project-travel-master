package com.tm.TravelMaster.leo.model;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tm.TravelMaster.chih.model.Member;

public interface PlayoneRepository extends JpaRepository<Playone, Integer> {

	public Playone findByPlayoneNick(String playoneNick);

	public List<Playone> findByPlayoneNameAndFixedValue(String playoneName, Integer fixedValue);
	
	public List<Playone> findByPlayoneNickContainingAndFixedValue(String playoneNick, Integer fixedValue);

	public List<Playone> findByPlayoneSexContainingAndFixedValue(String playoneSex, Integer fixedValue);

	public Playone findByPlayoneIdAndFixedValue(Integer playoneId, Integer fixedValue);
	
	List<Playone> findByFixedValue(Integer fixedValue);

	List<Playone> findByRegistered(Integer registered);
	
	@Query(value = "SELECT * FROM Playone WHERE fixedValue = 1 ORDER BY NEWID()",nativeQuery = true)
    Page<Playone> findByFixedValueRandom(Pageable pageable);
	
	@Query(value = "SELECT TOP 8 * FROM Playone WHERE fixedValue = 1 ORDER BY NEWID()", nativeQuery = true)
	List<Playone> findRandomEightByFixedValue();

	@Query(value = "SELECT TOP 6 * FROM Playone WHERE fixedValue = 1 ORDER BY NEWID()", nativeQuery = true)
	List<Playone> findRandomSixByFixedValue();
	
	Page<Playone> findByFixedValue(Integer fixedValue, Pageable pageable);
	
	Page<Playone> findByPlayoneSexContainingAndFixedValue(String playoneSex,Integer fixedValue, Pageable pageable);
	
	void deleteByPlayoneId(Integer playoneId);

	Playone findByMember(Member member);

}
