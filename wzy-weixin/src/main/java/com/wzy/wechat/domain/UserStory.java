package com.wzy.wechat.domain;


import javax.persistence.*;
import java.util.UUID;

/**
 * @program: wzy
 * @description:
 * @author: wanzeyu
 * @create: 2019-01-28 14:57
 **/
@Entity
@Table(name = "user_story")
public class UserStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String storyId =new String(UUID.randomUUID()+"");
    private String userId;
    private String story;
    private int state=0;
    private String wechatId;
    private int wechatIdGetcount=3;
    private String nickName;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public int getWechatIdGetcount() {
        return wechatIdGetcount;
    }

    public void setWechatIdGetcount(int wechatIdGetcount) {
        this.wechatIdGetcount = wechatIdGetcount;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
