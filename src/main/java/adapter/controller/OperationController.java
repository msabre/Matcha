package adapter.controller;

import config.MyConfiguration;
import usecase.*;

public class OperationController {
    private static OperationController instance;

    private CreateLink createLink;
    private CheckLink checkLink;
    private ConfirmLink confirmLink;

    private OperationController() {

    }

    public static OperationController getController() {
        if (instance == null) {
            instance = new OperationController();

            instance.createLink = MyConfiguration.createLink();
            instance.checkLink = MyConfiguration.checkLink();
            instance.confirmLink = MyConfiguration.confirmLink();
        }

        return instance;
    }

    public Integer addLink(String link) {
        return createLink.addLink(link);
    }

    public boolean isCorrectLink(Integer id, String url) {
        return checkLink.isRevelantLink(id, url);
    }

    public void confirmLink(Integer id) {
        confirmLink.markLink(id);
    }
}
