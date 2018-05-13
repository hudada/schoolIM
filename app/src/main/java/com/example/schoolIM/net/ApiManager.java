package com.example.schoolIM.net;

/**
 * Created by yezi on 2018/1/27.
 */

public class ApiManager {

    private static final String HTTP = "http://";
    private static final String IP = "192.168.55.103";
    private static final String PROT = ":8080";
    private static final String HOST = HTTP + IP + PROT;
    private static final String API = "/api";
    private static final String USER = "/user";
    private static final String REVIEW = "/review";
    private static final String REVIEWLIST = "/reviewlist";
    private static final String LIKE = "/like";
    private static final String FRIEND = "/friend";

    public static final String HEAD_PATH = HOST + API + USER;

    public static final String REGISTER = HOST + API + USER + "/register";
    public static final String LOGIN = HOST + API + USER + "/login";
    public static final String HEAD_CHANGE = HOST + API + USER + "/head";
    public static final String NIC_CHANGE = HOST + API + USER + "/change";
    public static final String USER_FIND = HOST + API + USER + "/find";

    public static final String REVIEW_LIST = HOST + API + REVIEW + "/list";
    public static final String REVIEW_ADD = HOST + API + REVIEW + "/add";

    public static final String REVIEWLIST_ADD = HOST + API + REVIEWLIST + "/add";

    public static final String LIKE_ADD = HOST + API + LIKE + "/add";

    public static final String FRIEND_ADD = HOST + API + FRIEND + "/add";
}
