//package com.tm.TravelMaster.yu.model;
//
//import com.tm.TravelMaster.chih.model.Member;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.Data;
//
//@Data
//@Entity
//@Table(name = "savedSpot")
//public class SavedSpot {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "savedSpotId")
//    private Integer savedSpotId;
//
//    @ManyToOne
//    @JoinColumn(name = "memberSeq", nullable=false)
//    private Member member;
//
//    @ManyToOne
//    @JoinColumn(name = "spotNo", nullable=false)
//    private Spot spot;
//
//    public SavedSpot() {
//    }
//
//}
