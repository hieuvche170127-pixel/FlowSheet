/* =========================================================
   Create database

!!!!!!!MUST DO UPDATE PARTS MANUALLY!!!!!!!!!!

   USE master;
ALTER DATABASE LABTimesheet SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE LABTimesheet;

   ========================================================= */

      USE master;
ALTER DATABASE LABTimesheet SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE LABTimesheet;

CREATE DATABASE LABTimesheet;
GO

USE LABTimesheet;
GO

/* =========================================================
   1. Roles & Users (Members / Supervisors / Admin)
   ========================================================= */

CREATE TABLE Role (
    RoleID   INT IDENTITY(1,1) PRIMARY KEY,
    RoleCode NVARCHAR(30)  NOT NULL UNIQUE,   -- STUDENT, SUPERVISOR, ADMIN
    RoleName NVARCHAR(100) NOT NULL
);
GO
/* Seed roles */
INSERT INTO Role (RoleCode, RoleName)
VALUES
 (N'STUDENT',    N'Student'),
 (N'SUPERVISOR', N'Supervisor'),
 (N'ADMIN',      N'Administrator');
GO

/* Thêm các vai trò cấp độ tác nghiệp */
INSERT INTO Role (RoleCode, RoleName)
VALUES
 ('TEAM_MEMBER',   N'Team Member'),
 ('TEAM_LEADER',   N'Team Leader'),
 ('PROJECT_MEMBER', N'Project Member'),
 ('PROJECT_LEADER', N'Project Leader'),
 ('PROJECT_COLEAD', N'Project Co-Leader');
GO


CREATE TABLE UserAccount (
    UserID       INT IDENTITY(1,1) PRIMARY KEY,
    Username     NVARCHAR(50)  NOT NULL UNIQUE,
    PasswordHash NVARCHAR(255) NOT NULL,
    FullName     NVARCHAR(100) NOT NULL,
    Email        NVARCHAR(100)     NULL,
    Phone        NVARCHAR(20)      NULL,
    RoleID       INT           NOT NULL,
    IsActive     BIT           NOT NULL DEFAULT 1,  -- đây là trạng thái kích hoạt tài khoản hay là người này có phải người của lab hay ko ?
    CreatedAt    DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    UpdatedAt    DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_UserAccount_Role
        FOREIGN KEY (RoleID) REFERENCES Role(RoleID)
);
GO

/* Some sample users (you can change these later) */
INSERT INTO UserAccount (Username, PasswordHash, FullName, Email, RoleID)
VALUES
 (N'admin',    N'admin_hash',  N'System Admin', N'admin@lab.com',
     (SELECT RoleID FROM Role WHERE RoleCode = N'ADMIN')),
 (N'sup_hoa',  N'sup_hash',    N'Nguyen Thi Hoa', N'hoa@lab.com',
     (SELECT RoleID FROM Role WHERE RoleCode = N'SUPERVISOR')),
 (N'stu_anh',  N'stu_hash_1',  N'Nguyen Hoang Anh', N'anh@lab.com',
     (SELECT RoleID FROM Role WHERE RoleCode = N'STUDENT')),
 (N'stu_bao',  N'stu_hash_2',  N'Tran Bao Minh', N'bao@lab.com',
     (SELECT RoleID FROM Role WHERE RoleCode = N'STUDENT')),
 (N'nghiakhac2005@gmail.com', N'nghia2432005', N'pham khac nghia',N'nghiakhac2005@gmail.com',1),
 (N'nghiakhac2345@gmail.com', N'nghiangaongo', N'pham quang nghia',N'khac2005@gmail.com',1),
 (N'minhdo2005', N'minh1234', N'do quang minh',N'minhdo2005@gmail.com',1),
 (N'nguyenta', N'tienanh123', N'nguyen tien anh',N'tienanh123@gmail.com',1),
 (N'tranminhvu', N'vu1234', N'tran minh vu',N'anhvu123@gmail.com',1),
 (N'viet2345@gmail.com', N'vietphamnenhehe', N'pham quang viet',N'vietpq2005@gmail.com',1);
GO


/* =========================================================
   2. Projects & Tasks (used by Weekly Timesheet filter)
   ========================================================= */
CREATE TABLE Project (
    ProjectID   INT IDENTITY(1,1) PRIMARY KEY,
    ProjectCode NVARCHAR(50)  NOT NULL UNIQUE,
    ProjectName NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX)     NULL,
    IsActive    BIT           NOT NULL DEFAULT 1,
    CreatedAt   DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);
GO
-- Add new columns for project lifecycle
ALTER TABLE Project
ADD StartDate DATE NULL,
    Deadline  DATE NULL,
    Status    NVARCHAR(20) NOT NULL DEFAULT N'OPEN';
GO
-- Optional: enforce allowed statuses
ALTER TABLE Project
ADD CONSTRAINT CHK_Project_Status
CHECK (Status IN (N'OPEN', N'IN_PROGRESS', N'COMPLETE'));
GO


INSERT INTO Project (
    ProjectCode, ProjectName, Description, 
    IsActive, CreatedAt, StartDate, Deadline, Status
)
VALUES
-- 1. Dự án Phát triển Chính (Đang tiến hành)
('P_SWP391_A', N'Timesheet Management System', N'Phát triển hệ thống quản lý bảng chấm công cho công ty.', 
1, '2025-01-05 10:30:00', '2025-01-15', '2025-05-30', 'IN_PROGRESS'),

-- 2. Dự án Nghiên cứu (Hoàn thành)
('P_RESEARCH_01', N'Nghiên cứu thị trường AI', N'Phân tích xu hướng và công nghệ AI mới trong năm 2025.', 
0, '2024-09-15 14:00:00', '2024-10-01', '2024-12-31', 'IN_PROGRESS'),

-- 3. Dự án Bảo trì (Đang tiến hành)
('P_MAINT_WEB', N'Bảo trì và nâng cấp website công ty', N'Cập nhật framework và sửa lỗi bảo mật trên website chính.', 
1, '2025-02-18 09:45:00', '2025-03-01', '2025-04-30', 'IN_PROGRESS'),

-- 4. Dự án Nội bộ (Mới mở) - Tạo gần đây
('P_HR_SETUP', N'Triển khai hệ thống E-learning nội bộ', N'Xây dựng nền tảng đào tạo trực tuyến cho nhân viên mới.', 
1, '2025-03-25 15:20:00', '2025-04-10', '2025-07-10', 'IN_PROGRESS'),

-- 5. Dự án Thiết kế (Đang tiến hành)
('P_UX_REDESIGN', N'Thiết kế lại giao diện người dùng sản phẩm', N'Tối ưu hóa UX/UI cho ứng dụng di động.', 
1, '2025-02-01 11:00:00', '2025-02-20', '2025-05-20', 'IN_PROGRESS');
GO

-- change from projectid not null to null 
-- vì mình có cả task của lab nữa, và lab thì ngoài project, nên nếu project id là null, nó là lab task. 
-- nếu giờ thay đổi tên bảng/ database các khóa thì mất thời gian, nên nó là tối ưu về thời gian và công sức nhất. 
-- date modify: 12/12/25/ 10h57pm made by nghia
-- có thể task đấy chưa có chẳng hạn.

-- new table
-- dùng khóa chính tự tăng thay vì cặp khóa chính vì nếu là cặp khóa chính,
-- một khi rời khỏi project sẽ ko thể quay lại 
-- và trong thực tế thì có thể đâu đó xảy ra trường hợp đó. 
-- Projectmember/ project assignee 
CREATE TABLE ProjectMember (
    ProjectID INT NOT NULL,
    UserID    INT NOT NULL,
    
    -- Thay đổi: Lưu RoleID thay vì chuỗi
    RoleID    INT NOT NULL, 
    
    JoinedAt  DATETIME DEFAULT GETDATE(),

    -- 1. Khóa chính tổ hợp (Mỗi User chỉ có 1 vai trò trong 1 Project tại 1 thời điểm)
    CONSTRAINT PK_ProjectMember PRIMARY KEY (ProjectID, UserID),

    -- 2. Các Khóa ngoại cơ bản
    CONSTRAINT FK_ProjectMember_Project FOREIGN KEY (ProjectID) 
        REFERENCES Project(ProjectID) ON DELETE CASCADE,

    CONSTRAINT FK_ProjectMember_User FOREIGN KEY (UserID) 
        REFERENCES UserAccount(UserID) ON DELETE CASCADE,

    -- 3. Khóa ngoại trỏ đến bảng ROLE
    CONSTRAINT FK_ProjectMember_Role FOREIGN KEY (RoleID) 
        REFERENCES Role(RoleID),

    -- 4. QUAN TRỌNG: Check giá trị RoleID chỉ được nằm trong khoảng 6-8
    -- (PROJECT_MEMBER, PROJECT_LEADER, PROJECT_COLEAD)
    CONSTRAINT CK_ProjectMember_RoleValid CHECK (RoleID >= 6 AND RoleID <= 8)
);

