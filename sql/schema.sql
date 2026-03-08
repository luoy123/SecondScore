-- =====================================================
-- SecondScore schema.sql
-- 作用: 第二课堂学分统计与分析系统建表脚本 (MySQL 8)
-- =====================================================
CREATE DATABASE IF NOT EXISTS second_score
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE second_score;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS credit_record;
DROP TABLE IF EXISTS activity_participation;
DROP TABLE IF EXISTS activity_signup;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS base_activity_category;
DROP TABLE IF EXISTS base_term;
DROP TABLE IF EXISTS base_class;
DROP TABLE IF EXISTS base_major;
DROP TABLE IF EXISTS base_college;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  real_name VARCHAR(50) NOT NULL,
  student_no VARCHAR(32) NULL UNIQUE,
  avatar_url VARCHAR(255) NULL,
  phone VARCHAR(20) NULL,
  email VARCHAR(100) NULL,
  college_id BIGINT NULL,
  major_id BIGINT NULL,
  class_id BIGINT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1=启用,0=停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(32) NOT NULL UNIQUE COMMENT '角色编码',
  role_name VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  perm_code VARCHAR(64) NOT NULL UNIQUE COMMENT '权限编码',
  perm_name VARCHAR(100) NOT NULL,
  perm_type VARCHAR(20) NOT NULL COMMENT 'MENU/API',
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_role (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role (id),
  CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE base_college (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  college_code VARCHAR(32) NOT NULL UNIQUE,
  college_name VARCHAR(100) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE base_major (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  college_id BIGINT NOT NULL,
  major_code VARCHAR(32) NOT NULL UNIQUE,
  major_name VARCHAR(100) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_major_college FOREIGN KEY (college_id) REFERENCES base_college (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE base_class (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  major_id BIGINT NOT NULL,
  class_name VARCHAR(100) NOT NULL,
  grade INT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_class_major FOREIGN KEY (major_id) REFERENCES base_major (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE base_term (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  term_name VARCHAR(50) NOT NULL UNIQUE,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  is_current TINYINT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE base_activity_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(100) NOT NULL UNIQUE,
  default_credit DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  max_credit_per_term DECIMAL(5,2) NOT NULL DEFAULT 99.00,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE activity (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  category_id BIGINT NOT NULL,
  term_id BIGINT NOT NULL,
  organizer_id BIGINT NOT NULL COMMENT '活动负责人(活动管理员)',
  location VARCHAR(255) NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  signup_start DATETIME NOT NULL,
  signup_end DATETIME NOT NULL,
  capacity INT NOT NULL DEFAULT 100,
  credit DECIMAL(5,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/FINISHED/CANCELLED',
  description TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_activity_category FOREIGN KEY (category_id) REFERENCES base_activity_category (id),
  CONSTRAINT fk_activity_term FOREIGN KEY (term_id) REFERENCES base_term (id),
  CONSTRAINT fk_activity_organizer FOREIGN KEY (organizer_id) REFERENCES sys_user (id),
  INDEX idx_activity_term_status (term_id, status, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE activity_signup (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  apply_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  review_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  review_by BIGINT NULL,
  review_time DATETIME NULL,
  reject_reason VARCHAR(255) NULL,
  signup_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/CANCELLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_signup_activity_student (activity_id, student_id),
  CONSTRAINT fk_signup_activity FOREIGN KEY (activity_id) REFERENCES activity (id),
  CONSTRAINT fk_signup_student FOREIGN KEY (student_id) REFERENCES sys_user (id),
  CONSTRAINT fk_signup_reviewer FOREIGN KEY (review_by) REFERENCES sys_user (id),
  INDEX idx_signup_student_status (student_id, review_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE activity_participation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  attendance_status VARCHAR(20) NOT NULL COMMENT 'PRESENT/ABSENT',
  marked_by BIGINT NOT NULL,
  marked_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_participation_activity_student (activity_id, student_id),
  CONSTRAINT fk_participation_activity FOREIGN KEY (activity_id) REFERENCES activity (id),
  CONSTRAINT fk_participation_student FOREIGN KEY (student_id) REFERENCES sys_user (id),
  CONSTRAINT fk_participation_marker FOREIGN KEY (marked_by) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE credit_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  activity_id BIGINT NOT NULL,
  term_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  credit DECIMAL(5,2) NOT NULL,
  grant_status VARCHAR(20) NOT NULL DEFAULT 'GRANTED' COMMENT 'GRANTED/REVOKED',
  grant_by BIGINT NOT NULL,
  grant_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  revoke_by BIGINT NULL,
  revoke_time DATETIME NULL,
  revoke_reason VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_credit_activity_student (activity_id, student_id),
  CONSTRAINT fk_credit_student FOREIGN KEY (student_id) REFERENCES sys_user (id),
  CONSTRAINT fk_credit_activity FOREIGN KEY (activity_id) REFERENCES activity (id),
  CONSTRAINT fk_credit_term FOREIGN KEY (term_id) REFERENCES base_term (id),
  CONSTRAINT fk_credit_category FOREIGN KEY (category_id) REFERENCES base_activity_category (id),
  CONSTRAINT fk_credit_grant_by FOREIGN KEY (grant_by) REFERENCES sys_user (id),
  CONSTRAINT fk_credit_revoke_by FOREIGN KEY (revoke_by) REFERENCES sys_user (id),
  INDEX idx_credit_student_term (student_id, term_id),
  INDEX idx_credit_term_category (term_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
