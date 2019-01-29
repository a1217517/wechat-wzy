package com.wzy.wechat.repository;

import com.wzy.wechat.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Long> {

    Activity findByUserId(String userId);

   /* //@Modifying
    @Query(value = "SELECT * FROM Activity  a   WHERE a.user_id NOT IN ( SELECT from_id FROM change_content c WHERE c.to_id =?1  ) AND   a.sotry IS NOT NULL  AND a.user_id !=?1 ORDER BY RAND() LIMIT 1  " ,nativeQuery = true)
    Activity getContentRandom(String userId);*/

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE activity SET put_count=put_count-1 WHERE user_id =?1  " ,nativeQuery = true)
    void decountPutCount(String userId);

}
