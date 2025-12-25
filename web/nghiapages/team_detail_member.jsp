<%-- 
    Document   : team_detail_member
    Created on : Dec 14, 2025, 3:54:43 PM
    Author     : Admin
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chi tiết đội</title>
        <!-- Import Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- FontAwesome cho icon -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            /* CSS cho giao diện Tab */
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
            }
            .tab-controls {
                display: flex;
                border-bottom: 2px solid #ccc;
            }
            .tab-button {
                padding: 10px 20px;
                cursor: pointer;
                border: none;
                background-color: #f1f1f1;
                margin-right: 2px;
                border-radius: 5px 5px 0 0;
                transition: background-color 0.3s;
            }
            .tab-button:hover {
                background-color: #ddd;
            }
            .tab-button.active {
                background-color: #fff;
                border: 1px solid #ccc;
                border-bottom: none;
                font-weight: bold;
            }
            .tab-content {
                padding: 20px;
                border: 1px solid #ccc;
                border-top: none;
            }
            .content-item {
                display: none;
            }
            .content-item.active {
                display: block;
            }

            /* -- CSS Modal Tự làm (Custom) -- */
            /* ĐÃ SỬA: Đổi tên class để không xung đột với Bootstrap */

            .modal-overlay {
                display: none;
                position: fixed;
                z-index: 9999;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
            }

            /* Đổi tên class từ .modal-content thành .custom-invite-content */
            .custom-invite-content {
                background-color: #fefefe;
                margin: 5% auto; /* Chỉnh margin cho đẹp hơn */
                padding: 20px;
                border: 1px solid #888;
                width: 500px; /* Tăng độ rộng chút */
                border-radius: 8px;
                box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
                position: relative;
            }

            .close-modal {
                color: #aaa;
                position: absolute;
                top: 10px;
                right: 20px;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
            }
            .close-modal:hover {
                color: black;
            }
        </style>
    </head>

    <!-- Thêm layout header nếu có -->
    <%@ include file="layout_header.jsp" %>


    <h1>Chi Tiết Đội: ${team.teamName}</h1>
    <hr>

    <!-- Phần thông tin Team -->
    <c:choose>
        <c:when test="${not empty requestScope.team}">
            <table class="table table-bordered detail-table">
                <tbody>
                    <tr><th>ID Team</th><td>${team.teamID}</td></tr>
                    <tr><th>Tên Team</th><td><strong>${team.teamName}</strong></td></tr>
                    <tr><th>Mô tả</th><td>${team.description}</td></tr>
                    <tr><th>Người Tạo</th><td>${team.createdBy}</td></tr>
                    <tr><th>Ngày Tạo</th><td>${team.createdAt}</td></tr>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="alert alert-warning">Không tìm thấy thông tin Đội.</div>
        </c:otherwise>
    </c:choose>

    <h2>Giao diện Chuyển đổi Nội dung</h2>

    <div class="tab-controls">
        <button class="tab-button active" onclick="openContent(event, 'content1')">Thành viên (Team List)</button>
        <button class="tab-button" onclick="openContent(event, 'content2')">Lời mời (Invitations)</button>
    </div>

    <div class="tab-content">

        <!-- CONTENT 1: DANH SÁCH THÀNH VIÊN -->
        <div id="content1" class="content-item active">
            <c:if test="${not empty requestScope.teamMateList}">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>UserID</th>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="stt" value="0"/> 
                        <c:forEach var="member" items="${requestScope.teamMateList}">
                            <c:set var="stt" value="${stt + 1}"/> 
                            <tr>
                                <td>${stt}</td>
                                <td>${member.userID}</td>
                                <td>${member.username}</td>
                                <td>${member.fullName}</td>
                                <td>${member.email}</td>
                                
                                <!--                         
                                <td>
                                <c:forEach var="tm" items="${teamMemberList}">
                                    <c:if test="${tm.userId == member.userID}">
                                        <span class="badge bg-info text-dark">
                                        ${memberRoleNameByUserId[member.userID]}
                                    </span>
                                    </c:if>
                                </c:forEach>
                                </td>
                                -->
                                
                                <td>
                                    <span class="badge bg-info text-dark">
                                        ${memberRoleNameByUserId[member.userID]}
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${canManageTeam}">
                                        <!-- Change Role -->
                                        <form action="${pageContext.request.contextPath}/teamMember" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="changeRole"/>
                                            <input type="hidden" name="teamId" value="${team.teamID}"/>
                                            <input type="hidden" name="userId" value="${member.userID}"/>

                                            <select name="roleId" class="form-select form-select-sm d-inline-block" style="width:160px;">
                                                <option value="4" ${memberRoleIdByUserId[member.userID] == 4 ? 'selected' : ''}>Team Member</option>
                                                <option value="5" ${memberRoleIdByUserId[member.userID] == 5 ? 'selected' : ''}>Team Leader</option>
                                            </select>

                                            <button type="submit" class="btn btn-sm btn-primary">Change</button>
                                        </form>

                                        <!-- Kick -->
                                        <form action="${pageContext.request.contextPath}/TeamDetail?teamId?"
                                              method="post"
                                              class="d-inline"
                                              onsubmit="return confirm('Kick this member out of the team?');">
                                            <input type="hidden" name="action" value="kick"/>
                                            <input type="hidden" name="teamId" value="${team.teamID}"/>
                                            <input type="hidden" name="userId" value="${member.userID}"/>
                                            <button type="submit" class="btn btn-sm btn-danger">
                                                <i class="fa fa-user-times"></i>
                                            </button>
                                        </form>
                                    </c:if>

                                    <c:if test="${not canManageTeam}">
                                        <span class="text-muted">-</span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <button class="btn btn-success" type="button" onclick="document.getElementById('inviteModal').style.display = 'block'">
                <i class="fa fa-plus"></i> Mời thành viên vào nhóm
            </button>

            <!-- MODAL MỜI THÀNH VIÊN (CUSTOM CSS) -->
            <div id="inviteModal" class="modal-overlay">
                <!-- Sửa class ở đây để khớp với CSS mới -->
                <div class="custom-invite-content">
                    <span class="close-modal" onclick="document.getElementById('inviteModal').style.display = 'none'">&times;</span>
                    <h3>Mời thành viên mới</h3>
                    <hr>
                    <form action="${pageContext.request.contextPath}/Invitation" method="GET">
                        <!-- Các input form giữ nguyên -->
                        <div class="mb-3">
                            <label>Mời vào Team:</label>
                            <input type="text" class="form-control" value="${team.teamName}" readonly>
                        </div>
                        <input type="hidden" name="teamId" value="${team.teamID}">

                        <div class="mb-3">
                            <label>Email người nhận:</label>
                            <input type="email" class="form-control" name="email" required placeholder="Nhập email...">
                        </div>

                        <div class="mb-3">
                            <label>Vai trò:</label>
                            <select name="roleName" class="form-select">
                                <option value="TeamMember">Team member</option>
                                <option value="TeamLead">Team lead</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label>Hết hạn vào:</label>
                            <input type="datetime-local" class="form-control" name="expiresAt" required>
                        </div>

                        <input type="hidden" name="invitedBy" value="${sessionScope.user.userID}">
                        <input type="hidden" name="action" value="add"/>

                        <div class="text-end">
                            <button type="button" class="btn btn-secondary" onclick="document.getElementById('inviteModal').style.display = 'none'">Hủy</button>
                            <button type="submit" class="btn btn-primary">Gửi lời mời</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- CONTENT 2: QUẢN LÝ LỜI MỜI -->
        <div id="content2" class="content-item">
            <div class="card mt-4">
                <div class="card-header"><h4>Lời mời đã gửi (Sent Invitations)</h4></div>
                <div class="card-body">
                    <c:if test="${empty invitationSendByTeam}">
                        <p class="text-muted">Chưa có lời mời nào được gửi đi.</p>
                    </c:if>
                    <c:if test="${not empty invitationSendByTeam}">
                        <table class="table table-bordered table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Invited By</th>
                                    <th>Status</th>
                                    <th>Sent At</th>
                                    <th>Expires At</th>
                                    <th>Accepted At</th>
                                    <th style="width: 150px;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="inv" items="${invitationSendByTeam}">
                                    <tr>
                                        <td>${inv.email}</td>
                                        <td>
                                            <c:forEach var="role" items="${allRole}">
                                                <c:if test="${role.roleId == inv.roleId}">${role.roleName}</c:if>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <!-- Logic hiển thị tên người mời -->
                                            <c:set var="inviterName" value="ID: ${inv.invitedById}" />
                                            <c:forEach var="member" items="${teamMateList}">
                                                <c:if test="${member.userID == inv.invitedById}">
                                                    <c:set var="inviterName" value="${member.fullName}" /> 
                                                </c:if>
                                            </c:forEach>
                                            ${inviterName}
                                        </td>
                                        <td>
                                            <span class="">
                                                ${inv.status}
                                            </span>
                                        </td>
                                        <td>${inv.createdAt}</td>
                                        <td>${inv.expiresAt}</td>
                                        <td>${inv.acceptedAt != null ? inv.acceptedAt : '-'}</td>

                                        <!-- CỘT ACTION -->
                                        <td>
                                            <c:set var="isOwner" value="${sessionScope.user.userID == inv.invitedById}" />

                                            <!-- NÚT EDIT: Đã kiểm tra kỹ các attribute data- -->
                                            <button class="btn btn-sm btn-primary" 
                                                    type="button"
                                                    title="Chỉnh sửa"
                                                    ${(isOwner && inv.status == 'PENDING') ? '' : 'disabled'}
                                                    onclick="openEditModal(this)" 
                                                    data-id="${inv.invitationId}"
                                                    data-invitedby="${inv.invitedById}"
                                                    data-email="${inv.email}"
                                                    data-role="${inv.roleId}"
                                                    data-expires="${inv.expiresAt}" >
                                                <i class="fa fa-edit"></i>
                                            </button>

                                            <!--nút xóa-->
                                            <button class="btn btn-sm btn-danger" 
                                                    onclick="revokeInvitation(${inv.invitationId})"
                                                    ${isOwner ? '' : 'disabled'}>
                                                <i class="fa fa-trash"></i>
                                            </button>

                                            <!-- Nút mở Modal Status -->
