package com.tm.TravelMaster.ming.db.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tm.TravelMaster.ming.model.dto.TrainTimeInfo;

public interface TrainTimeInfoRepostory extends JpaRepository<TrainTimeInfo, Integer> {

	@Query(value = "select ROW_NUMBER() OVER(ORDER BY CONVERT( TIME, dep_st.TrainArrivalTime ) ASC) AS ID, "
			+ "dep_st.TranNo TranNo, "
			+ "DepartureDate=getdate(), "
			+ "dep_st.StationID DepartureST, "
			+ "dep_st.TrainArrivalTime Departuretime, "
			+ "des_st.StationID DestinationST, "
			+ "des_st.TrainArrivalTime Arrivaltime "
			+ "from TranInfo dep_st "
			+ "left join TranInfo des_st "
			+ "on dep_st.TranNo = des_st.TranNo "
			+ "and dep_st.TrainArrivalTime <> des_st.TrainArrivalTime "
			+ "and des_st.TrainArrivalTime > dep_st.TrainArrivalTime "
			+ "where des_st.StationID is not null;", nativeQuery = true)
	public List<TrainTimeInfo> getTrainTimeInfo(); 
	
	
	@Query(value = "select ROW_NUMBER() OVER(ORDER BY CONVERT( TIME, dep_st.TrainArrivalTime ) ASC) AS ID, "
			+ "dep_st.TranNo TranNo, "
			+ "DepartureDate=getdate(), "
			+ "dep_st.StationID DepartureST, "
			+ "dep_st.TrainArrivalTime Departuretime, "
			+ "des_st.StationID DestinationST, "
			+ "des_st.TrainArrivalTime Arrivaltime "
			+ "from TranInfo dep_st "
			+ "left join TranInfo des_st "
			+ "on dep_st.TranNo = des_st.TranNo "
			+ "and dep_st.TrainArrivalTime <> des_st.TrainArrivalTime "
			+ "and des_st.TrainArrivalTime > dep_st.TrainArrivalTime "
			+ "where des_st.StationID is not null AND dep_st.StationID= :dep AND des_st.StationID = :des AND dep_st.TrainArrivalTime > :dep_time ", 
			 countQuery = "select count(*) "
						+ "from TranInfo dep_st "
						+ "left join TranInfo des_st "
						+ "on dep_st.TranNo = des_st.TranNo "
						+ "and dep_st.TrainArrivalTime <> des_st.TrainArrivalTime "
						+ "and des_st.TrainArrivalTime > dep_st.TrainArrivalTime "
						+ "where des_st.StationID is not null AND dep_st.StationID= :dep AND des_st.StationID = :des AND dep_st.TrainArrivalTime > :dep_time ",
			 nativeQuery = true)
	public Page<TrainTimeInfo> getTrainTimeInfo(
			@Param(value = "dep") String departureST, 
			@Param(value = "des")String destinationST, 
			@Param(value = "dep_time") String departureTime, 
			Pageable pageable); 
}	//上方的Query做查詢，下方的countQuery用於分頁查詢會去計算元素總數
