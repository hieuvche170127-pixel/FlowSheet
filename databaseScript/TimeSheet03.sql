x/* =========================================================
   Create database

!!!!!!!MUST DO UPDATE PARTS MANUALLY!!!!!!!!!!

   USE master;
ALTER DATABASE LABTimesheet SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE LABTimesheet;

   ========================================================= */




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

/* Seed roles */
INSERT INTO Role (RoleCode, RoleName)
VALUES
 (N'STUDENT',    N'Student'),
 (N'SUPERVISOR', N'Supervisor'),
 (N'ADMIN',      N'Administrator');
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
0, '2024-09-15 14:00:00', '2024-10-01', '2024-12-31', 'COMPLETE'),

-- 3. Dự án Bảo trì (Đang tiến hành)
('P_MAINT_WEB', N'Bảo trì và nâng cấp website công ty', N'Cập nhật framework và sửa lỗi bảo mật trên website chính.', 
1, '2025-02-18 09:45:00', '2025-03-01', '2025-04-30', 'IN_PROGRESS'),

-- 4. Dự án Nội bộ (Mới mở) - Tạo gần đây
('P_HR_SETUP', N'Triển khai hệ thống E-learning nội bộ', N'Xây dựng nền tảng đào tạo trực tuyến cho nhân viên mới.', 
1, '2025-03-25 15:20:00', '2025-04-10', '2025-07-10', 'OPEN'),

-- 5. Dự án Thiết kế (Đang tiến hành)
('P_UX_REDESIGN', N'Thiết kế lại giao diện người dùng sản phẩm', N'Tối ưu hóa UX/UI cho ứng dụng di động.', 
1, '2025-02-01 11:00:00', '2025-02-20', '2025-05-20', 'IN_PROGRESS'),


-- change from projectid not null to null 
-- vì mình có cả task của lab nữa, và lab thì ngoài project, nên nếu project id là null, nó là lab task. 
-- nếu giờ thay đổi tên bảng/ database các khóa thì mất thời gian, nên nó là tối ưu về thời gian và công sức nhất. 
-- date modify: 12/12/25/ 10h57pm made by nghia 

CREATE TABLE ProjectTask (
    TaskID      INT IDENTITY(1,1) PRIMARY KEY,
    ProjectID   INT           NULL,
    TaskCode    NVARCHAR(50)  NOT NULL UNIQUE,
    TaskName    NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX)     NULL,
    IsActive    BIT           NOT NULL DEFAULT 1,  -- đây là kiểu nó có thể thực hiện hay ko thể thực hiện (avalable) 
    CreatedAt   DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    Status      NVARCHAR(20)  NOT NULL DEFAULT N'TO_DO';

    CONSTRAINT FK_ProjectTask_Project
        FOREIGN KEY (ProjectID) REFERENCES Project(ProjectID)
);
GO

ALTER TABLE ProjectTask
ADD CONSTRAINT CHK_ProjectTask_Status
CHECK (Status IN (N'TO_DO', N'COMPLETE'));  -- ở đây có thể thêm suspend ? ko suspend để ở trên được.
GO

INSERT INTO ProjectTask (
    ProjectID, TaskCode, TaskName, Description, IsActive, CreatedAt, Status
)
VALUES
-- =================================================================
-- 1. ProjectID = 1: P_SWP391_A (Timesheet Management System)
-- StartDate: 2025-01-15
-- =================================================================
(1, 'SWP_T001', N'Phân tích Yêu cầu', N'Thu thập và phân tích chi tiết yêu cầu người dùng.', 
1, '2025-01-08 10:00:00', 'COMPLETE'),
(1, 'SWP_T002', N'Thiết kế CSDL', N'Thiết kế sơ đồ ERD và cấu trúc các bảng SQL.', 
1, '2025-01-12 14:30:00', 'COMPLETE'),
(1, 'SWP_T003', N'Phát triển API Đăng nhập', N'Xây dựng các API cho chức năng xác thực người dùng.', 
1, '2025-01-20 09:00:00', 'TO_DO'),
(1, 'SWP_T004', N'Xây dựng Giao diện Trang chủ', N'Phát triển giao diện người dùng (FE) cho trang tổng quan.', 
1, '2025-02-01 11:00:00', 'TO_DO'),
(1, 'SWP_T005', N'Thiết kế Module Timesheet', N'Thiết kế luồng nghiệp vụ và giao diện nhập Timesheet.', 
1, '2025-02-15 15:00:00', 'TO_DO'),
(1, 'SWP_T006', N'Viết Test Case cho Auth', N'Viết các kịch bản kiểm thử cho module đăng nhập.', 
1, '2025-03-01 10:00:00', 'TO_DO'),

