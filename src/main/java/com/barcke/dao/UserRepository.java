package com.barcke.dao;

import com.barcke.pojo.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UserRepository
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户数据访问层
 **/
@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {

    /**
     * 根据用户名查询用户
     */
    Optional<UserInfo> findByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
}

