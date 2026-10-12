package com.humanin.planpaz.infra.exception;

public class WateringNotAllowedException extends RuntimeException {

    private final String errorCode;

    public WateringNotAllowedException(String message) {
        super(message);
        this.errorCode = "WATERING_TOO_EARLY";
    }

    public WateringNotAllowedException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
