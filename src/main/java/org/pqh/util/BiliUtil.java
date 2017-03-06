package main.java.org.pqh.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import main.java.org.pqh.entity.Bangumi;
import main.java.org.pqh.entity.Bili;
import main.java.org.pqh.test.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BiliUtil {

	private static Logger log=TestSlf4j.getLogger(BiliUtil.class);
	private static org.dom4j.Document xml= null;

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
		strings.offer(PropertiesUtil.getProperties("access_token",String.class));
		strings.offer(PropertiesUtil.getProperties("appkey",String.class));
		strings.offer(aid+"");
		strings.offer(page+"");
		Map<String,String> map=parseXml(strings,Constant.aidApi);
		org.dom4j.Document document=null;
		String url= Constant.aidApi+map.get("params");
		document = CrawlerUtil.jsoupGet(url, org.dom4j.Document.class,Constant.GET);
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
				ThreadUtil.sleep(TestSlf4j.getLineInfo(),3);
				return setView(aid, page);
			}else if(code.getText().equals("-2")){
				TestSlf4j.errorLog(log,"当前配置项access_key已过期，请从配置文件更新access_key",true);
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
		if(checkbq(type, Constant.dougan)){
			return "动画";
		}else if(checkbq(type, Constant.bangumi)){
			return "番剧";
		}else if(checkbq(type, Constant.music)){
			return "音乐";
		}else if(checkbq(type, Constant.dance)){
			return "舞蹈";
		}else if(checkbq(type, Constant.game)){
			return "游戏";
		}else if(checkbq(type, Constant.technology)){
			return "科技";
		}else if(checkbq(type, Constant.ent)){
			return "娱乐";
		}else if(checkbq(type, Constant.kichiku)){
			return "鬼畜";
		}else if(checkbq(type, Constant.movie)){
			return "电影";
		}else if(checkbq(type, Constant.teleplay)){
			return "电视剧";
		}else if(checkbq(type, Constant.fashion)){
			return "时尚";
		}else{
			return null;
		}
	}

	/**
	 *
	 * @param em 二级分类
	 * @param list 二级分类列表
	 * @return 二级分类若存在返回true,否则返回flase
	 */
	private static boolean checkbq(String em,String[] list){
		for(String ems:list){
			if(em.equals(ems)){
				return true;
			}
		}
		return false;
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
		List list=new ArrayList();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			if(c==String.class) {
				return (T) matcher.group();
			}else if(c==List.class){
				list.add(matcher.group());
			}else{
				throw  new RuntimeException(c.getClass().getName()+"参数类型错误");
			}
		}
		throw new RuntimeException("无法用正则表达式("+regex+")从字符串("+str+")中匹配到任何结果");

	}

	/**
	 * 正则表达式匹配内容以外的结果
	 * @param str 匹配字符串
	 * @param regexs 正则表达式数组
	 * @param regex
	 * @return
	 */
	public static String matchStr(String str,String []regexs,String regex){
		for(int i=0;i<regexs.length;i++) {
			Pattern pattern = Pattern.compile(regexs[i]);
			Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
				return matcher.group().replaceAll(regex, "");
			}
		}
		return "";
	}

	/**
	 *从文件解析数据
	 * @param tClass
	 * @param filepath
	 * @param <T>
	 * @return
	 */
	public static <T>T parseFile(Class<T> tClass,String filepath){
		try {
			List<String> strings=FileUtils.readLines(new File(filepath),"UTF-8");
			T t=tClass.newInstance();
			for(String s:strings){
				if(tClass==ArrayList.class){
					((ArrayList) t).add(s);
				}else if(tClass==HashMap.class){
					((HashMap) t).put(s.split(":")[0],s.split(":")[1]);
				}
			}
			return t;
		} catch (IOException e) {
			TestSlf4j.outputLog(e,log);
		} catch (InstantiationException e) {
			TestSlf4j.outputLog(e,log);
		} catch (IllegalAccessException e) {
			TestSlf4j.outputLog(e,log);
		}
		throw new RuntimeException("解析文件出错");
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
					if(param.getStringValue().length()>0){
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
				String sign=AlgorithmUtil.MD5(params_+PropertiesUtil.getProperties("app_secret",String.class)).toLowerCase();
				params_+="&sign="+sign;
				map.put("sign",sign);
				map.put("params",params_);
				return map;
			}
		}
		throw new RuntimeException("找不到"+url+"的表单参数");
	}

	/**
	 * 调用window图片查看器打开图片
	 * @param file 图片文件对象
	 */
	public static void openImage(File file){
		try {
			Runtime.getRuntime().exec("rundll32 c:\\Windows\\System32\\shimgvw.dll,ImageView_Fullscreen "+file.getAbsoluteFile());
		} catch (IOException e) {
			TestSlf4j.outputLog(e,log);
		}
	}

	/**
	 * 获取Accesskey
	 * @return
	 */
//	public static String getAccesskey() {
//		String username=PropertiesUtil.getProperties("biliusername",String.class);
//		String pwd=PropertiesUtil.getProperties("bilipwd",String.class);
//		if ("".equals(username) || "".equals(pwd)) {
//			throw new RuntimeException("bilibili账号和密码不能为空");
//		}
//
//		String appkey= PropertiesUtil.getProperties("appkey",String.class);
//		String	app_secret= PropertiesUtil.getProperties("app_secret",String.class);
//		try {
//			username= URLEncoder.encode(username,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			TestSlf4j.outputLog(e,log);
//		}
//
//		Queue<String> strings = new LinkedList<String>();
//		strings.offer(appkey);
//		strings.offer(System.currentTimeMillis()+"");
//		Map<String,String> map=parseXml(strings,Constant.PUBLICKEYAPI);
//
//		JsonNode jsonNode=CrawlerUtil.jsoupGet(Constant.PUBLICKEYAPI+map.get("params"),JsonNode.class,Constant.POST);
//		String publicKey=jsonNode.get("data").get("key").asText();
//
//		return publicKey;
//
//	}

}
