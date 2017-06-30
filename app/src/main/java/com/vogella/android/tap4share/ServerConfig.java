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
    private String all_images = basicurl + "/api/newimages/";
    private String single_image_information_by_title = basicurl + "/api/image/";
    private String upload_file = basicurl + "/api/uploadfile/";
    private String random_image_data = basicurl + "/api/randomimagedata/";

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

    public String getAll_images() {
        return all_images;
    }

    public String getSingle_image_information_by_title() {
        return single_image_information_by_title;
    }

    public String getUpload_file() {
        return upload_file;
    }

    public String getRandom_image_data() {
        return random_image_data;
    }
}
