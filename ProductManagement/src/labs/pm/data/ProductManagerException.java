package labs.pm.data;

/**
 * @author hatzp
 **/
public class ProductManagerException extends Exception{
    public ProductManagerException() {
        super();
    }

    public ProductManagerException(String message) {
        super(message);
    }

    public ProductManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
