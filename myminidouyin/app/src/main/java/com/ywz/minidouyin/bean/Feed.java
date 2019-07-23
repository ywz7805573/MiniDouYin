package com.ywz.minidouyin.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:18
 */
public class Feed {

    // endTODO-C2 (1) Implement your Feed Bean here according to the response json
    @SerializedName("student_id")private String studentID;
    @SerializedName("user_name")private String name;
    @SerializedName("image_url")private String imgUrl;
    @SerializedName("video_url")private String videoUrl;
    @SerializedName("createdAt")private String createAt;
    @SerializedName("updatedAt")private String updatedAt;

    @Override
    public String toString() {
        return studentID+"  "+name;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
