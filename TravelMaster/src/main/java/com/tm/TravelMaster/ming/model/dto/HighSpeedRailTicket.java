package com.tm.TravelMaster.ming.model.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tm.TravelMaster.ming.model.entity.TicketInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

public class HighSpeedRailTicket {
	// View Object, Just for viewing
	private int ticketID;
	private String tranNo;
	private String seat;
	private String departureST;
	private String destinationST;
	private String departuredate;
	private String departuretime;
	private String arrivaltime;
	private int price;
	private String bookingdate;

	public HighSpeedRailTicket() {
	}
	
	public HighSpeedRailTicket(TicketInfo tickDto) {
		setTicketID(tickDto.getTicketID());
		setTranNo(tickDto.getTranNo());
		setSeat(tickDto.getSeat());
		setDepartureST(tickDto.getDepartureST());
		setDestinationST(tickDto.getDestinationST());
		setdeparturedate(tickDto.getDeparturedate());
		setDeparturetime(tickDto.getDeparturetime());
		setArrivaltime(tickDto.getArrivaltime());
		setPrice(tickDto.getPrice());
		setbookingdate(tickDto.getBookingdate());
	}

	public int getTicketID() {
		return ticketID;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public String getTranNo() {
		return tranNo;
	}

	public void setTranNo(String tranNo) {
		this.tranNo = tranNo;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getDepartureST() {
		return departureST;
	}

	public void setDepartureST(String departureST) {
		this.departureST = departureST;
	}

	public String getDestinationST() {
		return destinationST;
	}

	public void setDestinationST(String destinationST) {
		this.destinationST = destinationST;
	}

	public String getDeparturetime() {
		return departuretime;
	}

	public void setDeparturetime(String departuretime) {
		this.departuretime = departuretime;
	}

	public String getArrivaltime() {
		return arrivaltime;
	}

	public void setArrivaltime(String arrivaltine) {
		arrivaltime = arrivaltine;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getBookingdate() {
		return bookingdate;
	}

	public String getDeparturedate() {
		return departuredate;
	}

	public void setbookingdate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		bookingdate = sdf.format(date);
	}

	public void setbookingdate(String date) {
		bookingdate = date;
	}

	public void setdeparturedate(Date depdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		departuredate = sdf.format(depdate);
	}

	public void setdeparturedate(String depdate) {
		departuredate = depdate;
	}

	@Override
	public String toString() {
		return "BookingTk [ticketID=" + ticketID + ", tranNo=" + tranNo + ", seat=" + seat + ", DepartureST="
				+ departureST + ", DestinationST=" + destinationST + ",departuredate=" + departuredate
				+ ", Departuretime=" + departuretime + ", Arrivaltime=" + arrivaltime + ", price=" + price
				+ ", bookingdate=" + bookingdate + "]";
	}
}
