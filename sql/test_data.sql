-- =====================================================
-- SecondScore test_data.sql
-- 作用: 初始化演示数据（扩展版）
-- 说明: 初始密码先写明文 123456，应用启动后会自动转为 BCrypt
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
(2, 'EE', '电子信息学院', 1),
(3, 'MGT', '管理学院', 1);

INSERT INTO base_major (id, college_id, major_code, major_name, status) VALUES
(1, 1, 'SE', '软件工程', 1),
(2, 1, 'CS', '计算机科学与技术', 1),
(3, 1, 'AI', '人工智能', 1),
(4, 2, 'EE', '电子信息工程', 1),
(5, 3, 'BA', '工商管理', 1);

INSERT INTO base_class (id, major_id, class_name, grade, status) VALUES
(1, 1, '软件工程1班', 2023, 1),
(2, 1, '软件工程2班', 2023, 1),
(3, 2, '计科1班', 2023, 1),
(4, 3, '人工智能1班', 2023, 1),
(5, 4, '电子信息1班', 2023, 1),
(6, 5, '工商管理1班', 2023, 1);

INSERT INTO base_term (id, term_name, start_date, end_date, is_current, status) VALUES
(1, '2025-2026-1', '2025-09-01', '2026-01-20', 0, 1),
(2, '2025-2026-2', '2026-02-24', '2026-07-10', 1, 1),
(3, '2026-2027-1', '2026-09-01', '2027-01-20', 0, 1);

INSERT INTO base_activity_category (id, category_name, default_credit, max_credit_per_term, status) VALUES
(1, '思想成长', 1.00, 6.00, 1),
(2, '志愿公益', 1.50, 8.00, 1),
(3, '创新创业', 2.00, 10.00, 1),
(4, '文体活动', 1.00, 6.00, 1),
(5, '社会实践', 1.50, 8.00, 1);

INSERT INTO sys_user (id, username, password_hash, real_name, student_no, avatar_url, phone, email, college_id, major_id, class_id, status) VALUES
(1001, 'stu01', '123456', '张三', '20230001', NULL, '13800000001', 'stu01@example.com', 1, 1, 1, 1),
(1004, 'stu02', '123456', '李四', '20230002', NULL, '13800000004', 'stu02@example.com', 1, 1, 2, 1),
(1005, 'stu03', '123456', '王五', '20230003', NULL, '13800000005', 'stu03@example.com', 1, 2, 3, 1),
(1006, 'stu04', '123456', '赵六', '20230004', NULL, '13800000006', 'stu04@example.com', 1, 3, 4, 1),
(1007, 'stu05', '123456', '孙七', '20230005', NULL, '13800000007', 'stu05@example.com', 2, 4, 5, 1),
(1008, 'stu06', '123456', '周八', '20230006', NULL, '13800000008', 'stu06@example.com', 3, 5, 6, 1),
(1011, 'stu07', '123456', '吴九', '20230007', NULL, '13800000011', 'stu07@example.com', 1, 2, 3, 0),
(1002, 'actadmin', '123456', '李老师', NULL, NULL, '13800000002', 'actadmin@example.com', 1, NULL, NULL, 1),
(1009, 'actadmin2', '123456', '陈老师', NULL, NULL, '13800000009', 'actadmin2@example.com', 2, NULL, NULL, 1),
(1003, 'admin', '123456', '系统管理员', NULL, NULL, '13800000003', 'admin@example.com', 1, NULL, NULL, 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1001, 1), (1004, 1), (1005, 1), (1006, 1), (1007, 1), (1008, 1), (1011, 1),
(1002, 2), (1009, 2),
(1003, 3);

