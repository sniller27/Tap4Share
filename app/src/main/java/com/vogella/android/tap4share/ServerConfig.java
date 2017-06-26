package com.vogella.android.tap4share;

/**
 * Created by jonas on 6/21/17.
 */

public class ServerConfig {
    private String connectiontype = "http://";
    private String serverip = "134.103.176.119";
    private String serverport = "8080";
    private String basicurl = connectiontype + serverip + ":" + serverport;

    //URL endpoints
    private String single_image_by_name = basicurl + "/api/imagefile?name=";

    //GETTERS
    public String getServerip() {
        return serverip;
    }

    public String getServerport() {
        return serverport;
    }

    public String getSingle_image_by_name() {
        return single_image_by_name;
    }

    public String getConnectiontype() {
        return connectiontype;
    }
}
