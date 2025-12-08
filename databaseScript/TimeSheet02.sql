/* =========================================================
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
    IsActive     BIT           NOT NULL DEFAULT 1,
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
     (SELECT RoleID FROM Role WHERE RoleCode = N'STUDENT'));
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


CREATE TABLE ProjectTask (
    TaskID      INT IDENTITY(1,1) PRIMARY KEY,
    ProjectID   INT           NOT NULL,
    TaskCode    NVARCHAR(50)  NOT NULL UNIQUE,
    TaskName    NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX)     NULL,
    IsActive    BIT           NOT NULL DEFAULT 1,
    CreatedAt   DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_ProjectTask_Project
        FOREIGN KEY (ProjectID) REFERENCES Project(ProjectID)
);
GO
-- Add task status
ALTER TABLE ProjectTask
ADD Status NVARCHAR(20) NOT NULL DEFAULT N'TO_DO';
GO

ALTER TABLE ProjectTask
ADD CONSTRAINT CHK_ProjectTask_Status
CHECK (Status IN (N'TO_DO', N'COMPLETE'));
GO

/* Sample project & tasks */
INSERT INTO Project (ProjectCode, ProjectName, Description)
VALUES (N'PRJ001', N'AI Research Platform', N'Example lab project for testing');
GO

INSERT INTO ProjectTask (ProjectID, TaskCode, TaskName, Description)
VALUES
 ((SELECT ProjectID FROM Project WHERE ProjectCode = N'PRJ001'),
    N'TASK001', N'Data Cleaning',  N'Prepare and clean datasets'),
 ((SELECT ProjectID FROM Project WHERE ProjectCode = N'PRJ001'),
    N'TASK002', N'Model Training', N'Train ML models');
GO


/* =========================================================
   3. WEEKLY TIMESHEET DATA
   ========================================================= */
/*
   Each row = one member’s work for one day on one project/task.
   UI will aggregate these rows into a weekly grid (Mon–Sun).
*/

CREATE TABLE TimesheetEntry (
    EntryID        INT IDENTITY(1,1) PRIMARY KEY,
    UserID         INT           NOT NULL,          -- member
    ProjectID      INT               NULL,          -- optional, can be null
    TaskID         INT               NULL,          -- optional, can be null
    WorkDate       DATE          NOT NULL,          -- day of work
    StartTime      TIME              NULL,          -- optional
    EndTime        TIME              NULL,          -- optional
    MinutesWorked  INT           NOT NULL,          -- total minutes for that row
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
    RoleID       INT          NOT NULL,       -- role for the invited user
    InvitedByID  INT          NOT NULL,       -- who invited
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