-- =================================================================
-- 2. ProjectID = 2: P_RESEARCH_01 (Nghiên cứu thị trường AI) - Project COMPLETE
-- StartDate: 2024-10-01
-- =================================================================
(2, 'RES_T001', N'Lên danh sách Nguồn dữ liệu', N'Xác định các báo cáo, bài báo khoa học liên quan.', 
1, '2024-09-18 11:00:00', 'COMPLETE'),
(2, 'RES_T002', N'Thu thập Dữ liệu thị trường', N'Đọc và tóm tắt thông tin từ các nguồn đã chọn.', 
1, '2024-09-25 09:00:00', 'COMPLETE'),
(2, 'RES_T003', N'Phân tích xu hướng (Trend Analysis)', N'Phân loại và phân tích các xu hướng AI nổi bật.', 
1, '2024-10-10 13:00:00', 'COMPLETE'),
(2, 'RES_T004', N'Viết Báo cáo tổng hợp', N'Soạn thảo báo cáo cuối cùng.', 
1, '2024-11-01 16:00:00', 'COMPLETE'),
(2, 'RES_T005', N'Thiết kế Slide thuyết trình', N'Tạo slide trình bày kết quả nghiên cứu.', 
1, '2024-12-05 10:00:00', 'COMPLETE'),

-- =================================================================
-- 3. ProjectID = 3: P_MAINT_WEB (Bảo trì và nâng cấp website)
-- StartDate: 2025-03-01
-- =================================================================
(3, 'MNT_T001', N'Đánh giá Phiên bản Framework', N'Kiểm tra và xác định lộ trình nâng cấp Framework.', 
1, '2025-02-20 09:30:00', 'COMPLETE'),
(3, 'MNT_T002', N'Xử lý lỗ hổng XSS', N'Sửa chữa các lỗ hổng Cross-Site Scripting.', 
1, '2025-03-05 10:00:00', 'TO_DO'),
(3, 'MNT_T003', N'Nâng cấp Database Driver', N'Cập nhật driver kết nối CSDL.', 
1, '2025-03-10 14:00:00', 'TO_DO'),
(3, 'MNT_T004', N'Tối ưu hóa hình ảnh tĩnh', N'Nén và tối ưu hóa tốc độ tải các tệp hình ảnh tĩnh.', 
1, '2025-03-15 11:00:00', 'TO_DO'),

-- =================================================================
-- 4. ProjectID = 4: P_HR_SETUP (Triển khai hệ thống E-learning)
-- StartDate: 2025-04-10
-- =================================================================
(4, 'HR_T001', N'Lựa chọn Nền tảng LMS', N'Nghiên cứu và chọn nền tảng LMS phù hợp.', 
1, '2025-03-28 15:30:00', 'COMPLETE'),
(4, 'HR_T002', N'Thiết lập môi trường Server', N'Cài đặt và cấu hình máy chủ cho hệ thống LMS.', 
1, '2025-04-01 09:00:00', 'TO_DO'),
(4, 'HR_T003', N'Phát triển Module Đăng ký', N'Code module cho phép người dùng đăng ký khóa học.', 
1, '2025-04-05 14:00:00', 'TO_DO'),
(4, 'HR_T004', N'Tạo Khóa học Thử nghiệm', N'Tạo 3 khóa học mẫu để kiểm tra tính năng hệ thống.', 
1, '2025-04-08 10:00:00', 'TO_DO'),
(4, 'HR_T005', N'Viết Tài liệu Hướng dẫn', N'Viết tài liệu hướng dẫn sử dụng cho người quản trị.', 
1, '2025-04-15 16:00:00', 'TO_DO'),

