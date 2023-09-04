package example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MobiusRestApiExample {
    public static void main(String[] args) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://203.253.128.177:7579/Mobius/20191539/Data?fu=1&ty=4&lim=100");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("X-M2M-RI", "12345");
            httpGet.setHeader("X-M2M-Origin", "SOrigin");

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);

                // JSON 응답을 배열로 변환
                String[] urilArray = responseBody
                        .replace("{\"m2m:uril\":[\"", "")
                        .replace("\"]}", "")
                        .split("\",\"");

                // 각 요소에 대한 GET 요청
                for (String uril : urilArray) {
                    String url = "http://203.253.128.177:7579/" + uril;
                    HttpGet getRequest = new HttpGet(url);
                    getRequest.setHeader("Accept", "application/json");
                    getRequest.setHeader("X-M2M-RI", "12345");
                    getRequest.setHeader("X-M2M-Origin", "SOrigin");

                    try (CloseableHttpResponse getResponse = httpclient.execute(getRequest)) {
                        String responseContent = EntityUtils.toString(getResponse.getEntity());
                        System.out.println(responseContent);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