INSERT INTO activity (id, title, category_id, term_id, organizer_id, location, start_time, end_time, signup_start, signup_end, capacity, credit, status, description) VALUES
(2001, '校园清洁志愿活动', 2, 2, 1002, '东操场', '2026-03-20 14:00:00', '2026-03-20 17:00:00', '2026-03-05 08:00:00', '2026-03-19 23:59:59', 120, 1.50, 'PUBLISHED', '参与校园卫生清洁与环保宣传'),
(2002, '创新创业讲座', 3, 2, 1002, '图书馆报告厅', '2026-04-05 19:00:00', '2026-04-05 21:00:00', '2026-03-15 08:00:00', '2026-04-04 23:59:59', 220, 2.00, 'DRAFT', '邀请校友分享创业经验'),
(2003, '春季羽毛球友谊赛', 4, 2, 1002, '体育馆', '2026-03-15 14:00:00', '2026-03-15 18:00:00', '2026-03-01 08:00:00', '2026-03-14 23:59:59', 80, 1.00, 'PUBLISHED', '学院间羽毛球交流赛'),
(2004, '编程马拉松', 3, 2, 1002, '实训楼A201', '2026-02-26 09:00:00', '2026-02-26 18:00:00', '2026-02-10 08:00:00', '2026-02-24 23:59:59', 60, 2.00, 'FINISHED', '8小时团队编程挑战'),
(2005, '社区支教服务', 2, 2, 1009, '高新区社区中心', '2026-02-28 13:00:00', '2026-02-28 17:30:00', '2026-02-12 08:00:00', '2026-02-26 23:59:59', 50, 1.50, 'FINISHED', '为社区青少年提供学业辅导'),
(2006, '人工智能学术论坛', 3, 1, 1009, '学术报告厅', '2025-11-12 19:00:00', '2025-11-12 21:00:00', '2025-10-20 08:00:00', '2025-11-10 23:59:59', 180, 2.00, 'FINISHED', '前沿AI方向分享交流'),
(2007, '心理健康主题讲座', 1, 1, 1002, '大学生活动中心', '2025-10-16 15:00:00', '2025-10-16 17:00:00', '2025-09-20 08:00:00', '2025-10-14 23:59:59', 200, 1.00, 'FINISHED', '心理调适与学习压力管理'),
(2008, '创客训练营', 5, 2, 1009, '创新工坊', '2026-04-12 09:00:00', '2026-04-13 17:00:00', '2026-03-08 08:00:00', '2026-04-10 23:59:59', 90, 1.50, 'PUBLISHED', '两天创客实践活动'),
(2009, '校史文化参访', 1, 2, 1002, '校史馆', '2026-03-30 14:00:00', '2026-03-30 16:00:00', '2026-03-10 08:00:00', '2026-03-25 23:59:59', 100, 1.00, 'CANCELLED', '校园文化沉浸式参访');

INSERT INTO activity_signup (id, activity_id, student_id, apply_time, review_status, review_by, review_time, reject_reason, signup_status) VALUES
(3001, 2001, 1001, '2026-03-06 10:00:00', 'APPROVED', 1002, '2026-03-06 11:20:00', NULL, 'ACTIVE'),
(3002, 2001, 1004, '2026-03-06 11:00:00', 'PENDING', NULL, NULL, NULL, 'ACTIVE'),
(3003, 2001, 1005, '2026-03-06 11:35:00', 'REJECTED', 1002, '2026-03-06 12:10:00', '报名信息不完整', 'ACTIVE'),
(3004, 2001, 1006, '2026-03-07 09:00:00', 'APPROVED', 1002, '2026-03-07 09:30:00', NULL, 'ACTIVE'),
(3005, 2001, 1007, '2026-03-07 09:20:00', 'APPROVED', 1002, '2026-03-07 09:40:00', NULL, 'CANCELLED'),
(3006, 2001, 1008, '2026-03-07 10:10:00', 'PENDING', NULL, NULL, NULL, 'ACTIVE'),

(3011, 2003, 1001, '2026-03-02 10:00:00', 'APPROVED', 1002, '2026-03-02 10:35:00', NULL, 'ACTIVE'),
(3012, 2003, 1004, '2026-03-02 10:25:00', 'APPROVED', 1002, '2026-03-02 10:45:00', NULL, 'ACTIVE'),
(3013, 2003, 1005, '2026-03-03 08:40:00', 'PENDING', NULL, NULL, NULL, 'ACTIVE'),
(3014, 2003, 1006, '2026-03-03 09:15:00', 'APPROVED', 1002, '2026-03-03 09:55:00', NULL, 'ACTIVE'),
(3015, 2003, 1008, '2026-03-03 11:00:00', 'REJECTED', 1002, '2026-03-03 11:20:00', '与课程时间冲突', 'ACTIVE'),

(3021, 2004, 1001, '2026-02-12 08:45:00', 'APPROVED', 1002, '2026-02-12 10:00:00', NULL, 'ACTIVE'),
(3022, 2004, 1004, '2026-02-12 09:00:00', 'APPROVED', 1002, '2026-02-12 10:05:00', NULL, 'ACTIVE'),
(3023, 2004, 1005, '2026-02-12 09:20:00', 'APPROVED', 1002, '2026-02-12 10:10:00', NULL, 'ACTIVE'),
(3024, 2004, 1006, '2026-02-12 10:00:00', 'REJECTED', 1002, '2026-02-12 10:20:00', '已超过队伍人数上限', 'ACTIVE'),
(3025, 2004, 1007, '2026-02-12 10:25:00', 'APPROVED', 1002, '2026-02-12 10:40:00', NULL, 'CANCELLED'),

