<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/css/tom-select.bootstrap5.min.css" rel="stylesheet">
    
    <style>
        body { background-color: #f8f9fa; }
        .card { border: none; box-shadow: 0 0 15px rgba(0,0,0,0.05); }
        .text-teal { color: #00bfa5; }
        .btn-teal { background-color: #00bfa5; color: white; border: none; }
        .btn-teal:hover { background-color: #009688; color: white; }

        .btn-dashed {
            border: 2px dashed #adb5bd;
            background-color: #ffffff;
            color: #6c757d;
            width: 100%;
            text-align: left;
            padding: 10px 20px;
            border-radius: 8px;
            transition: all 0.2s;
            font-weight: 500;
        }
        .btn-dashed:hover {
            border-color: #00bfa5; color: #00bfa5; background-color: #e0f2f1;
        }

        /* Avatar cho phần Member */
        .avatar-circle {
            width: 38px; height: 38px;
            background-color: #e9ecef; color: #495057;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-weight: bold; font-size: 14px;
            margin-right: 10px; flex-shrink: 0;
            text-transform: uppercase;
        }
        .user-select-container {
            background-color: #fff; border: 1px solid #dee2e6;
            border-radius: 6px; padding: 4px 10px;
            display: flex; align-items: center;
        }
        .ts-wrapper.form-select, .ts-control { border: none !important; padding-left: 0 !important; box-shadow: none !important; }
        .fade-in { animation: fadeIn 0.3s; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(-5px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body>
<jsp:include page="/nghiapages/layout_header.jsp" />
<div class="container py-4" style="max-width: 900px;">
    <div id="errorAlertJS" class="alert alert-danger align-items-center mb-4 fade-in d-none" role="alert">
        <i class="fas fa-exclamation-triangle me-2"></i> <span id="errorMessage"></span>
    </div>
    <c:if test="${not empty error}">
        <div class="alert alert-danger fade-in"><i class="fas fa-exclamation-triangle me-2"></i> ${error}</div>
    </c:if>

    <div class="card p-4">
        <h4 class="mb-4 fw-bold text-teal">Create New Project</h4>
        
        <form action="${pageContext.request.contextPath}/project/create" method="post" id="createForm">
            
            <div class="row mb-3">
                <div class="col-md-4">
                    <label class="form-label fw-bold">Code <span class="text-danger">*</span></label>
                    <input type="text" name="projectCode" class="form-control" required>
                </div>
                <div class="col-md-8">
                    <label class="form-label fw-bold">Project Name <span class="text-danger">*</span></label>
                    <input type="text" name="projectName" class="form-control" required>
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label fw-bold">Start Date</label>
                    <input type="date" name="startDate" class="form-control">
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-bold">Deadline</label>
                    <input type="date" name="deadline" class="form-control">
                </div>
            </div>
            <div class="mb-3">
                <label class="fw-bold">Description</label>
                <textarea name="description" class="form-control" rows="3"></textarea>
            </div>

            <hr>

            <div class="mb-4">
                <label class="fw-bold mb-2 text-teal">Resource Assignment</label>
                <div class="btn-group w-100" role="group">
                    <input type="radio" class="btn-check" name="assignType" id="typeTeam" value="team" checked onclick="toggleAssign('team')">
                    <label class="btn btn-outline-success" for="typeTeam">Choose Existing Team</label>

                    <input type="radio" class="btn-check" name="assignType" id="typeMember" value="member" onclick="toggleAssign('member')">
                    <label class="btn btn-outline-success" for="typeMember">Select Individual Members</label>
                </div>
            </div>

            <div id="sectionTeam" class="bg-light p-3 rounded border">
                <label class="form-label fw-bold">Select Team:</label>
                <input class="form-control" list="teamOptions" id="teamInput" placeholder="Type team name..." onchange="updateTeamId(this)">
                
                <datalist id="teamOptions">
                    <c:forEach items="${teamList}" var="t">
                        <option data-value="${t.teamID}" value="${t.teamName}"></option>
                    </c:forEach>
                </datalist>
                
                <input type="hidden" name="teamId" id="hiddenTeamId">
                <div class="form-text text-teal mt-2"><i class="fas fa-info-circle"></i> All members from this team will be added automatically.</div>
            </div>

            <div id="sectionMember" class="d-none">
                <label class="form-label fw-bold">Add Individual Members:</label>
                
                <div id="members-container"></div>
                
                <button type="button" class="btn-dashed mt-2" onclick="addMemberRow()">
                    <i class="fas fa-plus-circle me-2"></i> Add Member
                </button>
            </div>

            <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                <a href="${pageContext.request.contextPath}/projects" class="btn btn-light border">Cancel</a>
                <button type="button" class="btn btn-teal px-4" onclick="validateAndSubmit()">Save Project</button>
            </div>
        </form>
    </div>
</div>

<template id="member-template">
    <div class="row align-items-center mb-2 member-row fade-in">
        <div class="col-md-7">
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
        <div class="col-md-4">
            <select class="form-select role-select" name="memberRoles">
                <option value="Member" selected>Member</option>
                <option value="Leader">Leader</option>
                <option value="Reviewer">Reviewer</option>
            </select>
        </div>
        <div class="col-md-1 text-end">
            <button type="button" class="btn btn-outline-danger border-0 rounded-circle" onclick="this.closest('.member-row').remove()">
                <i class="fas fa-times"></i>
            </button>
        </div>
    </div>
</template>

<script src="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/js/tom-select.complete.min.js"></script>

<script>
    // 1. CHUYỂN ĐỔI GIỮA TEAM VÀ MEMBER
    function toggleAssign(type) {
        if (type === 'team') {
            document.getElementById('sectionTeam').classList.remove('d-none');
            document.getElementById('sectionMember').classList.add('d-none');
        } else {
            document.getElementById('sectionTeam').classList.add('d-none');
            document.getElementById('sectionMember').classList.remove('d-none');
            
            // Nếu chưa có dòng member nào thì thêm 1 dòng mặc định
            if(document.getElementById("members-container").children.length === 0) {
                addMemberRow();
            }
        }
    }

    // 2. LOGIC CHỌN TEAM (Lấy ID từ Datalist)
    function updateTeamId(input) {
        var val = input.value;
        var list = document.getElementById('teamOptions').options;
        document.getElementById('hiddenTeamId').value = ""; // Reset
        for (var i = 0; i < list.length; i++) {
            if (list[i].value === val) {
                document.getElementById('hiddenTeamId').value = list[i].getAttribute('data-value');
                break;
            }
        }
    }

    // 3. LOGIC MEMBER (TOM SELECT - GIỐNG CREATE TEAM)
    const colors = ['#0d6efd', '#6610f2', '#6f42c1', '#d63384', '#dc3545', '#fd7e14', '#198754'];
    
    function getInitials(name) {
        if (!name) return "?";
        let cleanName = name.split('(')[0].trim();
        const parts = cleanName.split(" ");
        if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
        return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    function initTomSelect(element) {
        new TomSelect(element, {
            create: false,
            sortField: { field: "text", direction: "asc" },
            onChange: function(value) {
                let container = element.closest('.user-select-container');
                let avatar = container.querySelector('.avatar-circle');
                let text = "";
                if (element.options) {
                    for(let i=0; i<element.options.length; i++){
                         if(element.options[i].value == value) { text = element.options[i].text; break; }
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
        });
    }

    function addMemberRow() {
        const container = document.getElementById('members-container');
        const template = document.getElementById('member-template');
        const clone = template.content.cloneNode(true);
        const select = clone.querySelector('.tom-select-user');
        container.appendChild(clone);
        initTomSelect(select);
    }

    // 4. VALIDATE & SUBMIT
    function validateAndSubmit() {
        const errorAlert = document.getElementById('errorAlertJS');
        errorAlert.classList.add('d-none');
        
        // Kiểm tra xem đang ở chế độ nào
        const assignType = document.querySelector('input[name="assignType"]:checked').value;

        if (assignType === 'team') {
            // Validate Team
            const teamId = document.getElementById('hiddenTeamId').value;
            if (!teamId) {
                showError("Vui lòng chọn một Team hợp lệ từ danh sách!");
                return;
            }
        } else {
            // Validate Members
            const memberRows = document.querySelectorAll('.member-row');
            let selectedUsers = [];
            let leaderCount = 0;
            
            for (let row of memberRows) {
                const userSelect = row.querySelector('.tom-select-user');
                const roleSelect = row.querySelector('.role-select');
                
                if (userSelect && userSelect.value) {
                    if (selectedUsers.includes(userSelect.value)) {
                        showError("Thành viên bị trùng lặp!");
                        return;
                    }
                    selectedUsers.push(userSelect.value);
                    if (roleSelect.value === 'Leader') leaderCount++;
                }
            }
            if (leaderCount > 1) {
                showError("Chỉ được phép có 1 Leader!");
                return;
            }
        }

        // Submit Form
        document.getElementById('createForm').submit();
    }

    function showError(msg) {
        document.getElementById('errorMessage').innerText = msg;
        document.getElementById('errorAlertJS').classList.remove('d-none');
    }
</script>

</body>
</html>