INSERT INTO ProjectMember (ProjectID, UserID, RoleID, JoinedAt) VALUES
-- Project 1
(1, 3, 7, '2023-10-01 09:00:00'), -- Leader
(1, 4, 8, '2023-10-01 10:30:00'), -- Co-lead
(1, 5, 6, '2023-10-02 14:00:00'), -- Member
(1, 6, 6, '2023-10-02 15:00:00'), -- Member

-- Project 2
(2, 7, 7, '2023-10-05 08:00:00'), -- Leader
(2, 8, 8, '2023-10-05 08:30:00'), -- Co-lead
(2, 9, 6, '2023-10-06 09:00:00'), -- Member
(2, 3, 6, '2023-10-06 10:00:00'), -- Member

-- Project 3
(3, 4, 7, '2023-11-01 11:00:00'), -- Leader
(3, 5, 8, '2023-11-01 11:30:00'), -- Co-lead
(3, 6, 6, '2023-11-02 13:00:00'), -- Member
(3, 7, 6, '2023-11-02 14:00:00'), -- Member

-- Project 4
(4, 8, 7, '2023-11-10 09:15:00'), -- Leader
(4, 9, 8, '2023-11-10 10:00:00'), -- Co-lead
(4, 3, 6, '2023-11-11 15:45:00'), -- Member
(4, 4, 6, '2023-11-12 16:20:00'), -- Member

-- Project 5
(5, 5, 7, '2023-12-01 08:00:00'), -- Leader
(5, 6, 8, '2023-12-01 09:00:00'), -- Co-lead
(5, 7, 6, '2023-12-02 10:00:00'), -- Member
(5, 8, 6, '2023-12-02 11:00:00'); -- Member




-- 1 vài lưu ý - cho anh tiến anh
-- check xem deadline có sau ngày hôm nay không (mỗi khi add/update)
-- estimate time > 0 (hiển nhiên r)
CREATE TABLE ProjectTask (
    TaskID           INT IDENTITY(1,1) PRIMARY KEY,
    ProjectID        INT,
    TaskName         NVARCHAR(200) NOT NULL,
    Description      NVARCHAR(MAX) NULL,
    
    -- Deadline: Hạn chót
    Deadline         DATETIME2 NULL,
    
    -- EstimateHourToDo: Giờ dự kiến (Decimal 5,2 nghĩa là tối đa 999.99 giờ)
    EstimateHourToDo DECIMAL(5, 2) NULL,
    
    -- CreatedAt: Tự động lấy giờ hiện tại
    CreatedAt        DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    
    -- Status: Mặc định là TO_DO
    Status           NVARCHAR(20) NOT NULL DEFAULT N'TO_DO',

    -- Khóa ngoại liên kết với Project
    CONSTRAINT FK_ProjectTask_Project 
        FOREIGN KEY (ProjectID) REFERENCES Project(ProjectID)
        ON DELETE CASCADE -- Xóa Project là Task bay màu theo luôn
);
GO

-- 3. Ràng buộc (Constraint) cho cột Status
-- Chỉ chấp nhận 3 giá trị này (thêm DONE/COMPLETED vào đây nếu sau này cần nhé anh)
ALTER TABLE ProjectTask
ADD CONSTRAINT CHK_ProjectTask_Status
CHECK (Status IN (N'TO_DO', N'IN_PROGRESS', N'SUSPENDED'));
GO

INSERT INTO ProjectTask (ProjectID, TaskName, Description, Deadline, EstimateHourToDo, Status)
VALUES
-- Task 1: Thiết kế Database (Khớp với Thứ 2 của bạn)
(1, N'Database Design - Timesheet Module', 
 N'Thiết kế bảng Timesheet, Entry và các ràng buộc liên quan.', 
 '2025-12-25 23:59:59', 8.00, N'IN_PROGRESS'),

-- Task 2: Phát triển DAO & Entity (Khớp với Thứ 4 của bạn)
(1, N'Backend Development - CRUD Timesheet', 
 N'Viết các hàm Mapping, GetByID, Insert cho Timesheet và Entry.', 
 '2025-12-30 17:00:00', 16.00, N'IN_PROGRESS'),

-- Task 3: Xây dựng Giao diện (Khớp với Thứ 3 của bạn)
(1, N'Frontend - Timesheet UI', 
 N'Sử dụng Bootstrap 5 để xây dựng trang danh sách và Modal thêm mới.', 
 '2025-12-28 12:00:00', 12.50, N'IN_PROGRESS'),

-- Task 4: Chức năng Approval (Dự kiến tiếp theo)
(1, N'Workflow - Timesheet Review', 
 N'Xử lý logic Submit, Approved và Reviewed.', 
 '2026-01-05 17:00:00', 10.00, N'TO_DO'),

-- Task 5: Báo cáo & Xuất file
(1, N'Reporting - Export Excel', 
 N'Kết xuất dữ liệu chấm công ra file Excel cho phòng nhân sự.', 
 '2026-01-15 17:00:00', 20.00, N'TO_DO');
GO

INSERT INTO ProjectTask (ProjectID, TaskName, Description, Deadline, EstimateHourToDo, Status)
VALUES
-- Task 1: Thu thập dữ liệu sơ bộ
(2, N'Thu thập báo cáo thị trường AI 2024', 
 N'Tìm kiếm và tổng hợp các báo cáo từ Gartner, IDC về xu hướng AI.', 
 '2024-10-15 17:00:00', 10.00, N'IN_PROGRESS'),

-- Task 2: Phân tích công nghệ mới
(2, N'Phân tích các Large Language Models (LLM) mới', 
 N'Đánh giá hiệu năng của các model mới ra mắt trong quý 4/2024.', 
 '2024-11-10 17:00:00', 20.00, N'IN_PROGRESS'),

-- Task 3: Nghiên cứu đối thủ
(2, N'Nghiên cứu ứng dụng AI của đối thủ cạnh tranh', 
 N'Phân tích các tính năng AI mà đối thủ đã triển khai trên sản phẩm của họ.', 
 '2024-11-30 17:00:00', 15.50, N'IN_PROGRESS'),

-- Task 4: Khảo sát người dùng
(2, N'Khảo sát nhu cầu tích hợp AI vào Timesheet', 
 N'Lấy ý kiến từ bộ phận nhân sự về việc sử dụng AI để gợi ý task.', 
 '2024-12-15 17:00:00', 12.00, N'IN_PROGRESS'),

-- Task 5: Tổng kết và báo cáo
(2, N'Lập báo cáo tổng kết xu hướng AI 2025', 
 N'Hoàn thiện tài liệu dự báo và đề xuất lộ trình phát triển cho năm tới.', 
 '2024-12-30 23:59:59', 8.00, N'IN_PROGRESS');
GO

INSERT INTO ProjectTask (ProjectID, TaskName, Description, Deadline, EstimateHourToDo, Status)
VALUES
-- =========================================================================
-- PROJECT 3: Bảo trì và nâng cấp website công ty (01/03/2025 - 30/04/2025)
-- =========================================================================
(3, N'Cập nhật Framework và Library', 
 N'Nâng cấp React và các thư viện liên quan lên phiên bản mới nhất để đảm bảo hiệu năng.', 
 '2025-03-15 17:00:00', 16.00, N'IN_PROGRESS'),

(3, N'Kiểm tra và vá lỗi bảo mật SQL Injection', 
 N'Rà soát toàn bộ các câu truy vấn và thực hiện biện pháp ngăn chặn tấn công bảo mật.', 
 '2025-03-25 17:00:00', 12.50, N'IN_PROGRESS'),

(3, N'Tối ưu hóa tốc độ tải trang chủ', 
 N'Nén ảnh, minify CSS/JS và cấu hình cache để tăng điểm Lighthouse.', 
 '2025-04-10 17:00:00', 20.00, N'IN_PROGRESS'),

(3, N'Sửa lỗi hiển thị trên trình duyệt Safari', 
 N'Khắc phục các lỗi layout bị vỡ khi người dùng sử dụng iPhone/Macbook.', 
 '2025-04-20 17:00:00', 8.00, N'IN_PROGRESS'),

(3, N'Triển khai bản vá lên Production', 
 N'Tiến hành deploy và kiểm tra tính ổn định sau khi nâng cấp.', 
 '2025-04-29 23:00:00', 4.00, N'IN_PROGRESS'),

-- =========================================================================
-- PROJECT 4: Triển khai hệ thống E-learning nội bộ (10/04/2025 - 10/07/2025)
-- =========================================================================
(4, N'Khảo sát nhu cầu đào tạo nhân viên', 
 N'Gửi form khảo sát cho các phòng ban để xác định các khóa học cần thiết.', 
 '2025-04-25 17:00:00', 10.00, N'IN_PROGRESS'),

