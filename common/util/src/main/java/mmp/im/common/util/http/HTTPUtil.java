package mmp.im.common.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HTTPUtil {


    public static void main(String[] args) {

        String s = HTTPUtil.get("http://v.qq.com/x/cover/kvehb7okfxqstmc.html?vid=e01957zem6o", "");
        System.out.println(s);


        String sr = HTTPUtil.post("http://www.toutiao.com/stream/widget/local_weather/data/?city=%E4%B8%8A%E6%B5%B7", "");

        System.out.println(sr);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String get(String url, String param) {
        String result = "";

        String urlNameString = url + "?" + param;

        URLConnection connection = getConnection(urlNameString);
        if (connection == null) {
            return result;
        }
        try {
            // 建立实际的连接
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取所有响应头字段
        Map<String, List<String>> map = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : map.keySet()) {
            System.out.println(key + "--->" + map.get(key));
        }

        // 定义 BufferedReader输入流来读取URL的响应
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String param) {


        String result = "";

        // 打开和URL之间的连接
        URLConnection connection = getConnection(url);

        if (connection == null) {
            return result;
        }
        // 发送POST请求必须设置如下两行
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        // 定义BufferedReader输入流来读取URL的响应
        try (PrintWriter out = new PrintWriter(connection.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    private static URLConnection getConnection(String url) {
        URL realUrl;
        URLConnection connection = null;
        try {
            realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }


    private static String mapToParams(Map<String, String> map) {


        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = "";

            try {
                value = URLEncoder.encode(map.get(key), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
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
        return stringBuilder.toString();
    }


}