-- =================================================================
-- 5. ProjectID = 5: P_UX_REDESIGN (Thiết kế lại giao diện người dùng)
-- StartDate: 2025-02-20
-- =================================================================
(5, 'UX_T001', N'Nghiên cứu Người dùng', N'Tiến hành phỏng vấn và khảo sát người dùng.', 
1, '2025-02-05 09:00:00', 'COMPLETE'),
(5, 'UX_T002', N'Vẽ Wireframe (Low-fidelity)', N'Tạo bản nháp cấu trúc cơ bản của các trang.', 
1, '2025-02-10 14:00:00', 'COMPLETE'),
(5, 'UX_T003', N'Thiết kế Mockup (High-fidelity)', N'Hoàn thiện giao diện đồ họa chi tiết.', 
1, '2025-02-25 10:30:00', 'TO_DO'),
(5, 'UX_T004', N'Tạo Prototype tương tác', N'Sử dụng công cụ để tạo bản thử nghiệm có thể tương tác.', 
1, '2025-03-15 11:00:00', 'TO_DO'),
(5, 'UX_T005', N'Kiểm thử khả năng sử dụng (Usability Test)', N'Thực hiện kiểm thử với nhóm người dùng mẫu.', 
1, '2025-04-01 14:00:00', 'TO_DO');
GO

/* =========================================================
   3. WEEKLY TIMESHEET DATA
   ========================================================= */
/*
   Each row = one member’s work for one day on one project/task.
   UI will aggregate these rows into a weekly grid (Mon–Sun).
*/

-- Business rule: 
-- sau khi được review, thì timesheet sẽ ko còn có thể chỉnh sửa?
-- vì nếu thầy/supervisor đã xem và đánh giá cái timesheetentry đó, thì người tạo ko được sửa nữa.
CREATE TABLE TimesheetEntry (
    EntryID        INT IDENTITY(1,1) PRIMARY KEY,
    UserID         INT           NOT NULL,          -- member
    ProjectID      INT               NULL,          -- optional, can be null
    TaskID         INT               NULL,          -- optional, can be null
    WorkDate       DATE          NOT NULL,          -- day of work
    StartTime      TIME              NULL,          -- optional
    EndTime        TIME              NULL,          -- optional
    MinutesWorked  INT           NOT NULL,          -- total minutes for that row --estimate the time has work = end-start- delay time
    Note           NVARCHAR(MAX)    NULL,
    CreatedAt      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    UpdatedAt      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_TimesheetEntry_User
        FOREIGN KEY (UserID)    REFERENCES UserAccount(UserID),

    CONSTRAINT FK_TimesheetEntry_Project
        FOREIGN KEY (ProjectID) REFERENCES Project(ProjectID),

    CONSTRAINT FK_TimesheetEntry_Task
        FOREIGN KEY (TaskID)    REFERENCES ProjectTask(TaskID)
);
GO


--Update
ALTER TABLE TimesheetEntry
ADD Status        NVARCHAR(20) NOT NULL DEFAULT N'PENDING',
    ApprovedByID  INT NULL,
    ApprovedAt    DATETIME2 NULL;
GO

ALTER TABLE TimesheetEntry
ADD CONSTRAINT CHK_TimesheetEntry_Status
CHECK (Status IN (N'PENDING', N'APPROVED', N'REJECTED'));
GO

ALTER TABLE TimesheetEntry
ADD CONSTRAINT FK_TimesheetEntry_ApprovedBy
FOREIGN KEY (ApprovedByID) REFERENCES UserAccount(UserID);
GO


/* Sample weekly entries for stu_anh (Mon–Wed of one week) */
INSERT INTO TimesheetEntry
    (UserID, ProjectID, TaskID, WorkDate, StartTime, EndTime, MinutesWorked, Note)
VALUES
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_anh'),
        (SELECT ProjectID FROM Project WHERE ProjectCode = N'PRJ001'),
        (SELECT TaskID FROM ProjectTask WHERE TaskCode = N'TASK001'),
        '2025-01-06', '08:00', '11:00', 180, N'Cleaning dataset A'
    ),
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_anh'),
        (SELECT ProjectID FROM Project WHERE ProjectCode = N'PRJ001'),
        (SELECT TaskID FROM ProjectTask WHERE TaskCode = N'TASK002'),
        '2025-01-07', '09:00', '12:00', 180, N'Model training'
    ),
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_bao'),
        (SELECT ProjectID FROM Project WHERE ProjectCode = N'PRJ001'),
        (SELECT TaskID FROM ProjectTask WHERE TaskCode = N'TASK001'),
        '2025-01-07', '13:00', '16:00', 180, N'Cleaning dataset B'
    );
GO






/* =========================================================
   4. CLOCK IN / CLOCK OUT (Attendance)
   ========================================================= */