(4, N'Cấu hình server hosting cho LMS', 
 N'Thiết lập môi trường server và cài đặt mã nguồn hệ thống E-learning.', 
 '2025-05-15 17:00:00', 24.00, N'IN_PROGRESS'),

(4, N'Số hóa tài liệu đào tạo (Video/PDF)', 
 N'Chuyển đổi các bài giảng cũ sang định dạng số phù hợp với nền tảng web.', 
 '2025-06-10 17:00:00', 40.00, N'IN_PROGRESS'),

(4, N'Kiểm thử luồng đăng ký khóa học', 
 N'Đảm bảo nhân viên có thể đăng ký và làm bài kiểm tra cuối khóa mượt mà.', 
 '2025-06-30 17:00:00', 15.00, N'IN_PROGRESS'),

(4, N'Hướng dẫn sử dụng cho nhân viên', 
 N'Tổ chức buổi workshop online giới thiệu cách sử dụng hệ thống mới.', 
 '2025-07-08 17:00:00', 6.00, N'IN_PROGRESS'),

-- =========================================================================
-- PROJECT 5: Thiết kế lại giao diện người dùng (20/02/2025 - 20/05/2025)
-- =========================================================================
(5, N'Phỏng vấn trải nghiệm người dùng cũ', 
 N'Lấy ý kiến từ 10 khách hàng thân thiết về những điểm bất tiện của giao diện cũ.', 
 '2025-03-05 17:00:00', 14.00, N'IN_PROGRESS'),

(5, N'Xây dựng Wireframe cho Mobile App', 
 N'Phác thảo cấu trúc các màn hình chính trên Figma.', 
 '2025-03-25 17:00:00', 30.00, N'IN_PROGRESS'),

(5, N'Thiết kế bộ UI Kit mới', 
 N'Quy định màu sắc, font chữ, icon và các component dùng chung.', 
 '2025-04-15 17:00:00', 25.00, N'IN_PROGRESS'),

(5, N'Tạo Prototype tương tác (High-fidelity)', 
 N'Làm bản demo có thể click được để trình chiếu cho ban giám đốc.', 
 '2025-05-10 17:00:00', 20.00, N'IN_PROGRESS'),

(5, N'Bàn giao tài liệu thiết kế cho Dev', 
 N'Xuất file và viết mô tả các hiệu ứng chuyển cảnh cho bộ phận lập trình.', 
 '2025-05-18 17:00:00', 8.00, N'IN_PROGRESS');
GO


/* =========================================================
   Update 01 (MUST DO IT MANUALLY)
   ========================================================= */
USE LABTimesheet;
GO
CREATE TABLE TaskAssignee (
    TaskAssigneeID INT IDENTITY(1,1) PRIMARY KEY,
    TaskID         INT NOT NULL,
    UserID         INT NOT NULL,
    AssignedAt     DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_TaskAssignee_Task
        FOREIGN KEY (TaskID) REFERENCES ProjectTask(TaskID),

    CONSTRAINT FK_TaskAssignee_User
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID),

    CONSTRAINT UQ_TaskAssignee_Task_User
        UNIQUE (TaskID, UserID)  -- tránh gán trùng một member nhiều lần cho 1 task
);
GO

INSERT INTO TaskAssignee (TaskID, UserID, AssignedAt)
VALUES 
    (3, 5, SYSDATETIME()), -- Gán User 5 vào Task 3
    (5, 5, SYSDATETIME()), -- Gán User 5 vào Task 5
    (7, 5, SYSDATETIME()), -- Gán User 5 vào Task 7
    (9, 5, SYSDATETIME()); -- Gán User 5 vào Task 9
GO

INSERT INTO TaskAssignee (TaskID, UserID, AssignedAt)
VALUES
-- PROJECT 1 (Members: 3, 4, 5, 6)
(1, 5, GETDATE()),
(2, 3, GETDATE()),
(3, 6, GETDATE()),
(4, 4, GETDATE()),

-- PROJECT 2 (Members: 7, 8, 9, 3)
(6, 9, GETDATE()),
(7, 7, GETDATE()),
(8, 8, GETDATE()),
(9, 3, GETDATE()),
(10, 7, GETDATE()),

-- PROJECT 3 (Members: 4, 5, 6, 7)
(11, 6, GETDATE()),
(12, 4, GETDATE()),
(13, 5, GETDATE()),
(14, 7, GETDATE()),
(15, 4, GETDATE()),

-- PROJECT 4 (Members: 8, 9, 3, 4)
(16, 3, GETDATE()),
(17, 8, GETDATE()),
(18, 4, GETDATE()),
(19, 9, GETDATE()),
(20, 8, GETDATE()),

-- PROJECT 5 (Members: 5, 6, 7, 8)
(21, 7, GETDATE()),
(22, 5, GETDATE()),
(23, 6, GETDATE()),
(24, 5, GETDATE()),
(25, 8, GETDATE());
GO



-- bigupdate ver4.
-- Business rule: 
-- sau khi được review, thì timesheet sẽ ko còn có thể chỉnh sửa?
-- vì nếu thầy/supervisor đã xem và đánh giá cái timesheetentry đó, thì người tạo ko được sửa nữa.

--design của bảng: 
CREATE TABLE Timesheet (
    TimesheetID     INT IDENTITY(1,1) PRIMARY KEY,
    UserID          INT           NOT NULL,            -- Người dùng sở hữu Timesheet này
    DayStart        DATE          NOT NULL,            -- Ngày bắt đầu của Timesheet (thường là ngày làm việc)
    DayEnd          DATE              NULL,            -- Ngày kết thúc (có thể NULL nếu Timesheet chỉ là cho 1 ngày)
    LastUpdatedAt   DATETIME2     NOT NULL DEFAULT SYSDATETIME(), -- Cập nhật lần cuối (thay thế cho LastChange)
    -- Trạng thái: 0=Draft, 1=Submitted, 2=Reviewed/Locked
    Status          NVARCHAR(20)  NOT NULL DEFAULT N'Draft',
    -- Ràng buộc: Mỗi người dùng chỉ có một Timesheet cho mỗi ngày (nếu DayEnd NULL)
    CONSTRAINT UQ_Timesheet_User_DayStart UNIQUE (UserID, DayStart),
    CONSTRAINT FK_Timesheet_User
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID),
    CONSTRAINT CHK_Timesheet_Status
        CHECK (Status IN (N'Draft', N'Submitted', N'Reviewed'))
);
GO

-- 1. Thêm cột Summary vào bảng hiện tại
ALTER TABLE Timesheet
ADD Summary NVARCHAR(2000) NULL; 
GO


-- TUẦN HIỆN TẠI (08/12 - 14/12)
INSERT INTO Timesheet (UserID, DayStart, DayEnd, Status, Summary)
VALUES 
(5, '2025-12-08', '2025-12-14', N'Submitted', N'Làm chức năng Login và phân quyền User.'),
(6, '2025-12-08', '2025-12-14', N'Submitted', N'Thiết kế giao diện Dashboard và Sidebar.'),
(7, '2025-12-08', '2025-12-14', N'Submitted', N'Viết API lấy dữ liệu báo cáo tuần.');

-- TUẦN TRƯỚC (01/12 - 07/12)
INSERT INTO Timesheet (UserID, DayStart, DayEnd, Status, Summary)
VALUES 
(5, '2025-12-01', '2025-12-07', N'Submitted', N'Phân tích yêu cầu khách hàng và thiết kế ERD.'),
(6, '2025-12-01', '2025-12-07', N'Submitted', N'Setup môi trường dự án, cài đặt thư viện cần thiết.'),
(7, '2025-12-01', '2025-12-07', N'Submitted', N'Họp team chốt công nghệ sử dụng.');

-- TUẦN SAU (15/12 - 21/12)
INSERT INTO Timesheet (UserID, DayStart, DayEnd, Status, Summary)
VALUES 
(5, '2025-12-15', '2025-12-21', N'Draft', N'Dự kiến hoàn thành module Timesheet Management.'),
(6, '2025-12-15', '2025-12-21', N'Draft', N'Tối ưu hóa tốc độ load trang và kiểm thử giao diện.'),
(7, '2025-12-15', '2025-12-21', N'Draft', N'Xây dựng chức năng xuất báo cáo ra file Excel.');
GO

INSERT INTO Timesheet (UserID, DayStart, DayEnd, Status, Summary)
VALUES 
(3, '2025-12-08', '2025-12-14', N'Submitted', N'Điều phối tiến độ Project 1 và hỗ trợ review logic Database.'),
(4, '2025-12-08', '2025-12-14', N'Submitted', N'Kiểm tra hiệu năng Backend và bắt đầu lập kế hoạch bảo trì Project 3.'),
(8, '2025-12-08', '2025-12-14', N'Draft', N'Cấu hình hạ tầng Server cho dự án E-learning và họp kick-off Project 4.'),
(9, '2025-12-08', '2025-12-14', N'Draft', N'Nghiên cứu giải pháp AI cho Project 2 và viết tài liệu kỹ thuật.');
GO


