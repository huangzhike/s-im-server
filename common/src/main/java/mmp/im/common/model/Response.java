package mmp.im.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Response {
    private int code;
    private boolean success;
    private String msg;
    private Object data;
    private int count;


}
