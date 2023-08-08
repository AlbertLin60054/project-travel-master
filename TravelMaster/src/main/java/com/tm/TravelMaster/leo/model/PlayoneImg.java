package com.tm.TravelMaster.leo.model;


import java.util.Base64;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "playoneImg")
public class PlayoneImg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "playoneimgId")
    private int playoneimgId;
	@Column(name = "playonePhoto")
    private byte[] playonePhoto;
    @ManyToOne
    @JoinColumn(name = "playoneid", referencedColumnName = "playoneid")
    private Playone playone;
    @Column(name = "fixedValue")
    private int fixedValue;
    
    public String getBase64Photo() {
        return Base64.getEncoder().encodeToString(this.playonePhoto);
    }

    public void setBase64Photo(String base64Photo) {
        this.playonePhoto = Base64.getDecoder().decode(base64Photo);
    }
    
    public PlayoneImg() {};
    
    public PlayoneImg(byte[] playonePhoto, Playone playone, int fixedValue) {
		super();
		this.playonePhoto = playonePhoto;
		this.playone = playone;
		this.fixedValue = fixedValue;
	}
}
