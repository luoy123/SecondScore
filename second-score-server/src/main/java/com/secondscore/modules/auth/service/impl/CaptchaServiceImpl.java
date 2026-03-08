package com.secondscore.modules.auth.service.impl;

import com.secondscore.common.exception.BusinessException;
import com.secondscore.modules.auth.service.CaptchaService;
import com.secondscore.modules.auth.vo.CaptchaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private static final String CAPTCHA_KEY_PREFIX = "secondscore:captcha:";
    private static final String CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_WIDTH = 130;
    private static final int CAPTCHA_HEIGHT = 42;
    private static final int CAPTCHA_LENGTH = 4;
    private static final long EXPIRE_SECONDS = 180L;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public CaptchaResponse generateCaptcha() {
        String code = randomCode();
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        String redisKey = CAPTCHA_KEY_PREFIX + captchaId;

        try {
            stringRedisTemplate.opsForValue().set(redisKey, code, EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new BusinessException(500, "验证码服务暂不可用");
        }

        CaptchaResponse response = new CaptchaResponse();
        response.setCaptchaId(captchaId);
        response.setImageBase64("data:image/png;base64," + buildImageBase64(code));
        response.setExpireSeconds(EXPIRE_SECONDS);
        return response;
    }

    @Override
    public void validateCaptcha(String captchaId, String captchaCode) {
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captchaCode)) {
            throw new BusinessException(400, "验证码不能为空");
        }
        String redisKey = CAPTCHA_KEY_PREFIX + captchaId.trim();
        String expectedCode;
        try {
            expectedCode = stringRedisTemplate.opsForValue().get(redisKey);
            stringRedisTemplate.delete(redisKey);
        } catch (Exception ex) {
            throw new BusinessException(500, "验证码服务暂不可用");
        }

        if (!StringUtils.hasText(expectedCode)) {
            throw new BusinessException(400, "验证码已失效，请刷新后重试");
        }
        if (!expectedCode.equalsIgnoreCase(captchaCode.trim())) {
            throw new BusinessException(400, "验证码错误");
        }
    }

    private String randomCode() {
        StringBuilder builder = new StringBuilder(CAPTCHA_LENGTH);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            builder.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return builder.toString();
    }

    private String buildImageBase64(String code) {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(new GradientPaint(0, 0, new Color(242, 248, 255), CAPTCHA_WIDTH, CAPTCHA_HEIGHT, new Color(230, 242, 255)));
            g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
            drawNoise(g2d);
            drawCode(g2d, code);
        } finally {
            g2d.dispose();
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new BusinessException(500, "验证码生成失败");
        }
    }

    private void drawNoise(Graphics2D g2d) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            g2d.setColor(new Color(random.nextInt(120, 180), random.nextInt(120, 180), random.nextInt(120, 180), 130));
            int x1 = random.nextInt(CAPTCHA_WIDTH);
            int y1 = random.nextInt(CAPTCHA_HEIGHT);
            int x2 = random.nextInt(CAPTCHA_WIDTH);
            int y2 = random.nextInt(CAPTCHA_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }
        for (int i = 0; i < 25; i++) {
            g2d.setColor(new Color(random.nextInt(160, 210), random.nextInt(160, 210), random.nextInt(160, 210), 130));
            g2d.fillOval(random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HEIGHT), 2, 2);
        }
    }

    private void drawCode(Graphics2D g2d, String code) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        g2d.setFont(new Font("Verdana", Font.BOLD, 28));
        for (int i = 0; i < code.length(); i++) {
            g2d.setColor(new Color(random.nextInt(20, 90), random.nextInt(90, 140), random.nextInt(120, 190)));
            int x = 14 + i * 27;
            int y = 30 + random.nextInt(-2, 3);
            g2d.drawString(String.valueOf(code.charAt(i)), x, y);
        }
    }
}
