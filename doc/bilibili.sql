create table accesslog
(
  thread_id int not null auto_increment
    primary key,
  time timestamp default CURRENT_TIMESTAMP not null,
  localname varchar(40) null,
  machine_name varchar(40) null
)
;

create table aid
(
  aid int not null
    primary key,
  tid int null,
  typename varchar(255) null,
  arctype varchar(255) null,
  play int null,
  review int null,
  video_review int null,
  favorites int null,
  title varchar(255) null,
  allow_bp int null,
  allow_feed int null,
  allow_download int null,
  description varchar(500) null,
  tag varchar(255) null,
  pic varchar(255) null,
  author varchar(255) null,
  mid int null,
  face varchar(255) null,
  pages int null,
  instant_server varchar(255) null,
  created int null,
  created_at varchar(255) null,
  credit int null,
  coins int null,
  spid int null,
  src varchar(255) null,
  cid int null,
  partname varchar(255) null,
  offsite varchar(255) null,
  typename2 varchar(255) null,
  partid int null,
  sp_title varchar(255) null,
  season_id int null,
  season_index varchar(255) null,
  season_episode varchar(255) null,
  bangumi_id int null
)
;

create table aidcid
(
  aid int not null,
  cid int not null,
  partid int not null,
  partname varchar(255) null,
  primary key (aid, partid)
)
;

create table avcount
(
  date date not null,
  count int null
)
;

create table avplay
(
  title varchar(255) not null,
  play int(255) null,
  timestamp datetime not null,
  ranking int null,
  primary key (title, timestamp)
)
;

create table baiduyun
(
  id int not null auto_increment
    primary key,
  url varchar(128) null,
  password varchar(4) null,
  `desc` json null,
  animeName varchar(30) null,
  constraint baiduyun_url_uindex
  unique (url)
)
;

create table bangumi
(
  bangumi_id int not null,
  season_id int not null
    primary key,
  title varchar(255) null
)
;

create table cid
(
  cid int not null
    primary key,
  maxlimit int null,
  chatid int null,
  server varchar(255) null,
  vtype varchar(255) null,
  oriurl varchar(255) null,
  aid int null,
  typeid int null,
  pid int null,
  click int null,
  favourites int null,
  credits int null,
  coins int null,
  fw_click int(255) null,
  duration varchar(255) null,
  arctype varchar(255) null,
  danmu int null,
  bottom int null,
  sinapi int null,
  acceptguest varchar(255) null,
  acceptaccel varchar(255) null
)
;

create table data
(
  cid int not null
    primary key,
  aid int null,
  dp_done_mp4 tinyint(1) null,
  letv_vu varchar(255) null,
  dp_done_flv tinyint(1) null,
  upload_meta int null,
  type varchar(255) null,
  vp int null,
  upload int null,
  author varchar(255) null,
  cover varchar(255) null,
  title varchar(255) null,
  page int null,
  dispatch int null,
  vid varchar(255) null,
  backup_vid varchar(255) null,
  files int null,
  dispatch_servers int null,
  cache varchar(255) null,
  storage_server int null,
  dp_done tinyint(1) null,
  duration float null,
  mid int null,
  dp_done_hdmp4 tinyint(1) null,
  letv_vid int null,
  storage int null,
  letv_addr varchar(255) null,
  subtitle varchar(255) null
)
;

create table files
(
  filesize mediumtext null,
  `order` int null,
  md5 varchar(255) null,
  path varchar(255) null,
  length int null,
  storage_state int null,
  format varchar(255) null,
  cid int not null,
  id int not null,
  primary key (id, cid)
)
;

create table history_data
(
  aid int not null
    primary key,
  tid int null,
  tname varchar(15) null,
  copyright int null,
  pic varchar(256) null,
  title varchar(256) null,
  pubdate int null,
  ctime int null,
  `desc` varchar(512) null,
  state int null,
  attribute int null,
  reject varchar(256) null,
  duration int null,
  tags varchar(512) null,
  rights json null,
  owner json null,
  stat json null,
  view_at int null,
  favorite tinyint(1) null,
  access int null,
  constraint history_data_aid_uindex
  unique (aid)
)
;

