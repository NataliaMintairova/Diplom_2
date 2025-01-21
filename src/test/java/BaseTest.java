import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {
    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    final int HTTP_OK = 200;
    final int HTTP_BAD_REQUEST = 400;
    final int HTTP_UNAUTHORIZED = 401;
    final int HTTP_FORBIDDEN = 403;
    final int HTTP_INTERNAL_SERVER_ERROR = 500;


    public BaseTest(){
        RestAssured.baseURI = BASE_URI;
    }
}
