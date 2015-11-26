package tp.ve.com.tradingplatform.entity;

/**
 * Created by Zeng on 2015/11/26.
 */
public class ShareContent {
    String sURL;
    String sTitle;
    String sImg_path;
    String sContent;
    String sDate;

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String getsURL() {
        return sURL;
    }

    public void setsURL(String sURL) {
        this.sURL = sURL;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    public String getsImg_path() {
        return sImg_path;
    }

    public void setsImg_path(String sImg_path) {
        this.sImg_path = sImg_path;
    }

    public String getsContent() {
        return sContent;
    }

    public void setsContent(String sContent) {
        this.sContent = sContent;
    }


}