/*
   Each row = one member’s attendance for one day.
   Used for the Clock In/Out page.
*/

CREATE TABLE AttendanceRecord (
    AttendanceID   INT IDENTITY(1,1) PRIMARY KEY,
    UserID         INT           NOT NULL,
    AttendanceDate DATE          NOT NULL,
    ClockInTime    TIME              NULL,
    ClockOutTime   TIME              NULL,
    TotalMinutes   INT               NULL,   -- calculate when Clock Out
    Status         NVARCHAR(20) NOT NULL DEFAULT N'OPEN',
    -- OPEN: clocked in but not out
    -- CLOSED: clocked in and out
    -- APPROVED: (optional) after supervisor confirms

    CreatedAt      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    UpdatedAt      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_AttendanceRecord_User
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID),

    CONSTRAINT CHK_AttendanceRecord_Status
        CHECK (Status IN (N'OPEN', N'CLOSED', N'APPROVED'))
);
GO

/* Sample attendance */
INSERT INTO AttendanceRecord
    (UserID, AttendanceDate, ClockInTime, ClockOutTime, TotalMinutes, Status)
VALUES
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_anh'),
        '2025-01-06', '08:00', '11:00', 180, N'CLOSED'
    ),
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_anh'),
        '2025-01-07', '09:00', NULL, NULL, N'OPEN' -- clocked in only
    ),
    (
        (SELECT UserID FROM UserAccount WHERE Username = N'stu_bao'),
        '2025-01-07', '13:00', '17:00', 240, N'CLOSED'
    );
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

/* 
 TASK001 – Data Cleaning
 Assigned to:
   - stu_anh
   - stu_bao
*/
INSERT INTO TaskAssignee (TaskID, UserID)
VALUES
(
    (SELECT TaskID FROM ProjectTask WHERE TaskCode = N'TASK001'),
    (SELECT UserID FROM UserAccount WHERE Username = N'stu_anh')
),
(
    (SELECT TaskID FROM ProjectTask WHERE TaskCode = N'TASK001'),
    (SELECT UserID FROM UserAccount WHERE Username = N'stu_bao')
);

/* 
 TASK002 – Model Training
 Assigned to:
   - stu_anh
*/
INSERT INTO TaskAssignee (TaskID, UserID)
VALUES
(
    (SELECT TaskID FROM ProjectTask WHERE TaskCode = N'TASK002'),
    (SELECT UserID FROM UserAccount WHERE Username = N'stu_anh')
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

/* TeamMember: which users belong to which team */
CREATE TABLE TeamMember (
    TeamMemberID INT IDENTITY(1,1) PRIMARY KEY,
    TeamID       INT NOT NULL,
    UserID       INT NOT NULL,
    RoleInTeam   NVARCHAR(50) NULL,     -- e.g. 'Leader', 'Member'
    JoinedAt     DATETIME2   NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_TeamMember_Team
        FOREIGN KEY (TeamID) REFERENCES Team(TeamID),

    CONSTRAINT FK_TeamMember_User
        FOREIGN KEY (UserID) REFERENCES UserAccount(UserID),

    CONSTRAINT UQ_TeamMember_Team_User
        UNIQUE (TeamID, UserID)     -- tránh trùng
);
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
    Status       NVARCHAR(20)  NOT NULL DEFAULT N'PENDING',
    ExpiresAt    DATETIME2     NOT NULL,
    CreatedAt    DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    AcceptedAt   DATETIME2     NULL,

    CONSTRAINT FK_Invitation_Role
        FOREIGN KEY (RoleID) REFERENCES Role(RoleID),

    CONSTRAINT FK_Invitation_InvitedBy
        FOREIGN KEY (InvitedByID) REFERENCES UserAccount(UserID),

    CONSTRAINT CHK_Invitation_Status
        CHECK (Status IN (N'PENDING', N'ACCEPTED', N'EXPIRED', N'CANCELLED'))
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

-- 1) Invite someone to lab only (no project, no team)
INSERT INTO Invitation (Email, RoleID, InvitedByID, ExpiresAt)
VALUES (
    N'external1@example.com',
    (SELECT RoleID FROM Role WHERE RoleCode = N'STUDENT'),
    (SELECT UserID FROM UserAccount WHERE Username = N'sup_hoa'),
    DATEADD(DAY, 7, SYSDATETIME())
);

