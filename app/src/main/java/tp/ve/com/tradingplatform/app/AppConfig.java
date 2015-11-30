package tp.ve.com.tradingplatform.app;

public class AppConfig {
    private static final String IP_ADD = "10.0.3.99";
    // Server user login url
    public static String URL_LOGIN = "http://" + IP_ADD + "/api/members/token.json";

    // Server user register url
    public static String URL_REGISTER = "http://" + IP_ADD + "/api/members/register";

    public static String URL_UPDATE_MEMBER = "http://" + IP_ADD + "/api/members/edit/";

    public static String TEST_TOKEN = "http://" + IP_ADD + "/api/members/view/";

    public static String SHARE_INDEX = "http://" + IP_ADD + "/api/shares/index.json";
    public static String SHARE_ADD = "http://" + IP_ADD + "/api/shares/add";

    public static final String IMAGE_DIRECTORY_NAME = "myUpload";

}
