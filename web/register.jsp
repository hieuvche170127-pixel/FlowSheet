<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - LAB Timesheet</title>
    
    <!-- Bootstrap 5.3 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
          rel="stylesheet" 
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" 
          crossorigin="anonymous">
    
    <!-- Font Awesome 6 -->
    <link rel="stylesheet" 
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" 
          integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" 
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    
    <style>
        #toast-container {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1055;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .toast-notification {
            min-width: 300px;
            max-width: 400px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.25);
            overflow: hidden;
            animation: slideInRight 0.4s ease-out forwards;
            opacity: 0;
            transform: translateX(100%);
        }

        .toast-notification.show {
            opacity: 1;
            transform: translateX(0);
        }

        .toast-header-custom {
            background: #d9534f;               /* đỏ Bootstrap danger */
            color: white;
            padding: 12px 16px;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .toast-header-custom i {
            font-size: 1.2rem;
        }

        .toast-body-custom {
            padding: 16px;
            color: #333;
            line-height: 1.5;
        }

        .toast-notification.success .toast-header-custom {
            background: #5cb85c;               /* xanh success */
        }

        @keyframes slideInRight {
            from { opacity: 0; transform: translateX(100%); }
            to   { opacity: 1; transform: translateX(0); }
        }

        @keyframes slideOutRight {
            from { opacity: 1; transform: translateX(0); }
            to   { opacity: 0; transform: translateX(100%); }
        }
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', sans-serif;
        }
        .register-card {
            border: none;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(0,0,0,0.25);
            max-width: 480px;
        }
        .card-header {
            background: rgba(255,255,255,0.95);
            border-bottom: none;
            padding: 2.5rem 1rem 2rem;
        }
        .btn-register {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 50px;
            padding: 14px;
            font-weight: 600;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
        }
        .btn-register:hover {
            transform: translateY(-3px);
            box-shadow: 0 12px 25px rgba(102, 126, 234, 0.45);
        }
        .form-floating > label {
            color: #555;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.25rem rgba(102, 126, 234, 0.25);
        }
        .student-note {
            background: rgba(102, 126, 234, 0.1);
            border-left: 4px solid #667eea;
            padding: 12px 16px;
            border-radius: 8px;
            font-size: 0.95rem;
        }
    </style>
</head>
<body class="d-flex align-items-center">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-5 col-md-7 col-sm-9 col-11">
                <div class="card register-card">
                    <div class="card-header text-center bg-transparent">
                        <i class="fas fa-user-plus fa-4x text-primary mb-3"></i>
                        <h3 class="mb-1 fw-bold text-dark">LAB Timesheet</h3>
                        <p class="text-muted mb-0">Create your account</p>
                    </div>
                    <div class="card-body p-4 p-xl-5">
                        
                        <form action="register" method="post">
                            <div class="form-floating mb-3">
                                <input type="text" 
                                       class="form-control" 
                                       id="fullName" 
                                       name="fullName" 
                                       placeholder="Full Name" 
                                       required 
                                       autofocus>
                                <label for="fullName"><i class="fas fa-user me-2"></i>Full Name</label>
                            </div>
                            
                            <div class="form-floating mb-3">
                                <input type="text" 
                                       class="form-control" 
                                       id="username" 
                                       name="username" 
                                       placeholder="Username" 
                                       required>
                                <label for="username"><i class="fas fa-at me-2"></i>Username</label>
                            </div>
                            
                            <div class="form-floating mb-3">
                                <input type="email" 
                                       class="form-control" 
                                       id="email" 
                                       name="email" 
                                       placeholder="Email" 
                                       required>
                                <label for="email"><i class="fas fa-envelope me-2"></i>Email Address</label>
                            </div>
                            
                            <div class="form-floating mb-3">
                                <input type="password" 
                                       class="form-control" 
                                       id="password" 
                                       name="password" 
                                       placeholder="Password" 
                                       required 
                                       minlength="4">
                                <label for="password"><i class="fas fa-lock me-2"></i>Password</label>
                            </div>
                            
                            <div class="form-floating mb-4">
                                <input type="password" 
                                       class="form-control" 
                                       id="confirmPassword" 
                                       name="confirmPassword" 
                                       placeholder="Confirm Password" 
                                       required 
                                       minlength="4">
                                <label for="confirmPassword"><i class="fas fa-key me-2"></i>Confirm Password</label>
                            </div>
                            
                            <button type="submit" class="btn btn-primary btn-register w-100">
                                <i class="fas fa-user-plus me-2"></i>Create Account
                            </button>
                        </form>
                        
                        <div class="text-center mt-4">
                            <p class="mb-0 text-muted">
                                Already have an account? 
                                <a href="/FlowSheet/login.jsp" class="text-decoration-none fw-semibold">Login here</a>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Toast Container -->
    <div id="toast-container" aria-live="polite" aria-atomic="true"></div>

    <script>
        // Function to display toast with safe message escaping
        function showToast(message, type = 'error', duration = 5000) {
            if (!message || message.trim() === '') return; // Skip if no message

            const container = document.getElementById('toast-container');

            const toast = document.createElement('div');
            toast.className = `toast-notification ${type}`;

            // Escape the message for safe HTML insertion and prevent JS breakage
            const escapedMessage = message
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#x27;')  // Escape single quotes
                .replace(/\n/g, '<br>')   // Handle newlines gracefully
                .replace(/\\/g, '\\\\');  // Escape backslashes

            toast.innerHTML = `
        <div class="toast-header-custom">
            <i class="fas ${type == 'success' ? 'fa-check-circle' : 'fa-exclamation-triangle'}"></i>
            <strong>${type == 'success' ? 'Thành công' : 'Lỗi'}</strong>
            <button type="button" class="btn-close btn-close-white ms-auto"
                    onclick="this.closest('.toast-notification').remove()"></button>
        </div>
        <div class="toast-body-custom">
            ${escapedMessage}
        </div>
    `;

            container.appendChild(toast);

            // Trigger show animation
            setTimeout(() => toast.classList.add('show'), 100);

            // Auto-hide after duration
            setTimeout(() => {
                toast.style.animation = 'slideOutRight 0.4s ease-out forwards';
                toast.addEventListener('animationend', () => toast.remove());
            }, duration);
        }

        // Display error if present, with server-side pre-escaping for single quotes
        <c:if test="${not empty error}">
        <c:set var="escapedError" value="${fn:replace(error, \"'\", \"\\\\'\")}" />  <!-- JSTL escape for single quotes -->
        showToast('${escapedError}', 'error', 6000);
        <c:remove var="error" scope="request"/>
        </c:if>
    </script>

    <div id="server-error" data-error="${not empty error ? error : ''}" style="display:none"></div>

    <script>
        // Đọc lỗi từ data attribute – 100% an toàn, không bao giờ bị cắt chuỗi
        const errorElement = document.getElementById('server-error');
        const serverError = errorElement.getAttribute('data-error').trim();

        if (serverError) {
            showToast(serverError, 'error', 7000);
        }
    </script>
    
    <!-- Bootstrap JS (for alert dismiss) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>