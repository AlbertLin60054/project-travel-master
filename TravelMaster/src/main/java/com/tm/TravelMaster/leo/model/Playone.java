package com.tm.TravelMaster.leo.model;

import java.util.List;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tm.TravelMaster.chih.model.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "playone")
public class Playone {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "playoneId")
	private int playoneId;
	@Column(name = "playoneNick")
	private String playoneNick;
	@Column(name = "playoneName")
	private String playoneName;
	@Column(name = "playoneSex")
	private String playoneSex;
	@Column(name = "playoneBirth")
	private int playoneBirth;
	@Column(name = "playoneInterest")
	private String playoneInterest;
	@Column(name = "playoneIntroduce")
	private String playoneIntroduce;
	@OneToMany(mappedBy = "playone", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@Where(clause = "fixedvalue = 1")
	private List<PlayoneImg> playoneImgs;
	@Column(name = "fixedValue")
	private int fixedValue;
	@Column(name = "registered")
	private int registered;

	// 購物車使用
	@Transient
	private int playoneDays;

	@OneToOne
	@JoinColumn(name = "memberSeq", referencedColumnName = "memberSeq")
	private Member member;

	@Transient
	private int age;
	@Transient
	private String Zodiac;

	public Playone() {
	};

	public Playone(String playoneNick, String playoneName, String playoneSex, int playoneBirth, String playoneInterest,
			String playoneIntroduce, List<PlayoneImg> playoneImgs, int fixedValue, int registered, Member member) {
		this.playoneNick = playoneNick;
		this.playoneName = playoneName;
		this.playoneSex = playoneSex;
		this.playoneBirth = playoneBirth;
		this.playoneInterest = playoneInterest;
		this.playoneIntroduce = playoneIntroduce;
		this.playoneImgs = playoneImgs;
		this.fixedValue = fixedValue;
		this.registered = registered;
		this.member = member;
	}
}