CREATE TABLE TimesheetEntry (
    EntryID         INT IDENTITY(1,1) PRIMARY KEY,
    TimesheetID     INT           NOT NULL,          -- Khóa ngoại liên kết với Timesheet (Header)
    -- Project/Task có thể NULL (cho công việc không gắn với Project/Task)
    -- Cột thời gian chi tiết
    WorkDate        DATE          NOT NULL,          -- Ngày làm việc cụ thể
    StartTime       TIME          NOT NULL,          -- Bắt đầu (Tôi đề nghị NOT NULL để tính toán dễ hơn)
    EndTime         TIME          NOT NULL,          -- Kết thúc (Tôi đề nghị NOT NULL để tính toán dễ hơn)
    -- Số phút làm việc (Tính được: EndTime - StartTime - Delay)
    -- Giữ cột này vì nó lưu giá trị thực tế sau khi tính toán các khoảng nghỉ
    DelayMinutes    INT           NOT NULL DEFAULT 0, -- Thời gian nghỉ/delay được loại trừ (ví dụ: ăn trưa)
    Note            NVARCHAR(MAX)     NULL,
    CreatedAt       DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_TimesheetEntry_Timesheet
        FOREIGN KEY (TimesheetID) REFERENCES Timesheet(TimesheetID) ON DELETE CASCADE,
);
GO

-- Chèn dữ liệu cho Thứ Hai (15/12/2025): Phân tích & Thiết kế Database cho Module
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note)
VALUES (7, '2025-12-15', '08:00:00', '17:00:00', 60, N'Phân tích các trường dữ liệu và thiết kế bảng cho module Timesheet Management.');

-- Chèn dữ liệu cho Thứ Ba (16/12/2025): Code giao diện (Frontend)
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note)
VALUES (7, '2025-12-16', '08:30:00', '17:30:00', 60, N'Xây dựng giao diện danh sách và form thêm mới Timesheet bằng Bootstrap.');

-- Chèn dữ liệu cho Thứ Tư (17/12/2025): Code xử lý Logic (Backend)
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note)
VALUES (7, '2025-12-17', '08:00:00', '18:00:00', 90, N'Viết DAO và Servlet xử lý logic CRUD cho module Timesheet.');
GO


-- =========================================================================
-- TIMESHEET ID 1: User 5 (Tuần 08/12 - 14/12) - Login & Phân quyền
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(1, '2025-12-08', '08:00:00', '17:30:00', 60, N'Tìm hiểu cơ chế Spring Security và JWT.'),
(1, '2025-12-09', '08:30:00', '18:00:00', 90, N'Viết code cho chức năng Authentication và lưu Token.'),
(1, '2025-12-10', '09:00:00', '17:00:00', 60, N'Xây dựng Middleware kiểm tra quyền truy cập (Role-based).'),
(1, '2025-12-11', '08:00:00', '12:00:00', 0, N'Fix lỗi không nhận Token trên trình duyệt Chrome.');

-- =========================================================================
-- TIMESHEET ID 2: User 6 (Tuần 08/12 - 14/12) - Dashboard & Sidebar
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(2, '2025-12-08', '08:00:00', '17:00:00', 60, N'Vẽ layout tổng thể cho Dashboard.'),
(2, '2025-12-09', '08:00:00', '17:00:00', 60, N'Code sidebar menu đa cấp và hiệu ứng thu gọn.'),
(2, '2025-12-10', '08:00:00', '17:00:00', 60, N'Tích hợp Chart.js để hiển thị biểu đồ thống kê công việc.');

-- =========================================================================
-- TIMESHEET ID 3: User 7 (Tuần 08/12 - 14/12) - API Báo cáo
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(3, '2025-12-08', '08:30:00', '17:30:00', 60, N'Thiết kế câu lệnh SQL lấy dữ liệu tổng hợp theo tuần.'),
(3, '2025-12-09', '08:00:00', '17:00:00', 60, N'Viết API Endpoint cho báo cáo dự án.'),
(3, '2025-12-10', '08:00:00', '18:00:00', 120, N'Tối ưu hóa performance cho các câu truy vấn phức tạp.');

-- =========================================================================
-- TIMESHEET ID 4: User 5 (Tuần 01/12 - 07/12) - ERD
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(4, '2025-12-01', '08:00:00', '17:00:00', 60, N'Họp lấy yêu cầu từ Stakeholders.'),
(4, '2025-12-02', '08:30:00', '17:30:00', 60, N'Phác thảo sơ đồ ERD phiên bản 1.'),
(4, '2025-12-03', '08:00:00', '17:00:00', 60, N'Chuẩn hóa các bảng dữ liệu về dạng 3NF.');

-- =========================================================================
-- TIMESHEET ID 5: User 6 (Tuần 01/12 - 07/12) - Setup Env
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(5, '2025-12-01', '09:00:00', '18:00:00', 60, N'Cài đặt Docker, SQL Server và môi trường lập trình.'),
(5, '2025-12-02', '08:00:00', '17:00:00', 60, N'Khởi tạo project template (Frontend/Backend).'),
(5, '2025-12-03', '08:30:00', '12:30:00', 0, N'Cấu hình CI/CD cơ bản cho dự án.');

-- =========================================================================
-- TIMESHEET ID 6: User 7 (Tuần 01/12 - 07/12) - Họp Kick-off
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(6, '2025-12-01', '08:00:00', '12:00:00', 0, N'Tham gia buổi họp Kick-off toàn công ty.'),
(6, '2025-12-02', '13:00:00', '17:00:00', 0, N'Thảo luận về Tech-stack sử dụng (React vs Angular).'),
(6, '2025-12-03', '08:00:00', '17:00:00', 60, N'Viết tài liệu Coding Convention cho Team.');

-- =========================================================================
-- TIMESHEET ID 7: User 5 (Bổ sung thêm 1 dòng cho đủ 4 dòng)
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note)
VALUES (7, '2025-12-18', '08:00:00', '12:00:00', 0, N'Kiểm thử lại luồng lưu trữ TimesheetEntry và fix lỗi logic Start/End time.');

-- =========================================================================
-- TIMESHEET ID 8: User 6 (Tuần 15/12 - 21/12) - Tối ưu UI
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(8, '2025-12-15', '08:00:00', '17:00:00', 60, N'Kiểm tra độ tương thích giao diện trên điện thoại.'),
(8, '2025-12-16', '08:30:00', '17:30:00', 60, N'Tối ưu hóa kích thước hình ảnh và nén file CSS.'),
(8, '2025-12-17', '08:00:00', '17:00:00', 60, N'Sửa các lỗi giật lag khi chuyển đổi các trang Dashboard.');

-- =========================================================================
-- TIMESHEET ID 9: User 7 (Tuần 15/12 - 21/12) - Export Excel
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(9, '2025-12-15', '08:00:00', '17:00:00', 60, N'Nghiên cứu thư viện Apache POI (Java) để xuất file Excel.'),
(9, '2025-12-16', '08:00:00', '17:00:00', 60, N'Định dạng template file Excel báo cáo (Màu sắc, Font chữ).'),
(9, '2025-12-17', '08:00:00', '18:00:00', 60, N'Hoàn thành logic đổ dữ liệu từ DB vào file Excel.');

-- =========================================================================
-- TIMESHEET ID 10: User 3 (Tuần 08/12 - 14/12) - Quản lý P1
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(10, '2025-12-08', '08:00:00', '17:00:00', 60, N'Họp review tiến độ tuần của Project 1.'),
(10, '2025-12-09', '09:00:00', '12:00:00', 0, N'Phân bổ task cho các thành viên mới gia nhập.'),
(10, '2025-12-10', '08:00:00', '17:00:00', 60, N'Review code Backend cho Module Authentication.');

-- =========================================================================
-- TIMESHEET ID 11: User 4 (Tuần 08/12 - 14/12) - Backend/Bảo trì
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(11, '2025-12-08', '08:30:00', '17:30:00', 60, N'Viết script tự động backup database hàng ngày.'),
(11, '2025-12-09', '08:00:00', '17:00:00', 60, N'Phân tích lỗi log trên môi trường Production.'),
(11, '2025-12-10', '13:00:00', '18:00:00', 0, N'Lên danh sách các thư viện cần cập nhật cho Project 3.');

