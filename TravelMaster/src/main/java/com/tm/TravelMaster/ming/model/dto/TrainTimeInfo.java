package com.tm.TravelMaster.ming.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TranInfo")
@Data
public class TrainTimeInfo {

	public TrainTimeInfo() {
	}

	@Id
	@Column(name = "ID")
	private int id;

	@Column(name = "TranNo")
	private String tranNo;

	@Column(name = "DepartureDate")
	private String departureDate;

	@Column(name = "DepartureST")
	private String departureST;

	@Column(name = "DestinationST")
	private String destinationST;

	@Column(name = "Departuretime")
	private String departureTime;

	@Column(name = "Arrivaltime")
	private String arrivalTime;

}
