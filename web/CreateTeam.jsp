<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Team Mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/css/tom-select.bootstrap5.min.css" rel="stylesheet">
    
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', sans-serif; }
        
        .btn-dashed {
            border: 2px dashed #adb5bd;
            background-color: #ffffff;
            color: #6c757d;
            width: 100%;
            text-align: left;
            padding: 12px 20px;
            border-radius: 8px;
            transition: all 0.2s;
            font-weight: 500;
        }
        .btn-dashed:hover {
            border-color: #0d6efd;
            color: #0d6efd;
            background-color: #f0f8ff;
        }

        .avatar-circle {
            width: 40px; height: 40px;
            background-color: #e9ecef;
            color: #495057;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-weight: bold; font-size: 14px;
            margin-right: 12px;
            flex-shrink: 0;
            text-transform: uppercase;
        }

        .user-select-container {
            background-color: #fff;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            padding: 5px 10px;
            display: flex; align-items: center;
        }
        
        /* Fix Tom Select height */
        .ts-wrapper.form-select, .ts-control {
            border: none !important;
            padding-left: 0 !important;
            box-shadow: none !important;
        }
        
        .fade-in { animation: fadeIn 0.5s; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body>
    
<div class="container mt-5 mb-5" style="max-width: 900px;">
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger fade-in" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i> ${error}
        </div>
    </c:if>

    <div id="errorAlertJS" class="alert alert-danger align-items-center mb-4 fade-in d-none" role="alert">
        <i class="fas fa-exclamation-triangle me-2 fs-5"></i>
        <span id="errorMessage"></span>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-4">
            <h4 class="mb-4 text-primary"><i class="fas fa-users-cog me-2"></i>Chi tiết nhóm</h4>

            <form id="createTeamForm" action="${pageContext.request.contextPath}/team/create" method="POST">
                
                <div class="mb-3">
                    <label class="form-label fw-bold">Tên nhóm <span class="text-danger">*</span></label>
                    <input type="text" name="teamName" id="teamName" class="form-control" 
                           placeholder="Ví dụ: Team Alpha..." required 
                           value="${param.teamName}"> </div>

                <div class="mb-4">
                    <label class="form-label fw-bold">Mô tả</label>
                    <textarea name="description" class="form-control" rows="3" placeholder="Mô tả mục tiêu...">${param.description}</textarea>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-bold">Thành viên <span class="text-danger">*</span></label>
                    <div id="members-container">
                        </div>
                    <button type="button" class="btn-dashed mt-2" id="btnAddMember">
                        <i class="fas fa-plus-circle me-2"></i> Thêm thành viên
                    </button>
                    <div class="form-text text-muted fst-italic">
                        * Gõ tên hoặc email để tìm kiếm. Chỉ được phép chọn 1 Leader duy nhất.
                    </div>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-bold">Dự án tham gia</label>
                    <div id="projects-container"></div>
                    <button type="button" class="btn-dashed mt-2" id="btnAddProject">
                        <i class="fas fa-folder-plus me-2"></i> Thêm dự án
                    </button>
                </div>

                <hr class="my-4">

                <div class="d-flex justify-content-end gap-2">
                    <a href="${pageContext.request.contextPath}/team" class="btn btn-light px-4">Hủy bỏ</a>
                    <button type="button" class="btn btn-success px-4" onclick="validateAndSubmit()">
                        <i class="fas fa-save me-2"></i>Lưu & Đóng
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<template id="member-template">
    <div class="row align-items-center mb-2 member-row fade-in">
        <div class="col-md-6">
            <div class="user-select-container">
                <div class="avatar-circle">?</div>
                <select class="form-select tom-select-user" name="memberIds" placeholder="Tìm thành viên...">
                    <option value="">Chọn thành viên...</option>
                    <c:forEach items="${userList}" var="u">
                        <option value="${u.userID}">${u.fullName} (${u.email})</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="col-md-5">
            <select class="form-select role-select" name="roles">
                <option value="Member" selected>Team Member</option>
                <option value="Leader">Team Leader</option>
                <option value="Reporter">Reporter</option>
            </select>
        </div>
        <div class="col-md-1 text-end">
            <button type="button" class="btn btn-outline-danger border-0 rounded-circle remove-row-btn">
                <i class="fas fa-times"></i>
            </button>
        </div>
    </div>
</template>

<template id="project-template">
    <div class="row align-items-center mb-2 project-row fade-in">
        <div class="col-md-11">
            <div class="input-group">
                <span class="input-group-text bg-white border-end-0"><i class="fas fa-project-diagram text-secondary"></i></span>
                <select class="form-select border-start-0 ps-0 tom-select-project" name="projectIds" placeholder="Tìm dự án...">
                    <option value="">Chọn dự án...</option>
                    <c:forEach items="${projectList}" var="p">
                        <option value="${p.projectID}">${p.projectName} (${p.projectCode})</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="col-md-1 text-end">
            <button type="button" class="btn btn-outline-danger border-0 rounded-circle remove-row-btn">
                <i class="fas fa-times"></i>
            </button>
        </div>
    </div>
</template>

<script src="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/js/tom-select.complete.min.js"></script>

<script>
    // --- 1. KHỞI TẠO & AVATAR ---
    function initTomSelect(element, type) {
        new TomSelect(element, {
            create: false,
            sortField: { field: "text", direction: "asc" },
            onChange: function(value) {
                if (type === 'user') updateAvatar(element, value);
            }
        });
    }

    const colors = ['#0d6efd', '#6610f2', '#6f42c1', '#d63384', '#dc3545', '#fd7e14', '#198754'];
    
    function getInitials(name) {
        if (!name) return "?";
        let cleanName = name.split('(')[0].trim(); // Bỏ phần email trong ngoặc
        const parts = cleanName.split(" ");
        if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
        return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    function updateAvatar(selectElement, value) {
        let container = selectElement.closest('.user-select-container');
        let avatar = container.querySelector('.avatar-circle');
        
        // Lấy text từ option được chọn
        let text = "";
        let options = selectElement.options;
        for(let i=0; i<options.length; i++) {
            if(options[i].value == value) {
                text = options[i].text;
                break;
            }
        }

        if (value && text) {
            avatar.innerText = getInitials(text);
            avatar.style.backgroundColor = colors[value % colors.length];
            avatar.style.color = '#fff';
        } else {
            avatar.innerText = "?";
            avatar.style.backgroundColor = "#e9ecef";
            avatar.style.color = "#495057";
        }
    }

    // --- 2. THÊM / XÓA DÒNG ---

    function addMemberRow() {
        const container = document.getElementById('members-container');
        const template = document.getElementById('member-template');
        // JSTL đã chạy trên server và điền data vào template này rồi
        // Giờ JS chỉ việc clone lại HTML đã có data đó
        const clone = template.content.cloneNode(true);
        
        const select = clone.querySelector('.tom-select-user');
        container.appendChild(clone);
        initTomSelect(select, 'user');
    }

    function addProjectRow() {
        const container = document.getElementById('projects-container');
        const template = document.getElementById('project-template');
        const clone = template.content.cloneNode(true);
        
        const select = clone.querySelector('.tom-select-project');
        container.appendChild(clone);
        initTomSelect(select, 'project');
    }

    // Event Listeners
    document.getElementById('btnAddMember').addEventListener('click', addMemberRow);
    document.getElementById('btnAddProject').addEventListener('click', addProjectRow);

    document.addEventListener('click', function(e) {
        if (e.target.closest('.remove-row-btn')) {
            const row = e.target.closest('.member-row') || e.target.closest('.project-row');
            if (row) row.remove();
        }
    });

    // Load 1 dòng mặc định khi vào trang
    document.addEventListener("DOMContentLoaded", () => {
        addMemberRow(); 
    });

    // --- 3. VALIDATION CLIENT-SIDE ---
    function validateAndSubmit() {
        const errorAlert = document.getElementById('errorAlertJS');
        errorAlert.classList.add('d-none');
        
        const teamName = document.getElementById('teamName').value.trim();
        if (!teamName) {
            showError("Vui lòng nhập tên nhóm!");
            return;
        }

        const memberRows = document.querySelectorAll('.member-row');
        let selectedUsers = [];
        let leaderCount = 0;
        let hasMember = false;

        for (let row of memberRows) {
            const userSelect = row.querySelector('.tom-select-user');
            const roleSelect = row.querySelector('.role-select');
            
            if (userSelect && userSelect.value) {
                hasMember = true;
                if (selectedUsers.includes(userSelect.value)) {
                    showError("Thành viên bị trùng lặp!");
                    return;
                }
                selectedUsers.push(userSelect.value);
                if (roleSelect.value === 'Leader') leaderCount++;
            }
        }

        if (!hasMember) {
            showError("Nhóm cần ít nhất 1 thành viên!");
            return;
        }
        if (leaderCount > 1) {
            showError("Chỉ được phép có 1 Leader!");
            return;
        }

        // Check trùng Project
        const projectRows = document.querySelectorAll('.project-row');
        let selectedProjects = [];
        for (let row of projectRows) {
            const prjSelect = row.querySelector('.tom-select-project');
            if (prjSelect && prjSelect.value) {
                if (selectedProjects.includes(prjSelect.value)) {
                    showError("Dự án bị trùng lặp!");
                    return;
                }
                selectedProjects.push(prjSelect.value);
            }
        }

        // Nếu OK hết -> Submit form thật
        document.getElementById('createTeamForm').submit();
    }

    function showError(msg) {
        const errorAlert = document.getElementById('errorAlertJS');
        document.getElementById('errorMessage').innerText = msg;
        errorAlert.classList.remove('d-none');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
</script>

</body>
</html>