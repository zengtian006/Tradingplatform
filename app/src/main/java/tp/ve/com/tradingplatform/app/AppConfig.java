package tp.ve.com.tradingplatform.app;

public class AppConfig {
    private static final String IP_ADD = "10.0.3.99";
    // Server user login url
    public static String URL_LOGIN = "http://" + IP_ADD + "/api/members/token.json";

    // Server user register url
    public static String URL_REGISTER = "http://" + IP_ADD + "/api/members/register";

    public static String URL_UPDATE_MEMBER = "http://" + IP_ADD + "/api/members/edit/";
    public static String URL_UPLOAD_IMG = "http://" + IP_ADD + "/api/members/uploadImg";

    public static String TEST_TOKEN = "http://" + IP_ADD + "/api/members/view/";

    public static String SHARE_INDEX = "http://" + IP_ADD + "/api/shares/index/";
    public static String SHARE_ADD = "http://" + IP_ADD + "/api/shares/add";
    public static String SHARE_DEL = "http://" + IP_ADD + "/api/shares/delete/";
    public static String SHARE_UPDATE = "http://" + IP_ADD + "/api/shares/edit/";

    public static final String IMAGE_DIRECTORY_NAME = "myUpload";

}