create table param
(
  `key` varchar(255) not null
    primary key,
  value varchar(1024) null,
  `desc` varchar(255) null
)
;

create table save
(
  id int not null,
  bilibili varchar(20) null,
  lastUpdateTime timestamp null,
  latest tinyint(1) null
)
;

create table tsdm
(
  animeName varchar(50) not null
    primary key,
  tsdmUrl varchar(512) null,
  playTime date null,
  updateTime varchar(30) null,
  copyright varchar(20) null,
  bangumi json null,
  lastUpdateTimes json null
)
;

create view 番剧 as
  SELECT
    `c`.`aid`      AS `aid`,
    `c`.`cid`      AS `cid`,
    `c`.`pid`      AS `pid`,
    `c`.`typeid`   AS `typeid`,
    `d`.`title`    AS `title`,
    `d`.`subtitle` AS `subtitle`
  FROM (`bilibili`.`cid` `c`
    JOIN `bilibili`.`data` `d` ON (((`c`.`cid` = `d`.`cid`) AND ((`c`.`typeid` = 32) OR (`c`.`typeid` = 33)))));


-- ----------------------------
-- 数据初始化
-- ----------------------------
#爬虫记录初始化
INSERT INTO bilibili.save(id,bilibili)VALUES(1,'1:1');
INSERT INTO bilibili.save(id,bilibili)VALUES(2,'1');
INSERT INTO bilibili.save(id,bilibili)VALUES(4,'1');
#配置参数初始化

INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('163_password', '', '#163邮箱密码');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('163_username', '', '#163邮箱帐号');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('access_key', '', '#');

INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('DedeUserID', '', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('DedeUserID__ckMd5', '', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('SESSDATA', '', '#');

INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('7zpwd', 'A\\u73ED\\u59EC\\u8DEF', '#数据库压缩包密码');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('acgdoge', '17463', '#acgdoge最新地址记录');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('appkey', '27eb53fc9058f8c3', '#接口密钥');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('app_secret', 'c2ed53a74eeefe3cf99fbd01d8c9c375', '#接口参数加密串');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('assqqlcookie', 'PHPSESSID=4cv2qd0re3qqthhqlnurped3d1; path=/', '#在线抠图cookie');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('backuptables', 'aid aidcid baiduyun bangumi cid history_data param save tsdm', '#自动备份的表');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('baiducookie', '', '#百度cookie');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('biliAvPlay', '0 0 0/24 * * ?', '#刷新哔哩哔哩正版视频播放量频率');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('danmu%', '40', '#弹幕池弹幕数下限');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('dbbackup', ' 0 0 0 ? * 1', '#数据库备份频率');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('dbpassword', '', '#数据库密码');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('dburl', 'jdbc:mysql://localhost:3306/bilibili?serverTimezone=UTC&&useSSL=true', '#数据库连接');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('dbusername', 'root', '#数据库账号');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('driverClassName', 'com.mysql.cj.jdbc.Driver', '#数据库驱动');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('errornum', '1000', '#允许无效aid信息个数');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('errortime', '300', '#检测爬虫异常间隔时长（秒）');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('exclude', 'dispatch_servers,upload,node_server,upload_meta', '#vstorage接口不需要的json数据');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('excludenode', 'from,vid,allow_download', '#view接口屏蔽的json节点');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('ithome', '308427', '#软媒IT之家最新地址记录');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('localPath', 'E:/mysql-5.7.17-winx64/backup/', '#数据库本地备份目录');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('MonitoringGroup', '~海军本部', '#监控群列表');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('mysqlPath', 'E:/mysql-5.7.17-winx64/bin/', '#Mysql安装目录');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('query1', 'tid=33', '#查询条件一');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('redis.host', 'localhost', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('redis.maxActive', '600', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('redis.maxIdle', '300', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('redis.maxWait', '1000', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('redis.pass', '', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('redis.port', '6379', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('redis.testOnBorrow', 'true', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('refresh_token', 'ee1dde5be286ed0f72a30b06fe54f460', '#');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('serverPath', 'C:/Users/10295/OneDrive/bilibili\\u6570\\u636E\\u5E93\\u5907\\u4EFD/', '#数据库同步盘备份目录');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('task.core_pool_size', '10', '#核心线程数');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('task.keep_alive_seconds', '60', '#线程池维护线程所允许的空闲时间，默认为60s');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('task.max_pool_size', '50', '#最大线程数');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('task.queue_capacity', '10000000', '#队列最大长度');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('timeout', '10', '#连接超时(秒)');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('TSDMCookie', '', '#天使动漫cookie');
INSERT INTO bilibili.param (`key`, value, `desc`) VALUES ('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2919.0 Safari/537.36', '#用户代理');
#番剧信息初始化
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('100%帕斯卡老师', '', '2017-04-11', '每周三06:30', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('BORUTO -火影新世代-', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801563', '2017-03-31', '连载中, 每周三 17:55更新', 'Bilibili／优酷网／土豆网／爱奇艺', '{"1": "2017-5-12 00:08"}', '{"biliId": "5978", "biliUrl": "http://www.bilibili.com/video/av9948681/index_1.html", "iqiyiId": "19rrh9f0tx", "youkuId": "XMjcwMTA4Mzg2MA", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbb8rnc.html", "youkuUrl": "http://v.youku.com/v_show/id_XMjcwMTA4Mzg2MA==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('BUPPU每一天', '', '2017-03-08', '每周四01:55', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('FRAME ARMS GIRL', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801557', '2017-03-31', '连载中, 每周二 01:30更新', 'Bilibili／爱奇艺', '{"1": "2017-5-9 23:41"}', '{"biliId": "5994", "iqiyiId": "19rrh9f66t"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('ID-0', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=804967', '2017-04-06', '连载中, 每周一 00:00更新', 'Bilibili／优酷网／土豆网', '{"0": "2017-5-3 23:36", "1": "2017-5-10 06:59"}', '{"biliId": "5999", "biliUrl": "http://www.bilibili.com/video/av10218437/index_1.html", "youkuId": "XMjY5ODAyNTc3Mg", "youkuUrl": "http://v.youku.com/v_show/id_XMjY5ODAyNTc3Mg==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('Love米 -WE LOVE RICE-', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801808', '2017-04-05', '每周三22:50更新', '优酷网／土豆网', '{"0": "2017-4-17 23:06", "1": "2017-5-11 09:06"}', '{"youkuId": "XMjcwMzIwNzk5Ng", "youkuUrl": "http://v.youku.com/v_show/id_XMjcxNzE2NzQ1Mg==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('Re:CREATORS', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801578', '2017-04-04', '连载中, 每周日 00:00更新', 'Bilibili／爱奇艺', '{"0": "2017-4-17 23:13", "1": "2017-5-8 07:08", "2": "2017-5-9 14:22", "3": "2017-5-8 10:03"}', '{"biliId": "5998", "biliUrl": "http://www.bilibili.com/video/av10186429/index_1.html", "iqiyiId": "19rrh9f1v5", "iqiyiUrl": "http://www.iqiyi.com/v_19rrba5vh8.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('Room Mate ～One Room side M～', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=757692', '2017-04-09', '连载中, 每周三 21:40更新', 'Bilibili', '{"1": "2017-5-11 08:50", "2": "2017-5-7 16:38"}', '{"biliId": "5959", "biliUrl": "http://www.bilibili.com/video/av9790798/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('sin 七大罪', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803652', '2017-04-11', '每周三00:00', '', '{"1": "2017-5-8 00:40", "2": "2017-5-9 16:02", "3": "2017-5-11 19:13"}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('SNACK WORLD', '', '2017-04-10', '每周二19:25', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('Tomica Hyper Rescue Drive Head 机动救急警察', '', '2017-04-12', '每周四07:00', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('不正经的魔术讲师与禁忌教典', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801807', '2017-03-31', '每周二21:00更新一集', '爱奇艺', '{"0": "2017-4-17 23:03", "1": "2017-5-10 12:39", "2": "2017-5-6 21:29", "3": "2017-5-11 12:35", "4": "2017-4-29 10:54", "5": "2017-5-5 09:22"}', '{"iqiyiId": "19rrh9f27t", "iqiyiUrl": "http://www.iqiyi.com/v_19rrawnjnw.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('不要输!!恶之军团!', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=806529', '2017-04-02', '连载中, 每周三 21:45更新', 'Bilibili', '{"1": "2017-5-7 08:08"}', '{"biliId": "5956", "biliUrl": "http://www.bilibili.com/video/av9791915/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('与僧侣交往的色欲之夜', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803644', '2017-03-30', '每周一', '', '{"1": "2017-5-9 11:14", "2": "2017-5-10 12:42", "3": "2017-5-9 21:23", "4": "2017-5-12 00:15"}', '{"biliId": "5960"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('世界之暗图鉴', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801575', '2017-03-30', '连载中, 每周一 03:05更新', 'Bilibili／优酷网／土豆网／爱奇艺', '{"1": "2017-5-9 07:49"}', '{"biliId": "6011", "biliUrl": "http://www.bilibili.com/video/av10215590/index_1.html", "iqiyiId": "19rrh9f8xh", "youkuId": "XMjY5MzE1Nzg5Ng", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbckk8k.html", "youkuUrl": "http://v.youku.com/v_show/id_XMjY5MzE1Nzg5Ng==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('从零开始的魔法书', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=805776', '2017-04-07', '连载中, 每周一 23:30更新', 'Bilibili', '{"0": "2017-4-11 16:13", "1": "2017-5-9 18:39", "2": "2017-5-10 09:38", "3": "2017-5-11 06:20", "4": "2017-5-11 11:20"}', '{"biliId": "6001", "biliUrl": "http://www.bilibili.com/video/av10069921/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('信长的忍者～伊势·金崎篇～', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=757823', '2017-04-05', '连载中, 每周六 01:35更新', 'Bilibili／优酷网／土豆网／爱奇艺', '{"1": "2017-5-11 06:30"}', '{"biliId": "5965", "biliUrl": "http://www.bilibili.com/video/av9834730/index_1.html", "iqiyiId": "19rrh9f8t5", "youkuId": "XMjY5OTI1MzQ2MA", "youkuUrl": "http://v.youku.com/v_show/id_XMjY5OTI1MzQ2MA==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('偶像时光PriPara', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=806524', '2017-04-01', '连载中, 每周二 17:55更新', 'Bilibili／优酷网／土豆网／爱奇艺', '{"0": "2017-4-29 21:56"}', '{"biliId": "5963", "biliUrl": "http://www.bilibili.com/video/av9928300/index_1.html", "iqiyiId": "19rrh9f5s1", "youkuId": "XMjcwMDk4MjkzMg", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbc3hq4.html", "youkuUrl": "http://v.youku.com/v_show/id_XMjcwMDk4MjkzMg==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('偶像活动Stars! 星之翼', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803896', '2017-04-02', '每周四19:30更新一集', '爱奇艺', '{"1": "2017-5-8 11:14"}', '{"iqiyiId": "19rrh9f4mh", "iqiyiUrl": "http://www.iqiyi.com/v_19rr795jmc.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('决斗大师(新系列)', '', '2017-03-30', '每周五08:30', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('剑姬神圣谭', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=805743', '2017-04-12', '每周六01:00更新一集', '爱奇艺', '{"0": "2017-5-7 20:25", "1": "2017-5-8 06:41", "2": "2017-5-8 07:06", "3": "2017-4-26 13:31"}', '{"iqiyiId": "19rrh9f4qt", "iqiyiUrl": "http://www.iqiyi.com/v_19rrb95gac.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('商人星的小比索', '', '2017-03-31', '每周六21:55', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('喧哗番长 乙女 -Girl Beats Boys-', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=726153', '2017-04-09', '每周一22:30', '', '{"0": "2017-4-17 23:08", "1": "2017-5-11 09:13"}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('埃罗芒阿老师', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=804965', '2017-04-06', '连载中, 每周日 01:00更新', 'Bilibili', '{"0": "2017-5-3 23:17", "1": "2017-5-7 17:21", "2": "2017-5-10 12:11", "3": "2017-5-8 19:48", "4": "2017-5-9 19:02", "5": "2017-5-8 08:23"}', '{"biliId": "5997", "biliUrl": "http://www.bilibili.com/video/av10024652/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('境界之轮回3', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=804959', '2017-04-05', '每周六19:00更新', '优酷网／土豆网', '{"0": "2017-5-3 23:23"}', '{"youkuId": "XMjcwODA0MTY4MA", "youkuUrl": "http://v.youku.com/v_show/id_XMjcwODA0MTY4MA==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('夏目友人帐 陆', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803654', '2017-04-08', '连载中, 每周三 03:00更新', 'Bilibili／优酷网／土豆网／爱奇艺', '{"1": "2017-5-11 06:22", "2": "2017-5-12 00:21", "3": "2017-5-7 14:28", "4": "2017-5-4 09:23"}', '{"biliId": "5977", "biliUrl": "http://www.bilibili.com/video/av9936389/index_1.html", "iqiyiId": "19rrh9f5ut", "youkuId": "XMjcwMjQ2ODYzMg", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbaz9bw.html", "youkuUrl": "http://v.youku.com/v_show/id_XMjcwMjQ2ODYzMg==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('少年阿贝 GO!GO!小芝麻 第2系列', '', '2017-04-01', '连载中, 每周二 20:00更新', 'Bilibili／优酷网／土豆网／爱奇艺', '{}', '{"biliId": "6010", "biliUrl": "http://www.bilibili.com/video/av9928704/index_1.html", "iqiyiId": "19rrh9f6b5", "youkuId": "XMjcwMTU3MTc2NA", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbb0hxs.html", "youkuUrl": "http://v.youku.com/v_show/id_XMjcwMTU3MTc2NA==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('巴哈姆特之怒 VIRGIN SOUL', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=777896', '2017-04-04', '连载中, 每周五更新', '', '{"1": "2017-5-6 14:56", "2": "2017-5-9 06:34", "3": "2017-5-7 09:51"}', '{"biliId": "5968"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('快把我哥带走', '', '2017-04-04', '每周三21:55', '腾讯视频', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('快盗天使双胞胎BREAK', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=804973', '2017-04-04', '连载中, 每周五更新', '', '{"0": "2017-5-3 23:10"}', '{"biliId": "5955"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('怪怪守护神', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801553', '2017-03-29', '连载中, 每周一 00:00更新', 'Bilibili', '{"1": "2017-5-9 07:45", "2": "2017-5-11 09:21", "3": "2017-5-6 23:54"}', '{"biliId": "5988", "biliUrl": "http://www.bilibili.com/video/av10214753/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('怪物弹珠 second season', '', '2017-03-29', '每周四19:00', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('恋爱暴君', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803648', '2017-04-03', '连载中, 每周五 02:35更新', 'Bilibili', '{"1": "2017-5-12 00:17", "2": "2017-5-5 17:27", "3": "2017-5-7 09:49"}', '{"biliId": "5996", "biliUrl": "http://www.bilibili.com/video/av10303662/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('我的英雄学院 2期', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801549', '2017-03-21', '每周六18:00更新', '优酷网／土豆网', '{"1": "2017-5-3 07:58", "2": "2017-4-5 10:02", "3": "2017-5-7 14:21"}', '{"youkuId": "XMjcwNzMzNjMzNg", "youkuUrl": "http://v.youku.com/v_show/id_XMjcwNzMzNjMzNg==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('战斗陀螺BURST 神', '', '2017-03-31', '每周一  16:55', '', '{}', '{"youkuId": "XMjcyNjQzMDQzMg"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('时钟机关之星', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801569', '2017-04-03', '连载中, 每周五 12:00更新', 'Bilibili', '{"0": "2017-4-17 23:09", "1": "2017-5-6 00:52", "2": "2017-5-10 12:13", "3": "2017-4-22 20:58", "4": "2017-5-8 19:35"}', '{"biliId": "6000", "biliUrl": "http://www.bilibili.com/video/av9825957/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('月色真美', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803425', '2017-04-03', '连载中, 每周四 23:00更新', 'Bilibili', '{"1": "2017-5-6 07:59", "2": "2017-5-7 22:55"}', '{"biliId": "5989", "biliUrl": "http://www.bilibili.com/video/av9971804/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('有顶天家族2', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803423', '2017-04-05', '每周日21:30更新', '优酷网／土豆网', '{"1": "2017-5-9 21:51", "2": "2017-5-11 07:08", "3": "2017-5-10 09:35"}', '{"youkuId": "XMjY5MzMyNDE3Mg", "youkuUrl": "http://v.youku.com/v_show/id_XMjczMzU0NjMzMg==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('未来卡搭档对战X', '', '2017-03-29', '每周四08:00', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('末日时在做什么？有没有空？可以来拯救吗？', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=805752', '2017-04-08', '每周三01:00更新一集', '爱奇艺', '{"1": "2017-5-10 21:59", "2": "2017-5-6 15:09", "3": "2017-5-12 00:19", "4": "2017-5-10 22:29", "5": "2017-5-9 21:28", "6": "2017-4-25 09:30"}', '{"iqiyiId": "19rrh9f4gx", "iqiyiUrl": "http://www.iqiyi.com/v_19rrawovdo.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('樱花任务', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803421', '2017-04-02', '连载中, 每周四 00:30更新', 'Bilibili', '{"1": "2017-5-11 10:04", "2": "2017-5-5 12:38", "3": "2017-5-10 23:04", "4": "2017-5-11 15:04"}', '{"biliId": "5992", "biliUrl": "http://www.bilibili.com/video/av10114058/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('正解的卡多', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=777498', '2017-04-03', '每周二22:30', '', '{"1": "2017-5-6 15:05", "2": "2017-5-9 12:52", "3": "2017-5-7 07:57", "4": "2017-5-10 09:36"}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('武装少女Machiavellism', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=804962', '2017-04-02', '每周四00:30更新一集', '爱奇艺', '{"0": "2017-5-3 23:00", "1": "2017-5-5 17:24", "2": "2017-4-24 19:28"}', '{"iqiyiId": "19rrh9f4jd", "iqiyiUrl": "http://www.iqiyi.com/v_19rrb17rd0.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('水嫩小叽!!', '', '2017-04-12', '每周四06:30', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('游戏王VRAINS', '', '2017-04-28', '每周六18:25', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('灰姑娘女孩剧场', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=805035', '2017-04-01', '连载中, 每周二 21:55更新', 'Bilibili', '{"1": "2017-5-11 06:13", "2": "2017-4-30 09:11"}', '{"biliId": "5966", "biliUrl": "http://www.bilibili.com/video/av9931173/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('烙印勇士 次篇', '', '2017-04-07', '每周五22:30(初回放送22:00)', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('爱丽丝与藏六', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803636', '2017-03-29', '连载中, 每周日 23:00更新', 'Bilibili／爱奇艺', '{"0": "2017-4-17 23:02", "1": "2017-5-1 13:07", "2": "2017-5-9 22:50", "3": "2017-5-2 10:44", "4": "2017-5-11 23:39"}', '{"biliId": "5995", "biliUrl": "http://www.bilibili.com/video/av10214739/index_1.html", "iqiyiId": "19rrh9f64p", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbbjsjw.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('猫猫日本史 第2系列', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=706053', '2017-04-01', '每周日18:45', '', '{"0": "2017-4-22 00:15", "1": "2016-4-13 19:11"}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('王室教师海涅', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801806', '2017-04-01', '连载中, 每周三 02:05更新', 'Bilibili／优酷网／土豆网／爱奇艺', '{"1": "2017-5-4 07:48"}', '{"biliId": "5990", "biliUrl": "http://www.bilibili.com/video/av9936402/index_1.html", "iqiyiId": "19rrh9f8up", "youkuId": "XMjcwMDkzMTM4NA", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbc3kmk.html", "youkuUrl": "http://v.youku.com/v_show/id_XMjcwMDkzMTM4NA==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('矶部矶兵卫物语 2期', '', '2017-03-06', '每周一', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('碧蓝幻想 The Animation', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=777487', '2017-04-01', '每周日00:30更新一集', 'PPTV', '{"0": "2017-4-17 23:12", "1": "2017-5-11 09:09", "2": "2017-5-8 07:33"}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('笑面推销员NEW', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801555', '2017-04-02', '连载中, 每周一 23:00更新', 'Bilibili', '{"1": "2017-5-9 23:46", "2": "2017-5-10 16:06"}', '{"biliId": "5951", "biliUrl": "http://www.bilibili.com/video/av10075314/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('胖胖裤猪 O-NEW-SAN', '', '2017-04-06', '每周五01:46', '', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('莉露莉露妖精莉露～魔法之镜～', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=806437', '2017-04-07', '每周五17:55', '', '{"1": "2017-5-7 11:36"}', '{"biliId": "5958"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('覆面系NOISE', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=803651', '2017-04-10', '连载中, 每周二 23:30更新', 'Bilibili／爱奇艺', '{"1": "2017-4-28 14:55"}', '{"biliId": "5981", "biliUrl": "http://www.bilibili.com/video/av9775067/index_1.html", "iqiyiId": "19rrh9f60d", "iqiyiUrl": "http://www.iqiyi.com/v_19rrbaz7os.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('路人女主的养成方法♭', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=805760', '2017-04-13', '每周五01:30更新一集', '爱奇艺', '{"0": "2017-4-7 10:06", "1": "2017-5-8 02:14", "2": "2017-5-9 21:15", "3": "2017-5-6 11:03", "4": "2017-4-17 20:36", "5": "2017-5-10 11:08", "6": "2017-5-8 12:27"}', '{"iqiyiId": "19rrh9f1yl", "iqiyiUrl": "http://www.iqiyi.com/v_19rrb0ssd8.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('进击的巨人 season 2', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801797', '2017-03-31', '连载中, 每周日 03:30更新', '', '{"0": "2017-4-17 23:12", "1": "2017-5-11 22:40", "2": "2017-5-9 21:50", "3": "2017-5-8 19:43", "4": "2017-5-10 13:17", "5": "2017-5-9 22:19", "6": "2017-5-3 23:00"}', '{"biliId": "5970"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('重启咲良田', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801567', '2017-04-04', '每周四00:00更新', '优酷网／土豆网', '{"0": "2017-4-17 23:09", "1": "2017-5-12 00:11", "2": "2017-5-5 13:48", "3": "2017-5-5 10:27", "4": "2017-5-9 12:13"}', '{"youkuId": "XMjcwMzk1MjgyMA", "youkuUrl": "http://v.youku.com/v_show/id_XMjczMDA4MzQ2NA==.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('银之守墓人', '', '2017-04-01', '每周六21:00', '腾讯视频', '{}', '{}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('阿童木起源', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801810', '2017-04-14', '连载中, 每周日 00:30更新', 'Bilibili', '{"0": "2017-4-17 22:58", "1": "2017-5-9 07:14"}', '{"biliId": "5979", "biliUrl": "http://www.bilibili.com/video/av9854945/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('雏子的笔记', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801540', '2017-04-06', '连载中, 每周五 21:30更新', 'Bilibili', '{"1": "2017-5-8 07:10", "2": "2017-4-22 18:07", "3": "2017-5-9 14:05", "4": "2017-5-7 09:46", "5": "2017-5-6 21:51"}', '{"biliId": "5993", "biliUrl": "http://www.bilibili.com/video/av9993135/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('青春歌舞伎', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=801809', '2017-04-06', '连载中, 每周五 02:28更新', 'Bilibili', '{"1": "2017-4-29 22:31"}', '{"biliId": "5991", "biliUrl": "http://www.bilibili.com/video/av10134721/index_1.html"}');
INSERT INTO bilibili.tsdm (animeName, tsdmUrl, playTime, updateTime, copyright, lastUpdateTimes, bangumi) VALUES ('高校星歌剧2', 'http://www.tsdm.me/forum.php?mod=viewthread&tid=757697', '2017-04-03', '连载中, 每周二 00:00更新', 'Bilibili／优酷网／土豆网', '{"0": "2017-4-19 20:32", "1": "2017-5-9 22:40", "2": "2017-4-19 20:31"}', '{"biliId": "5973", "biliUrl": "http://www.bilibili.com/video/av9913055/index_1.html", "youkuId": "XMjY5ODg2NjY2NA", "youkuUrl": "http://v.youku.com/v_show/id_XMjY5ODg2NjY2NA==.html"}');
