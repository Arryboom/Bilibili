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
INSERT INTO bilibili.save(id,bilibili)VALUES(1,'1:1');
INSERT INTO bilibili.save(id,bilibili)VALUES(2,'1');
INSERT INTO bilibili.save(id,bilibili)VALUES(4,'1');