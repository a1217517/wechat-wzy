package com.wzy.wechat.service;

import com.wzy.wechat.domain.Activity;
import com.wzy.wechat.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @program: wzy
 * @description:
 * @author: wanzeyu
 * @create: 2019-01-27 13:28
 **/
@Service
public class ActivityService {
    public static final String activityTips =
            "发送 [扔瓶子] 您可以写一个故事并将它扔出去\n" +
                    "发送 [捞瓶子] 您可以从大海里捞出一个瓶子\n" +
                    "发送 [我的瓶子] 您可以看到可以扔瓶子和捞瓶子的次数\n" +
                    "您也可以随时回复 [0] 退出活动";
    @Autowired
    ActivityRepository activityRepository;

    @Transactional
    public String quitActivity(Activity activityUser){
        activityUser.setActStatus(0);
        activityUser.setStepState(0);
        activityRepository.save(activityUser);
        return  "已经退出活动" ;
    }

    @Transactional
    public void toFirstStep(Activity activityUser){
        activityUser.setActStatus(1);
        activityUser.setStepState(1);
        activityRepository.save(activityUser);
        return   ;
    }

    @Transactional
    public String existNickName(Activity activityUser){
        String Content ="";
        if (activityUser.getNickName() == null) {
            //起个昵称
            Content = "您现在想要扔瓶子 , 那么起个昵称吧";
            activityUser.setActStatus(1);
            activityUser.setStepState(2);
            activityRepository.save(activityUser);
        } else {
            Content = "你的昵称是: " + activityUser.getNickName() + " , 确认 , 修改";
            activityUser.setActStatus(1);
            activityUser.setStepState(3);
            activityRepository.save(activityUser);
        }
        return Content  ;
    }
}