<!--                                            <button class="btn btn-sm btn-info" 
                                                    type="button"
                                                    onclick="openStatusModal(${inv.invitationId})"
                                                    title="Đổi trạng thái">
                                                <i class="fa fa-refresh"></i> Status
                                            </button>-->

                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- MODAL EDIT INVITATION (Bootstrap Modal) -->
    <div class="modal fade" id="editInvitationModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content"> <!-- Class này của Bootstrap, ko được đổi -->
                <form action="${pageContext.request.contextPath}/Invitation" method="GET">
                    <div class="modal-header">
                        <h5 class="modal-title">Chỉnh sửa lời mời</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <div class="modal-body">
                        <!-- CÁC TRƯỜNG HIDDEN -->
                        <input type="hidden" id="modalInvId" name="invitationId">

                        <!-- QUAN TRỌNG: Đã thêm field này để JS không bị lỗi null -->
                        <input type="hidden" id="modalInvitedById" name="invitedBy">

                        <input type="hidden" name="action" value="edit">

                        <div class="mb-3">
                            <label class="form-label">Team Name</label>
                            <input type="text" class="form-control" value="${team.teamName}" readonly style="background-color: #e9ecef;">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Email người nhận</label>
                            <input type="email" class="form-control" id="modalEmail" name="email" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Vai trò (Role)</label>
                            <select class="form-select" id="modalRole" name="roleId">
                                <c:forEach var="role" items="${allRole}">
                                    <%-- Chỉ hiển thị nếu ID là 4 hoặc 5 --%>
                                    <c:if test="${role.roleId == 4 || role.roleId == 5}">
                                        <option value="${role.roleId}" 
                                                ${role.roleId == invitation.roleId ? 'selected' : ''}>
                                            ${role.roleName}
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Hết hạn vào (Expires At)</label>
                            <input type="datetime-local" class="form-control" id="modalExpiresAt" name="expiresAt">
                            <small class="text-muted">Giá trị cũ: <span id="currentExpiresDisplay"></span></small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Trạng thái (Status)</label>
                            <!-- Dùng form-select cho đẹp nếu xài Bootstrap 5, hoặc form-control nếu Bootstrap 4 -->
                            <select class="form-select" id="modalStatus" name="status">
                                <option value="PENDING">PENDING</option>
                                <option value="CANCELLED">CANCELLED</option>
                            </select>
                            <small class="text-muted">Hiện tại: <span id="currentStatusDisplay"></span></small>
                        </div>

                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Form ẩn dùng chung để Revoke/Delete -->
    <form id="formRevokeInvitation" action="${pageContext.request.contextPath}/Invitation" method="Get" style="display: none;">
        <!-- Tham số để Controller biết đang gọi case nào (vd: delete, revoke) -->
        <input type="hidden" name="action" value="delete"> 
        <!-- Tham số chứa ID cần xóa, sẽ được JS điền vào -->
        <input type="hidden" name="invitationId" id="inputRevokeId"> 
    </form>

