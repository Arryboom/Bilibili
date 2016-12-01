package org.pqh.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.pqh.dao.BiliDao;
import org.pqh.dao.VstorageDao;
import org.pqh.entity.Bangumi;
import org.pqh.entity.Bili;
import org.pqh.entity.Cid;
import org.pqh.entity.Save;
import org.pqh.test.Test;
import org.pqh.util.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class InsertServiceImpl implements InsertService {
	private static Logger log= TestSlf4j.getLogger(InsertServiceImpl.class);
	@Resource
	private BiliDao biliDao;
	@Resource
	private VstorageDao vstorageDao;

	public static int count;

	public static int count_;

	public void insertBili() {
		Map<String, Object> map;
		Bili bili=null;
		Bangumi bangumi=null;
		Test test=new Test();
		int num[]=test.getSave();
		int aid=num[0];
		int page=num[1];
		while(true){
			do{
				List list=BiliUtil.setView(aid,page);
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

						if(bangumi.getBangumi_id()!=null){;
							bili.setBangumi_id(bangumi.getBangumi_id());
							biliDao.insertBangumi(bangumi);
						}
						if(bili.getLink()==""){
							bili.setLink(null);
						}
						biliDao.insertBili(bili);
					}
					biliDao.insertCid(bili);
					biliDao.setAid(new Save(1,aid+":"+page,new Timestamp(System.currentTimeMillis())));
					log.info("最新AV:http://www.bilibili.com/video/av"+aid+"/index_"+page+".html更新于"+TimeUtil.formatDateToString(bili.getCreated()*1000));
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
					biliDao.setAid(new Save(1,aid+":"+page,new Timestamp(System.currentTimeMillis())));
				}
				page++;
			}while(page<=bili.getPages());

			if(aid-biliDao.getLastAid("aid","aid")>PropertiesUtil.getProperties("errornum",Integer.class)){
				aid-=PropertiesUtil.getProperties("errornum",Integer.class);
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
				TestSlf4j.outputLog(e,log,false);
			} catch (InstantiationException e) {
				TestSlf4j.outputLog(e,log,false);
			} catch (IllegalAccessException e) {
				TestSlf4j.outputLog(e,log,false);
			}
		}
		String classname = null;
		JsonNode jsonNode=null;
		try {
			String url = Constant.VSTORAGEAPI + cid;
			classname = Class.forName(classnames[0]).getName();
			jsonNode=CrawlerUtil.jsoupGet(url,JsonNode.class,Constant.GET);
		} catch (ClassNotFoundException e) {
			TestSlf4j.outputLog(e,log,false);
		}

		if(jsonNode.get("list")!=null){
			count++;
			return;
		}
		Test test=new Test();
		map = test.getMap(jsonNode, map, classname, false, 0, cid);
		test.setData(vstorageDao, map);
		if(!biliDao.getAid(3).getBilibili().equals(cid+"")){
			biliDao.setAid(new Save(3,cid+"",new Timestamp(System.currentTimeMillis())));
		}

	}

	public  void insertCid(Integer cid){
		String url = Constant.CIDAPI+cid;
		Cid c=new Cid();
		Field fields[]=c.getClass().getDeclaredFields();
		Document document = CrawlerUtil.jsoupGet(url, Document.class,Constant.GET);
		for(Field field:fields ){
			field.setAccessible(true);
			String key=field.getName();
			String value=document.select(field.getName()).html();
			if(key.equals("aid")&&value.length()==0){
				count_++;
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
		if(!biliDao.getAid(2).getBilibili().equals(cid+"")){
			biliDao.setAid(new Save(2,cid+"",new Timestamp(System.currentTimeMillis())));
		}
	}
	
	public void insertLimit(Integer aid) {
		if(biliDao.count(aid)==20){
			aid=biliDao.getLimit(aid);
			System.out.println(aid);
			biliDao.setLimit(aid);
			insertLimit(aid);
		}else{
			return;
		}
	}


}
