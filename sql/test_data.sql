-- =====================================================
-- SecondScore test_data.sql
-- 作用: 初始化测试数据
-- 说明: 初始密码先写明文 123456, 应用启动时会自动转为 BCrypt
-- =====================================================
USE second_score;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE credit_record;
TRUNCATE TABLE activity_participation;
TRUNCATE TABLE activity_signup;
TRUNCATE TABLE activity;
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_permission;
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_user;
TRUNCATE TABLE base_class;
TRUNCATE TABLE base_major;
TRUNCATE TABLE base_college;
TRUNCATE TABLE base_term;
TRUNCATE TABLE base_activity_category;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sys_role (id, role_code, role_name, status) VALUES
(1, 'STUDENT', '学生', 1),
(2, 'ACTIVITY_ADMIN', '活动管理员', 1),
(3, 'SYSTEM_ADMIN', '系统管理员', 1);

INSERT INTO sys_permission (id, perm_code, perm_name, perm_type, status) VALUES
(1, 'menu:student:hall', '菜单-活动大厅', 'MENU', 1),
(2, 'menu:student:signups', '菜单-我的报名', 'MENU', 1),
(3, 'menu:student:credits', '菜单-我的学分', 'MENU', 1),
(4, 'menu:admin:analysis', '菜单-数据分析', 'MENU', 1),
(5, 'menu:admin:activities', '菜单-活动管理', 'MENU', 1),
(6, 'menu:admin:signup-review', '菜单-报名审核', 'MENU', 1),
(7, 'menu:admin:participation', '菜单-参与登记', 'MENU', 1),
(8, 'menu:admin:credit-grant', '菜单-学分发放', 'MENU', 1),
(9, 'menu:system:users', '菜单-用户管理', 'MENU', 1),
(10, 'menu:system:base', '菜单-基础信息管理', 'MENU', 1),
(11, 'menu:system:roles', '菜单-角色权限', 'MENU', 1),
(12, 'activity:view', '活动查看', 'API', 1),
(13, 'activity:manage:own', '活动管理(本人)', 'API', 1),
(14, 'activity:manage:all', '活动管理(全部)', 'API', 1),
(15, 'signup:apply', '学生报名', 'API', 1),
(16, 'signup:review:own', '报名审核(本人)', 'API', 1),
(17, 'signup:review:all', '报名审核(全部)', 'API', 1),
(18, 'participation:mark:own', '参与登记(本人)', 'API', 1),
(19, 'participation:mark:all', '参与登记(全部)', 'API', 1),
(20, 'credit:grant:own', '学分发放(本人)', 'API', 1),
(21, 'credit:grant:all', '学分发放(全部)', 'API', 1),
(22, 'credit:revoke:own', '学分撤销(本人)', 'API', 1),
(23, 'credit:revoke:all', '学分撤销(全部)', 'API', 1),
(24, 'credit:view:self', '学分查看(本人)', 'API', 1),
(25, 'credit:view:own', '学分查看(本人活动)', 'API', 1),
(26, 'credit:view:all', '学分查看(全部)', 'API', 1),
(27, 'analysis:view', '数据分析查看', 'API', 1),
(28, 'user:manage', '用户管理', 'API', 1),
(29, 'base:manage', '基础信息管理', 'API', 1),
(30, 'role:manage', '角色权限管理', 'API', 1);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
-- STUDENT
(1, 1), (1, 2), (1, 3), (1, 12), (1, 15), (1, 24),
-- ACTIVITY_ADMIN
(2, 4), (2, 5), (2, 6), (2, 7), (2, 8),
(2, 12), (2, 13), (2, 16), (2, 18), (2, 20), (2, 22), (2, 25), (2, 27),
-- SYSTEM_ADMIN
(3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9), (3, 10), (3, 11),
(3, 12), (3, 14), (3, 17), (3, 19), (3, 21), (3, 23), (3, 26), (3, 27), (3, 28), (3, 29), (3, 30);

INSERT INTO base_college (id, college_code, college_name, status) VALUES
(1, 'CS', '计算机学院', 1),
(2, 'EE', '电子信息学院', 1);

INSERT INTO base_major (id, college_id, major_code, major_name, status) VALUES
(1, 1, 'SE', '软件工程', 1),
(2, 1, 'CS', '计算机科学与技术', 1),
(3, 2, 'EE', '电子信息工程', 1);

INSERT INTO base_class (id, major_id, class_name, grade, status) VALUES
(1, 1, '软件工程1班', 2023, 1),
(2, 2, '计科1班', 2023, 1),
(3, 3, '电子信息1班', 2023, 1);

INSERT INTO base_term (id, term_name, start_date, end_date, is_current, status) VALUES
(1, '2025-2026-1', '2025-09-01', '2026-01-20', 0, 1),
(2, '2025-2026-2', '2026-02-24', '2026-07-10', 1, 1);

INSERT INTO base_activity_category (id, category_name, default_credit, max_credit_per_term, status) VALUES
(1, '思想成长', 1.00, 6.00, 1),
(2, '志愿公益', 1.50, 8.00, 1),
(3, '创新创业', 2.00, 10.00, 1),
(4, '文体活动', 1.00, 6.00, 1);

INSERT INTO sys_user (id, username, password_hash, real_name, student_no, avatar_url, phone, email, college_id, major_id, class_id, status) VALUES
(1001, 'stu01', '123456', '张三', '20230001', NULL, '13800000001', 'stu01@example.com', 1, 1, 1, 1),
(1002, 'actadmin', '123456', '李老师', NULL, NULL, '13800000002', 'actadmin@example.com', 1, NULL, NULL, 1),
(1003, 'admin', '123456', '系统管理员', NULL, NULL, '13800000003', 'admin@example.com', 1, NULL, NULL, 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1001, 1),
(1002, 2),
(1003, 3);

INSERT INTO activity (id, title, category_id, term_id, organizer_id, location, start_time, end_time, signup_start, signup_end, capacity, credit, status, description) VALUES
(2001, '校园清洁志愿活动', 2, 2, 1002, '东操场', '2026-03-20 14:00:00', '2026-03-20 17:00:00', '2026-03-10 08:00:00', '2026-03-19 23:59:59', 100, 1.50, 'PUBLISHED', '参与校园卫生清洁与环保宣传'),
(2002, '创新创业讲座', 3, 2, 1002, '图书馆报告厅', '2026-04-05 19:00:00', '2026-04-05 21:00:00', '2026-03-15 08:00:00', '2026-04-04 23:59:59', 200, 2.00, 'DRAFT', '邀请校友分享创业经验');

INSERT INTO activity_signup (id, activity_id, student_id, apply_time, review_status, review_by, review_time, signup_status) VALUES
(3001, 2001, 1001, '2026-03-11 10:00:00', 'APPROVED', 1002, '2026-03-11 12:00:00', 'ACTIVE');

INSERT INTO activity_participation (id, activity_id, student_id, attendance_status, marked_by, marked_time, remark) VALUES
(4001, 2001, 1001, 'PRESENT', 1002, '2026-03-20 18:00:00', '按时参加');

INSERT INTO credit_record (id, student_id, activity_id, term_id, category_id, credit, grant_status, grant_by, grant_time) VALUES
(5001, 1001, 2001, 2, 2, 1.50, 'GRANTED', 1002, '2026-03-20 20:00:00');
