package com.tm.TravelMaster.ming.model.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tm.TravelMaster.ming.model.dto.HighSpeedRailTicket;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TicketInfo")
@Data
public class TicketInfo {

	@Id
	@Column(name = "TicketID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ticketID;

	@Column(name = "TranNo")
	private String tranNo;

	@Column(name = "Seat")
	private String seat;

	@Column(name = "DepartureST")
	private String departureST;

	@Column(name = "DestinationST")
	private String destinationST;

	@Column(name = "DepartureDate")
	private String departuredate;

	@Column(name = "DepartureTime")
	private String departuretime;

	@Column(name = "ArrivalTime")
	private String arrivaltime;

	@Column(name = "Price")
	private int price;

	@Column(name = "BookingDate")
	private String bookingdate;
	
	// ↓ 這才是真正的欄位資訊(FK) 設定 insertable=false, updatable=false 因為希望由 TicketInfoGroup 這張表去更新
	@Column(name = "cart_Id", insertable=false, updatable=false) 
	private int cartId;
		
	@JsonBackReference //不由這邊做對面的 JSON序列化
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinColumn(name = "cart_Id")
	// 這個是對應的物件 跟Table無關 純粹是告訴Hibernate對應關係 跟著他走
	private TicketInfoGroup ticketInfoGroup;
	
	
	public TicketInfo() {
	}

	public TicketInfo(HighSpeedRailTicket bkdto) {
		setTicketID(bkdto.getTicketID());
		setTranNo(bkdto.getTranNo());
		setSeat(bkdto.getSeat());
		setDepartureST(bkdto.getDepartureST());
		setDestinationST(bkdto.getDestinationST());
		setDeparturedate(bkdto.getDeparturedate());
		setDeparturetime(bkdto.getDeparturetime());
		setArrivaltime(bkdto.getArrivaltime());
		setPrice(bkdto.getPrice());
		setBookingdate(bkdto.getBookingdate());
	}

	@Override
	public String toString() {
		return "TicketDTO [ticketID=" + ticketID + ", tranNo=" + tranNo + ", seat=" + seat + ", DepartureST="
				+ departureST + ", DestinationST=" + destinationST + ",departuredate=" + departuredate
				+ ", Departuretime=" + departuretime + ", Arrivaltime=" + arrivaltime + ", price=" + price
				+ ", bookingdate=" + bookingdate + "]";
	}

	public String getBookingdate() {
		return bookingdate;
	}

	public String getDeparturedate() {
		return departuredate;
	}

	public void setBookingdate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		bookingdate = sdf.format(date);
	}

	public void setBookingdate(String date) {
		bookingdate = date;
	}

	public void setDeparturedate(Date depdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		departuredate = sdf.format(depdate);
	}

	public void setDeparturedate(String depdate) {
		departuredate = depdate;
	}

}
