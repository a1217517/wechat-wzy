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

    @Autowired
    ActivityRepository activityRepository;

    @Transactional
    public String quitActivity(Activity activityUser){
        activityUser.setActStatus(0);
        activityUser.setStepState(0);
        activityRepository.save(activityUser);
        return  "已经退出活动" ;
    }


}