-- =========================================================================
-- TIMESHEET ID 12: User 8 (Tuần 08/12 - 14/12) - Server E-learning
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(12, '2025-12-08', '08:00:00', '17:00:00', 60, N'Cấu hình Web Server (Nginx) cho hệ thống E-learning.'),
(12, '2025-12-09', '08:30:00', '17:30:00', 60, N'Phân quyền thư mục và cài đặt SSL Certificate.'),
(12, '2025-12-10', '08:00:00', '12:00:00', 0, N'Tham gia họp kick-off với đối tác cung cấp nội dung.');

-- =========================================================================
-- TIMESHEET ID 13: User 9 (Tuần 08/12 - 14/12) - AI Research
-- =========================================================================
INSERT INTO TimesheetEntry (TimesheetID, WorkDate, StartTime, EndTime, DelayMinutes, Note) VALUES
(13, '2025-12-08', '08:00:00', '17:00:00', 60, N'Nghiên cứu OpenAI API và LangChain.'),
(13, '2025-12-09', '08:00:00', '17:00:00', 60, N'Chạy thử nghiệm một số prompt mẫu cho việc gợi ý task.'),
(13, '2025-12-10', '08:30:00', '17:30:00', 60, N'Viết tài liệu so sánh chi phí giữa các Model AI hiện nay.');
GO


CREATE TABLE TimesheetReview (
    TimesheetReviewID INT IDENTITY(1,1) PRIMARY KEY,  -- Khóa chính Tự tăng
    TimesheetID       INT           NOT NULL,         -- Timesheet được đánh giá
    ReviewedByID      INT           NOT NULL,         -- Người duyệt (Thường là Manager/Admin)
    Comment           NVARCHAR(MAX)     NULL,         -- Nhận xét về timesheet
    ReviewedAt        DATETIME2     NOT NULL DEFAULT SYSDATETIME(), -- Thời điểm duyệt
    CONSTRAINT FK_Review_Timesheet
        FOREIGN KEY (TimesheetID) REFERENCES Timesheet(TimesheetID) ON DELETE CASCADE,
    CONSTRAINT FK_Review_Reviewer
        FOREIGN KEY (ReviewedByID) REFERENCES UserAccount(UserID),
);
GO

-- =========================================================================
-- SUPERVISOR (ID = 2) REVIEW CHO CÁC TIMESHEET TỪ 1 ĐẾN 13
-- Thời điểm review: Thường là cuối tuần hoặc cuối đợt log (khoảng 20/12/2025)
-- =========================================================================

INSERT INTO TimesheetReview (TimesheetID, ReviewedByID, Comment, ReviewedAt)
VALUES 
-- Review cho Timesheet 1 (User 5 - Login & Auth)
(1, 2, N'Cơ chế JWT đã được triển khai đúng tiêu chuẩn bảo mật của công ty. Tốt.', '2025-12-14 16:00:00'),

-- Review cho Timesheet 2 (User 6 - Dashboard)
(2, 2, N'Giao diện Dashboard trực quan, phần biểu đồ Chart.js cần tối ưu thêm màu sắc.', '2025-12-14 16:30:00'),

-- Review cho Timesheet 3 (User 7 - API Báo cáo)
(3, 2, N'Các câu query phức tạp đã được tối ưu hóa, API trả về dữ liệu nhanh.', '2025-12-14 17:00:00'),

-- Review cho Timesheet 4 (User 5 - ERD)
(4, 2, N'Sơ đồ ERD rất chi tiết, đã chuẩn hóa 3NF giúp database sạch hơn.', '2025-12-07 15:00:00'),

-- Review cho Timesheet 5 (User 6 - Setup Env)
(5, 2, N'Môi trường Docker đã ổn định, CI/CD hoạt động tốt.', '2025-12-07 15:30:00'),

-- Review cho Timesheet 6 (User 7 - Kick-off)
(6, 2, N'Tài liệu Coding Convention viết rất kỹ, team cần tuân thủ nghiêm ngặt.', '2025-12-07 16:00:00'),

-- Review cho Timesheet 7 (User 5 - Timesheet Module - Đang làm)
(7, 2, N'Tiến độ Module Timesheet Management đang đi đúng hướng, cần chú ý phần Validation.', '2025-12-20 09:00:00'),

-- Review cho Timesheet 8 (User 6 - Tối ưu UI)
(8, 2, N'Responsive trên mobile đã mượt mà hơn, đã check trên các thiết bị iPhone/Samsung.', '2025-12-20 10:00:00'),

-- Review cho Timesheet 9 (User 7 - Export Excel)
(9, 2, N'Logic Apache POI hoạt động ổn định, file Excel xuất ra đúng định dạng yêu cầu.', '2025-12-20 11:00:00'),

-- Review cho Timesheet 10 (User 3 - Quản lý P1)
(10, 2, N'Phân bổ task cho thành viên mới hợp lý, tiến độ dự án 1 đang rất tốt.', '2025-12-14 14:00:00'),

-- Review cho Timesheet 11 (User 4 - Bảo trì P3)
(11, 2, N'Script backup database hoạt động tốt, đã kiểm tra file backup trên cloud.', '2025-12-14 14:30:00'),

-- Review cho Timesheet 12 (User 8 - Server E-learning)
(12, 2, N'Server Nginx cấu hình tốt, SSL đã được kích hoạt thành công.', '2025-12-14 15:00:00'),

-- Review cho Timesheet 13 (User 9 - AI Research)
(13, 2, N'Bản so sánh các model AI rất giá trị cho quyết định đầu tư sắp tới của sếp.', '2025-12-14 15:30:00');
GO



-- BR: chỉ thằng được assign cho task mới được làm cái này, giả sử có nhiều thk làm 1 task
-- thì nhiều thk cũng được làm,
-- nếu chỉ có 1 thk được giao thì chỉ nó mới được làm, check trong assignneeTask
CREATE TABLE TaskReport (
    ReportID        INT IDENTITY(1,1) PRIMARY KEY,
    
    -- Người báo cáo & Task được báo cáo
    UserID          INT NOT NULL,
    TaskID          INT NOT NULL,
    
    -- Nội dung báo cáo
    ReportDescription NVARCHAR(MAX) NULL,
    
    -- Tiến độ công việc (0% - 100%)
    -- Dùng decimal(5,2) để lỡ có ông nhập 50.5% cũng nhận
    EstimateWorkPercentDone DECIMAL(5, 2) NOT NULL DEFAULT 0,
    
    -- Tổng thời gian đã tiêu tốn cho task này (tính đến thời điểm report)
    TotalHourUsed   DECIMAL(5, 2) NOT NULL DEFAULT 0,
    
    -- Link đến Timesheet (Có thể null nếu report mà không log timesheet)
    TimesheetEntryID INT NULL,
    
    -- Thời gian chỉnh sửa cuối cùng
    CreatedAt      DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    -- 1. Khóa ngoại User (Giả sử bảng User tên là [User] hoặc Users)
    CONSTRAINT FK_TaskReport_User 
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID), 
        
    -- 2. Khóa ngoại Task
    CONSTRAINT FK_TaskReport_Task 
        FOREIGN KEY (TaskID) REFERENCES ProjectTask(TaskID)
        ON DELETE CASCADE, -- Xóa Task thì xóa luôn Report

    -- 3. Khóa ngoại Timesheet (Giả sử bảng TimesheetEntry tồn tại)
    CONSTRAINT FK_TaskReport_Timesheet 
        FOREIGN KEY (TimesheetEntryID) REFERENCES TimesheetEntry(EntryID),

    -- 4. Constraint bắt buộc phần trăm chỉ từ 0 đến 100
    CONSTRAINT CHK_Report_Percent 
        CHECK (EstimateWorkPercentDone >= 0 AND EstimateWorkPercentDone <= 100)
);
GO



-- =========================================================================
-- BÁO CÁO CHO PROJECT 1 (UserID: 3, 4, 5, 6)
-- =========================================================================
INSERT INTO TaskReport (UserID, TaskID, ReportDescription, EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID)
VALUES 
-- Task 1: Database Design (Assignee: User 5)
(5, 1, N'Đã hoàn thành sơ đồ ERD sơ bộ và các bảng chính.', 60.00, 5.00, 1),
(5, 1, N'Đã thêm các ràng buộc Check Constraint và Index.', 100.00, 8.00, 26), -- 26 là entry bổ sung ở bước trước

-- Task 2: Backend Development (Assignee: User 3)
(3, 2, N'Thiết kế xong cấu trúc các lớp Entity và DTO.', 40.00, 6.00, 30),

-- Task 3: Frontend UI (Assignee: User 6)
(6, 3, N'Hoàn thành giao diện danh sách, đang làm Modal thêm mới.', 70.00, 9.00, 5),

-- Task 4: Workflow (Assignee: User 4)
(4, 4, N'Đang nghiên cứu logic chuyển trạng thái Draft sang Submitted.', 20.00, 2.00, NULL),

-- Task 5: Reporting (Assignee: User 5)
(5, 5, N'Đang tìm hiểu thư viện xuất file Excel.', 10.00, 2.00, NULL);

