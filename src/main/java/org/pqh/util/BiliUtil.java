package org.pqh.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Connection;
import org.pqh.entity.Bangumi;
import org.pqh.entity.Bili;
import org.pqh.test.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.pqh.util.SpringContextHolder.biliDao;
public class BiliUtil {

	private static Logger log=Logger.getLogger(BiliUtil.class);
	private static org.dom4j.Document xml= null;
	private static JsonNode jsonNode;
	private static String access_token;
	private static String appkey;
	private static String app_secret;
	static {
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		try {
			jsonNode=objectMapper.readTree(new File(BiliUtil.class.getClassLoader().getResource("region.json").getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		access_token=biliDao.selectParam("access_token").getValue();
		appkey=biliDao.selectParam("appkey").getValue();
		app_secret=biliDao.selectParam("app_secret").getValue();
	}

	/**
	 * 把xml节点值通过反射注入到不同对象
	 * @param elements  xml文档节点列表
	 * @param list 反射的类对象
	 * @param index 反射类索引
	 * @return
	 */
	public static List setElement(List<Element> elements,List list,int index){
		Test test=new Test();
		for(org.dom4j.Element element:elements){
			//不需要入库的节点数据
			if(PropertiesUtil.getProperties("excludenode",String.class).contains(element.getName())){
				continue;
			}
			if(element.elements().size()>0){
				setElement(element.elements(),list,1);
			}else{
				ReflexUtil.setObject(list.get(index),element.getName(),element.getText());
			}
		}
		return list;
	}

	/**
	 * 爬取接口api.bilibili.com/view的信息
	 * @param aid  视频av号
	 * @param page 视频分P
	 * @return 返回接口数据注入的对象集合
	 */
	public static List setView(int aid, int page){
		Queue<String> strings=new LinkedList<String>();
		strings.offer(access_token);
		strings.offer(appkey);
		strings.offer(aid+"");
		strings.offer(page+"");
		Map<String,String> map=parseXml(strings,ApiUrl.AID.getUrl());
		org.dom4j.Document document=null;
		String url= ApiUrl.AID.getUrl(map.get("params"));
		document = CrawlerUtil.jsoupGet(url, org.dom4j.Document.class, Connection.Method.GET);
		if(document==null){
			return null;
		}
		List list=new ArrayList();
		list.add(new Bili());
		list.add(new Bangumi());
		Element element=document.getRootElement();
		Element code=element.element("code");
		if(code!=null) {
			if (code.getText().equals("-403") || code.getText().equals("-404")||code.getText().equals("10")) {
				return null;
			} else if (code.getText().equals("-503")) {
				ThreadUtil.sleep(LogUtil.getLineInfo()+"\n"+code.getText(),3);
				return setView(aid, page);
			}else if(code.getText().equals("-2")){
				log.info("access_key已过期，发送access_key续期请求");
				JsonNode jsonNode=CrawlerUtil.jsoupGet(ApiUrl.accessKey.getUrl(access_token), JsonNode.class,Connection.Method.GET);
				log.info(jsonNode);
				if(jsonNode.get("code").equals("0")){
					long expires = jsonNode.get("expires").asLong();
					log.info("access_key续期到" + TimeUtil.formatDate(new Date(expires), null));
				}else{
					log.info(jsonNode.get("message").asText());
					String updateKey = "";
					do {
						ThreadUtil.sleep("access_key续期失败，请手动到数据库更新access_key", 30);
						updateKey = biliDao.selectParam("access_token").getValue();
					} while (updateKey.equals(access_token));
					access_token = updateKey;
				}
				return setView(aid, page);
			}

		}
		list=setElement(element.elements(),list,0);
		return list;
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

	/**
	 * 正则表达式匹配
	 * @param str 匹配字符串
	 * @param regex 正则表达式
	 * @param c 返回类型
	 * @param <T>
	 * @return
	 */
	public static <T>T matchStr(String str,String regex,Class<T> c){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		String s=null;
		List<String> list=null;
		while (matcher.find()) {
			s=matcher.group();
			log.info("匹配结果"+s);
			if(c==String.class) {
				return (T)s;
			}else if(c==List.class){
				if(list==null){
					list=new ArrayList<>();
				}
				list.add(s);
			}
		}
		if(s==null){
			return null;
		}else if(c==String.class){
			return (T) s;
		}else if(c==List.class){
			return (T) list;
		}else{
			return null;
		}

	}


	public static Map<String,String> parseXml(Queue<String> strings,String url){
		Map<String,String> map=new HashMap<String, String>();

		try {
			if(xml==null) {
				String xmlPath=BiliUtil.class.getClassLoader().getResource("formparam.xml").getPath();
				xml = new SAXReader().read(xmlPath);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		List<org.dom4j.Element> forms=xml.getRootElement().elements();
		for(org.dom4j.Element form:forms){
			if(form.attribute("url").getStringValue().equals(url)){
				List<Element> params=form.elements();
				String params_="";
				for(org.dom4j.Element param:params){
					String value="";
					if(!param.getStringValue().isEmpty()){
						value=param.getStringValue();
					}else if(strings!=null){
						value=strings.poll();
					}else{
						throw new RuntimeException("栈为空无法注入预定义参数");
					}
					params_+=param.getName()+"="+value+"&";
					map.put(param.getName(),value);
				}
				params_=params_.substring(0,params_.length()-1);
				String sign=AlgorithmUtil.MD5(params_+app_secret).toLowerCase();
				params_+="&sign="+sign;
				map.put("sign",sign);
				map.put("params",params_);
				return map;
			}
		}
		throw new RuntimeException("找不到"+url+"的表单参数");
	}


}
