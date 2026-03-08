package com.secondscore.common.security;

import com.secondscore.common.exception.BusinessException;
import com.secondscore.modules.user.entity.SysUser;
import com.secondscore.modules.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        List<String> roles = sysUserService.getRoleCodes(user.getId());
        if (roles.isEmpty()) {
            throw new BusinessException("用户未分配角色");
        }
        List<String> permissions = sysUserService.getPermissionCodes(user.getId());
        return new LoginUser(user.getId(), user.getUsername(), user.getPasswordHash(), user.getStatus(), roles, permissions);
    }
}
