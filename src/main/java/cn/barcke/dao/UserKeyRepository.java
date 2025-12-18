package cn.barcke.dao;

import cn.barcke.pojo.UserKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className UserKeyRepository
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 用户密钥数据访问层
 **/
@Repository
public interface UserKeyRepository extends JpaRepository<UserKey, String> {

    /**
     * 根据用户ID查询密钥
     */
    Optional<UserKey> findByUserId(String userId);
}

