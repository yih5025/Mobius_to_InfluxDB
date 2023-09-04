package example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileWriter;
import java.io.IOException;

class MobiusRestApiExampleNotJson {
    public static void main(String[] args) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://203.253.128.177:7579/Mobius/20191539/AIR?fu=1&ty=4&crb=20250817T190446&cra=20230815T190502&lim=10");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("X-M2M-RI", "12345");
            httpGet.setHeader("X-M2M-Origin", "SOrigin");

            StringBuilder jsonContent = new StringBuilder();
            jsonContent.append("["); // 시작하는 대괄호

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

                        // 응답 JSON 추가
                        jsonContent.append(responseContent).append(",");
                    }
                }

                // 마지막 콤마 제거하고 끝나는 대괄호 추가
                jsonContent.setLength(jsonContent.length() - 1);
                jsonContent.append("]");
            }

            // JSON 파일에 저장
            try (FileWriter fileWriter = new FileWriter("responses.json")) {
                fileWriter.write(jsonContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class MobiusRestApiToInfluxDBNotJson {
    private static char[] token = "DcKEp5NVq6hrMfrvjbEBkwW3TdqKtukNrnLoSwijEUxhN90454sSnHGubrOlm8ZJdiJDmb3tPSj5ZylOdIZ1cw==".toCharArray();
    private static String org = "Ubicomp";
    private static String bucket = "Ubicomp-Bucket";
    public static void main(String[] args) throws ParseException {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        String jsonFilePath = "/Users/ilhanyu/Documents/SCH/Lab/maven_ex/hello/responses.json"; // JSON 파일의 경로를 적절히 지정해주세요

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonArray jsonArray = JsonParser.parseString(content.toString()).getAsJsonArray();
        String[][] conDataArray = new String[jsonArray.size()][];

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            JsonObject cin = jsonObject.getAsJsonObject("m2m:cin");
            String conData = cin.get("con").getAsString();
            String[] conDataSplit = conData.split(",");

            if (conDataSplit.length == 12) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = inputFormat.parse(conDataSplit[0]);

                Long time = date.getTime();
                Double microDust_1 = Double.parseDouble(conDataSplit[1]);
                Double microDust_2 = Double.parseDouble(conDataSplit[2]);
                Double microDust_3 = Double.parseDouble(conDataSplit[3]);
                Double temperature = Double.parseDouble(conDataSplit[4]);
                Double humidity = Double.parseDouble(conDataSplit[5]);
                Double ozone = Double.parseDouble(conDataSplit[6]);
                Double sulfurDioxide = Double.parseDouble(conDataSplit[7]);
                Double carbonMonoxide = Double.parseDouble(conDataSplit[8]);
                Double carbonDioxide = Double.parseDouble(conDataSplit[9]);
                Double windDirection = Double.parseDouble(conDataSplit[10]);
                Double windSpeed = Double.parseDouble(conDataSplit[11]);

                // 변수 출력
                System.out.println("Time: " + time);
                System.out.println("Micro Dust 1: " + microDust_1);
                System.out.println("Micro Dust 2: " + microDust_2);
                System.out.println("Micro Dust 3: " + microDust_3);
                System.out.println("Temperature: " + temperature);
                System.out.println("Humidity: " + humidity);
                System.out.println("Ozone: " + ozone);
                System.out.println("Sulfur Dioxide: " + sulfurDioxide);
                System.out.println("Carbon Monoxide: " + carbonMonoxide);
                System.out.println("Carbon Dioxide: " + carbonDioxide);
                System.out.println("Wind Direction: " + windDirection);
                System.out.println("Wind Speed: " + windSpeed);
                System.out.println();

                Point point = Point
                        .measurement("MobiusData")
                        .addTag("sensor_id", "TLM0103")
                        .addField("time", time)
                        .addField("microDust_1", microDust_1)
                        .addField("microDust_2", microDust_2)
                        .addField("microDust_3", microDust_3)
                        .addField("temperature", temperature)
                        .addField("humidity", humidity)
                        .addField("ozone", ozone)
                        .addField("sulfurDioxide", sulfurDioxide)
                        .addField("carbonMonoxide", carbonMonoxide)
                        .addField("carbonDioxide", carbonDioxide)
                        .addField("windDirection", windDirection)
                        .addField("windSpeed", windSpeed)
                        .time(time, WritePrecision.MS);

                // InfluxDB에 데이터 저장
                writeApi.writePoint(bucket, org, point);
            }

            conDataArray[i] = conDataSplit;
        }
    }
}
