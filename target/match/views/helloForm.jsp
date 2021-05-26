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
    <title>Регистрация</title>
    <style>
        <%@ include file="/views/css/helloForm.css" %>
    </style>
</head>
<body>
<div class="form-wrap">
    <div class="profile"><img src="https://html5book.ru/wp-content/uploads/2016/10/profile-image.png">
        <h1>Регистрация</h1>
    </div>
    <form method="post" action="/registration">
        <div>
            <label for="fio">ФИО</label>
            <input type="text" name="fio" required>
        </div>
        <div class="radio">
            <span>Пол</span>
            <label>
                <input type="radio" name="sex" value="мужской">мужской
                <div class="radio-control male"></div>
            </label>
            <label>
                <input type="radio" name="sex" value="женский">женский
                <div class="radio-control female"></div>
            </label>
        </div>
        <div>
            <label for="email">E-mail</label>
            <input type="email" name="email" required>
        </div>
        <div>
            <label for="password">Пароль</label>
            <input type="password" name="password" required>
        </div>
        <div>
            <label for="country">Страна</label>
            <select name="country">
                <option>Выберите страну проживания</option>
                <option value="Россия">Россия</option>
                <option value="Украина">Украина</option>
                <option value="Беларусь">Беларусь</option>
            </select>
            <div class="select-arrow"></div>
        </div>
        <button type="submit">Отправить</button>
    </form>
</div>

</body>
</html>