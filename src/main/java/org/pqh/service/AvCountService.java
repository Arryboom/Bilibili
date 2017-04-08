package org.pqh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.pqh.dao.BiliDao;
import org.pqh.entity.statistics.AvCount;
import org.pqh.entity.statistics.AvPlay;
import org.pqh.entity.statistics.ComparatorAvPlay;
import org.pqh.entity.statistics.Ranking;
import org.pqh.util.ApiUrl;
import org.pqh.util.CrawlerUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by 10295 on 2016/5/19.
 */
@Service
public class AvCountService {
    @Resource
    private BiliDao biliDao;
    private static Logger log= Logger.getLogger(AvCountService.class);
    public Map<String, List> getAvCount() {
        List<AvCount> avCountList = biliDao.selectAvCount();
        List<String> stringList = new ArrayList<String>();
        List<Integer> integerList = new ArrayList<Integer>();
        Map<String, List> map = new HashMap<String, List>();
        for (AvCount avCount : avCountList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(avCount.getDate());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            stringList.add(year + "年" + month + "月" + day + "日");
            integerList.add(avCount.getCount());
        }
        map.put("Date", stringList);
        map.put("Count", integerList);
        return map;
    }

    public  void setPlays() {
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        List<AvPlay> avPlays=new ArrayList<>();
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            JsonNode jsonNode=CrawlerUtil.jsoupGet(ApiUrl.bangumi.getUrl(),JsonNode.class, Connection.Method.GET);
            if(jsonNode==null){
                log.info("无法解析番剧接口信息");
                return;
            }
            JsonNode arrayNode=jsonNode.get("result").get("list");
            for (int i=0;i<arrayNode.size();i++) {
                jsonNode=arrayNode.get(i);
                String bgmid=jsonNode.get("season_id").toString();
                String title=jsonNode.get("title").toString();
                Document document= CrawlerUtil.jsoupGet(ApiUrl.bangumiAnime.getUrl(bgmid),Document.class,Connection.Method.GET);
                String jsonStr=document.body().html();
                jsonStr=jsonStr.substring(jsonStr.indexOf("{"),jsonStr.lastIndexOf("}"))+"}";
                jsonNode=objectMapper.readTree(jsonStr).get("result");
                int newest_ep_index=0;
                newest_ep_index = jsonNode.get("newest_ep_index").asInt();
                int avgPlay=jsonNode.get("play_count").asInt()/newest_ep_index;
                AvPlay avPlay=new AvPlay(title,avgPlay,timestamp);
                avPlays.add(avPlay);
            }
            Collections.sort(avPlays,new ComparatorAvPlay("play"));
            int ranking=avPlays.size();
            for(AvPlay avPlay1:avPlays){
                avPlay1.setRanking(ranking--);
            }
            biliDao.insertAvPlay(avPlays);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (DuplicateKeyException e){
            e.printStackTrace();
        }
    }

    public  Map<String,Object> getAvPlay(){
        List<AvPlay> list=biliDao.selectAvPlay();
        List<Integer> play=new ArrayList<Integer>();
        Map<String,Object> map=new HashMap<String, Object>();
        List<Ranking> rankings=biliDao.selectRanking();
        for(AvPlay avPlay:list){
            if(avPlay.getPlay()==0){
                play.add(null);
            }else {
                play.add(avPlay.getPlay());
            }
            if(list.indexOf(avPlay)+1==list.size()){
                map.put(avPlay.getTitle(),play);
                break;
            }
            if(!list.get(list.indexOf(avPlay)+1).getTitle().equals(avPlay.getTitle())){
                    map.put(avPlay.getTitle(),play);
                    play=new ArrayList<Integer>();
            }
//            time.add(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(avPlay.getTimestamp()));

        }
        map.put("Rankings",rankings);
        return map;
    }




}