-- =========================================================================
-- BÁO CÁO CHO PROJECT 2 (UserID: 7, 8, 9, 3) - Dự án nghiên cứu AI
-- =========================================================================
INSERT INTO TaskReport (UserID, TaskID, ReportDescription, EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID)
VALUES 
-- Task 6: Thu thập dữ liệu (Assignee: User 9)
(9, 6, N'Đã thu thập đủ báo cáo từ Gartner và IDC.', 100.00, 10.00, 2),

-- Task 7: Phân tích LLM (Assignee: User 7)
(7, 7, N'Đã chạy thử nghiệm Benchmark trên GPT-4 và Claude 3.', 80.00, 15.00, 9),

-- Task 8: Nghiên cứu đối thủ (Assignee: User 8)
(8, 8, N'Hoàn thành bảng so sánh tính năng AI của 3 đối thủ lớn.', 100.00, 12.00, NULL),

-- Task 9: Khảo sát người dùng (Assignee: User 3)
(3, 9, N'Đã nhận được 50 bản phản hồi từ khảo sát.', 50.00, 6.00, 31),

-- Task 10: Tổng kết (Assignee: User 7)
(7, 10, N'Đang tổng hợp các báo cáo thành slide trình chiếu.', 30.00, 4.00, NULL);

-- =========================================================================
-- BÁO CÁO CHO PROJECT 3 (UserID: 4, 5, 6, 7) - Bảo trì Web
-- =========================================================================
INSERT INTO TaskReport (UserID, TaskID, ReportDescription, EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID)
VALUES 
-- Task 11: Framework Update (Assignee: User 6)
(6, 11, N'Nâng cấp thành công lên React 18 nhưng bị lỗi một số thư viện cũ.', 50.00, 8.00, 21),

-- Task 12: SQL Injection Fix (Assignee: User 4)
(4, 12, N'Đã rà soát và sửa lỗi tại các màn hình User Profile.', 90.00, 10.00, 34),

-- Task 13: Optimize Page Load (Assignee: User 5)
(5, 13, N'Đã cấu hình nén ảnh trên Server, tốc độ tăng 20%.', 45.00, 10.00, 13),

-- Task 14: Safari Bug (Assignee: User 7)
(7, 14, N'Đã tìm ra nguyên nhân lỗi CSS trên Safari 15.', 60.00, 5.00, 17),

-- Task 15: Deploy (Assignee: User 4)
(4, 15, N'Chờ approve để tiến hành deploy bản vá.', 0.00, 0.00, NULL);

-- =========================================================================
-- BÁO CÁO CHO PROJECT 4 (UserID: 8, 9, 3, 4) - E-learning
-- =========================================================================
INSERT INTO TaskReport (UserID, TaskID, ReportDescription, EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID)
VALUES 
-- Task 16: Khảo sát nhu cầu (Assignee: User 3)
(3, 16, N'Đã thiết kế xong Google Form khảo sát.', 100.00, 4.00, NULL),

-- Task 17: Hosting LMS (Assignee: User 8)
(8, 17, N'Đã cài đặt xong môi trường Ubuntu Server.', 40.00, 12.00, 36),

-- Task 18: Số hóa tài liệu (Assignee: User 4)
(4, 18, N'Đã quay xong 2 video hướng dẫn đầu tiên.', 25.00, 15.00, NULL),

-- Task 19: Test luồng đăng ký (Assignee: User 9)
(9, 19, N'Phát hiện lỗi không gửi email xác nhận khi đăng ký.', 30.00, 5.00, NULL),

-- Task 20: Workshop (Assignee: User 8)
(8, 20, N'Đang soạn tài liệu hướng dẫn nhanh cho buổi Workshop.', 15.00, 2.00, 38);

-- =========================================================================
-- BÁO CÁO CHO PROJECT 5 (UserID: 5, 6, 7, 8) - UX Redesign
-- =========================================================================
INSERT INTO TaskReport (UserID, TaskID, ReportDescription, EstimateWorkPercentDone, TotalHourUsed, TimesheetEntryID)
VALUES 
-- Task 21: Phỏng vấn (Assignee: User 7)
(7, 21, N'Đã phỏng vấn xong 5 khách hàng đầu tiên.', 50.00, 10.00, 39),

-- Task 22: Wireframe (Assignee: User 5)
(5, 22, N'Đã vẽ xong luồng Checkout và Giỏ hàng.', 70.00, 20.00, NULL),

-- Task 23: UI Kit (Assignee: User 6)
(6, 23, N'Đã chọn xong bảng màu và Typography chính.', 40.00, 10.00, 41),

-- Task 24: Prototype (Assignee: User 5)
(5, 24, N'Đang tạo hiệu ứng chuyển cảnh cho màn hình Homepage.', 20.00, 5.00, NULL),

-- Task 25: Bàn giao cho Dev (Assignee: User 8)
(8, 25, N'Đang liệt kê các thông số kỹ thuật (margin, padding) cho Dev.', 10.00, 3.00, NULL);
GO



CREATE TABLE TaskReview (
    ReviewID                INT IDENTITY(1,1) PRIMARY KEY,
    
    -- Task được review
    TaskID                  INT NOT NULL,
    
    -- Người review (Liên kết tới bảng UserAccount)
    ReviewedBy              INT NOT NULL,
    
    -- Đánh giá tiến độ thực tế (0% - 100%)
    EstimateWorkPercentDone DECIMAL(5, 2) NOT NULL DEFAULT 0,
    
    -- Nhận xét của người review
    ReviewComment           NVARCHAR(MAX) NULL,
    
    -- Ngày tạo review (Mặc định lấy giờ hiện tại)
    DateCreated             DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    -- 1. Khóa ngoại liên kết với ProjectTask
    CONSTRAINT FK_TaskReview_ProjectTask 
        FOREIGN KEY (TaskID) REFERENCES ProjectTask(TaskID)
        ON DELETE CASCADE, -- Nếu Task bị xóa thì Review cũng bay màu theo

    -- 2. Khóa ngoại liên kết với UserAccount (Như anh yêu cầu)
    CONSTRAINT FK_TaskReview_UserAccount 
        FOREIGN KEY (ReviewedBy) REFERENCES UserAccount(UserID),

    -- 3. Ràng buộc phần trăm chỉ được từ 0 đến 100
    CONSTRAINT CHK_TaskReview_Percent 
        CHECK (EstimateWorkPercentDone >= 0 AND EstimateWorkPercentDone <= 100)
);
GO

INSERT INTO TaskReview (TaskID, ReviewedBy, EstimateWorkPercentDone, ReviewComment, DateCreated)
VALUES 
-- PROJECT 1 (UserID 2 Review)
(1, 2, 60.00, N'Supervisor: Cấu trúc ERD ổn, cần chú ý thêm các index cho bảng Log.', '2025-12-10 10:00:00'),
(1, 2, 100.00, N'Supervisor: Đã duyệt, Database thiết kế rất chuyên nghiệp.', '2025-12-20 14:00:00'),
(2, 2, 35.00, N'Supervisor: Code cần refactor lại phần DTO cho gọn hơn.', '2025-12-21 09:00:00'),
(3, 2, 70.00, N'Supervisor: Giao diện sạch sẽ, cần check thêm trên trình duyệt Edge.', '2025-12-15 16:30:00'),
(4, 2, 20.00, N'Supervisor: Đẩy nhanh tiến độ phần Workflow phê duyệt.', '2025-12-26 10:00:00'),
(5, 2, 10.00, N'Supervisor: Đã xem qua kế hoạch làm báo cáo Excel.', '2026-01-02 11:00:00'),

-- PROJECT 2 (UserID 2 Review)
(6, 2, 100.00, N'Supervisor: Nguồn dữ liệu thu thập rất chất lượng.', '2024-10-16 08:00:00'),
(7, 2, 75.00, N'Supervisor: Phân tích LLM khá sâu, cần bổ sung bảng so sánh giá.', '2024-11-12 14:00:00'),
(8, 2, 100.00, N'Supervisor: Đã xem báo cáo đối thủ, rất hữu ích cho sếp tổng.', '2024-12-01 09:30:00'),
(9, 2, 50.00, N'Supervisor: Tiếp tục lấy thêm survey từ khách hàng thực tế.', '2024-12-16 10:00:00'),
(10, 2, 30.00, N'Supervisor: Slide báo cáo cuối năm cần chỉnh sửa màu sắc thương hiệu.', '2024-12-31 15:00:00'),

