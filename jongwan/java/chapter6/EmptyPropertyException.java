package chapter6;

public class EmptyPropertyException extends RuntimeException {
    public EmptyPropertyException(String propertyName) {
        super(propertyName);
    }
}
