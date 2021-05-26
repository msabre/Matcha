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
    <title>Ошибка</title>
    <style>
        <%@ include file="/views/css/helloForm.css" %>
    </style>
</head>
<body>
<form action="/interes">

    <p>Отметьте до 5 интересов</p>
    <p><input type="checkbox" name="a" value="Геймер(ша)"> 1417</p>
    <p><input type="checkbox" name="a" value="Спортсмен(ка)"> 1680</p>
    <p><input type="checkbox" name="a" value="Блогинг"> 1883</p>
    <p><input type="checkbox" name="a" value="Мода"> 1934</p>
    <p><input type="checkbox" name="a" value="Караоке"> 2010</p>
    <p><input type="checkbox" name="a" value="Пропустить по стаканчику"> 2010</p>
    <p><input type="checkbox" name="a" value="Йога"> 2010</p>
    <p><input type="checkbox" name="a" value="Гольф"> 2010</p>
    <p><input type="checkbox" name="a" value="Футбол"> 2010</p>
    <p><input type="checkbox" name="a" value="Баскетбол"> 2010</p>
    <p><input type="checkbox" name="a" value="Книги"> 2010</p>
    <p><input type="checkbox" name="a" value="Природа"> 2010</p>
    <p><input type="checkbox" name="a" value="Рисование"> 2010</p>
    <p><input type="checkbox" name="a" value="Аниме"> 2010</p>

    <p><input type="submit" value="Отправить"></p>
</form>
</body>
</html>