<%-- 
    Document   : LandingPage
    Created on : Dec 5, 2025, 1:21:46 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <!--import bootstrap-->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">

        <link rel="stylesheet" href="css_files/LandingPage.css"/>
    </head>


    <body>

        <div class="background part1">
            <div class="empty_div"></div>

            <div class="container header_container">
                <!--tạo 1 cái div rỗng đễ cách dòng :))-->

                <div class="header d-flex justify-content-between align-items-center">
                    <div class="logo_image_container d-flex justify-content-between align-items-center ">
                        <img src="https://th.bing.com/th/id/OIP.wpJ9IG8zdEhYPBsy8X9wYwHaHa?o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3" class="logo_image" alt="" />
                    </div>

                    <div>
                        <!--                        
                        đây là nút mà nếu ấn vào thì nó hiện cái form/ modal ra, nó có cái thuộc tính
                        ata-bs-target: sẽ là cái id của model, còn cái aria-labelledby thì cũng chưa rõ, nhưng cứ để nó đồng
                        nhất với cái  id trong modal header(chuyển xuống chỗ body rồi :)) ko biết nữa hehe)
                        -->

                        <button type="button" class="btn btn-light" data-bs-toggle="modal" data-bs-target="#SignInModal">Sign In</button>
                        <!-- Modal -->
                        <div class="modal fade" id="SignInModal" tabindex="-1" aria-labelledby="SignInModalLabel" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="container">
                                            <h1 class="modal-title fs-1 text-center" id="SignInModalLabel">Sign In</h1>
                                            <form action="action" method="">
                                                <p style="margin-bottom: 0px; margin-top: 10px"> Username: </p>
                                                <input type="text" name="username" placeholder="Type your email:" 
                                                       style="width: 100%; border-radius: 20px;padding: 10px">
                                                <br/>
                                                <p style="margin-bottom: 0px; margin-top: 10px"> Password: </p>
                                                <input type="password" name="password" placeholder="Type your password:" 
                                                       style="width: 100%;border-radius: 20px;padding: 10px"">
                                                <br>
                                                <div class="d-flex" style="justify-content: flex-end" >
                                                    <a href="" style=" text-decoration: none; text-align: end"> Forgot password?</a>
                                                </div>
                                                <br>
                                                <button type="submit" class="btn btn-dark" 
                                                        style="width: 100%; border-radius: 25px; padding: 10px">
                                                    Sign In 
                                                </button>
                                                <p style="text-align: center; margin-top: 100px " >have not account yet? </p>
                                                <button type="button" class="btn btn-white" data-bs-toggle="modal" data-bs-target="#SignUpModal"
                                                        style="width:100%; border-radius: 25px; padding: 10px; border: 1px solid black">
                                                    Sign Up
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>


                        <button type="button" class="btn btn-dark" data-bs-toggle="modal" data-bs-target="#SignUpModal">Sign Up</button>
                        <!-- Modal -->
                        <div class="modal fade" id="SignUpModal" tabindex="-1" aria-labelledby="SignUpModalLabel" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">

                                        <div class="container">
                                            <h1 class="modal-title fs-1 text-center" id="SignUpModalLabel">Sign Up</h1>
                                            <form action="action" method="">
                                                <p style="margin-bottom: 0px; margin-top: 10px"> Username: </p>
                                                <input type="text" name="username" placeholder="Enter your email:" 
                                                       style="width: 100%; border-radius: 20px;padding: 10px">
                                                <br/>
                                                <p style="margin-bottom: 0px; margin-top: 10px"> Password: </p>
                                                <input type="password" name="password" placeholder="Enter your password:" 
                                                       style="width: 100%;border-radius: 20px;padding: 10px"">
                                                <br>
                                                <p style="margin-bottom: 0px; margin-top: 10px"> Re-enter Password: </p>
                                                <input type="password" name="password" placeholder="Re-Enter your password:" 
                                                       style="width: 100%;border-radius: 20px;padding: 10px"">
                                                <br>
                                                <button type="submit" class="btn btn-dark"
                                                        style="width:100%; border-radius: 25px; padding: 10px; border: 1px solid black;
                                                        margin-top: 30px;">
                                                    Sign Up
                                                </button>

                                                <p style="text-align: center; margin-top: 100px " >already have account? </p>
                                                <button type="button" class="btn btn-white" data-bs-toggle="modal" data-bs-target="#SignInModal" 
                                                        style="width: 100%; border-radius: 25px; padding: 10px; border: 1px solid black">
                                                    Sign In 
                                                </button>

                                            </form>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>


                    </div>
                </div>
            </div>

            <div class="carousel_container container">
                <div>
                    <h1>Timesheet Feature</h1>
                </div>
                <div id="carouselExample" class="carousel slide">
                    <div class="carousel-inner">
                        <div class="carousel-item active">
                            <img src="https://img.freepik.com/premium-photo/night-sky-filled-with-lots-stars_662214-439284.jpg" class="d-block w-100 carousel_image" alt="...">
                        </div>
                        <div class="carousel-item">
                            <img src="https://i.pinimg.com/736x/73/82/29/738229f56d2dfb707f216f8560cd3da7.jpg" class="d-block w-100 carousel_image" alt="..." >
                        </div>
                        <div class="carousel-item">
                            <img src="https://cdn.mos.cms.futurecdn.net/9idmQtNfUtB7WZinQz5ngA-1024-80.jpg" class="d-block w-100 carousel_image" alt="..." >
                        </div>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#carouselExample" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
            </div>
            <div class="empty_div_height_100px">
            </div>
        </div>

        <div class="part2">
            <div class="carousel_container container">
                <div>
                    <h1>Sumarry Feature </h1>
                </div>
                <div id="carouselExample2" class="carousel slide">
                    <div class="carousel-inner">
                        <div class="carousel-item active">
                            <img src="https://img.freepik.com/premium-photo/night-sky-filled-with-lots-stars_662214-439284.jpg" class="d-block w-100 carousel_image" alt="...">
                        </div>
                        <div class="carousel-item">
                            <img src="https://i.pinimg.com/736x/73/82/29/738229f56d2dfb707f216f8560cd3da7.jpg" class="d-block w-100 carousel_image" alt="..." >
                        </div>
                        <div class="carousel-item">
                            <img src="https://cdn.mos.cms.futurecdn.net/9idmQtNfUtB7WZinQz5ngA-1024-80.jpg" class="d-block w-100 carousel_image" alt="..." >
                        </div>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample2" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#carouselExample2" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
            </div>
            <div class="empty_div_height_100px">
            </div>
        </div>

        <div class="part3">
            <div class="carousel_container container">
                <div>
                    <h1>Report Feature (For admin/teamlead)</h1>
                </div>
                <div id="carouselExample3" class="carousel slide">
                    <div class="carousel-inner">
                        <div class="carousel-item active">
                            <img src="https://img.freepik.com/premium-photo/night-sky-filled-with-lots-stars_662214-439284.jpg" class="d-block w-100 carousel_image" alt="...">
                        </div>
                        <div class="carousel-item">
                            <img src="https://i.pinimg.com/736x/73/82/29/738229f56d2dfb707f216f8560cd3da7.jpg" class="d-block w-100 carousel_image" alt="..." >
                        </div>
                        <div class="carousel-item">
                            <img src="https://cdn.mos.cms.futurecdn.net/9idmQtNfUtB7WZinQz5ngA-1024-80.jpg" class="d-block w-100 carousel_image" alt="..." >
                        </div>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample3" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#carouselExample3" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
            </div>
            <div class="empty_div_height_100px">
            </div>



            <div class="footer">
                <div class="container d-flex justify-content-between">
                    <div class="left">
                        <h1>Contact: </h1>
                        <p>Email:</p>
                        <p>Phone:</p>
                        <p>Facebook:</p>
                    </div> 
                    <div class="right">
                        <h1>Hello World!</h1>
                    </div> 
                </div>

                <p style="text-align: center">copyright @2025</p>

            </div>

        </div>



        <!--import bootstrap-->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>

    </body>
</html>
