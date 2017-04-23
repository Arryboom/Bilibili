package org.pqh.dao;

import org.apache.ibatis.annotations.Param;
import org.pqh.entity.Bangumi;
import org.pqh.entity.Bili;
import org.pqh.entity.Cid;
import org.pqh.entity.Save;
import org.pqh.entity.statistics.AvCount;
import org.pqh.entity.statistics.AvPlay;
import org.pqh.entity.statistics.Ranking;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface BiliDao extends Serializable{

	List<Save> selectSave(Integer id);

	void updateSave(Save save);

	void insertBili(Bili bili);

	void updateBili(Bili bili);

	void insertCid(Bili bili);

	void updateCid(Bili bili);

	void insertC(Cid cid);

	void updateC(Cid cid);

	//查询所有日期当天视频投稿量
	List<AvCount> selectAvCount(@Param("date") String date,@Param("count") String count);

	void insertAvCount(String date);

	void updateAvCount(String date);

	void insertAvPlay(List list);

	List<AvPlay> selectAvPlay();

	List<Ranking> selectRanking();

	void insertParam(org.pqh.entity.Param param);

	void updateParam(org.pqh.entity.Param param);

	List<org.pqh.entity.Param> selectParam(String key);

	void insertBangumi(Bangumi bangumi);

	void updateBangumi(Bangumi bangumi);

	List<Bangumi> selectBangumi(Bangumi bangumi);

	int getLastAid(@Param("table") String table,@Param("field") String field);




}
