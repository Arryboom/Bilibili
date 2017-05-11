package org.pqh.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Connection;
import org.pqh.entity.Bangumi;
import org.pqh.entity.Bili;
import org.springframework.dao.DuplicateKeyException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.pqh.util.SpringContextHolder.biliDao;
public class BiliUtil {

	private static Logger log=Logger.getLogger(BiliUtil.class);
	private static org.dom4j.Document xml= null;
	private static JsonNode jsonNode;
	public static String access_key;
	private static String appkey;
	private static String app_secret;
	public static Map<String,String> bili_cookie;
	static {
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		try {
			jsonNode=objectMapper.readTree(new File(BiliUtil.class.getClassLoader().getResource("region.json").getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		access_key=biliDao.selectParam("access_key").get(0).getValue();
		refreshCookie();
		if(access_key.isEmpty()||bili_cookie.size()==0){
			throw new RuntimeException("请先到数据库param表设置好access_key或者bili用户cookie");
		}
		appkey=biliDao.selectParam("appkey").get(0).getValue();
		app_secret=biliDao.selectParam("app_secret").get(0).getValue();
	}

	public static void refreshCookie(){
			bili_cookie=new HashMap<>();
			String keys[]=new String[]{"DedeUserID","DedeUserID__ckMd5","SESSDATA"};
			for(String key:keys){
				bili_cookie.put(key,biliDao.selectParam(key).get(0).getValue());
			}
	}

	/**
	 * 把xml节点值通过反射注入到不同对象
	 * @param elements  xml文档节点列表
	 * @return
	 */
	public static Bili setElement(List<Element> elements){
		Bili bili=new Bili();
		for(org.dom4j.Element element:elements){
			//不需要入库的节点数据
			if(PropertiesUtil.getProperties("excludenode",String.class).contains(element.getName())){
				continue;
			}
			ReflexUtil.setObject(bili,element.getName(),element.getText());
		}
		return bili;
	}

	/**
	 * 爬取接口api.bilibili.com/view的信息
	 * @param aid  视频av号
	 * @param page 视频分P
	 * @return 返回接口数据注入的对象集合
	 */
	public static Bili setView(int aid, int page){
		Queue<String> strings=new LinkedList<String>();
		strings.offer(access_key);
		strings.offer(appkey);
		strings.offer(aid+"");
		strings.offer(page+"");
		//拼接请求参数
		Map<String,String> map=parseXml(strings,ApiUrl.AID.getUrl());
		org.dom4j.Document document=null;
		document = CrawlerUtil.jsoupGet(ApiUrl.AID.getUrl(), CrawlerUtil.DataType.domcument, Connection.Method.GET,map.get("params_").split(","));
		if(document==null){
			return null;
		}

		Element element=document.getRootElement();
		Element code=element.element("code");
		if(code!=null) {
			if (code.getText().equals("-403") || code.getText().equals("-404")||code.getText().equals("10")) {
				return null;
			} else if (code.getText().equals("-503")) {
				ThreadUtil.sleep(LogUtil.getLineInfo()+"\n"+code.getText(),3);
				return setView(aid, page);
			}else if(code.getText().equals("-2")){
				String updateKey = "";
				do {
					ThreadUtil.sleep("access_key续期失败，请手动到数据库更新access_key", 30);
					updateKey = biliDao.selectParam("access_token").get(0).getValue();
				} while (updateKey.equals(access_key));
				access_key = updateKey;
			}
			ThreadUtil.sleep(LogUtil.getLineInfo()+"\n"+code.getText(),3);
			return setView(aid, page);

		}
		return setElement(element.elements());
	}


	/**
	 * 根据视频二级分类返回一级分类
	 * @param type 二级分类
	 * @return
	 */
	public static String getBq(String type){

		for(JsonNode node:jsonNode){
			String name=node.findValue("name").asText();
			for(JsonNode node1:node.findValue("children")){
				if(type.equals(node1.get("name").asText())){
					return name;
				}
			}
		}
		return null;
	}


	/**
	 * 使同一个文件夹里的视频跟对应的字幕的文件名一致
	 * @param dirpath 需要修改文件名的目录
	 * @param suffix  被同步的字幕文件格式
	 * @param size  视频体积过滤大小单位MB
	 */
	public static void replaceFileName(String dirpath,String suffix,int size){
		File file=new File(dirpath);
		List<File> subtitle= (List<File>) FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(suffix),null);
		List<File> video= (List<File>) FileUtils.listFiles(file,FileFilterUtils.sizeFileFilter(1024*1024*size),null);
		if(subtitle.size()!=video.size()){
			throw  new RuntimeException("视频文件数与弹幕文件数不一致无法同步文件名");
		}
		for(int i=0;i<video.size();i++){
			try {
				String path=video.get(i).getAbsolutePath();
				String _suffix=path.substring(path.lastIndexOf("."));
				FileUtils.moveFile(subtitle.get(i),new File(path.replace(_suffix,"."+suffix)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void insertBangumi() {
		for (int season_id = 1; season_id < 7000; season_id++) {
			log.info("season_id:" + season_id);
			JsonNode node = CrawlerUtil.jsoupGet(ApiUrl.bangumiAnime.getUrl(season_id), CrawlerUtil.DataType.json, Connection.Method.GET);
			if (node.get("code").asInt() == 10) {
				continue;
			}
			String title = node.get("result").get("title").asText();
			int bangumi_id = node.get("result").get("bangumi_id").asInt();
			Bangumi bangumi = new Bangumi(season_id, bangumi_id, title);
			try {
				biliDao.insertBangumi(bangumi);
			} catch (DuplicateKeyException e) {
				biliDao.updateBangumi(bangumi);
			}
		}
	}

	/**
	 *
	 *获取爬虫进度
	 * @param id
	 * @param <T>
	 * @return
	 */
	public static <T>T getSave(int id){
		String bilibili=biliDao.selectSave(id).get(0).getBilibili();
		if(bilibili.contains(":")) {
			String str[] = bilibili.split(":");
			int num[] = new int[2];
			num[0] = Integer.valueOf(str[0]);
			num[1] = Integer.valueOf(str[1]);
			return (T) num;
		}else{
			return (T) Integer.valueOf(bilibili);
		}
	}

	/**
	 * 自动续期access_key
	 */
	public static void updateAccesskey(){
		log.info("自动续期access_key");
		JsonNode jsonNode=CrawlerUtil.jsoupGet(ApiUrl.accessKey.getUrl(BiliUtil.access_key), CrawlerUtil.DataType.json,Connection.Method.GET);
		log.info(jsonNode);
	}


	/**
	 * 构建表单参数。
	 * @param strings
	 * @param url
	 * @return
	 */
	public static Map<String,String> parseXml(Queue<String> strings,String url){
		Map<String,String> map=new HashMap<>();

		try {
			if(xml==null) {
				String xmlPath=BiliUtil.class.getClassLoader().getResource("formparam.xml").getPath();
				xml = new SAXReader().read(xmlPath);
			}
		} catch (DocumentException e) {
			log.error(e);
			e.printStackTrace();
		}
		List<org.dom4j.Element> forms=xml.getRootElement().elements();
		for(org.dom4j.Element form:forms){
			if(form.attribute("url").getStringValue().equals(url)){
				List<Element> params=form.elements();
				List<String> type1=new ArrayList<>();
				List<String> type2=new ArrayList<>();
				for(org.dom4j.Element param:params){
					String value="";
					if(!param.getStringValue().isEmpty()){
						value=param.getStringValue();
					}else if(strings!=null){
						value=strings.poll();
					}else{
						throw new RuntimeException("栈为空无法注入预定义参数");
					}
					type1.add(param.getName()+"="+value);
					type2.add(param.getName());
					type2.add(value);
					map.put(param.getName(), value);
				}

				String p=StringUtils.join(type1,"&");
				String sign=AlgorithmUtil.MD5(p+app_secret).toLowerCase();
				type1.add("sign="+sign);
				p=StringUtils.join(type1,"&");
				map.put("sign", sign);
				map.put("params_", StringUtils.join(type2,","));
				map.put("params", p);
				return map;
			}
		}
		throw new RuntimeException("找不到"+url+"的表单参数");
	}

}
