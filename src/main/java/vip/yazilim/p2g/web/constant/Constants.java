package vip.yazilim.p2g.web.constant;

public class Constants {

    public static final String TABLE_PREFIX = "p2g_";
    public static final String RELATION_TABLE_PREFIX = "p2g_rel_";

    public static final String FORM_OPERATION_INSERT = "insert";
    public static final String FORM_OPERATION_UPDATE = "update";

    public static final String FORM_OPERATION_STUDENT_ENROLL = "enroll";
    public static final String FORM_OPERATION_STUDENT_LEAVE = "leave";

    public static final String BEAN_NAME_AUTHORIZATION_CODE = "authorizationCodeApi";

    public static final String BEAN_NAME_CLIENT_CREDENTIALS = "clientCredentialsApi";

    public static final String SCOPE = "user-read-recently-played,user-read-private,playlist-read-private" +
            ",user-library-modify,playlist-read-collaborative,playlist-modify-private,user-follow-modify" +
            ",user-read-currently-playing,user-read-email,user-library-read,user-top-read,playlist-modify-public" +
            ",user-follow-read,user-read-playback-state,user-modify-playback-state,app-remote-control,streaming";

}
