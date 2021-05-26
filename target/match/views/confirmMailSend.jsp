<%@ page import="domain.entity.User" %>
  Created by IntelliJ IDEA.
  User: Андрей
  Date: 24.03.2021
  Time: 21:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Подтверждение</title>
    <style>
        <%@ include file="/views/css/helloForm.css" %>
    </style>
</head>
<body>
<div class="form-wrap">
    <div class="profile"><img src="https://html5book.ru/wp-content/uploads/2016/10/profile-image.png">
        <h1>Подтвердите аккаунт </h1>
    </div>
    <form method="post" action="/confirmAccount">
        <div>
            <h2>Письмо с подтверждением отправлено на ваш почтовый адрес:
            <%String email = (String) session.getAttribute("email");%>

            <%= email %>

            </h2>
            <h3><a href="/login">Авторизуйтесь после подтверждения</a></h3>
        </div>
    </form>
</div>

</body>
</html>