-- PROJECT 3 (UserID 2 Review)
(11, 2, 45.00, N'Supervisor: Lưu ý backup code trước khi nâng cấp Framework.', '2025-03-16 10:00:00'),
(12, 2, 95.00, N'Supervisor: Phần bảo mật làm rất tốt, cần duy trì tiêu chuẩn này.', '2025-03-26 11:00:00'),
(13, 2, 50.00, N'Supervisor: Page speed đã tăng, cố gắng tối ưu thêm CSS.', '2025-04-11 14:00:00'),
(14, 2, 60.00, N'Supervisor: Đã nhận thấy bug trên Safari, cố gắng fix trong tuần.', '2025-04-21 09:00:00'),
(15, 2, 0.00, N'Supervisor: Chờ lịch họp với bộ phận vận hành để deploy.', '2025-04-29 10:00:00'),

-- PROJECT 4 (UserID 2 Review)
(16, 2, 100.00, N'Supervisor: Khảo sát rất đầy đủ thông tin cần thiết.', '2025-04-26 08:30:00'),
(17, 2, 40.00, N'Supervisor: Server đã setup xong, cần chú ý bảo mật cổng 8080.', '2025-05-16 13:00:00'),
(18, 2, 30.00, N'Supervisor: Video hướng dẫn cần làm ngắn gọn hơn.', '2025-06-11 10:00:00'),
(19, 2, 30.00, N'Supervisor: Đã ghi nhận lỗi SMTP, yêu cầu fix trong 2 ngày.', '2025-07-01 11:00:00'),
(20, 2, 20.00, N'Supervisor: Tài liệu Workshop cần chuyên nghiệp hơn.', '2025-07-08 16:00:00'),

-- PROJECT 5 (UserID 2 Review)
(21, 2, 55.00, N'Supervisor: Kết quả phỏng vấn UX rất chi tiết.', '2025-03-06 09:00:00'),
(22, 2, 75.00, N'Supervisor: Bản vẽ Wireframe rất logic, sếp đã duyệt.', '2025-03-26 14:00:00'),
(23, 2, 40.00, N'Supervisor: UI Kit đẹp, đồng bộ với bộ nhận diện.', '2025-04-16 10:30:00'),
(24, 2, 20.00, N'Supervisor: Chú ý tốc độ phản hồi của các hiệu ứng Prototype.', '2025-05-11 11:00:00'),
(25, 2, 10.00, N'Supervisor: Cần viết thêm file Guide cho bộ phận Frontend.', '2025-05-18 15:00:00');
GO

/* =========================================================
   3. WEEKLY TIMESHEET DATA
   ========================================================= */
/*
   Each row = one member’s work for one day on one project/task.
   UI will aggregate these rows into a weekly grid (Mon–Sun).
*/


CREATE TABLE AttendanceRecord (
    AttendanceID   INT IDENTITY(1,1) PRIMARY KEY,
    UserID         INT  NOT NULL,
    AttendanceDate DATE NOT NULL,
    CONSTRAINT FK_AttendanceRecord_User 
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID)
);
GO

-- Chèn 30 dòng dữ liệu điểm danh (AttendanceRecord)
INSERT INTO AttendanceRecord (UserID, AttendanceDate)
VALUES 
-- Thứ Hai: 15/12/2025 (Cả 7 người đi đủ)
(3, '2025-12-15'), (4, '2025-12-15'), (5, '2025-12-15'), (6, '2025-12-15'), (7, '2025-12-15'), (8, '2025-12-15'), (9, '2025-12-15'),

-- Thứ Ba: 16/12/2025 (User 6 vắng)
(3, '2025-12-16'), (4, '2025-12-16'), (5, '2025-12-16'), (7, '2025-12-16'), (8, '2025-12-16'), (9, '2025-12-16'),

-- Thứ Tư: 17/12/2025 (User 7 vắng)
(3, '2025-12-17'), (4, '2025-12-17'), (5, '2025-12-17'), (6, '2025-12-17'), (8, '2025-12-17'), (9, '2025-12-17'),

-- Thứ Năm: 18/12/2025 (User 4 vắng)
(3, '2025-12-18'), (5, '2025-12-18'), (6, '2025-12-18'), (7, '2025-12-18'), (8, '2025-12-18'), (9, '2025-12-18'),

-- Thứ Sáu: 19/12/2025 (User 3 và 5 vắng)
(4, '2025-12-19'), (6, '2025-12-19'), (7, '2025-12-19'), (8, '2025-12-19'), (9, '2025-12-19');
GO


/* =========================================================
   5. LEAVE MANAGEMENT
   ========================================================= */
/*
   Each row = one leave application.
   Status is driven by: Pending / Accepted / Rejected / Withdrawn.
*/

CREATE TABLE LeaveRequest (
    LeaveID        INT IDENTITY(1,1) PRIMARY KEY,
    UserID         INT           NOT NULL,      -- member applying
    FromDate       DATE          NOT NULL,
    ToDate         DATE          NOT NULL,
    DurationDays   INT           NOT NULL,      -- total days of leave
    LeaveType      NVARCHAR(50)  NOT NULL DEFAULT N'GENERAL',  -- optional
    Reason         NVARCHAR(MAX)     NULL,

    Status         NVARCHAR(20)  NOT NULL DEFAULT N'PENDING',
    -- PENDING / ACCEPTED / REJECTED / WITHDRAWN

    AppliedAt      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    ReviewedAt     DATETIME2         NULL,
    ApproverID     INT               NULL,      -- supervisor/admin
    ApproverComment NVARCHAR(MAX)    NULL,

    WithdrawnAt    DATETIME2         NULL,      -- set when member withdraws

    CONSTRAINT FK_LeaveRequest_User
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID),

    CONSTRAINT FK_LeaveRequest_Approver
        FOREIGN KEY (ApproverID) REFERENCES UserAccount(UserID),

    CONSTRAINT CHK_LeaveRequest_Status
        CHECK (Status IN (N'PENDING', N'ACCEPTED', N'REJECTED', N'WITHDRAWN'))
);
GO

/* Sample leave requests */
INSERT INTO LeaveRequest
    (UserID, FromDate, ToDate, DurationDays, LeaveType, Reason, Status)
VALUES
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_anh'),
        '2025-01-10', '2025-01-11', 2, N'SICK',
        N'Flu and fever', N'PENDING'
    ),
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_bao'),
        '2025-01-15', '2025-01-17', 3, N'PERSONAL',
        N'Family event', N'ACCEPTED'
    );
GO







/* =========================================================
   Update 02 (MUST DO IT MANUALLY)
   ========================================================= */
   USE LABTimesheet;
GO

/* Team: represents a group in the lab */
CREATE TABLE Team (
    TeamID      INT IDENTITY(1,1) PRIMARY KEY,
    TeamName    NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX) NULL,
    CreatedBy   INT           NULL,           -- UserID of creator
    CreatedAt   DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Team_CreatedBy
        FOREIGN KEY (CreatedBy) REFERENCES UserAccount(UserID)
);
GO


INSERT INTO Team (
    TeamName, Description, CreatedBy, CreatedAt
)
VALUES
-- 1. Team Phát triển Chính
(N'Team Alpha (Phát triển Timesheet)', 
N'Chịu trách nhiệm chính về phát triển hệ thống Timesheet và các module liên quan.', 
4, '2025-01-01 09:30:00'),

-- 2. Team Nghiên cứu và Hạ tầng
(N'Team Beta (Nghiên cứu & Hạ tầng)', 
N'Tập trung vào các dự án nghiên cứu AI và quản lý, bảo trì hệ thống Cloud/Server Lab.', 
5, '2025-02-15 14:00:00'),

-- 3. Team Thiết kế và QA
(N'Team Gamma (Thiết kế & QA)', 
N'Đảm nhận các công việc liên quan đến thiết kế UX/UI và kiểm thử chất lượng sản phẩm.', 
6, '2025-03-01 11:15:00');
GO

CREATE TABLE TeamMember (
    TeamMemberID INT IDENTITY(1,1) PRIMARY KEY,
    TeamID       INT           NOT NULL,
    UserID       INT           NOT NULL,
    -- THAY ĐỔI: Khóa ngoại trỏ đến Role (Chứa RoleID = 4 hoặc 5)
    RoleID       INT           NOT NULL, 
    JoinedAt     DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_TeamMember_Team
        FOREIGN KEY (TeamID) REFERENCES Team(TeamID),

    CONSTRAINT FK_TeamMember_User
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID),
        
    -- THÊM Khóa ngoại trỏ đến Role
    CONSTRAINT FK_TeamMember_Role
        FOREIGN KEY (RoleID) REFERENCES Role(RoleID),

    CONSTRAINT UQ_TeamMember_Team_User
        UNIQUE (TeamID, UserID)  
);
GO

-- 3. THÊM Ràng buộc CHECK để giới hạn Vai trò chỉ là Team Member (4) hoặc Team Leader (5) 
ALTER TABLE TeamMember
ADD CONSTRAINT CHK_TeamMember_RoleID
CHECK (RoleID IN (4, 5)); 
GO

