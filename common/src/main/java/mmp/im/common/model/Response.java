package mmp.im.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Response {
    private int code;
    private boolean success;
    private Object data;
    private int count;


}
