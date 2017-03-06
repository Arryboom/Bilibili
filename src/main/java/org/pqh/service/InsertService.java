package main.java.org.pqh.service;

import com.fasterxml.jackson.databind.JsonNode;
import main.java.org.pqh.dao.BiliDao;
import main.java.org.pqh.dao.VstorageDao;
import main.java.org.pqh.entity.Bangumi;
import main.java.org.pqh.entity.Bili;
import main.java.org.pqh.entity.Cid;
import main.java.org.pqh.entity.Save;
import main.java.org.pqh.test.Test;
import main.java.org.pqh.util.*;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class InsertService{
	private static Logger log= TestSlf4j.getLogger(InsertService.class);
	@Resource
	private BiliDao biliDao;
	@Resource
	private VstorageDao vstorageDao;

	public void insertBili(int aid,int page,int $aid) {
		Bili bili=null;
		Bangumi bangumi=null;

		if(aid==0&&page==0){
			int num[]= Test.getSave(1);
			aid=num[0];
			page=num[1];
		}

		while($aid==0?true:aid<=$aid){
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
				try{
					if(page==1){

						if(bangumi.getBangumi_id()!=null){
							bili.setBangumi_id(bangumi.getBangumi_id());
							biliDao.insertBangumi(bangumi);
						}
						biliDao.insertBili(bili);
					}
					biliDao.insertCid(bili);
					biliDao.setAid(new Save(1,aid+":"+page,new Timestamp(System.currentTimeMillis()),false));
					log.info("最新AV:http://www.bilibili.com/video/av"+aid+"/index_"+page+".html更新于"+ TimeUtil.formatDateToString(bili.getCreated()*1000));
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
					biliDao.setAid(new Save(1,aid+":"+page,new Timestamp(System.currentTimeMillis()),false));
				}
				page++;
			}while(page<=bili.getPages());

			int lastAid=biliDao.getLastAid("aid","aid");

			if(aid-lastAid> PropertiesUtil.getProperties("errornum",Integer.class)){
				biliDao.setLatest(1,true);
				ThreadUtil.sleep(60);
				aid=lastAid;
			}else{
				aid++;
			}
			page=1;

		}

	}

	public void insertVstorage(Integer cid){
		Map<String, Object> map = new HashMap<String, Object>();
		String classnames[] = Constant.CLASSNAME.split(",");
		for (String classname : classnames) {
			try {
				if (classname.contains("<")) {
					StringBuffer stringBuffer = new StringBuffer(classname);
					String type1 = stringBuffer.substring(stringBuffer.indexOf("<") + 1, stringBuffer.indexOf(">"));
					String type2 = stringBuffer.substring(0, stringBuffer.indexOf("<"));
					map.put(type1, ReflexUtil.getObject(type2));
				} else {
					Class c = Class.forName(classname);
					map.put(c.getName(), c.newInstance());
				}
			} catch (ClassNotFoundException e) {
				TestSlf4j.outputLog(e,log);
			} catch (InstantiationException e) {
				TestSlf4j.outputLog(e,log);
			} catch (IllegalAccessException e) {
				TestSlf4j.outputLog(e,log);
			}
		}
		String classname = null;
		JsonNode jsonNode=null;
		try {
			String url = Constant.vstorageApi + cid;
			classname = Class.forName(classnames[0]).getName();
			jsonNode=CrawlerUtil.jsoupGet(url,JsonNode.class,Constant.GET);
		} catch (ClassNotFoundException e) {
			TestSlf4j.outputLog(e,log);
		}

		if(jsonNode.get("list")!=null&&jsonNode.get("list").size()==0){
			biliDao.setLatest(3,true);
			return;
		}
		Test test=new Test();
		map = test.getMap(jsonNode, map, classname, false, 0, cid);
		test.setData(vstorageDao, map);
		int lastCid=Test.getSave(3);
		biliDao.setAid(new Save(3,cid+"",new Timestamp(System.currentTimeMillis()),false));


	}

	public  void insertCid(Integer cid){
		String url = Constant.cidApi+cid;
		Cid c=new Cid();
		Field fields[]=c.getClass().getDeclaredFields();
		Document document = CrawlerUtil.jsoupGet(url, Document.class,Constant.GET);
		if(document==null){
			biliDao.setLatest(2,true);
			return;
		}
		for(Field field:fields ){
			field.setAccessible(true);
			String key=field.getName();
			String value=document.select(field.getName()).html();
			if(key.equals("aid")&&value.length()==0){
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
		int lastCid=Test.getSave(2);
		biliDao.setAid(new Save(2,cid+"",new Timestamp(System.currentTimeMillis()),false));

	}



}
