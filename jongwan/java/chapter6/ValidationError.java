package chapter6;

public class ValidationError {
    private String message;
    private String type;

    public ValidationError(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public static ValidationError of(String message, String type) {
        return new ValidationError(message,type);
    }
}
