package com.secondscore.modules.auth.service.impl;

import com.secondscore.common.exception.BusinessException;
import com.secondscore.common.security.JwtTokenProvider;
import com.secondscore.common.security.LoginUser;
import com.secondscore.modules.auth.dto.ChangePasswordRequest;
import com.secondscore.modules.auth.dto.LoginRequest;
import com.secondscore.modules.auth.service.AuthService;
import com.secondscore.modules.auth.service.CaptchaService;
import com.secondscore.modules.auth.vo.CaptchaResponse;
import com.secondscore.modules.auth.vo.LoginResponse;
import com.secondscore.modules.user.entity.SysUser;
import com.secondscore.modules.user.service.SysUserService;
import com.secondscore.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserService sysUserService;
    private final CaptchaService captchaService;

    @Override
    public LoginResponse login(LoginRequest request) {
        captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaCode());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            SysUser user = sysUserService.getById(loginUser.getUserId());
            List<String> roles = loginUser.getRoles();
            List<String> permissions = loginUser.getPermissions();

            LoginResponse response = new LoginResponse();
            response.setToken(jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roles));
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setRealName(user.getRealName());
            response.setRoles(roles);
            response.setPermissions(permissions);
            return response;
        } catch (BadCredentialsException ex) {
            throw new BusinessException(401, "用户名或密码错误");
        }
    }

    @Override
    public CaptchaResponse captcha() {
        return captchaService.generateCaptcha();
    }

    @Override
    public UserVO me(Long userId) {
        return sysUserService.getCurrentUserProfile(userId);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        sysUserService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
    }
}
