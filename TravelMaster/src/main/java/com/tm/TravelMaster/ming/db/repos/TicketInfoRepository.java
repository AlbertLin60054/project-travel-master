package com.tm.TravelMaster.ming.db.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tm.TravelMaster.ming.model.entity.TicketInfo;

@Repository
public interface TicketInfoRepository extends JpaRepository<TicketInfo, Integer>{

	List<TicketInfo> findByCartIdIs(int cart_Id);
}
