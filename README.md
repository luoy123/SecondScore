# SecondScore - 第二课堂学分统计与分析系统

基于 `Spring Boot 3 + Vue 3` 的毕业设计项目，完成业务闭环：

`活动发布 -> 学生报名 -> 管理员审核/参与登记 -> 学分发放 -> 统计分析`

## 1. 技术栈

### 后端
- Java 17
- Spring Boot 3.3.5
- Spring Security + JWT
- Redis（登录验证码）
- MyBatis-Plus 3.5.7
- MySQL 8
- Hibernate Validator
- Knife4j (OpenAPI)
- Maven

### 前端
- Vue 3 + Vite + TypeScript
- Element Plus
- Pinia
- Vue Router
- Axios
- ECharts

## 2. 目录结构

```text
E:/SecondScore/
├─ sql/
│  ├─ schema.sql
│  └─ test_data.sql
├─ second-score-server/
│  ├─ src/main/java/com/secondscore/
│  └─ src/main/resources/application.yml
└─ second-score-web/
   ├─ src/
   ├─ package.json
   └─ vite.config.ts
```

## 3. 数据库初始化

1. 确保 MySQL 8 已启动。
2. 执行脚本：
   - `sql/schema.sql`
   - `sql/test_data.sql`
   - 增量补丁（可选）：`sql/patch_20260308_avatar.sql`
   - 若你之前已初始化过旧库，RBAC 改造后请重新执行上述两份脚本
   - 若仅想增量升级旧库，可执行 `sql/patch_20260308_avatar.sql`
3. 当前默认连接（已配置）：
   - host: `127.0.0.1`
   - database: `second_score`
   - username: `root`
   - password: `2004`

> 如需修改，编辑 `second-score-server/src/main/resources/application.yml`。

## 4. 启动步骤

### 4.1 启动后端

```powershell
cd E:\SecondScore\second-score-server
mvn spring-boot:run
```

- 默认端口：`8080`
- 接口文档：`http://localhost:8080/doc.html`
- 验证码接口：`GET http://localhost:8080/api/v1/auth/captcha`
- 头像静态访问：`http://localhost:8080/uploads/...`

### 4.2 启动前端

```powershell
cd E:\SecondScore\second-score-web
npm install
npm run dev
```

- 默认地址：`http://localhost:5173`
- 已配置代理：`/api` -> `http://127.0.0.1:8080`
- 已配置代理：`/uploads` -> `http://127.0.0.1:8080`

### 4.3 启动 Redis（验证码依赖）

项目登录启用了 Redis 验证码校验，默认配置：

- host: `127.0.0.1`
- port: `6379`
- username: `root`
- password: 空
- database: `2`

若你的 Redis 配置不同，请修改 `second-score-server/src/main/resources/application.yml` 的 `spring.data.redis`。

## 5. 默认账号

- 系统管理员：`admin / 123456`
- 活动管理员：`actadmin / 123456`
- 学生：`stu01 / 123456`

## 6. 核心模块

- 登录认证与权限控制（JWT + 角色权限）
- RBAC 权限模型（用户-角色-权限，角色可配置菜单与接口权限）
- 基础信息管理（学院、专业、班级、学期、活动类别）
- 活动管理（新建、编辑、发布、完结）
- 报名与参与管理（报名、审核、参与登记）
- 学分认定（发放、撤销、记录查询、个人汇总）
- 数据分析（总览、趋势、类别分布、活动排行）
- 角色权限管理（新增角色、配置权限、菜单动态显示）
- 登录验证码（Redis 存储，登录时强制校验）
- 用户头像上传（个人头像上传并在顶部导航展示）

## 6.1 RBAC 说明

- 新增数据表：`sys_permission`、`sys_role_permission`
- 默认仍提供三种角色（学生/活动管理员/系统管理员），但现在支持新增角色
- 左侧菜单根据 `menu:*` 权限动态展示
- 后端接口通过 `hasAuthority(...)` 做权限校验，可通过角色分配权限后生效

## 7. 联调说明

### 7.0 登录接口调用顺序（验证码）

1. 先调用 `GET /api/v1/auth/captcha`，拿到 `captchaId` 和 `imageBase64`。
2. 再调用 `POST /api/v1/auth/login`，请求体需带：`username/password/captchaId/captchaCode`。
3. 登录成功后用 JWT 访问其余接口。

### 7.1 统一返回结构

所有业务接口采用：

```json
{
  "code": 200,
  "message": "OK",
  "data": {}
}
```

- `code = 200` 代表业务成功
- `code != 200` 代表业务失败（如 400/401/403/500）

> 注意：部分业务异常会返回 HTTP 200 + 业务 code。前端 Axios 已按 `code` 处理错误提示。

### 7.2 推荐演示顺序

1. `actadmin` 登录 -> 活动管理：新建并发布活动
2. `stu01` 登录 -> 活动大厅：报名活动
3. `actadmin` 登录 -> 报名审核：通过报名
4. `actadmin` 登录 -> 参与登记：登记参与状态
5. `actadmin` 登录 -> 学分发放：一键发放
6. `stu01` 登录 -> 我的学分：查看学分变化
7. `admin` 登录 -> 角色权限管理：新建角色并分配菜单/接口权限
8. 使用新角色账号登录 -> 左侧菜单按配置变化
9. `actadmin/admin` 登录 -> 数据分析：查看图表变化

## 8. 常见问题

1. 端口 8080 被占用
   - 关闭占用进程，或修改后端端口。

2. 前端请求 401
   - 重新登录，检查 `Authorization: Bearer <token>`。

3. 数据库连接失败
   - 检查 MySQL 用户名密码、数据库名、端口和网络。

4. 首次登录失败
   - 确保已执行 `test_data.sql`，并让后端完整启动一次（会自动处理初始密码编码）。

5. 验证码无法获取/登录报验证码错误
   - 检查 Redis 是否启动，且后端 Redis 配置可连接。

6. 头像不显示
   - 检查后端 `uploads` 目录是否有文件，且前端 `vite.config.ts` 已配置 `/uploads` 代理。

## 9. 构建命令

### 后端

```powershell
cd E:\SecondScore\second-score-server
mvn -DskipTests compile
```

### 前端

```powershell
cd E:\SecondScore\second-score-web
npm run build
```

