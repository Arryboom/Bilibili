package org.pqh.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by reborn on 2017/4/23.
 */
public class RunCommand {
    private static Logger log= Logger.getLogger(RunCommand.class);
    /**
     * 调用命令行运行命令
     * @param command 运行命令
     */
    public static void  runCommand(String command){
        InputStreamReader ir=null;
        BufferedReader br=null;
        InputStream in=null;
        File file=new File("run.bat");
        try {
            FileUtils.writeStringToFile(file,command,"GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            long a=System.currentTimeMillis();
            Process ps=Runtime.getRuntime().exec(file.getName());
            in=ps.getInputStream();
            ir = new InputStreamReader(in,"GBK");
            br = new BufferedReader(ir);
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.replaceAll(" ","").isEmpty()) {
                    log.info(line);
                }
            }
            long b=System.currentTimeMillis();
            log.info("运行命令花费时间"+TimeUtil.longTimeFormatString(b-a));

        } catch (IOException e) {
            log.error(e);
        }finally {
            try {
                if(in!=null)
                    in.close();
                if(ir!=null)
                    ir.close();
                if(br!=null)
                    br.close();
               ThreadUtil.excute(()->{
                   while (file.exists()){
                       try {
                           FileUtils.forceDelete(file);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       ThreadUtil.sleep(1);
                   }
               });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 压缩备份文件
     * @param _7zFile 压缩包文件
     * @param sqlFile 数据库文件
     * @param _7zpwd 压缩包密码
     */
    public  static void compress(File _7zFile,File sqlFile,String _7zpwd){
        String command="7z a -t7z "+_7zFile.getAbsolutePath()+" "+sqlFile.getAbsolutePath()+" -mx=9 -m0=LZMA2:a=2:d=26 -ms=4096m -mmt ";
        if(StringUtils.isNotEmpty(_7zpwd)){
            command+="-p"+_7zpwd;
        }
        runCommand(command);
    }
}
