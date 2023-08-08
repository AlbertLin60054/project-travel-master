package com.tm.TravelMaster.ming.model.entity;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "TicketInfoGroup")
public class TicketInfoGroup {

	@Id
	@Column(name = "cart_Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer cart_Id; 

	@Column(name = "member_id")
	private String member_id;

	@Column(name = "status")
	private TicketInfoGroupStatus status;

	@JsonManagedReference // 由這邊做JSON序列化
	@OneToMany(mappedBy = "ticketInfoGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	// 這個是對應的Collection 跟Table無關 純粹是告訴Hibernate對應關係 一併拉一並存宜並更新
	private List<TicketInfo> ticketInfos = new ArrayList<>(); 

	public TicketInfoGroup() {
	}

	public int getStatus() {
		return status.getId();
	}

	public void setStatus(int status_id) {
		for (TicketInfoGroupStatus status : TicketInfoGroupStatus.values()) {
			if (status.getId() == status_id) {
				this.status = status;
				break;
			}
		}
	}

	@Override
	public String toString() {
		return String.format("TicketInfoGroup[cart_id=%d, member_id=%s, status=%d(%s)]", cart_Id, member_id,
				status.getId(), status.getDesc());
	}

}

enum TicketInfoGroupStatus {
	NOT_CHECKED_OUT(0, "未結帳"), CHECKED_OUT(1, "已結帳");

	private int id;
	private String desc;

	TicketInfoGroupStatus(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}

	public int getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
}

