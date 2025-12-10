<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Create New Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        /* CSS tùy chỉnh cho giống mẫu */
        body { background-color: #f8f9fa; }
        .card { border: none; box-shadow: 0 0 15px rgba(0,0,0,0.05); }
        
        /* Màu xanh ngọc chủ đạo */
        .text-teal { color: #00bfa5; }
        .btn-teal { background-color: #00bfa5; color: white; border: none; }
        .btn-teal:hover { background-color: #009688; color: white; }
        
        /* Style cho tiêu đề các phần */
        .form-section-title {
            color: #333;
            font-weight: 600;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
            margin-bottom: 20px;
            margin-top: 20px;
        }

        /* Nút nét đứt (Dashed Button) theo yêu cầu */
        .btn-dashed {
            border: 2px dashed #00bfa5;
            color: #00bfa5;
            background-color: transparent;
            width: 100%;
            padding: 10px;
            font-weight: 600;
            transition: all 0.3s;
        }
        .btn-dashed:hover {
            background-color: #e0f2f1;
            cursor: pointer;
        }
    </style>
</head>
<body>

<div class="container py-4">
    
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/projects" class="text-decoration-none text-secondary">
            <i class="fas fa-arrow-left"></i> Back to all projects
        </a>
    </div>

    <div class="card p-4">
        <h4 class="mb-4 fw-bold">Create New Project</h4>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/project/create" method="post">
            
            <div class="form-section-title">
                <i class="fas fa-list-ul text-teal me-2"></i> General details
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label fw-bold">Project Name <span class="text-danger">*</span></label>
                    <input type="text" name="projectName" class="form-control" 
                           placeholder="Enter project name" value="${p.projectName}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-bold">Project Code</label>
                    <input type="text" name="projectCode" class="form-control" 
                           placeholder="e.g. PRJ001" value="${p.projectCode}" required>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label fw-bold">Start Date <span class="text-danger">*</span></label>
                    <input type="date" name="startDate" class="form-control" value="${p.startDate}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-bold">Deadline</label>
                    <input type="date" name="deadline" class="form-control" value="${p.deadline}">
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label fw-bold">Description</label>
                <textarea name="description" class="form-control" rows="3" 
                          placeholder="Project goals...">${p.description}</textarea>
            </div>

            <div class="form-section-title">
                <i class="fas fa-users text-teal me-2"></i> Add People & Team
            </div>

            <div class="mb-4 bg-light p-3 rounded">
                <label class="form-label fw-bold">Assign a whole Team</label>
                <select name="teamId" class="form-select">
                    <option value="0">-- Select Team (Optional) --</option>
                    <c:forEach items="${teams}" var="t">
                        <option value="${t.teamID}">${t.teamName}</option>
                    </c:forEach>
                </select>
                <div class="form-text">Selecting a team will assign the project to that group.</div>
            </div>

            <label class="form-label fw-bold">Add Individual Members</label>
            
            <div id="members-container">
                </div>

            <button type="button" class="btn btn-dashed mt-2" onclick="addMemberRow()">
                <i class="fas fa-plus-circle"></i> Add Member
            </button>

            <div class="d-flex justify-content-end gap-2 mt-5 pt-3 border-top">
                <a href="${pageContext.request.contextPath}/projects" class="btn btn-light border">Cancel</a>
                <button type="submit" class="btn btn-teal px-4">Save & Proceed</button>
            </div>

        </form>
    </div>
</div>

<div id="member-row-template" style="display: none;">
    <div class="input-group mb-2 member-row">
        <span class="input-group-text bg-white"><i class="fas fa-user text-muted"></i></span>
        
        <select name="memberIds" class="form-select">
            <option value="">-- Search/Select member by email --</option>
            <c:forEach items="${users}" var="u">
                <option value="${u.userId}">${u.email} - ${u.fullName}</option>
            </c:forEach>
        </select>
        
        <button type="button" class="btn btn-outline-danger" onclick="removeMemberRow(this)">
            <i class="fas fa-times"></i>
        </button>
    </div>
</div>

<script>
    /**
     * Hàm thêm dòng chọn thành viên mới
     */
    function addMemberRow() {
        // 1. Lấy nội dung từ template ẩn
        var template = document.getElementById("member-row-template").innerHTML;
        
        // 2. Tạo một div mới để chứa nó
        var newRow = document.createElement("div");
        newRow.innerHTML = template;
        
        // 3. Chèn vào container (ngay trên nút "Add Member")
        document.getElementById("members-container").appendChild(newRow);
    }

    /**
     * Hàm xóa dòng thành viên
     * @param btn Nút xóa vừa được bấm
     */
    function removeMemberRow(btn) {
        // Tìm thẻ cha (div.member-row) gần nhất và xóa nó
        // btn -> div.input-group -> remove()
        btn.closest(".member-row").remove(); // Cách viết hiện đại
        // Hoặc: btn.parentNode.remove();
    }
</script>

</body>
</html>