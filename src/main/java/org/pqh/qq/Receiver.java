package org.pqh.qq;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.scienjus.smartqq.callback.MessageCallback;
import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class Receiver {
    private static List<Friend> friendList = new ArrayList();
    private static List<Group> groupList = new ArrayList();
    private static List<Discuss> discussList = new ArrayList();
    public static Map<Long, Friend> friendFromID = new HashMap();
    public static Map<Long, Group> groupFromID = new HashMap();
    public static Map<Long, GroupInfo> groupInfoFromID = new HashMap();
    public static Map<Long, Discuss> discussFromID = new HashMap();
    public static Map<Long, DiscussInfo> discussInfoFromID = new HashMap();
    private static DoSoming doSoming=new DoSoming();
    private static boolean working;
    public static SmartQQClient client;

    public Receiver() {
    }

    private static String getTime() {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return time.format(new Date());
    }

    private static GroupInfo getGroupInfoFromID(Long id) {
        if(!groupInfoFromID.containsKey(id)) {
            groupInfoFromID.put(id, client.getGroupInfo(((Group)groupFromID.get(id)).getCode()));
        }

        return (GroupInfo)groupInfoFromID.get(id);
    }

    private static DiscussInfo getDiscussInfoFromID(Long id) {
        if(!discussInfoFromID.containsKey(id)) {
            discussInfoFromID.put(id, client.getDiscussInfo(((Discuss)discussFromID.get(id)).getId()));
        }

        return (DiscussInfo)discussInfoFromID.get(id);
    }

    public static String getGroupName(GroupMessage msg) {
        return getGroup(msg).getName();
    }

    private static String getDiscussName(DiscussMessage msg) {
        return getDiscuss(msg).getName();
    }

    public static Group getGroup(GroupMessage msg) {
        return (Group)groupFromID.get(Long.valueOf(msg.getGroupId()));
    }

    private static Discuss getDiscuss(DiscussMessage msg) {
        return (Discuss)discussFromID.get(Long.valueOf(msg.getDiscussId()));
    }

    private static String getFriendNick(Message msg) {
        Friend user = (Friend)friendFromID.get(Long.valueOf(msg.getUserId()));
        return user.getMarkname() != null && !user.getMarkname().equals("")?user.getMarkname():user.getNickname();
    }

    private static String getGroupUserNick(GroupMessage msg) {
        Iterator var1 = getGroupInfoFromID(Long.valueOf(msg.getGroupId())).getUsers().iterator();

        GroupUser user;
        do {
            if(!var1.hasNext()) {
                return "系统消息";
            }

            user = (GroupUser)var1.next();
        } while(user.getUin() != msg.getUserId());

        if(user.getCard() != null && !user.getCard().equals("")) {
            return user.getCard();
        } else {
            return user.getNick();
        }
    }

    private static String getDiscussUserNick(DiscussMessage msg) {
        Iterator var1 = getDiscussInfoFromID(Long.valueOf(msg.getDiscussId())).getUsers().iterator();

        DiscussUser user;
        do {
            if(!var1.hasNext()) {
                return "系统消息";
            }

            user = (DiscussUser)var1.next();
        } while(user.getUin() != msg.getUserId());

        return user.getNick();
    }

    public static void main(String[] args) {
        working = false;
        friendList = client.getFriendList();
        groupList = client.getGroupList();
        discussList = client.getDiscussList();
        Iterator var1 = friendList.iterator();

        while(var1.hasNext()) {
            Friend friend = (Friend)var1.next();
            friendFromID.put(Long.valueOf(friend.getUserId()), friend);
        }

        var1 = groupList.iterator();

        while(var1.hasNext()) {
            Group group = (Group)var1.next();
            groupFromID.put(Long.valueOf(group.getId()), group);
        }

        var1 = discussList.iterator();

        while(var1.hasNext()) {
            Discuss discuss = (Discuss)var1.next();
            discussFromID.put(Long.valueOf(discuss.getId()), discuss);
        }

        working = true;
    }

    static {

        client = new SmartQQClient(new MessageCallback() {
            public void onMessage(Message msg) {
                if(Receiver.working) {
                    try {
                        System.out.println("[" + Receiver.getTime() + "] [私聊] " + Receiver.getFriendNick(msg) + "：" + msg.getContent());
                        doSoming.doSoming(msg);
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }

                }
            }

            public void onGroupMessage(GroupMessage msg) {
                if(Receiver.working) {
                    try {
                        System.out.println("[" + Receiver.getTime() + "] [" + Receiver.getGroupName(msg) + "] " + Receiver.getGroupUserNick(msg) + "：" + msg.getContent());
                        doSoming.doSoming(msg);
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }

                }
            }

            public void onDiscussMessage(DiscussMessage msg) {
                if(Receiver.working) {
                    try {
                        System.out.println("[" + Receiver.getTime() + "] [" + Receiver.getDiscussName(msg) + "] " + Receiver.getDiscussUserNick(msg) + "：" + msg.getContent());
                        doSoming.doSoming(msg);
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }

                }
            }
        });
    }
}
