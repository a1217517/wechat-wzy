package com.wzy.wechat.controller;

import com.wzy.wechat.domain.Activity;
import com.wzy.wechat.domain.UserStory;
import com.wzy.wechat.repository.ActivityRepository;
import com.wzy.wechat.repository.UserStoryRepository;
import com.wzy.wechat.service.ActivityService;
import com.wzy.wechat.utils.CheckUtil;
import com.wzy.wechat.utils.MessageUtil;
import com.wzy.wechat.utils.TextMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @program: wzy
 * @description: 对接验证
 * @author: wanzeyu
 * @create: 2019-01-06 11:01
 **/
@RestController
@RequestMapping("wechat")
public class LoginController {

    public static final String countDown =
            "您的捞瓶子次数已用完\n\n" +
            "提醒:扔瓶子可获得额外捞瓶子机会 , 最多增加3次\n"+
            "发送 [扔瓶子] 您可以写一个故事并将它扔出去\n" +
            "发送 [捞瓶子] 您可以从大海里捞出一个瓶子\n" +
            "发送 [我的瓶子] 您可以看到可以扔瓶子和捞瓶子的次数\n" +
            "您也可以随时回复 [0] 退出活动";
    public static final String activityTips =
            "发送 [扔瓶子] 您可以写一个故事并将它扔出去\n" +
            "发送 [捞瓶子] 您可以从大海里捞出一个瓶子\n" +
            "发送 [我的瓶子] 您可以看到可以扔瓶子和捞瓶子的次数\n" +
            "您也可以随时回复 [0] 退出活动";
    public static final String errorContext ="您的格式错误,请重新输入";

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserStoryRepository userStoryRepository;

    @Autowired
    TextMessageUtil textMessageUtil;

    @Autowired
    ActivityService activityService;

    @RequestMapping(value = "test",method=RequestMethod.GET)
    public Object test(){

        return "ok";


    }
    @RequestMapping(value = "wx",method=RequestMethod.GET)
    public void login(HttpServletRequest request, HttpServletResponse response){
        System.out.println("success");
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            if(CheckUtil.checkSignature(signature, timestamp, nonce)){
                out.write(echostr);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            out.close();
        }

    }


