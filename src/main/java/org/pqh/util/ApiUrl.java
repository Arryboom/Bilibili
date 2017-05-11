package org.pqh.util;

/**
 * Created by reborn on 2017/3/3.
 * Api常量，构建带参数Api
 */
public enum ApiUrl {
    //续期accessKey接口
    accessKey("https://passport.bilibili.com/api/oauth?access_key="),
    //用AV号读取视频信息接口
    AID("http://api.bilibili.com/view"),
    //B站播放地址
    AV("http://www.bilibili.com/video/av{0}/index_{1}.html"),
    //用弹幕编号读取视频信息接口
    CID("http://interface.bilibili.com/player?id=cid:{0}"),
    //View接口
    view("https://app.bilibili.com/x/v2/view?"),
    //刷新token接口
    refreshToken("https://passport.bilibili.com/api/oauth2/refreshToken"),
    //获取个人信息接口
    userMsg("https://account.bilibili.com/api/myinfo/v2?"),
    //用弹幕编号读取视频信息接口（包括被删视频信息）
    vstorage("http://api.bilibili.com/vstorage/state?cid={0}"),
    //获取正版番剧信息接口
    bangumi("http://www.bilibili.com/api_proxy?app=bangumi&action=get_season_by_tag_v2&tag_id={0}&page=1&pagesize=50&indexType=0"),
    //根据关键字搜索专题
    bangumiSearch("http://search.bilibili.com/bangumi?keyword={0}"),
    //番剧专题接口
    bangumiAnime("http://bangumi.bilibili.com/jsonp/seasoninfo/{0}.ver","http://bangumi.bilibili.com/anime/{0}"),
    //episode_id转换av_id接口
    episodeIdToAid("http://bangumi.bilibili.com/web_api/episode/get_source?episode_id="),
    //百度百科
    baikeIndex("http://baike.baidu.com{0}"),
    //BtAcg动画种子资源搜索接口
    btAcgSearch("http://bt.acg.gg/search.php?keyword={0}&page={1}"),
    //BtAcg
    btAcgIndex("http://bt.acg.gg/{0}"),
    //简繁体转换接口
    zhConvert("http://tool.lu/zhConvert/ajax.html"),
    //百度云
    yunPan("http://pan.baidu.com/"),
    //天使论坛主页
    tsdm("http://www.tsdm.me/"),
    //天使论坛音乐索引页面
    tsdmMusicIndex(tsdm.getUrl(0)+"/forum.php?mod=viewthread&tid=104454"),
    //弹幕xml文档地址
    danMu("http://comment.bilibili.com/{0}.xml"),
    //获取弹幕池历史新增弹幕的时间戳
    danmuDmroll("http://comment.bilibili.com/dmroll,{0},{1}"),
    //获取历史弹幕
    danmuHistorys("http://comment.bilibili.com/rolldate,{0}"),
    //生成二维码接口
    qrcode("http://qr.topscan.com/api.php?text={0}&w{1}"),
    //在线抠图主页
    assqql("http://www.asqql.com/gifsgz/"),
    //生成闪光文字图片接口
    wordArt(assqql.getUrl()+"index_s.php?mid=2016-07-24%2002:02:24&tbgcolor=ffffff&tfontsize=20&isresize=0&tfont=0007.ttf&turl=http://www.asqql.com/gifsgz/demo.jpg&ttxt={0}"),
    //闪光文字图片生成地址
    wordArtPath("http://www.asqql.com/upfile/u/{0}"),
    //百度云群组发送消息地址
    bduSendMsg("http://pan.baidu.com/mbox/msg/send"),
    //ACG狗屋
    acgdoge("http://www.acgdoge.net/"),
    //软媒IT之家
    ithome("http://www.ithome.com/"),
    //爱奇艺番剧合集信息
    iqiyi("http://search.video.iqiyi.com/m?if=video_library&video_library_type=play_source&platform=1&key={0}"),
    //爱奇艺番剧关键字搜索
    iqiyiSerach("http://so.iqiyi.com/so/q_{0}"),
    //爱奇艺动漫专题
    iqiyiBangumi("http://www.iqiyi.com/dongman"),
    //爱奇艺番剧播放列表
    iqiyiPlay("http://www.iqiyi.com/a_{0}.html"),
    //优酷正版新番放送表
    youkuBangumi("http://comic.youku.com/bangumi"),
    //优酷番剧合集信息
    youku("http://play-dxk.youku.com/play/get.json?vid={0}==&ct=10"),
    //优酷番剧播放
    youkuPlay("http://v.youku.com/v_show/id_{0}.html"),
    //优酷弹幕池
    youkuDanMu("http://service.danmu.youku.com/pool"),
    //pptv弹幕池
    pptvDanMu("http://apicdn.danmu.pptv.com/danmu/v2/pplive/ref/vod_{0}/danmu?pos={1}"),
    //获取B站播放历史纪录
    biliHistory("http://api.bilibili.com/x/v2/history?access_key={0}&pn={1}&ps={2}","http://api.bilibili.com/x/v2/history/{0}"),
    //B站视频源地址
    biliVideoUrl("http://vs{0}.acg.tv{1}")
    ;
    private  final String url;

    private  final String url1;

    private int index;

    ApiUrl(String url) {
        this.url=url;
        url1 = null;
    }

    public ApiUrl s(int index) {
        this.index = index;
        return this;
    }

    ApiUrl(String url, String url1) {
        this.url = url;
        this.url1 = url1;
    }

    public String getUrl(){
        switch (this.index){
            case 2:return this.url1;
            case 1:
            default:return this.url;
        }
    }

    public String getUrl(Object ...obj){
        String buildUrl=getUrl();
        if(buildUrl==null){
            return null;
        }
        for(int j=0;j<obj.length;j++){
            buildUrl=buildUrl.replace("{"+j+"}",obj[j].toString());
        }
        return buildUrl;
    }




}
