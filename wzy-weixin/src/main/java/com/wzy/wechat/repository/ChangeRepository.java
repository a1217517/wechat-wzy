package com.wzy.wechat.repository;

import com.wzy.wechat.domain.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeRepository extends JpaRepository<Change,Long> {



}
