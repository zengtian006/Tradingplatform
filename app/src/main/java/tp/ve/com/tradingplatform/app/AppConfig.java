package tp.ve.com.tradingplatform.app;

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://10.0.3.91/api/members/token.json";

    // Server user register url
    public static String URL_REGISTER = "http://10.0.3.91/api/members/register";

    public static String URL_UPDATE_MEMBER = "http://10.0.3.91/api/members/edit/";

    public static String TEST_TOKEN = "http://10.0.3.91/api/members/view/";

    public static String SHARE_INDEX = "http://10.0.3.91/api/shares/index.json";
    public static String SHARE_ADD = "http://10.0.3.91/api/shares/add";

    public static final String IMAGE_DIRECTORY_NAME = "myUpload";

}