    @Transactional
    @RequestMapping(value = "wx",method=RequestMethod.POST)
    public void dopost(HttpServletRequest request,HttpServletResponse response){
        response.setCharacterEncoding("utf-8");
        PrintWriter out = null;
        //将微信请求xml转为map格式，获取所需的参数
        Map<String,String> map = MessageUtil.xmlToMap(request);
        String ToUserName = map.get("ToUserName");
        String FromUserName = map.get("FromUserName");
        String MsgType = map.get("MsgType");
        String Content = map.get("Content");

        String message = null;
        //处理文本类型,回复用户输入的内容
        if("text".equals(MsgType)){
            Activity activityUser = activityRepository.findByUserId(FromUserName);
            //如果content是活动名称.设置status为1,记录用户id
            if (activityUser==null || activityUser.getActStatus()==0){
                //step0 --  用户未点亮或者摘星,putCount还要不为0
                //发送点亮或者摘星吧
                if ("活动".equals(Content)){
                    Content=activityTips;
                    if (activityUser==null){
                        Activity activity = new Activity();
                        activity.setUserId(FromUserName);
                        activity.setActStatus(1);
                        activity.setStepState(1);
                        activityRepository.save(activity);
                    }else {
                        activityUser.setUserId(FromUserName);
                        activityUser.setActStatus(1);
                        activityUser.setStepState(1);
                        activityRepository.save(activityUser);
                    }
                }
            }else if (activityUser.getStepState()==1 ||activityUser.getStepState()==0){
                //step1 -- 用户发送了点亮
                if ("0".equals(Content)) {
                    Content =  activityService.quitActivity(activityUser);
                }else if ("扔瓶子".equals(Content)) {
                    if (activityUser.getPutCount() == 0) {
                        Content = "您的点亮次数已用完 回复0 退出活动";
                    } else {

                        //判断是否存在过昵称 ,是的话为这个逻辑 , 否则直接跳到 step3 -- 用户确认昵称后
                        if (activityUser.getNickName()==null){
                            //起个昵称
                            Content = "起个昵称吧";
                            activityUser.setActStatus(1);
                            activityUser.setStepState(2);
                            activityRepository.save(activityUser);
                        }else {
                            Content = "你的昵称是: " + activityUser.getNickName() + " , 确认 , 修改";
                            activityUser.setActStatus(1);
                            activityUser.setStepState(3);
                            activityRepository.save(activityUser);
                        }
                    }
                } else if ("捞瓶子".equals(Content)){
                    if (activityUser.getGetCount()<1){
                        Content = countDown ;
                        activityUser.setActStatus(1);
                        activityUser.setStepState(1);
                        activityRepository.save(activityUser);
                    }else {
                        Content =textMessageUtil.getStoryfromOthers(FromUserName) ;
                    }
                }else if ("我的瓶子".equals(Content)){
                    Content="您还可以扔 "+activityUser.getPutCount()+"次瓶子\n"+"您还可以捞 "+activityUser.getGetCount()+"次瓶子"
                    + "\n\n"+activityTips;
                }else {
                    Content =errorContext;
                }

            }else if (activityUser.getStepState()==2){
                //step2 --  用户起了昵称
                if ("0".equals(Content)){
                    Content =  activityService.quitActivity(activityUser);
                }else {
                    //你的昵称是xx , 确认 , 修改
                    String nickName = new String(Content);
                    activityUser.setNickName(nickName);
                    activityUser.setActStatus(1);
                    activityUser.setStepState(3);
                    activityRepository.save(activityUser);
                    Content = "你的昵称是: " + nickName + "\n    确认 ,修改";
                }

            }else if (activityUser.getStepState()==3){
                //step3 -- 用户确认昵称后
                if ("0".equals(Content)){
                    Content =  activityService.quitActivity(activityUser);
                }
                //写下你的故事
                if ("确认".equals(Content)){
                    Content="写下你的故事吧";
                    activityUser.setActStatus(1);
                    activityUser.setStepState(4);
                    activityRepository.save(activityUser);
                }else if ("修改".equals(Content)){
                    Content="请输入你想取的昵称";
                    activityUser.setActStatus(1);
                    activityUser.setStepState(2);
                    activityRepository.save(activityUser);
                }else {
                    Content =errorContext;
                }

            }else if (activityUser.getStepState()==4){
                if ("0".equals(Content)){
                    Content=activityService.quitActivity(activityUser);
                }else {
                    //step4 --用户输入故事后 确认 ,修改
                    String str = new String(Content);
                    UserStory userStory = new UserStory();
                    userStory.setStory(str);
                    userStory.setUserId(FromUserName);
                    userStoryRepository.save(userStory);

                    activityUser.setSotryId(userStory.getStoryId());
                    activityUser.setActStatus(1);
                    activityUser.setStepState(5);
                    activityRepository.save(activityUser);

                    Content="你的故事是: "+str+"\n    确认 ,修改" ;
                }

            } else if (activityUser.getStepState()==5){
                if ("0".equals(Content)){
                    Content =  activityService.quitActivity(activityUser);
                }

                //step5 -- 接收到故事后
                // 您的故事已经被点亮了
                if ("确认".equals(Content)){
                    Content="  您的故事已经装入瓶子了!\n\n现在你可以告诉我们你的微信号," +
                            "它可以被捞到瓶子的人获取,并且最多被三个人获取到,如果你不想告诉别人你的微信号可以回复 匿名";
                    activityUser.setActStatus(1);
                    activityUser.setStepState(6);
                    activityRepository.save(activityUser);
                }else if ("修改".equals(Content)){
                    String storyId = activityUser.getSotryId();
                    Content="您的故事是 : \n"+userStoryRepository.findUserByStoryId(storyId).getStory()+
                            "\n请重新输入您的故事";
                    //原来那条数据作废
                    userStoryRepository.deleteByStoryId(storyId);
                    activityUser.setActStatus(1);
                    activityUser.setStepState(4);
                    activityRepository.save(activityUser);
                }else {
                    Content =errorContext;
                }
            }else if (activityUser.getStepState()==6){

                if ("0".equals(Content)){
                    activityUser.setActStatus(0);
                    activityUser.setStepState(0);
                    activityRepository.save(activityUser);
                    Content="已经退出活动" ;
                }else {
                    //告诉我你的微信号 , 最多被获取两次
                    //step6 -- 确认 ,修改
                    //你的昵称是xx , 确认 , 修改
                    String str = new String(Content);
                    activityUser.setWechatId(str);
                    activityUser.setActStatus(1);
                    activityUser.setStepState(7);
                    activityRepository.save(activityUser);

                    //将微信号存入userStory
                    UserStory userStory = userStoryRepository.findUserByStoryId(activityUser.getSotryId());
                    userStory.setWechatId(str);
                    userStoryRepository.save(userStory);
                    Content="你的微信号是: "+str+"\n    确认 ,修改" ;
                }

            }else if (activityUser.getStepState()==7){
                if ("0".equals(Content)){
                    activityUser.setActStatus(0);
                    activityUser.setStepState(0);
                    activityRepository.save(activityUser);
                    Content="已经退出活动" ;
                }

                if ("确认".equals(Content)){
                    activityUser.setActStatus(1);
                    activityUser.setStepState(1);
                    activityUser.setPutCount(activityUser.getPutCount()-1);
                    int addCount = activityUser.getAddPutCount()<=3 ?
                                   activityUser.getPutCount()+1 :
                                   activityUser.getPutCount();
                    activityUser.setGetCount(addCount);
                    activityRepository.save(activityUser);
                    activityRepository.flush();
                    Content="您已经成功扔出了瓶子,已获得额外捞瓶子机会1次\n" +
                            "您还可以捞"+activityUser.getGetCount()+"个瓶子\n"+
                            "也可以继续扔"+activityUser.getPutCount()+"个瓶子\n"+
                            "回复 [扔瓶子] 再扔一个\n" +
                            "回复 [捞瓶子] 捞瓶子\n" +
                            "回复 [我的瓶子] 查看我的瓶子\n" +
                            "回复 [0] 退出活动";
                }else if ("修改".equals(Content)){
                    Content="您的微信号是 : "+activityUser.getWechatId()+" 请重新输入您的微信id";
                    activityUser.setActStatus(1);
                    activityUser.setStepState(6);
                    activityRepository.save(activityUser);
                }else {
                    Content =errorContext;
                }

            }else if (activityUser.getStepState()==10){
                //step10 -- 用户收到了瓶子, 回复了1 或者2
                if ("0".equals(Content)){
                    activityUser.setActStatus(0);
                    activityUser.setStepState(0);
                    activityRepository.save(activityUser);
                    Content="已经退出活动" ;
                }else if ("1".equals(Content)){
                    activityUser.setActStatus(1);
                    activityUser.setStepState(11);
                    activityRepository.save(activityUser);
                    //*******************************
                    //该瓶子的微信id可获取次数-1
                    UserStory userStory =userStoryRepository.findUserByStoryId(activityUser.getSotryId());
                    System.out.println("userStory.getWechatIdGetcount()  ===========" +userStory.getWechatIdGetcount());
                    if (userStory.getWechatIdGetcount()<1){
                        Content="对方微信获取次数已用完\n\n" +
                                "提醒:\n 可以继续捞瓶子, 获取别人微信,也可以丢瓶子,等待有缘人加你\n\n"+
                                activityTips;
                        activityUser.setActStatus(1);
                        activityUser.setStepState(1);
                        activityRepository.save(activityUser);
                    }else {
                        userStory.setWechatIdGetcount(userStory.getWechatIdGetcount()-1);
                        userStoryRepository.save(userStory);
                        Content="瓶子主人的微信号为: \n    "+activityUser.getCurWechatId() +"\n回复 2 继续捞一个瓶子 ,  回复 0 退出活动 ";
                    }
                }else if ("2".equals(Content)){
                    if (activityUser.getGetCount()<1){
                        Content =countDown;
                    }else {
                        Content =  textMessageUtil.getStoryfromOthers(FromUserName) ;
                    }
                }else {
                    Content ="您的格式错误, 请重新输入";
                }

            } else if (activityUser.getStepState()==11) {
                //step10 -- 用户收到了瓶子主人的微信号, 回复了0 或者2
                if ("0".equals(Content)){
                    Content=activityService.quitActivity(activityUser);
                }else if ("2".equals(Content)){
                    if (activityUser.getGetCount()<1){
                        Content = countDown ;
                        activityUser.setActStatus(1);
                        activityUser.setStepState(1);
                        activityRepository.save(activityUser);
                    }else {
                        Content = textMessageUtil.getStoryfromOthers(FromUserName);
                    }
                }else {
                    Content =errorContext;
                }
            } else {
                Content =errorContext;
            }
            //回复点亮 , 摘星  , 0 退出活动
            message = textMessageUtil.initMessage(FromUserName, ToUserName,Content);
        }
        try {
            out = response.getWriter();
            out.write(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.close();
    }



}
