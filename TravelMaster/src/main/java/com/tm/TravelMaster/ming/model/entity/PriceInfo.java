package com.tm.TravelMaster.ming.model.entity;

import java.io.Serializable;
import java.util.Objects;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity @Table(name = "PriceInfo")
@Data
public class PriceInfo {
	@EmbeddedId
	private PriceInfo_PK pk;
	
	@Column(name = "Price")
	private int price;

	public PriceInfo() {
	}


	@Override
	public String toString() {
		return "PriceInfoDTO [DepartureST=" + pk.getDepartureST() + ", DestinationST=" + pk.getDestinationST() + ", Price=" + price
				+ "]";
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	public String getDepartureST() {
		return pk.getDepartureST();
	}

	public void setDepartureST(String departureST) {
		pk.setDepartureST(departureST);
	}

	public String getDestinationST() {
		return pk.getDestinationST();
	}

	public void setDestinationST(String destinationST) {
		pk.setDestinationST(destinationST);
	}
}


@Embeddable
class PriceInfo_PK implements Serializable {
	private static final long serialVersionUID = 8764734968205185910L;
	@Column(name = "DepartureST")
	private String departureST;

	@Column(name = "DestinationST")
	private String destinationST;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PriceInfo_PK pk = (PriceInfo_PK) o;
		return Objects.equals(departureST, pk.departureST) && Objects.equals(destinationST, pk.destinationST);
	}

	@Override
	public int hashCode() {
		return Objects.hash(departureST, departureST);
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
}
