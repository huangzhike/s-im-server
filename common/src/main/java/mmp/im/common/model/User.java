package mmp.im.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

    private Long id;
    private String name;
    private String password;
    private String sign;
    private String token;

}
