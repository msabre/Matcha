
<%@ page import="domain.entity.UserCard" %>
<%@ page import="domain.entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Изменить профиль</title>
</head>
<body>

    <form enctype="multipart/form-data" method="post" action="/main/profile">
        <div>Добавить фото</div>
            <div>
                <p><input type="file" name="photo1" multiple accept="image/*,image/jpeg"></p>
                <p><input type="file" name="photo2" multiple accept="image/*,image/jpeg"></p>
                <p><input type="file" name="photo3" multiple accept="image/*,image/jpeg"></p>
                <p><input type="file" name="photo4" multiple accept="image/*,image/jpeg"></p>
                <p><input type="file" name="photo5" multiple accept="image/*,image/jpeg"></p>
            </div>
        <%--</form>--%>
            <br>
            <br>

            <%
                User user = (User) session.getAttribute("user");
                if (user.getCard() == null)
                    user.setCard(new UserCard());

            %>

            <label for="gender">Гендер</label>
            <p><input type="text" name="gender" size="30" value=<%= user.getCard().getGender()%> ></p>

            <label for="sexual_preference">Ориентация</label>
            <p><input type="text" name="sexual_preference" size="30" value=<%= user.getCard().getSexual_preference()%> ></p>

            <label for="biography">О себе</label>
            <p><input type="text" name="biography" size="500" value=<%=user.getCard().getBiography()%> ></p>

            <label for="workPlace">Компания</label>
            <p><input type="text" name="workPlace" size="100" value=<%= user.getCard().getWorkPlace()%> ></p>

            <label for="position">Должность</label>
            <p><input type="text" name="position" size="70" value=<%= user.getCard().getPosition()%> ></p>

            <label for="education">Образование</label>
            <p><input type="text" name="education" size="120" value=<%= user.getCard().getEducation()%> ></p>

            <label for="location">Местоположение</label>
            <p><input type="text" name="location" size="120" value=<%= user.getLocation()%> ></p>

            <br>
            <br>

            <p>Отметьте до 5 интересов</p>
            <p><input type="checkbox" name="a1" value="Геймер(ша)"> Геймер(ша)</p>
            <p><input type="checkbox" name="a2" value="Спортсмен(ка)"> Спортсмен(ка)</p>
            <p><input type="checkbox" name="a3" value="Блогинг"> Блогинг</p>
            <p><input type="checkbox" name="a4" value="Мода"> Мода</p>
            <p><input type="checkbox" name="a5" value="Караоке"> Караоке</p>
            <p><input type="checkbox" name="a6" value="Пропустить по стаканчику"> Пропустить по стаканчику</p>
            <p><input type="checkbox" name="a7" value="Йога"> Йога</p>
            <p><input type="checkbox" name="a8" value="Гольф"> Гольф</p>
            <p><input type="checkbox" name="a9" value="Футбол"> Футбол</p>
            <p><input type="checkbox" name="a10" value="Баскетбол"> Баскетбол</p>
            <p><input type="checkbox" name="a11" value="Книги"> Книги</p>
            <p><input type="checkbox" name="a12" value="Природа"> Природа</p>
            <p><input type="checkbox" name="a13" value="Рисование"> Рисование</p>
            <p><input type="checkbox" name="a14" value="Аниме"> Аниме</p>

            <input type="submit" value="Сохранить">
        </div>
    </form>

</body>
</html>
