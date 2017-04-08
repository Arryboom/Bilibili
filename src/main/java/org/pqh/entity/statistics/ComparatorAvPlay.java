package org.pqh.entity.statistics;

import org.pqh.util.LogUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Created by 10295 on 2016/7/4.
 */
public class ComparatorAvPlay implements Comparator {
    private String fieldName;
    private static Logger log= Logger.getLogger(ComparatorAvPlay.class);
    public ComparatorAvPlay(String fieldName) {
        this.fieldName = fieldName;
    }


    public int compare(Object o1, Object o2) {
        Field field=null;
        try {
            field=o1.getClass().getDeclaredField(this.fieldName);
            field.setAccessible(true);
            o1=field.get(o1);
            o2=field.get(o2);
            Double d=Double.parseDouble(o1.toString())-Double.parseDouble(o2.toString());
            return d>0?1:-1;
        } catch (NoSuchFieldException e) {
            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
        } catch (IllegalAccessException e) {
            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
        }
        return 0;
    }
}
