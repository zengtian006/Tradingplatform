package tp.ve.com.tradingplatform.entity;

/**
 * Created by Zeng on 2015/11/13.
 */
public class Member {

    String member_id;
    String member_name;
    String member_email;
    String member_mobile;
    String member_gender;
    String member_language;

    public String getMember_gender() {
        return member_gender;
    }

    public void setMember_gender(String member_gender) {
        this.member_gender = member_gender;
    }

    public String getMember_language() {
        return member_language;
    }

    public void setMember_language(String member_language) {
        this.member_language = member_language;
    }


    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_email() {
        return member_email;
    }

    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public String getMember_mobile() {
        return member_mobile;
    }

    public void setMember_mobile(String member_mobile) {
        this.member_mobile = member_mobile;
    }


}
