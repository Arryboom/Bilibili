package org.pqh.util;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pqh.entity.BtAcg;
import org.pqh.task.TaskBtacg;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 10295 on 2016/7/10.
 */
public class FindResourcesUtil {

    public static Map<String,List<BtAcg>> map=new HashMap<String, List<BtAcg>>();
    /**
     * 多线程从Btacg搜索关键字资源种子
     * @param threadPoolTaskExecutor
     * @param keyword
     * @return
     */
    public static Map<String,List<BtAcg>> findBy_Btacg(ThreadPoolTaskExecutor threadPoolTaskExecutor,String keyword) {

        int page = 1;
        eachPage(keyword, page);
        if (TaskBtacg.pages > 1) {
            page = 2;
            do {
                TaskBtacg taskBtacg = new TaskBtacg(page, keyword);
                if (!taskBtacg.getFlag()) {
                    threadPoolTaskExecutor.execute(taskBtacg);
                    page++;
                }
            } while (!(threadPoolTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount() == TaskBtacg.pages - 1));
        }
        for (String key : map.keySet()) {
            map.get(key).sort((BtAcg b1,BtAcg b2)->(Double.parseDouble(b1.getSize())-Double.parseDouble((b2.getSize()))>0?1:-1));

        }
        return map;
    }

    /**
     * 获取单页搜索结果
     * @param keyword 关键字
     * @param page 页数
     */
    public static void eachPage(String keyword,int page){
        Document document = CrawlerUtil.jsoupGet(ApiUrl.btAcgSearch.getUrl(keyword,page), CrawlerUtil.DataType.domcument, Connection.Method.GET);
        if(page==1) {
            String message=document.select(".text_bold").text();
            LogUtil.getLogger().info(message);
            int i=Integer.parseInt(StringUtil.matchStr(message,"\\d+条",String.class).replaceAll("条",""));
            TaskBtacg.pages=i/30+(i%30==0?0:1);
        }
        Elements elements = document.select("#listTable>tbody>tr");
        eachTable(elements, map);
    }

    /**
     * 种子资源筛选
     * @param map 筛选种子
     * @param bt  筛选条件
     * @return
     */
    public static Map<String,String> screenUrl(Map<String,List<BtAcg>> map,BtAcg bt){
        Map<String,String> href=new HashMap<String, String>();
        Field fields[]=bt.getClass().getDeclaredFields();
        for(String type:map.keySet()) {
            List<BtAcg> btAcgs=map.get(type);
            for(BtAcg btAcg:btAcgs) {
                boolean flag=true;
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        if(field.getName().equals("resourceName")&&!field.get(btAcg).toString().contains(field.get(bt).toString())){
                            flag=false;
                            break;
                        }
                        else if(field.get(bt)!=null&&field.get(btAcg).equals(field.get(bt))){
                            flag=false;
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        LogUtil.getLogger().error(String.valueOf(e));
                    }
                }
                if(flag){
                    href.put(btAcg.getResourceName(),btAcg.getHref());
                }
            }
        }

        return href;
    }

    /**
     * 遍历Btacg每一页的表格信息
     * @param elements
     * @param map
     * @return
     */
    private static Map<String,List<BtAcg>> eachTable(Elements elements, Map<String, List<BtAcg>> map){
        Field[] fields= BtAcg.class.getDeclaredFields();
        for(Element element:elements){
            BtAcg btAcg=new BtAcg();
            int i=0;
            for(Element td:element.select("td")){
                fields[i].setAccessible(true);
                try {
                    if (i != element.select("td").size() - 1) {
                        String text=td.text();
                        if(fields[i].getName().equals("size")) {
                            if(text.replaceAll("[\\d\\.]","").equals("GB")) {
                                text = Double.parseDouble(text.replaceAll("[A-Z]", "")) * 1024 + "";
                            }else{
                                text =text.replaceAll("[A-Z]", "");
                            }
                        }
                        fields[i].set(btAcg,text);
                    } else {
                        fields[i].set(btAcg, ApiUrl.btAcgIndex.getUrl(td.select("a").attr("href")));
                    }
                }catch (IllegalAccessException e) {
                    LogUtil.getLogger().error(String.valueOf(e));
                }
                i++;
            }
            if(map.get(btAcg.getType())==null){
                List<BtAcg> list=new ArrayList<BtAcg>();
                list.add(btAcg);
                map.put(btAcg.getType(),list);
            }else{
                map.get(btAcg.getType()).add(btAcg);
            }
        }
        return map;
    }



}
