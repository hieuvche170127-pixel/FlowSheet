<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Absence Request List</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            .status-PENDING {
                background-color: #ffc107;
                color: #000;
            }
            .status-ACCEPTED {
                background-color: #198754;
                color: #fff;
            }
            .status-REJECTED {
                background-color: #dc3545;
                color: #fff;
            }
            .status-WITHDRAWN {
                background-color: #6c757d;
                color: #fff;
            }
        </style>
    </head>
    <body class="bg-light">
        <jsp:include page="nghiapages/layout_header.jsp" />
        <div class="container bg-white p-4 shadow rounded">
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                    <i class="fas fa-exclamation-triangle"></i>

                    <c:choose>
                        <c:when test="${param.error == 'past_date'}">
                            <strong>Ngày không hợp lệ:</strong> Ngày bắt đầu phải tính từ hôm nay trở đi.
                        </c:when>
                        <c:when test="${param.error == 'invalid_range'}">
                            <strong>Khoảng thời gian sai:</strong> "Từ ngày" không được lớn hơn "Đến ngày".
                        </c:when>
                        <c:otherwise>
                            <strong>Lỗi hệ thống:</strong> Đã có lỗi xảy ra, vui lòng thử lại (${param.error}).
                        </c:otherwise>
                    </c:choose>

                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${not empty param.msg && param.msg == 'success'}">
                <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                    <i class="fas fa-check-circle"></i> Tạo yêu cầu nghỉ phép thành công!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3><i class="fas fa-calendar-minus text-primary"></i> Absence Requests</h3>

                <c:if test="${sessionScope.user.roleID == 1}">
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createRequestModal">
                        <i class="fas fa-plus"></i> Create New Request
                    </button>
                </c:if>

                <c:if test="${sessionScope.user.roleID == 2}">
                    <form action="${pageContext.request.contextPath}/request" method="get" class="d-flex align-items-center">
                        <label class="me-2 fw-bold">Filter:</label>
                        <select name="statusFilter" class="form-select form-select-sm" onchange="this.form.submit()">
                            <option value="PENDING" ${currentFilter == 'PENDING' ? 'selected' : ''}>Pending Only</option>
                            <option value="ALL" ${currentFilter == 'ALL' ? 'selected' : ''}>Show All</option>
                        </select>
                    </form>
                </c:if>
            </div>

            <table class="table table-bordered table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>No.</th>
                            <c:if test="${sessionScope.user.roleID == 2}">
                            <th>Requester</th>
                            </c:if>
                        <th>From Date</th>
                        <th>To Date</th>
                        <th>Days</th>
                        <th style="width: 30%">Reason</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty leaveList}">
                            <tr>
                                <td colspan="8" class="text-center text-muted">No requests found.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="req" items="${leaveList}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>

                                    <c:if test="${sessionScope.user.roleID == 2}">
                                        <td><strong>${req.requesterName}</strong></td>
                                    </c:if>

                                    <td>${req.fromDate}</td>
                                    <td>${req.toDate}</td>
                                    <td>${req.durationDays}</td>
                                    <td>${req.reason}</td>

                                    <td>
                                        <span class="badge status-${req.status}">
                                            ${req.status}
                                        </span>
                                    </td>

                                    <td>
                                        <c:if test="${sessionScope.user.roleID == 1 && req.status == 'PENDING'}">
                                            <a href="${pageContext.request.contextPath}/request/action?action=delete&id=${req.leaveId}" 
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('Withdraw this request?');">
                                                <i class="fas fa-trash"></i> Delete
                                            </a>
                                        </c:if>

                                        <c:if test="${sessionScope.user.roleID == 2 && req.status == 'PENDING'}">
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/request/action?action=approve&id=${req.leaveId}" 
                                                   class="btn btn-success" title="Approve">
                                                    <i class="fas fa-check"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/request/action?action=reject&id=${req.leaveId}" 
                                                   class="btn btn-danger" title="Reject">
                                                    <i class="fas fa-times"></i>
                                                </a>
                                            </div>
                                        </c:if>

                                        <c:if test="${req.status != 'PENDING'}">
                                            <span class="text-muted small">Locked</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <div class="modal fade" id="createRequestModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/request/create" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title">Create Absence Request</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label">From Date</label>
                                <input type="date" name="fromDate" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">To Date</label>
                                <input type="date" name="toDate" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Reason</label>
                                <textarea name="reason" class="form-control" rows="3" required placeholder="Reason..."></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="submit" class="btn btn-primary">Submit Request</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                   document.addEventListener("DOMContentLoaded", function () {
                                                       const urlParams = new URLSearchParams(window.location.search);
                                                       if (urlParams.has('error')) {
                                                           var myModal = new bootstrap.Modal(document.getElementById('createRequestModal'));
                                                           myModal.show();
                                                       }
                                                   });
        </script>
    </body>
</html>