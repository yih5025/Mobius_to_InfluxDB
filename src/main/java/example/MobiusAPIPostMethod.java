package example;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class MobiusAPIPostMethod {
    public static void main(String[] args) throws Exception {
        String url = "http://203.253.128.177:7579/Mobius/20191539/Data";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        while (true) {
            // 현재 시간 가져오기
            Date currentTime = new Date();

            // 포맷에 맞게 현재 시간 문자열로 변환
            String formattedTime = dateFormat.format(currentTime);

            int microDust_1 = ThreadLocalRandom.current().nextInt(0, 101);
            int microDust_2 = ThreadLocalRandom.current().nextInt(0, 101);
            int microDust_3 = ThreadLocalRandom.current().nextInt(0, 101);
            int temperature = ThreadLocalRandom.current().nextInt(20, 31);
            int humidity = ThreadLocalRandom.current().nextInt(0, 101);
            int ozone = ThreadLocalRandom.current().nextInt(0, 51);
            int sulfurDioxide = ThreadLocalRandom.current().nextInt(0, 6);
            double carbonMonoxide = Math.round(ThreadLocalRandom.current().nextDouble(0.0, 0.1) * 10.0) / 10.0;
            double carbonDioxide = Math.round(ThreadLocalRandom.current().nextDouble(0.0, 0.01) * 10.0) / 10.0;
            int windDirection = (ThreadLocalRandom.current().nextInt(0, 361) * 100) / 360;
            double windSpeed = Math.round(ThreadLocalRandom.current().nextDouble(0.0, 10.0) * 10.0) / 10.0;

            // 요청 바디 생성
            String requestBody = "{\n    \"m2m:cin\": {\n        \"con\": \"" + formattedTime + ","
                    + microDust_1 + "," + microDust_2 + "," + microDust_3 + "," + temperature + ","
                    + humidity + "," + ozone + "," + sulfurDioxide + "," + carbonMonoxide + ","
                    + carbonDioxide + "," + windDirection + "," + windSpeed + "\"\n    }\n}";

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("X-M2M-RI", "12345");
                httpPost.setHeader("X-M2M-Origin", "SJwALr1knZv");
                httpPost.setHeader("Content-Type", "application/vnd.onem2m-res+json; ty=4");

                HttpEntity requestEntity = new StringEntity(requestBody);
                httpPost.setEntity(requestEntity);

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    HttpEntity responseEntity = response.getEntity();
                    if (responseEntity != null) {
                        String responseString = EntityUtils.toString(responseEntity);
                        System.out.println(responseString);
                    }
                }
            }

            // 1분 대기
            Thread.sleep(5000);
        }
    }
}
