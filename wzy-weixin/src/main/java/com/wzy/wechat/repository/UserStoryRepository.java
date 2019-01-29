package com.wzy.wechat.repository;

import com.wzy.wechat.domain.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory,Long> {

    UserStory findUserByStoryId(String storyId);

    void deleteByStoryId(String storyId);


    @Query(value = "SELECT a.* FROM user_story  a   WHERE a.user_id NOT IN ( SELECT from_id FROM change_content c WHERE c.to_id =?1  ) AND   a.story IS NOT NULL  AND a.user_id !=?1 ORDER BY RAND() LIMIT 1  " ,nativeQuery = true)
    UserStory getStoryRandom(String userId);


    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE user_story SET wechat_id_getcount=wechat_id_getcount-1 WHERE story_id =?1  " ,nativeQuery = true)
    void decountGetCount(String storyId);

}
