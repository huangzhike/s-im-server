package mmp.im.client.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import mmp.im.common.util.http.HTTPUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class AuthService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /*
     * 改成RPC是否更好
     * 其实RPC也是将调用的方法和参数序列化发出去，接收方反序列化再通过反射调用方法，将结果序列化传输，再反序列化
     * 也就是通信协议和网络传输，搞个注册中心和负载均衡，像Dubbo和ZooKeeper
     * 甚至简单点直接通过HTTP传输，连反射都省了
     * 不知道Java有没有类似Promise一样的东西，emmm
     * */

    public void asynGet() {

        HTTPUtil.asynGet("", null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    // Object object = JSON.parseObject(response.body().string(), Object.class);
                    Object object = JSON.parseObject(response.body().string(), new TypeReference<Object>() {
                    });
                } catch (Exception e) {
                    LOG.error("onResponse Exception... {}", e);
                }

            }
        });
    }


    public Object getToken(Object param) {

        return HTTPUtil.syncGet("", param);

    }

}
