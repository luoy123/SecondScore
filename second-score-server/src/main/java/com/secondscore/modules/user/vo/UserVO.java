package com.secondscore.modules.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String studentNo;
    private String avatarUrl;
    private String phone;
    private String email;
    private Long collegeId;
    private Long majorId;
    private Long classId;
    private Integer status;
    private List<String> roles;
    private List<String> permissions;
}
