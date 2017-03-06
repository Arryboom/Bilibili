package main.java.org.pqh.dao;

import main.java.org.pqh.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BiliDao {
	Bili findByCid(int cid);
	Bili findByAid(int aid);

	Save getAid(int id);
	void setAid(Save save);
	void setLatest(@Param("id")int id,@Param("latest")boolean latest);

	List<Bili> findAids(int aid);
	List<Bili> findCids(@Param("startcid")int a,@Param("overcid")int b);

	void insertBili(Bili bili);
	void updateBili(Bili bili);

	void insertCid(Bili bili);
	void updateCid(Bili bili);

	int count(int aid);
	List<Bili> findVCC(@Param("startaid")int a,@Param("overaid")int b);

	void insertC(Cid cid);

	void updateC(Cid cid);
	//查询所有日期当天视频投稿量
	List<AvCount> selectAvCount();

	void insertAvPlay(List list);
	List<AvPlay> selectAvPlay();

	List<Ranking> selectRanking();

	void insertParam(main.java.org.pqh.entity.Param param);

	main.java.org.pqh.entity.Param selectParam(String key);

	List<main.java.org.pqh.entity.Param> selectParams();

	void updateParam(main.java.org.pqh.entity.Param param);

	void insertBangumi(Bangumi bangumi);

	void updateBangumi(Bangumi bangumi);

	Bangumi selectBangumi_id(Integer bangumi_id);

	int getLastAid(@Param("table") String table,@Param("field") String field);
}
