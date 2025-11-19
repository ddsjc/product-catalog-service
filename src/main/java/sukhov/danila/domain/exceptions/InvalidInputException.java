package sukhov.danila.domain.exceptions;

public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String message){
        super(message);
    }
}
