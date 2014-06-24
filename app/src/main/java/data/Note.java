package data;

import java.io.Serializable;

/**
 * Created by huanglizhuo on 14-5-6.
 */
public class Note implements Serializable{
    private int _id;
    private String content;
    private String createdtime;
    private String status;
    private String frequency;
    private String attribute;
    private String remindtime;
    private String updatedtime;

    public Note(){
        _id = -1;
        content = "content";
        createdtime = "createdtime";
        status = "status";
        frequency = "frequency";
        attribute = "attribute";
        remindtime = "remindtime";
        updatedtime = "updatedtime";
    }

    public int get_id(){
        return this._id;
    }

    public void set_id(int id){
        this._id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createdtime;
    }

    public void setCreatetime(String createtime) {
        this.createdtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getAttributes() {
        return attribute;
    }

    public void setAttributes(String attributes) {
        this.attribute = attributes;
    }

    public String getRemindtime() {
        return remindtime;
    }

    public void setRemindtime(String remindtime) {
        this.remindtime = remindtime;
    }

    public String getUpdatedtime() {
        return updatedtime;
    }

    public void setUpdatedtime(String updatedtime) {
        this.updatedtime = updatedtime;
    }

}
