package domain.entity.model;

import java.util.List;

public class ProcessResult<T> {
    private String operationName;
    private List<T> objectsList;
    private ProcessResultType ProcessResultType;
    private String serviceComment;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public List<T> getObjectsList() {
        return objectsList;
    }

    public void setObjectsList(List<T> objectsList) {
        this.objectsList = objectsList;
    }

    public domain.entity.model.ProcessResultType getProcessResultType() {
        return ProcessResultType;
    }

    public void setProcessResultType(domain.entity.model.ProcessResultType processResultType) {
        ProcessResultType = processResultType;
    }

    public String getServiceComment() {
        return serviceComment;
    }

    public void setServiceComment(String serviceComment) {
        this.serviceComment = serviceComment;
    }
}

enum ProcessResultType {
    COMPLETE_SUCCESS("Завершено успешно"),
    COMPLETE_ERROR("Завершено с ошибкой"),
    ERROR_JWT("Ошибка токена авторизации"),
    INVALID_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    FILE_NOT_FOUND("Файл не найден"),
    COMPLETE_FILE_DELETE("Файл удален"),
    COMPLETE_SAVE("Файл сохранен");

    String value;

    ProcessResultType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static ProcessResultType fromStr(String value) {
        for (ProcessResultType processResultType : ProcessResultType.values()) {
            if (processResultType.getValue().equals(value))
                return processResultType;
        }
        return null;
    }
}
