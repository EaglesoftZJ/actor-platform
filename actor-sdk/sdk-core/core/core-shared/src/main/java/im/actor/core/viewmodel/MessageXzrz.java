package im.actor.core.viewmodel;

import com.google.j2objc.annotations.ObjectiveCName;
import com.google.j2objc.annotations.Property;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class MessageXzrz implements Serializable {
    public MessageXzrz() {
    }

    @NotNull
    private long messageId;
    private long userId;
    private String userName;
    private String time;


    @ObjectiveCName("getMessageId")
    public long getMessageId() {
        return messageId;
    }

    @ObjectiveCName("setMessageId:")
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @ObjectiveCName("getUserId")
    public long getUserId() {
        return userId;
    }

    @ObjectiveCName("setUserId:")
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @ObjectiveCName("getUserName")
    public String getUserName() {
        return userName;
    }

    @ObjectiveCName("setUserName:")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ObjectiveCName("getTime")
    public String getTime() {
        return time;
    }

    @ObjectiveCName("setTime:")
    public void setTime(String time) {
        this.time = time;
    }
}
