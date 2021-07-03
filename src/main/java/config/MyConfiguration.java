package config;

import adapter.controller.*;
import adapter.port.*;
import usecase.*;
import usecase.port.*;

public class MyConfiguration {

    public static UserController userController() {
        return UserController.getController();
    }

    public static CreateUser createUser() {
        return new CreateUser(userRepository(), passwordEncoder());
    }

    public static FindUser findUser() {
        return new FindUser(userRepository(), passwordEncoder());
    }

    public static LoginUser loginUser() {
        return new LoginUser(userRepository(), passwordEncoder());
    }

    public static ConfirmUser confirmUser() {
        return new ConfirmUser(userRepository());
    }

    public static PasswordUpdate passwordUpdate() {
        return new PasswordUpdate(userRepository());
    }

    public static UpdateUserCard updateUserCard() {
        return new UpdateUserCard(userCardRepository());
    }

    public static RecommendUsersList recommendUsersList() {
        return new RecommendUsersList(userRepository(), likesActionRepository());
    }

    public static PutLikeAction putLikeAction() {
        return new PutLikeAction(likesActionRepository(), userRepository());
    }


    public static OperationController operationController() {
        return OperationController.getController();
    }

    public static CreateLink createLink() {
        return new CreateLink(urlRepository());
    }

    public static CheckLink checkLink() {
        return new CheckLink(urlRepository());
    }

    public static ConfirmLink confirmLink() {
        return new ConfirmLink(urlRepository());
    }



    public static JwtController jwtController() {
        return JwtController.getController();
    }

    public static CreateTokenJWS createTokenJWS() {
        return new CreateTokenJWS(jwtRepository(), passwordEncoder());
    }

    public static VerifyTokenJWS verifyTokenJWS() {
        return new VerifyTokenJWS(passwordEncoder());
    }

    public static GetTokenId refreshTokenJWS() {
        return new GetTokenId(jwtRepository());
    }

    public static RemoveTokenJWS removeTokenJWS() {
        return new RemoveTokenJWS(jwtRepository());
    }



    public static UserRepository userRepository() {
        return UserRepositoryImpl.getRepository();
    }

    public static UserCardRepository userCardRepository() {
        return UserCardRepositoryImpl.getRepository();
    }

    public static UrlRepository urlRepository() {
        return UrlRepositoryImpl.getRepository();
    }

    public static JwtRepository jwtRepository() {
        return JwtRepositoryImpl.getRepository();
    }

    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderImpl.getEncoder();
    }

    public static FilterParamsRepository filterParamsRepository() {
        return FilterParamsRepositoryImpl.getRepository();
    }

    public static LikesActionRepository likesActionRepository() {
        return LikesActionRepositoryImpl.getRepository();
    }

    public static MessageRepository messageRepository() {
        return MessageRepositoryImpl.getRepository();
    }

}
