<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Edit Project | FlowSheet</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            body {
                background-color: #f8f9fa;
                padding-bottom: 80px;
            }

            /* Style cho Avatar */
            .avatar-circle {
                width: 35px;
                height: 35px;
                background-color: #6c757d; /* Màu xám mặc định */
                color: white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: bold;
                font-size: 14px;
                margin-right: 10px;
                flex-shrink: 0; /* Không bị co méo */
            }

            /* Style cho bảng */
            .member-row td {
                vertical-align: middle;
            }

        </style>
    </head>
    <body>

        <nav class="navbar navbar-light bg-white shadow-sm mb-4">
            <div class="container">
                <span class="navbar-brand mb-0 h1">Edit Project: ${project.projectName}</span>
                <a href="${pageContext.request.contextPath}/project/details?id=${project.projectID}" class="btn btn-sm btn-outline-secondary">
                    <i class="fas fa-arrow-left"></i> Back
                </a>
            </div>
        </nav>

        <div class="container">
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i> <strong>Error:</strong> <c:out value="${requestScope.error}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/project/edit" method="POST" onsubmit="return validateForm()">
                <input type="hidden" name="id" value="${project.projectID}">
                <input type="hidden" name="deleted_members" id="deletedMembersInput" value="">

                <div class="row g-4"> 
                    <div class="col-lg-8">
                        <div class="card shadow-sm border-0">
                            <div class="card-header bg-white fw-bold py-3">General Information</div>
                            <div class="card-body p-4">
                                
                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label text-muted">Code</label>
                                    <div class="col-sm-9">
                                        <input type="text" class="form-control bg-light" value="${project.projectCode}" readonly>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label">Project Name</label>
                                    <div class="col-sm-9">
                                        <input type="text" name="name" class="form-control" value="${project.projectName}" required>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label">Timeline</label>
                                    <div class="col-sm-4">
                                        <input type="date" id="startDate" name="startDate" class="form-control" value="${project.startDate}">
                                        <div class="form-text">Start Date</div>
                                    </div>
                                    <div class="col-sm-5">
                                        <input type="date" id="deadline" name="deadline" class="form-control" value="${project.deadline}">
                                        <div class="form-text">Deadline</div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label">Status</label>
                                    <div class="col-sm-9">
                                        <select name="status" class="form-select">
                                            <option value="OPEN" ${project.status == 'OPEN' ? 'selected' : ''}>Open</option>
                                            <option value="IN_PROGRESS" ${project.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                            <option value="COMPLETE" ${project.status == 'COMPLETE' ? 'selected' : ''}>Complete</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label">Description</label>
                                    <div class="col-sm-9">
                                        <textarea name="description" class="form-control" rows="4">${project.description}</textarea>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div> 

                    <div class="col-lg-4">
                        <div class="card shadow-sm border-0 mb-3">
                            <div class="card-header bg-white fw-bold py-3 d-flex justify-content-between align-items-center">
                                <span>Team Members</span>
                                <span class="badge bg-light text-dark border" id="countBadge">${currentMembers.size()}</span>
                            </div>

                            <div class="p-3 bg-light border-bottom">
                                <div class="input-group">
                                    <input type="text" id="newMemberInput" class="form-control" list="userSuggestions" placeholder="Add user...">
                                    <datalist id="userSuggestions">
                                        <c:forEach var="u" items="${allUsers}">
                                            <option value="${u.username}">${u.fullName}</option>
                                        </c:forEach>
                                    </datalist>
                                    <button class="btn btn-success" type="button" onclick="addNewMember()">+</button>
                                </div>
                                <div id="addError" class="text-danger small mt-1" style="display:none;"></div>
                            </div>

                            <div class="table-responsive" style="max-height: 400px; overflow-y: auto;">
                                <table class="table table-hover mb-0" id="membersTable">
                                    <tbody style="border-top: none;">
                                        <c:if test="${empty currentMembers}">
                                            <tr><td colspan="3" class="text-center text-muted py-3">No members yet.</td></tr>
                                        </c:if>

                                        <c:forEach var="m" items="${currentMembers}">
                                            <tr class="member-row" id="row_${m.userId}">
                                                <td style="padding-left: 15px;">
                                                    <div class="d-flex align-items-center">
                                                        <div class="avatar-circle">
                                                            ${not empty m.username ? m.username.substring(0, 1).toUpperCase() : '?'}
                                                        </div>
                                                        <div style="line-height: 1.2;">
                                                            <div class="fw-bold text-dark">${m.username}</div>
                                                            <small class="text-muted" style="font-size: 11px;">${m.fullName}</small>
                                                            <input type="hidden" name="exist_member_ids[]" value="${m.userId}">
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <select name="exist_member_roles[]" class="form-select form-select-sm" style="font-size: 12px;">
                                                        <option value="Leader" ${m.roleInProject != null && m.roleInProject.trim().equalsIgnoreCase('Leader') ? 'selected' : ''}>Leader</option>
                                                        <option value="Member" ${m.roleInProject == null || m.roleInProject.trim().equalsIgnoreCase('Member') ? 'selected' : ''}>Member</option>
                                                    </select>
                                                </td>
                                                <td style="width: 30px;" class="text-end pe-3">
                                                    <button type="button" class="btn btn-link text-danger p-0" onclick="removeExistingMember(this, ${m.userId})">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary btn-lg fw-bold">SAVE CHANGES</button>
                            <a href="${pageContext.request.contextPath}/project/details?id=${project.projectID}" class="btn btn-outline-secondary">CANCEL</a>
                        </div>
                    </div>
                </div> 
            </form>
        </div>

        <script>
            // 2. JAVASCRIPT VALIDATION (QUAN TRỌNG ĐỂ KHÔNG MẤT DỮ LIỆU MEMBER)
            function validateForm() {
                var startStr = document.getElementById("startDate").value;
                var endStr = document.getElementById("deadline").value;

                if (startStr && endStr) {
                    var startDate = new Date(startStr);
                    var deadline = new Date(endStr);
                    if (startDate > deadline) {
                        alert("Start Date must be before Deadline!");
                        return false; // Chặn submit form
                    }
                }
                return true; // Cho phép submit
            }

            function addNewMember() {
                var input = document.getElementById('newMemberInput');
                var username = input.value.trim();
                var errorDiv = document.getElementById('addError');
                var dataList = document.getElementById('userSuggestions');

                errorDiv.style.display = 'none';
                if (username === "") return;

                // Check duplicate visually
                var isExist = false;
                document.querySelectorAll('#membersTable .fw-bold').forEach(el => {
                    if (el.innerText === username && el.closest('tr').style.display !== 'none')
                        isExist = true;
                });

                if (isExist) {
                    errorDiv.innerText = "Already added!";
                    errorDiv.style.display = 'block';
                    return;
                }

                // Get fullname
                var fullName = "New Member";
                for (var i = 0; i < dataList.options.length; i++) {
                    if (dataList.options[i].value === username) {
                        fullName = dataList.options[i].innerText;
                        break;
                    }
                }

                // Create Row
                var firstChar = username.charAt(0).toUpperCase();
                // 3. FIX BUG: Logic option Value và Text đã được sửa lại cho đúng
                var newRow = `
                    <tr class="member-row new-row" style="background-color: #e8f5e9;">
                        <td style="padding-left: 15px;">
                            <div class="d-flex align-items-center">
                                <div class="avatar-circle" style="background-color: #28a745;">` + firstChar + `</div>
                                <div style="line-height: 1.2;">
                                    <div class="fw-bold text-dark">` + username + `</div>
                                    <small class="text-success">New: ` + fullName + `</small>
                                    <input type="hidden" name="new_members[]" value="` + username + `">
                                </div>
                            </div>
                        </td>
                        <td>
                            <select name="new_roles[]" class="form-select form-select-sm" style="font-size: 12px;">
                                <option value="Member" selected>Member</option>
                                <option value="Leader">Leader</option>
                            </select>
                        </td>
                        <td class="text-end pe-3">
                            <button type="button" class="btn btn-link text-danger p-0" onclick="this.closest('tr').remove(); updateCount();">
                                <i class="fas fa-times"></i>
                            </button>
                        </td>
                    </tr>`;

                // Remove empty msg if exists
                var emptyMsg = document.querySelector('#membersTable td[colspan="3"]');
                if (emptyMsg) emptyMsg.closest('tr').remove();

                document.querySelector('#membersTable tbody').insertAdjacentHTML('beforeend', newRow);
                input.value = "";
                updateCount();
            }

            function removeExistingMember(btn, userId) {
                if (confirm('Remove this member?')) {
                    btn.closest('tr').style.display = 'none';
                    var inp = document.getElementById('deletedMembersInput');
                    // Logic ghép chuỗi ID: "1,2,5"
                    inp.value = inp.value ? inp.value + "," + userId : userId;
                    updateCount();
                }
            }

            function updateCount() {
                var count = 0;
                document.querySelectorAll('#membersTable tr').forEach(r => {
                    if (r.style.display !== 'none' && !r.querySelector('td[colspan]'))
                        count++;
                });
                document.getElementById('countBadge').innerText = count;
            }
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>