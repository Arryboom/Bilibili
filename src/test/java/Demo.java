import org.pqh.entity.Cid;
import org.pqh.test.Test;
import org.pqh.util.ReflexUtil;

/**
 * Created by reborn on 2017/4/23.
 */

public class Demo {
    public static void main(String[] args) {
        try {
            Test.main(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *测试反射工具类
     */
    @org.junit.Test
    public void testReflex(){
        Cid cid=new Cid();
        cid.setAid(123);
        System.out.println(cid.getAid());
        ReflexUtil.setObject(cid,"aid",456);
        System.out.println(cid.getAid());
    }


}
