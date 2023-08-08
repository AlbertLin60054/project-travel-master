package com.tm.TravelMaster.ming.db.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tm.TravelMaster.ming.db.repos.ShoppingCartRepository;
import com.tm.TravelMaster.ming.db.repos.TicketInfoRepository;
import com.tm.TravelMaster.ming.model.entity.TicketInfoGroup;
import com.tm.TravelMaster.ming.model.entity.TicketInfo;


@Service
public class TicketInfoService {

	@Autowired
	private TicketInfoRepository ticketInfoRepos;
	
	@Autowired
	private ShoppingCartRepository shoppingCartRepos;
	
	// TicketInfo CRUD
	
	@Transactional(rollbackFor = SQLException.class)
	public void insertTicketInfos(List<TicketInfo> ticketInfos) throws SQLException {
		for (TicketInfo ticketInfo : ticketInfos) {
			ticketInfoRepos.save(ticketInfo);
		}
	}

	public void insertTicketInfo(TicketInfo ticket) {
		ticketInfoRepos.save(ticket);
	}

	public TicketInfo findTicketInfoById(int ticketID) {
		Optional<TicketInfo> optional = ticketInfoRepos.findById(ticketID);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public List<TicketInfo> findAllTicketInfo(){
		return ticketInfoRepos.findAll();
	}

	@Transactional
	public TicketInfo updateTicketInfo(TicketInfo tickets) {
		Optional<TicketInfo> optional = ticketInfoRepos.findById(tickets.getTicketID());
		
		if(optional.isPresent()) {
			TicketInfo ticket = optional.get();
			ticket.setTranNo(tickets.getTranNo());
			ticket.setSeat(tickets.getSeat());
			ticket.setDepartureST(tickets.getDepartureST());
			ticket.setDestinationST(tickets.getDestinationST());
			ticket.setDeparturedate(tickets.getDeparturedate());
			ticket.setDeparturetime(tickets.getDeparturetime());
			ticket.setArrivaltime(tickets.getArrivaltime());
			ticket.setPrice(tickets.getPrice());
			ticket.setBookingdate(tickets.getBookingdate());
			System.out.println("update data");
			return tickets;
			
		}
		System.out.println("no update data");
		return null;
	}

	public boolean deleteTickerInfoById(int ticketID) {
	    if (ticketID > 0) {
	        ticketInfoRepos.deleteById(ticketID);
	        System.out.println("delete data");
	        return true;
	    }
	    return false;
	}
	
	public boolean deleteTicketInfo(TicketInfo ticketInfo) {
	    if (ticketInfo != null) {
	        ticketInfoRepos.delete(ticketInfo);
	        return true;
	    }
	    return false;
	}
	
	// ShoppingCart CRUD
	@Transactional(rollbackFor = SQLException.class)
	public TicketInfoGroup insertShoppingCart(TicketInfoGroup cart)throws SQLException {
		return shoppingCartRepos.save(cart);
	}
	
	// 利用ShoppingCart的 cart_Id 找到 TicketInfo 資訊
	// EX: cart_Id = 1001 ， TicketInfo = [ticketID=11, tranNo=919, seat=01D, ...]
 	public TicketInfoGroup findShoppingCartById(int cart_Id) {
		Optional<TicketInfoGroup> optional = shoppingCartRepos.findById(cart_Id);
		if (optional.isPresent()) {
			TicketInfoGroup result = optional.get();
			result.setTicketInfos(ticketInfoRepos.findByCartIdIs(cart_Id));
			return result;
		}
		return null;
	}
 	
 	public boolean deleteShoppingCart(TicketInfoGroup shoppingCart) {
	    if (shoppingCart != null) {
	    	shoppingCartRepos.delete(shoppingCart);
	        return true;
	    }
	    return false;
	}
	
}
