package org.pqh.qq;

import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.Message;
import org.apache.log4j.Logger;
import org.pqh.dao.BiliDao;
import org.pqh.entity.Bili;
import org.pqh.entity.Param;
import org.pqh.util.LogUtil;
import org.pqh.util.TimeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by reborn on 2017/3/14.
 */
@Component
public class QueryA implements QueryRule{

    private Map<String,String> fields;

    private Logger log=Logger.getLogger(QueryA.class);

    @Resource
    private BiliDao biliDao;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public boolean check(Bili bili) {
        if(fields==null&&!setQuery()){
            log.error("匹配条件不能为空，并且尝试获取匹配条件失败，请检查配置表query1的值");
            return false;
        }
        for(String fieldName :fields.keySet()){
            try {

                Field field=bili.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                if(!String.valueOf(field.get(bili)).contains(fields.get(fieldName))){
                    return false;
                }
            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
                LogUtil.outPutLog(LogUtil.getLineInfo(),e);
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
                LogUtil.outPutLog(LogUtil.getLineInfo(),e);
            }
        }

        return true;

    }

    public boolean setQuery(){
        fields=new HashMap<>();
        try{
            Param param=biliDao.selectParam("query1");
            if(param.getValue().equals("*")){
                return false;
            }
            for(String value:param.getValue().split("&")){
                String val[]=value.split("=");
                fields.put(val[0],val[1]);
            }
            return true;
        }catch (Exception e){
            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
            return false;
        }
    }

    @Override
    public void onMessage(Message message) {
        log.info(TimeUtil.formatDate(new Timestamp(message.getTime()),TimeUtil.DATETIME)+"\n"+message.getContent());
    }

    @Override
    public void onGroupMessage(GroupMessage groupMessage) {
        String msg=groupMessage.getContent();
        log.info(TimeUtil.formatDate(new Timestamp(groupMessage.getTime()),TimeUtil.DATETIME)+"\n"+msg);

    }

    @Override
    public void onDiscussMessage(DiscussMessage discussMessage) {
        log.info(TimeUtil.formatDate(new Timestamp(discussMessage.getTime()),TimeUtil.DATETIME)+"\n"+discussMessage.getContent());
    }
}
