-- 增量补丁：给旧库补充用户头像字段
USE second_score;

ALTER TABLE sys_user
  ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255) NULL AFTER student_no;
