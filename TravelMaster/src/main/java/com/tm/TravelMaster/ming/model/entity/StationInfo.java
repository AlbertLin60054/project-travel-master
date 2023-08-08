package com.tm.TravelMaster.ming.model.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity @Table(name = "StationInfo")
@Data
public class StationInfo {

	@Id @Column(name = "StationID")
	private int stationID;
	
	@Column(name = "StationName")
	private String stationName;
	
	public StationInfo() {
	}

	public StationInfo(ResultSet rs) throws SQLException {
		this.stationID = rs.getInt("StationID");
		this.stationName = rs.getString("StationName");
	}

	@Override
	public String toString() {
		return "StationInfoDTO [StationID=" + stationID + ", StationName=" + stationName + "]";
	}
	
}
