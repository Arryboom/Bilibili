package org.pqh.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.pqh.dao.BiliDao;
import org.pqh.dao.BiliHistoryDao;
import org.pqh.entity.Bili;
import org.pqh.entity.Cid;
import org.pqh.entity.Save;
import org.pqh.entity.history.Data;
import org.pqh.entity.statistics.AvCount;
import org.pqh.task.TaskQuery;
import org.pqh.util.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class InsertService{
	private static Logger log= Logger.getLogger(InsertService.class);
	@Resource
	private BiliDao biliDao;
	@Resource
	private BiliHistoryDao biliHistoryDao;
	@Resource
	private TaskQuery taskQuery;

	public static boolean stop=false;

	public InsertService() {

	}

	public void insertBili(int id, int aid, int page, int $aid) {
		Bili bili;

		if(aid==0&&page==0){
			int num[]= BiliUtil.getSave(id);
			aid=num[0];
			page=num[1];
		}

		while(($aid==0?true:aid<=$aid)){
			do{
				bili= BiliUtil.setView(aid,page);
				if (bili==null){
					break;
				}
				bili.setAid(aid);

				bili.setTypename2(BiliUtil.getBq(bili.getTypename()));
				bili.setPartid(page);

				taskQuery.setBili(bili);
				ThreadUtil.excute(taskQuery);

				try{
					if(page==1){
						biliDao.insertBili(bili);
					}
					biliDao.insertCid(bili);
					biliDao.updateSave(new Save(id,aid+":"+page,new Timestamp(System.currentTimeMillis()),false));
					log.debug("最新AV:"+ApiUrl.AV.getUrl(new Object[]{aid,page})+"更新于"+ TimeUtil.formatDate(new Date(bili.getCreated()*1000),null));
				}
				catch(DuplicateKeyException e){
					if(e.getMessage().contains("insertBili")){
						biliDao.updateBili(bili);
					}else{
						biliDao.updateCid(bili);
					}
					biliDao.updateSave(new Save(id,aid+":"+page,new Timestamp(System.currentTimeMillis()),false));
				}
				page++;
			}while(page<=bili.getPages());

			int lastAid=biliDao.getLastAid("aid","aid");

			if(aid-lastAid> PropertiesUtil.getProperties("errornum",Integer.class)){
				if(id==1){
					Save save=biliDao.selectSave(id).get(0);
					save.setLatest(true);
					biliDao.updateSave(save);
				}
				ThreadUtil.sleep(60);
				aid=lastAid;
			}else{
				aid++;
			}
			page=1;

		}

	}

	public void insertHistory() {
		Save save=biliDao.selectSave(4).get(0);
		for (int aid = Integer.parseInt(save.getBilibili());!stop ;) {
			int count=aid;
			do {
				JsonNode n=CrawlerUtil.jsoupGet(ApiUrl.biliHistory.s(2).getUrl("add"), JsonNode.class, Connection.Method.POST, "access_key", BiliUtil.access_token, "aid", String.valueOf(aid), "jsonp", "jsonp");
				if(n.get("code").asInt()==-500){
					try {
						Thread.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				aid++;
			}while (aid-count<=100);
			JsonNode jsonNode = CrawlerUtil.jsoupGet(ApiUrl.biliHistory.s(1).getUrl(BiliUtil.access_token, 1, 100), JsonNode.class, Connection.Method.GET);
			if(jsonNode.get("data")==null||jsonNode.get("data").size()==0){
				if(jsonNode.get("code").asInt()==-500){
					ThreadUtil.sleep(60);
				}
				continue;
			}else{

				for(JsonNode node:jsonNode.get("data")) {
					Data data = new Data();
					Iterator<String> iterator = node.fieldNames();

					while (iterator.hasNext()) {
						Object obj  ;
						String key = iterator.next();
						if ("type,device,progress".contains(key)) {
							continue;
						}
						JsonNode keys = node.get(key);
						if (keys.isInt()) {
							obj = keys.asInt();
						} else if (keys.isBoolean()) {
							obj = keys.asBoolean();
						} else if (keys.isTextual()) {
							obj = keys.asText();
						} else if (keys.isArray()) {
							obj = StringUtils.join(keys);
						} else {
							obj = keys.toString();
						}
						data = ReflexUtil.setObject(data, key, obj);
					}
					String date=TimeUtil.formatDate(new Timestamp(data.getCtime()*1000),TimeUtil.DATE);
					try {

						List<AvCount> avCounts= biliDao.selectAvCount(date,null);
						if(avCounts.size()==0){
							biliDao.insertAvCount(date);
						}else {
							biliDao.updateAvCount(date);
						}
						biliHistoryDao.insertHistory(data);
					}catch (DuplicateKeyException e){
						biliHistoryDao.updateHistory(data);
					}

				}

			}
			CrawlerUtil.jsoupGet(ApiUrl.biliHistory.s(2).getUrl("clear"), JsonNode.class, Connection.Method.POST, "access_key", BiliUtil.access_token, "jsonp", "jsonp");
			save.setBilibili((aid)+"");
			save.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			biliDao.updateSave(save);
		}
	}

	public  void insertCid(Integer cid){
		String url = ApiUrl.CID.getUrl(cid);
		Cid c=new Cid();
		Field fields[]=c.getClass().getDeclaredFields();
		Document document = CrawlerUtil.jsoupGet(url, Document.class, Connection.Method.GET);
		if(document==null){
			Save save=biliDao.selectSave(2).get(0);
			biliDao.updateSave(save);
			return;
		}
		for(Field field:fields ){
			field.setAccessible(true);
			String key=field.getName();
			String value=document.select(field.getName()).html();
			if(key.equals("aid")&&value.isEmpty()){
				return;
			}
			c= ReflexUtil.setObject(c,key,value);
		}
		c.setCid(cid);
		try {
			biliDao.insertC(c);
		}catch (DuplicateKeyException e){
			biliDao.updateC(c);
		}
		biliDao.updateSave(new Save(2,cid+"",new Timestamp(System.currentTimeMillis()),false));

	}


}
