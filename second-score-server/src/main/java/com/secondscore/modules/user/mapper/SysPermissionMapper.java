package com.secondscore.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondscore.modules.user.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    @Select("""
            SELECT DISTINCT p.perm_code
            FROM sys_permission p
            JOIN sys_role_permission rp ON rp.permission_id = p.id
            JOIN sys_user_role ur ON ur.role_id = rp.role_id
            JOIN sys_role r ON r.id = ur.role_id
            WHERE ur.user_id = #{userId}
              AND p.status = 1
              AND r.status = 1
            """)
    List<String> selectPermissionCodesByUserId(Long userId);

    @Select("""
            SELECT p.id
            FROM sys_permission p
            JOIN sys_role_permission rp ON rp.permission_id = p.id
            WHERE rp.role_id = #{roleId}
            """)
    List<Long> selectPermissionIdsByRoleId(Long roleId);
}
