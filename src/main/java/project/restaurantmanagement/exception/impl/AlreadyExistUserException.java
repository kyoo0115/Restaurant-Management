package project.restaurantmanagement.exception.impl;

import project.restaurantmanagement.exception.AbstractException;

public class AlreadyExistUserException extends AbstractException {

    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
