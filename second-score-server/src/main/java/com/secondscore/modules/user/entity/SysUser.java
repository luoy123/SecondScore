package com.secondscore.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private String studentNo;
    private String avatarUrl;
    private String phone;
    private String email;
    private Long collegeId;
    private Long majorId;
    private Long classId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
