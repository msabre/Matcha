<%--
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
    <title>Сброс пароля</title>
    <style>
        <%@ include file="/views/css/helloForm.css" %>
    </style>
</head>
<body>
<div class="form-wrap">
    <div class="profile"><img src="https://html5book.ru/wp-content/uploads/2016/10/profile-image.png">
        <h1>Сброс пароля</h1>
    </div>
    <form method="post" action="/resetpassend">
        <div>
            <h2>Введите Email пользователя к которому привязан аккаунт</h2>
        </div>
        <div>
            <label for="email">Email</label>
            <input type="text" name="email" required>
        </div>
        <button type="submit">Отправить</button>
    </form>
</div>

</body>
</html>