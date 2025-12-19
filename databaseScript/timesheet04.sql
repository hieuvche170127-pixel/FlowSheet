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


