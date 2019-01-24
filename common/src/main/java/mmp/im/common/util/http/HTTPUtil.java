package mmp.im.common.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HTTPUtil {


    private static final Logger LOG = LoggerFactory.getLogger(HTTPUtil.class);

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient okHttpClient = new OkHttpClient();


    private static Call get(String url, Object param) {

        LOG.warn("get url... {}", url);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(param);

        LOG.warn("get jsonObject... {}", jsonObject);

        String paramsString = objectToParams(jsonObject);

        LOG.warn("get param... {}", paramsString);

        final Request request = new Request.Builder().url(url + "?" + paramsString).get().build();

        return okHttpClient.newCall(request);
    }


    private static Call post(String url, Object param) {

        LOG.warn("post url... {}", url);

        String json = JSON.toJSONString(param);
        LOG.warn("post param... {}", json);

        // 请求body
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        LOG.warn("post body... {}", body);

        // 请求header
        Headers headers = new Headers.Builder().add("token", "this is the token").add("id", "this is the id").build();
        LOG.warn("post headers... {}", headers);

        // 请求创建
        final Request request = new Request.Builder().url(url).post(body).headers(headers).build();

        return okHttpClient.newCall(request);
    }


    public static void asynGet(String url, Object param, Callback callback) {
        get(url, param).enqueue(callback);
    }
    public static void asynPost(String url, Object param, Callback callback) {
        post(url, param).enqueue(callback);
    }

    public static Object syncGet(String url, Object param) {
        Object result = null;
        try {
            result = get(url, param).execute();
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return result;
    }
    public static Object syncPost(String url, Object param) {

        Object result = null;
        try {
            result = post(url, param).execute();
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return result;
    }


    private static String objectToParams(Map<String, Object> map) {

        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();

        LOG.warn("objectToParams keys... {}", keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = "";

            try {
                String s = (String) map.get(key);
                if (s != null) {
                    value = URLEncoder.encode(s, "UTF-8");
                }

            } catch (Exception e) {
                LOG.error("objectToParams Exception... {}", e);
            }

            if (i == keys.size() - 1) { // 拼接时，不包括最后一个&字符
                stringBuilder.append(key);
                stringBuilder.append("=");
                stringBuilder.append(value);
            } else {
                stringBuilder.append(key);
                stringBuilder.append("=");
                stringBuilder.append(value);
                stringBuilder.append("&");
            }
        }

        LOG.warn("objectToParams... {}", stringBuilder);
        return stringBuilder.toString();
    }


}
