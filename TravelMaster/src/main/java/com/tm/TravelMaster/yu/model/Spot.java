package com.tm.TravelMaster.yu.model;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "spot")
public class Spot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "spotNo")
	private Integer spotNo;

	@Column(name = "spotName")
	private String spotName;

	@Column(name = "cityRegion")
	private String cityRegion;

	@Column(name = "cityName")
	private String cityName;

	@Column(name = "spotType")
	private String spotType;

	@Column(name = "spotInfo")
	private String spotInfo;

	@Column(name = "spotPic")
	private String spotPic;

	@Column(name = "clickCount")
	private Integer clickCount;
	
	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "latitude")
	private Double latitude;
	
//	@OneToMany(mappedBy = "spot",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//	private List<SavedSpot> spots = new ArrayList<>(0);
	
	@PrePersist
	public void onCreate() {
	    if (clickCount == null) {
	        clickCount = 0;
	    }
	}

	
}