INSERT INTO TeamMember (
    TeamID, UserID, RoleID, JoinedAt
)
VALUES
-- =================================================================
-- 1. TeamID = 1: Team Alpha (Phát triển Timesheet)
-- =================================================================
-- Team Leader
(1, 4, 5, '2025-01-01 10:00:00'), 
-- Team Member 1
(1, 3, 4, '2025-01-05 11:30:00'),

-- =================================================================
-- 2. TeamID = 2: Team Beta (Nghiên cứu & Hạ tầng)
-- =================================================================
-- Team Leader
(2, 5, 5, '2025-02-15 14:05:00'), 
-- Team Member
(2, 3, 4, '2025-02-20 09:00:00'),

-- =================================================================
-- 3. TeamID = 3: Team Gamma (Thiết kế & QA)
-- =================================================================
-- Team Leader
(3, 6, 5, '2025-03-01 11:30:00'),
-- Team Member
(3, 7, 4, '2025-03-05 16:00:00');
GO

CREATE TABLE PasswordResetOtp (
    OtpID     INT IDENTITY(1,1) PRIMARY KEY,
    UserID    INT           NOT NULL,
    OtpCode   NVARCHAR(10)  NOT NULL,
    ExpiresAt DATETIME2     NOT NULL,
    IsUsed    BIT           NOT NULL DEFAULT 0,
    CreatedAt DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_PasswordResetOtp_User
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID)
);
GO

CREATE TABLE Invitation (
    InvitationID INT IDENTITY(1,1) PRIMARY KEY,
    Email        NVARCHAR(200) NOT NULL,
    RoleID       INT          NOT NULL,       -- role for the invited user could be team member /project mem/ project lead/ project co-leader
    InvitedByID  INT          NOT NULL,       -- who invited team lead or project lead or supervisor
    Token        UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
    Status       NVARCHAR(20)  NOT NULL DEFAULT N'PENDING',  -- trạng thái có thể là reject,accept, pending,cancelled
    ExpiresAt    DATETIME2     NOT NULL,    -- nhỡ đâu vô thời hạn- thôi ông immedi nghia a, suy nghi linh tinh vl.
    CreatedAt    DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    AcceptedAt   DATETIME2     NULL,

    CONSTRAINT FK_Invitation_Role
        FOREIGN KEY (RoleID) REFERENCES Role(RoleID),

    CONSTRAINT FK_Invitation_InvitedBy
        FOREIGN KEY (InvitedByID) REFERENCES UserAccount(UserID),

    CONSTRAINT CHK_Invitation_Status
        CHECK (Status IN (N'PENDING', N'ACCEPTED', N'REJECT', N'CANCELLED'))
);
GO

-- Add optional Project and Team references for the invitation
ALTER TABLE Invitation
ADD ProjectID INT NULL,
    TeamID    INT NULL;
GO

-- Add foreign key to Project
ALTER TABLE Invitation
ADD CONSTRAINT FK_Invitation_Project
FOREIGN KEY (ProjectID) REFERENCES Project(ProjectID);
GO

-- Add foreign key to Team
ALTER TABLE Invitation
ADD CONSTRAINT FK_Invitation_Team
FOREIGN KEY (TeamID) REFERENCES Team(TeamID);
GO

-- 1. Lời mời còn hạn (Sẽ hiện nút Accept/Reject)
INSERT INTO Invitation (Email, RoleID, InvitedByID, Status, ExpiresAt, CreatedAt, TeamID)
VALUES (N'nghiakhac2005@gmail.com', 4, 3, N'PENDING', DATEADD(day, 7, SYSDATETIME()), SYSDATETIME(), 1),
 (N'nghiakhac2005@gmail.com', 4, 6, N'PENDING', DATEADD(day, 7, SYSDATETIME()), SYSDATETIME(), 3),
 (N'nghiakhac2005@gmail.com', 4, 6, N'PENDING', DATEADD(day, -7, SYSDATETIME()), DATEADD(day, -14, SYSDATETIME()), 3),
 (N'nghiakhac2005@gmail.com', 4, 3, N'PENDING', DATEADD(day, -7, SYSDATETIME()), DATEADD(day, -14, SYSDATETIME()), 1);


 -- =========================================================================
-- 1. LỜI MỜI VÀO DỰ ÁN (PROJECT INVITATIONS)
-- =========================================================================

-- Mời làm Project Member (6) vào Project 1 - Trạng thái ACCEPTED
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, Status, CreatedAt, ExpiresAt, AcceptedAt)
VALUES (N'user_test_1@gmail.com', 6, 2, 1, N'ACCEPTED', '2025-12-01 08:00:00', '2025-12-08 08:00:00', '2025-12-02 10:30:00');

-- Mời làm Project Co-Lead (8) vào Project 3 - Trạng thái PENDING (Còn hạn)
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, Status, CreatedAt, ExpiresAt)
VALUES (N'developer_pro@hotmail.com', 8, 4, 3, N'PENDING', SYSDATETIME(), DATEADD(day, 7, SYSDATETIME()));

-- Mời làm Project Leader (7) vào Project 5 - Trạng thái REJECT
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, Status, CreatedAt, ExpiresAt)
VALUES (N'leader_candidate@yahoo.com', 7, 2, 5, N'REJECT', '2025-12-10 09:00:00', '2025-12-17 09:00:00');

-- Mời vào Project 4 - Trạng thái CANCELLED (Người mời chủ động hủy)
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, Status, CreatedAt, ExpiresAt)
VALUES (N'old_friend@gmail.com', 6, 8, 4, N'CANCELLED', '2025-12-05 14:00:00', '2025-12-12 14:00:00');


-- =========================================================================
-- 2. LỜI MỜI VÀO NHÓM (TEAM INVITATIONS)
-- =========================================================================

-- Mời vào Team 1 (Alpha) - Trạng thái PENDING (Sắp hết hạn)
INSERT INTO Invitation (Email, RoleID, InvitedByID, TeamID, Status, CreatedAt, ExpiresAt)
VALUES (N'newbie_member@gmail.com', 4, 4, 1, N'PENDING', DATEADD(day, -6, SYSDATETIME()), DATEADD(hour, 5, SYSDATETIME()));

-- Mời vào Team 2 (Beta) - Trạng thái ACCEPTED
INSERT INTO Invitation (Email, RoleID, InvitedByID, TeamID, Status, CreatedAt, ExpiresAt, AcceptedAt)
VALUES (N'researcher_01@gmail.com', 4, 5, 2, N'ACCEPTED', '2025-11-20 10:00:00', '2025-11-27 10:00:00', '2025-11-21 15:00:00');

-- Mời làm Team Leader cho Team mới - Trạng thái PENDING
INSERT INTO Invitation (Email, RoleID, InvitedByID, TeamID, Status, CreatedAt, ExpiresAt)
VALUES (N'manager_test@gmail.com', 5, 2, 3, N'PENDING', SYSDATETIME(), DATEADD(day, 14, SYSDATETIME()));


-- =========================================================================
-- 3. CÁC TRƯỜNG HỢP ĐẶC BIỆT CHO EMAIL CỦA BẠN (nghiakhac2005@gmail.com)
-- =========================================================================

-- Mời làm Project Member cho Project 2 (Còn hạn)
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, Status, CreatedAt, ExpiresAt)
VALUES (N'nghiakhac2005@gmail.com', 6, 7, 2, N'PENDING', SYSDATETIME(), DATEADD(day, 3, SYSDATETIME()));

-- Mời làm Project Co-Lead cho Project 1 (Đã hết hạn)
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, Status, CreatedAt, ExpiresAt)
VALUES (N'nghiakhac2005@gmail.com', 8, 3, 1, N'PENDING', '2025-11-01 08:00:00', '2025-11-08 08:00:00');

-- Lời mời đã bị từ chối trước đó
INSERT INTO Invitation (Email, RoleID, InvitedByID, TeamID, Status, CreatedAt, ExpiresAt)
VALUES (N'nghiakhac2005@gmail.com', 4, 6, 2, N'REJECT', '2025-10-15 10:00:00', '2025-10-22 10:00:00');

GO




/* =========================================================
   Update 03 (MUST DO IT MANUALLY)
   ========================================================= */
   USE LABTimesheet;
GO

CREATE TABLE TeamProject (
    TeamProjectID INT IDENTITY(1,1) PRIMARY KEY,
    TeamID        INT NOT NULL,
    ProjectID     INT NOT NULL,
    AssignedAt    DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_TeamProject_Team
        FOREIGN KEY (TeamID) REFERENCES Team(TeamID),

    CONSTRAINT FK_TeamProject_Project
        FOREIGN KEY (ProjectID) REFERENCES Project(ProjectID),

    CONSTRAINT UQ_TeamProject_Team_Project
        UNIQUE (TeamID, ProjectID)   -- tránh gán trùng một project cho cùng 1 team
);
GO

USE LABTimesheet;
GO