(3031, 2005, 1005, '2026-02-14 13:20:00', 'APPROVED', 1009, '2026-02-14 14:00:00', NULL, 'ACTIVE'),
(3032, 2005, 1006, '2026-02-14 13:40:00', 'APPROVED', 1009, '2026-02-14 14:05:00', NULL, 'ACTIVE'),
(3033, 2005, 1008, '2026-02-14 14:05:00', 'APPROVED', 1009, '2026-02-14 14:10:00', NULL, 'ACTIVE'),

(3041, 2006, 1001, '2025-10-25 09:00:00', 'APPROVED', 1009, '2025-10-25 09:30:00', NULL, 'ACTIVE'),
(3042, 2006, 1007, '2025-10-25 09:10:00', 'APPROVED', 1009, '2025-10-25 09:35:00', NULL, 'ACTIVE'),
(3043, 2006, 1008, '2025-10-25 09:25:00', 'APPROVED', 1009, '2025-10-25 09:45:00', NULL, 'ACTIVE'),

(3051, 2007, 1004, '2025-09-28 15:20:00', 'APPROVED', 1002, '2025-09-28 16:00:00', NULL, 'ACTIVE'),
(3052, 2007, 1006, '2025-09-28 16:05:00', 'APPROVED', 1002, '2025-09-28 16:20:00', NULL, 'ACTIVE'),

(3061, 2008, 1004, '2026-03-08 09:10:00', 'PENDING', NULL, NULL, NULL, 'ACTIVE'),
(3062, 2008, 1005, '2026-03-08 09:25:00', 'APPROVED', 1009, '2026-03-08 10:05:00', NULL, 'ACTIVE'),
(3063, 2008, 1006, '2026-03-08 09:40:00', 'APPROVED', 1009, '2026-03-08 10:12:00', NULL, 'ACTIVE');

INSERT INTO activity_participation (id, activity_id, student_id, attendance_status, marked_by, marked_time, remark) VALUES
(4001, 2004, 1001, 'PRESENT', 1002, '2026-02-26 18:20:00', '完成全部挑战任务'),
(4002, 2004, 1004, 'PRESENT', 1002, '2026-02-26 18:21:00', '表现积极'),
(4003, 2004, 1005, 'ABSENT', 1002, '2026-02-26 18:22:00', '中途离场'),

(4011, 2005, 1005, 'PRESENT', 1009, '2026-02-28 18:00:00', '服务时长达标'),
(4012, 2005, 1006, 'PRESENT', 1009, '2026-02-28 18:01:00', '积极互动'),
(4013, 2005, 1008, 'ABSENT', 1009, '2026-02-28 18:02:00', '未到场'),

(4021, 2006, 1001, 'PRESENT', 1009, '2025-11-12 21:20:00', '全程参与'),
(4022, 2006, 1007, 'PRESENT', 1009, '2025-11-12 21:21:00', '全程参与'),
(4023, 2006, 1008, 'PRESENT', 1009, '2025-11-12 21:22:00', '提出有效问题'),

(4031, 2007, 1004, 'PRESENT', 1002, '2025-10-16 17:30:00', '认真听讲'),
(4032, 2007, 1006, 'PRESENT', 1002, '2025-10-16 17:32:00', '互动积极');

INSERT INTO credit_record (id, student_id, activity_id, term_id, category_id, credit, grant_status, grant_by, grant_time, revoke_by, revoke_time, revoke_reason) VALUES
(5001, 1005, 2005, 2, 2, 1.50, 'GRANTED', 1009, '2026-03-01 10:00:00', NULL, NULL, NULL),
(5002, 1006, 2005, 2, 2, 1.50, 'GRANTED', 1009, '2026-03-01 10:03:00', NULL, NULL, NULL),
(5003, 1008, 2005, 2, 2, 1.50, 'REVOKED', 1009, '2026-03-01 10:05:00', 1003, '2026-03-05 09:15:00', '考勤异常，撤销误发放记录'),
(5004, 1001, 2006, 1, 3, 2.00, 'GRANTED', 1009, '2025-11-13 10:20:00', NULL, NULL, NULL),
(5005, 1007, 2006, 1, 3, 2.00, 'GRANTED', 1009, '2025-11-13 10:22:00', NULL, NULL, NULL),
(5006, 1008, 2006, 1, 3, 2.00, 'GRANTED', 1009, '2025-11-13 10:24:00', NULL, NULL, NULL),
(5007, 1004, 2007, 1, 1, 1.00, 'GRANTED', 1002, '2025-10-17 09:10:00', NULL, NULL, NULL),
(5008, 1006, 2007, 1, 1, 1.00, 'GRANTED', 1002, '2025-10-17 09:12:00', NULL, NULL, NULL);
