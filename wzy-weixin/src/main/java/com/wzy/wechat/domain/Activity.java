package com.wzy.wechat.domain;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

/**
 * @program: wzy
 * @description:
 * @author: wanzeyu
 * @create: 2019-01-26 14:24
 **/
@Entity
@Table(name = "activity" , schema = "wx_activity")
public class Activity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String userId;
    private String nickName;
    private String sotryId;
    private String wechatId;
    private String curWechatId;
    private int  actStatus=0;
    private int  putCount=3;
    private int getCount=3;
    private int  stepState=0;
    private int  addPutCount=0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSotryId() {
        return sotryId;
    }

    public void setSotryId(String sotryId) {
        this.sotryId = sotryId;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getCurWechatId() {
        return curWechatId;
    }

    public void setCurWechatId(String curWechatId) {
        this.curWechatId = curWechatId;
    }

    public int getActStatus() {
        return actStatus;
    }

    public void setActStatus(int actStatus) {
        this.actStatus = actStatus;
    }

    public int getPutCount() {
        return putCount;
    }

    public void setPutCount(int putCount) {
        this.putCount = putCount;
    }

    public int getGetCount() {
        return getCount;
    }

    public void setGetCount(int getCount) {
        this.getCount = getCount;
    }

    public int getStepState() {
        return stepState;
    }

    public void setStepState(int stepState) {
        this.stepState = stepState;
    }

    public int getAddPutCount() {
        return addPutCount;
    }

    public void setAddPutCount(int addPutCount) {
        this.addPutCount = addPutCount;
    }
}