-- 2) Invite to AI project only
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, ExpiresAt)
VALUES (
    N'external2@example.com',
    (SELECT RoleID FROM Role WHERE RoleCode = N'STUDENT'),
    (SELECT UserID FROM UserAccount WHERE Username = N'sup_hoa'),
    (SELECT ProjectID FROM Project WHERE ProjectCode = N'PRJ001'),
    DATEADD(DAY, 7, SYSDATETIME())
);

-- 3) Invite to AI project + specific team
INSERT INTO Invitation (Email, RoleID, InvitedByID, ProjectID, TeamID, ExpiresAt)
VALUES (
    N'external3@example.com',
    (SELECT RoleID FROM Role WHERE RoleCode = N'STUDENT'),
    (SELECT UserID FROM UserAccount WHERE Username = N'sup_hoa'),
    (SELECT ProjectID FROM Project WHERE ProjectCode = N'PRJ001'),
    (SELECT TOP 1 TeamID FROM Team),  -- ví dụ lấy 1 team bất kỳ
    DATEADD(DAY, 7, SYSDATETIME())
);



/* =========================================================
   Update 03 (MUST DO IT MANUALLY)
   ========================================================= */
   USE LABTimesheet;
GO
INSERT INTO Team (TeamName, Description, CreatedBy)
VALUES (N'Team Alpha', N'Example sample team', 
        (SELECT UserID FROM UserAccount WHERE Username = N'sup_hoa'));
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

/* 1. Xóa bảng cũ */
IF OBJECT_ID('dbo.TeamMember', 'U') IS NOT NULL 
    DROP TABLE dbo.TeamMember;
GO

/* 2. Tạo bảng mới: KHÔNG CÓ cột TeamMemberID */
CREATE TABLE TeamMember (
    TeamID       INT NOT NULL,
    UserID       INT NOT NULL,
    RoleInTeam   NVARCHAR(50) NULL,     -- 'Leader', 'Member'
    JoinedAt     DATETIME2   NOT NULL DEFAULT SYSDATETIME(),

    -- KHÓA CHÍNH là sự kết hợp của TeamID và UserID
    CONSTRAINT PK_TeamMember PRIMARY KEY (TeamID, UserID),

    -- Khóa ngoại
    CONSTRAINT FK_TeamMember_Team FOREIGN KEY (TeamID) REFERENCES Team(TeamID) ON DELETE CASCADE,
    CONSTRAINT FK_TeamMember_User FOREIGN KEY (UserID) REFERENCES UserAccount(UserID)
);
GO

-- dùng khóa chính tự tăng thay vì cặp khóa chính vì nếu là cặp khóa chính,
-- một khi rời khỏi project sẽ ko thể quay lại 
-- và trong thực tế thì có thể đâu đó xảy ra trường hợp đó. 

CREATE TABLE ProjectAssignee (
    -- Khóa chính Tự tăng (Surrogate Key)
    ProjectAssigneeID INT IDENTITY(1,1) PRIMARY KEY, 
    
    ProjectID      INT NOT NULL,
    UserID         INT NOT NULL,
    
    RoleInProject  NVARCHAR(50) NULL,      
    AssignedAt     DATETIME2   NOT NULL DEFAULT SYSDATETIME(),

    -- Thời điểm rời đi (Để NULL nếu đang tham gia)
    LeftAt         DATETIME2   NULL, 
    
    -- Ghi chú về lý do rời đi (Tùy chọn)
    LeaveReason    NVARCHAR(255) NULL, 

    -- *** Ràng buộc mới (Loại bỏ Khóa chính kết hợp, thay bằng Khóa Duy nhất) ***
    -- Ràng buộc Duy nhất: Ngăn chặn việc cùng một người tham gia CÙNG một dự án
    -- mà không có LeftAt (chưa rời đi)
    -- CONSTRAINT UQ_ProjectAssignee UNIQUE (ProjectID, UserID), -- KHÔNG DÙNG NỮA
    
    -- Khóa ngoại: Tham chiếu đến Project
    CONSTRAINT FK_ProjectAssignee_Project FOREIGN KEY (ProjectID) 
        REFERENCES Project(ProjectID) 
        ON DELETE CASCADE, 
    
    -- Khóa ngoại: Tham chiếu đến UserAccount
    CONSTRAINT FK_ProjectAssignee_User FOREIGN KEY (UserID) 
        REFERENCES UserAccount(UserID)
);
GO
