package com.wzy.wechat.utils;

import com.thoughtworks.xstream.XStream;
import com.wzy.wechat.domain.Activity;
import com.wzy.wechat.domain.Change;
import com.wzy.wechat.domain.MessageText;
import com.wzy.wechat.domain.UserStory;
import com.wzy.wechat.repository.ActivityRepository;
import com.wzy.wechat.repository.ChangeRepository;
import com.wzy.wechat.repository.UserStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @program: wzy
 * @description:
 * @author: wanzeyu
 * @create: 2019-01-06 12:43
 **/
@Service("TextMessageUtil")
public class TextMessageUtil  implements BaseMessageUtil<MessageText>{

    public static final String allBottleCountDown =
            "瓶子已经被捞完了\n\n" +
                    "提醒:扔瓶子可获得额外捞瓶子机会 , 最多增加3次\n"+
                    "发送 [扔瓶子] 您可以写一个故事并将它扔出去\n" +
                    "发送 [捞瓶子] 您可以从大海里捞出一个瓶子\n" +
                    "发送 [我的瓶子] 您可以看到可以扔瓶子和捞瓶子的次数\n" +
                    "您也可以随时回复 [0] 退出活动";

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ChangeRepository changeRepository;
    @Autowired
    UserStoryRepository userStoryRepository;
    public String messageToxml(MessageText message) {
        XStream xstream  = new XStream();
        xstream.alias("xml", message.getClass());
        return xstream.toXML(message);
    }

    @Override
    public String initMessage(String FromUserName, String ToUserName) {
        MessageText text = new MessageText();
        text.setToUserName(FromUserName);
        text.setFromUserName(ToUserName);
        text.setContent("欢迎关注神笔骂娘");
        text.setCreateTime(new Date().getTime());
        text.setMsgType("text");
        return  messageToxml(text);
    }



    public String initMessage(String FromUserName, String ToUserName,String Content) {
        MessageText text = new MessageText();
        text.setToUserName(FromUserName);
        text.setFromUserName(ToUserName);
        text.setCreateTime(new Date().getTime());
        text.setMsgType("text");
        text.setContent(Content);
        return  messageToxml(text);
    }

    @Transactional
    @Override
    public String getStoryfromOthers(String userId) {
        Activity activityUser = activityRepository.findByUserId(userId);
        //从库里随机取一条
        UserStory userStory = userStoryRepository.getStoryRandom(userId);
        //可获取微信号次数-1
        userStoryRepository.decountGetCount(userId);
        if (userStory==null){
            activityUser.setActStatus(1);
            activityUser.setStepState(1);
            activityRepository.save(activityUser);
            return allBottleCountDown;
        }
   /*     //该用户的可获取次数-1
        activityRepository.decountGetCount(userId);*/
        //交换表新增一条记录
        Change change = new Change();
        change.setToId(userId);
        change.setFromId(userStory.getUserId());
        change.setContent(userStory.getStory());
        change.setUpdateTime(System.currentTimeMillis()+"");
        changeRepository.save(change);

        //将临时的微信号放入activity中 , 将step置为10
      //  Activity activity = activityRepository.findByUserId(userId);
        activityUser.setCurWechatId(userStory.getWechatId());
        activityUser.setSotryId(userStory.getStoryId());
        activityUser.setStepState(10);
        activityRepository.saveAndFlush(activityUser);
        //提醒:扔瓶子可获得额外捞瓶子机会 , 回复
        return "  您捞到了"+activityUser.getNickName()+"的漂流瓶!\n\n"+"漂流瓶里的故事是: \n\n "+ userStory.getStory() + "\n\n您还可以回复 1 查看该瓶子主人的微信号 , 回复 2 继续捞一个瓶子 ";
    }
}
