# Bilibili<br/>
哔哩哔哩爬虫系统<br/>
开发语言：<br/>
Java1.8<br/>
项目框架：<br/>
SpringMVC+MyBatis<br/>
数据库：<br/>
Mysql5.7<br/>
开发IDE：<br/>
IDEA 15<br/>
----------------------------------------------<br/>
<h1 >实现功能：</h1><br/>
<h2 >1.获取以下三个接口信息并写入数据库</h2><br/>
接口一：http://api.bilibili.com/view<br/>
接口二：http://interface.bilibili.com/player?id=cid:<br/>
接口三：http://api.bilibili.com/vstorage/state?cid=<br/>
然后这三个接口的数据有什么卵用？</br>

<a href="http://ww2.sinaimg.cn/large/005Jr6NYgw1f5x82npzo1g310b0lse8e.gif">接口数据作用一睹为快</a></br>
<a href="pqh.share666.com"></a></br>
看完上面的录制视频就应该明白这些数据是搭配bilibili黑科技食用的，通过aid(AV号)或者cid(弹幕号)看到被和谐的视频，这些接口就是用</br>
来收集这些数据的，基本上只要是存在过B站的视频都能找到</br>
<b><a href="pqh.share666.com">完整数据库在线查询</a>帐号:bilibili 密码:2233 有msyql客户端可以用6666端口连接数据库</b>
<b><a href="#down">数据库部分数据打包下载链接</a></b>

例如：<b>缘之空、日在校园、学园默示录、记忆女神的女儿们、新妹魔王的契约者等</br>
http://pan.baidu.com/s/1geEizWB</b>不定期更新弹幕库，这个弹幕库不是从数据库里扫出来的，是我自己补番顺便生成的弹幕库</br>
想要全站弹幕的还是去下载上面的完整数据库吧</br>
当然还可以拿这些数据做各种各样的统计。
然后还有下面一些比较零碎的功能</br>

<h2 >2.获取天使论坛当季番剧音乐资源</h2><br/>
<h2 >3.根据关键字获取bt.acg.gg上的动画资源的种子并下载到本地</h2><br/>
![image](https://github.com/luffy9412/Bilibili/blob/master/WebContent/image/btacg.png)<br/>
<h2 >4.对哔哩哔哩2016年7月(已过时)版权番剧单集平均播放量定时进行统计，                          （相关活动页见：http://www.bilibili.com/html/activity-20160620newbangumi.html）<br/>
并用js echart库以图表形式展示</h2><br/>
<a href="http://ww2.sinaimg.cn/large/005Jr6NYgw1f5x1fnohh7g30zg0i07wv.gif">echart动态效果(15M动态图，小水管加载会较慢)</a><br/>
<h3 >echart折线图</h3><br/>
![image](https://github.com/luffy9412/Bilibili/blob/master/WebContent/image/echart折线图.png)<br/>
<h3 >echart柱状图</h3><br/>
![image](https://github.com/luffy9412/Bilibili/blob/master/WebContent/image/echart柱状图.png)<br/>
-----------------------------------------------------------------------------------------------------------------<br/>
项目初始化：<br/>
第一步：jar包都是用Maven进行管理的，所以项目克隆下来还要从Maven本地仓库/远程仓库中加载jar包。<br/>
第二步：用<a href="https://github.com/luffy9412/Bilibili/blob/master/doc/bilibili.sql">数据库表创建脚本</a>创建数据库以及表<br/>
![image](https://github.com/luffy9412/Bilibili/blob/master/WebContent/image/创建数据库.png)<br/>
如无意外创建完毕表结构应该跟下图一样。
数据库结构图
![image](https://github.com/luffy9412/Bilibili/blob/master/WebContent/image/数据库结构详解.png)<br/>
<a href="http://ww3.sinaimg.cn/large/005Jr6NYgw1f5x22mc4zlg30x40bye81.gif">数据库部分数据展示</a><br/>
第三步：参数配置<br/>
如何配置看<a href="https://github.com/luffy9412/Bilibili/blob/master/doc/config.properties配置详细说明.docx">配置文档</a><br/>
第四步：启动项目<br/>
直接运行main方法即可<br/>
-------------------------------------------------------------------------------------------------------------------------------------------------<br/>

数据库打包到OneDriver<br/>
<a name='down' href='https://1drv.ms/f/s!AqIrS5Y3YYnjg00rhqs5pOw6KO4n'>OneDriver</a><br/>
压缩包解压密码“A班姬路”，也是我贴吧ID,关于项目问题可以<a href="http://tieba.baidu.com/im/pcmsg?from=820363216">私信</a>。<br/>
数据库是自动定时备份，默认每天0点和12点各备份一次，具体配置项看<a href="https://github.com/luffy9412/Bilibili/blob/master/src/config.properties">配置文件</a><br/>
以上~~~~~~~~~~~~~~~~~~~~~~<br/>
