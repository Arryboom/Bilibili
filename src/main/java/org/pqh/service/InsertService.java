package org.pqh.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.pqh.dao.BiliDao;
import org.pqh.entity.*;
import org.pqh.entity.vstorage.Vstorage;
import org.pqh.task.TaskQuery;
import org.pqh.test.Test;
import org.pqh.util.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class InsertService{
	private static Logger log= Logger.getLogger(InsertService.class);
	@Resource
	private BiliDao biliDao;
	@Resource
	private TaskQuery taskQuery;
	@Resource
	private Test test;

	public InsertService() {

	}

	public void insertBili(int id, int aid, int page, int $aid)throws InterruptedException {
		Bili bili=null;
		Bangumi bangumi=null;

		if(aid==0&&page==0){
			int num[]= Test.getSave(0,id);
			aid=num[0];
			page=num[1];
		}

		while(($aid==0?true:aid<=$aid)){
			do{
				List list= BiliUtil.setView(aid,page);
				if(list==null){
					break;
				}
				bili= (Bili) list.get(0);
				bangumi= (Bangumi) list.get(1);
				bili.setAid(aid);

				bili.setTypename2(BiliUtil.getBq(bili.getTypename()));
				bili.setPartid(page);

				taskQuery.setBili(bili);
				Test.excute(taskQuery);

				try{
					if(page==1){

						if(bangumi.getBangumi_id()!=null){
							bili.setBangumi_id(bangumi.getBangumi_id());
							biliDao.insertBangumi(bangumi);
						}
						biliDao.insertBili(bili);
					}
					biliDao.insertCid(bili);
					biliDao.setAid(new Save(id,aid+":"+page,new Timestamp(System.currentTimeMillis()),false));
					log.debug("最新AV:"+ApiUrl.AV.getUrl(aid,page)+"更新于"+ TimeUtil.formatDate(new Date(bili.getCreated()*1000),null));
				}
				catch(DuplicateKeyException e){
					if(e.getMessage().contains("insertBili")){
						biliDao.updateBili(bili);
					}else if(e.getMessage().contains("insertBangumi")){
						biliDao.updateBangumi(bangumi);
					}
					else{
						biliDao.updateCid(bili);
					}
					biliDao.setAid(new Save(id,aid+":"+page,new Timestamp(System.currentTimeMillis()),false));
				}
				page++;
			}while(page<=bili.getPages());

			int lastAid=biliDao.getLastAid("aid","aid");

			if(aid-lastAid> PropertiesUtil.getProperties("errornum",Integer.class)){
				if(id==1){
					biliDao.setLatest(id,true);
				}
				ThreadUtil.sleep(60);
				aid=lastAid;
			}else{
				aid++;
			}
			page=1;

		}

	}

	public void insertVstorage(Integer cid){
		String url = ApiUrl.vstorage.getUrl(cid);
		JsonNode jsonNode=CrawlerUtil.jsoupGet(url,JsonNode.class,Connection.Method.GET);

		if(jsonNode==null||jsonNode.get("list")!=null){
			biliDao.setLatest(3,true);
			return;
		}

		Map<String,Object> map = test.getMap(jsonNode,ReflexUtil.getMap(), Vstorage.class.getName(), false, 0, cid);
		test.setData(map);
		biliDao.setAid(new Save(3,cid+"",new Timestamp(System.currentTimeMillis()),false));

	}

	public  void insertCid(Integer cid){
		String url = ApiUrl.CID.getUrl(cid);
		Cid c=new Cid();
		Field fields[]=c.getClass().getDeclaredFields();
		Document document = CrawlerUtil.jsoupGet(url, Document.class, Connection.Method.GET);
		if(document==null){
			biliDao.setLatest(2,true);
			return;
		}
		for(Field field:fields ){
			field.setAccessible(true);
			String key=field.getName();
			String value=document.select(field.getName()).html();
			if(key.equals("aid")&&value.isEmpty()){
				return;
			}
			c= (Cid) ReflexUtil.setObject(c,key,value);
		}
		c.setCid(cid);
		try {
			biliDao.insertC(c);
		}catch (DuplicateKeyException e){
			biliDao.updateC(c);
		}
		biliDao.setAid(new Save(2,cid+"",new Timestamp(System.currentTimeMillis()),false));

	}


}