<!--     Modal Change Status 
    <div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="statusModalLabel">Cập nhật trạng thái</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                 FORM GỬI VỀ SERVLET 
                <form action="${pageContext.request.contextPath}/Invitation" method="GET">  Nhớ đổi MainController thành tên Servlet của bạn 
                    <div class="modal-body">
                         Input ẩn chứa Service 
                        <input type="hidden" name="action" value="updateStatus">

                         Input ẩn chứa ID (sẽ được JS điền vào) 
                        <input type="hidden" name="invitationId" id="modalInvId">

                        <p>Chọn trạng thái mới cho lời mời này:</p>

                         2 OPTION: PENDING và CANCELLED 
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="newstatus" id="optPending" value="PENDING" checked>
                                <label class="form-check-label" for="optPending">
                                    <span class="badge bg-warning text-dark">PENDING</span> (Chờ xử lý / Mời lại)
                                </label>
                            </div>
                            <div class="form-check mt-2">
                                <input class="form-check-input" type="radio" name="newstatus" id="optCancelled" value="CANCELLED">
                                <label class="form-check-label" for="optCancelled">
                                    <span class="badge bg-secondary">CANCELLED</span> (Hủy lời mời)
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </div>
    </div>-->

    <!-- SCRIPT XỬ LÝ -->
    <script>
        // Tab logic
        function openContent(evt, contentId) {
            let i, contentItems, tabButtons;
            contentItems = document.getElementsByClassName("content-item");
            for (i = 0; i < contentItems.length; i++) {
                contentItems[i].classList.remove("active");
            }
            tabButtons = document.getElementsByClassName("tab-button");
            for (i = 0; i < tabButtons.length; i++) {
                tabButtons[i].classList.remove("active");
            }
            document.getElementById(contentId).classList.add("active");
            evt.currentTarget.classList.add("active");
        }

        // Logic đóng mở Custom Modal (Mời thành viên)
        window.onclick = function (event) {
            var modal = document.getElementById('inviteModal');
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }

        // --- HÀM XỬ LÝ MODAL EDIT (Đã sửa lỗi) ---
        function openEditModal(button) {
            // 1. Lấy dữ liệu an toàn
            var id = button.getAttribute("data-id") || "";
            var invitedBy = button.getAttribute("data-invitedby") || "";
            var email = button.getAttribute("data-email") || "";
            var roleId = button.getAttribute("data-role") || "";
            var expiresRaw = button.getAttribute("data-expires") || "";

            // 2. Gán dữ liệu vào form (Đảm bảo ID tồn tại trong HTML)
            if (document.getElementById("modalInvId"))
                document.getElementById("modalInvId").value = id;

            if (document.getElementById("modalInvitedById"))
                document.getElementById("modalInvitedById").value = invitedBy;

            if (document.getElementById("modalEmail"))
                document.getElementById("modalEmail").value = email;

            if (document.getElementById("modalRole"))
                document.getElementById("modalRole").value = roleId;

            // 3. Xử lý Date Time
            var dateInput = document.getElementById("modalExpiresAt");
            var displaySpan = document.getElementById("currentExpiresDisplay");

            if (displaySpan)
                displaySpan.innerText = expiresRaw;

            if (expiresRaw) {
                // Xử lý chuỗi ngày tháng: Thay khoảng trắng thành 'T' để hợp lệ với input type="datetime-local"
                // Định dạng input cần: YYYY-MM-DDTHH:mm
                var formattedDate = expiresRaw.replace(" ", "T");

                // Cắt bỏ phần giây nếu có (độ dài > 16)
                if (formattedDate.length > 16) {
                    formattedDate = formattedDate.substring(0, 16);
                }
                if (dateInput)
                    dateInput.value = formattedDate;
            } else {
                if (dateInput)
                    dateInput.value = "";
            }

            // 4. Gọi Bootstrap Modal
            var modalEl = document.getElementById('editInvitationModal');
            if (modalEl) {
                var myModal = new bootstrap.Modal(modalEl);
                myModal.show();
            } else {
                console.error("Không tìm thấy modal editInvitationModal");
            }
        }

        // 1. Hiển thị trạng thái cũ (để user biết)
        $('#currentStatusDisplay').text(data.status);

        // 2. Set giá trị cho select box
        $('#modalStatus').val(data.status);

        // 3. Xử lý logic: Chỉ cho sửa nếu đang là PENDING
        if (data.status === 'PENDING') {
            // Nếu là PENDING thì cho phép sửa (để chuyển sang CANCELLED)
            $('#modalStatus').prop('disabled', false);

            // Tùy chọn: Nếu anh muốn CHẮC CHẮN chỉ có 2 option này thì giữ nguyên HTML.
            // Nếu data.status đang là 'APPROVED' thì code ở block else sẽ chạy.
        } else {
            // Nếu KHÔNG PHẢI là PENDING (ví dụ: đã CANCELLED rồi hoặc APPROVED)
            // Thì khóa lại, không cho sửa nữa.
            $('#modalStatus').prop('disabled', true);
        }

        function revokeInvitation(id) {
            // 1. Hỏi xác nhận trước khi xóa (Optional nhưng nên có)
            if (confirm("Bạn có chắc chắn muốn thu hồi lời mời này không?")) {

                // 2. Gán ID vào thẻ input ẩn trong form
                document.getElementById('inputRevokeId').value = id;

                // 3. Submit form
                document.getElementById('formRevokeInvitation').submit();
            }
        }

// comment tạm, chắc để CRUD đơn giản thôi.
//        function openStatusModal(id) {
//            // 1. Gán ID vào thẻ input ẩn trong modal
//            document.getElementById('modalInvId').value = id;
//
//            // 2. Mở Modal (Code này dùng cho Bootstrap 5)
//            var myModal = new bootstrap.Modal(document.getElementById('statusModal'));
//            myModal.show();
//
//            // LƯU Ý: Nếu bạn dùng Bootstrap 4 thì dùng dòng dưới này thay thế:
//            // $('#statusModal').modal('show');
//        }
    </script>

    <!-- Import Bootstrap JS Bundle (Bắt buộc phải có) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <%@ include file="layout_footer.jsp" %>
</html>