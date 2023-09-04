package example;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class MobiusAPIwithInfluxDB {

    private static char[] token = "DcKEp5NVq6hrMfrvjbEBkwW3TdqKtukNrnLoSwijEUxhN90454sSnHGubrOlm8ZJdiJDmb3tPSj5ZylOdIZ1cw==".toCharArray();
    private static String org = "Ubicomp";
    private static String bucket = "Ubicomp-Bucket";
    static int i = 0;
    static int j = 0;
    public static void main(String[] args) throws Exception, ParseException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            i = 00;
            j = 01;
            while(j < 24) {
                //런던 시간으로 계산해야 한다. 즉 -9시간을 빼야 한다. 그리고 최대 1000개까지만 get이 된다.
                HttpGet httpGet = new HttpGet("http://114.71.220.59:7579/Mobius/Ksensor_project/Data?fu=1&ty=4&cra=20230826T"+i+"0000&crb=20230826T"+j+"0001");
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("X-M2M-RI", "12345");
                httpGet.setHeader("X-M2M-Origin", "Sj2e_LoUMsR");

                try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println(responseBody);

                    JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray urilArray = json.getAsJsonArray("m2m:uril");

                    String[][] conDataArray = new String[urilArray.size()][];

                    // 각 요소에 대한 GET 요청
                    for (int i = 0; i < urilArray.size(); i++) {
                        String uril = urilArray.get(i).getAsString();
                        String url = "http://114.71.220.59:7579/" + uril;
                        HttpGet getRequest = new HttpGet(url);
                        getRequest.setHeader("Accept", "application/json");
                        getRequest.setHeader("X-M2M-RI", "12345");
                        getRequest.setHeader("X-M2M-Origin", "Sj2e_LoUMsR");

                        try (CloseableHttpResponse getResponse = httpclient.execute(getRequest)) {
                            String responseContent = EntityUtils.toString(getResponse.getEntity());
                            System.out.println(responseContent);

                            JsonObject responseJson = JsonParser.parseString(responseContent).getAsJsonObject();
                            String conData = responseJson.getAsJsonObject("m2m:cin").get("con").getAsString();

                            // 응답 문자열을 ","를 기준으로 분할하여 2차원 배열에 저장
                            String[] conDataSplit = conData.split(",");
                            conDataArray[i] = conDataSplit;

                        }
                    }

                    InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);
                    WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

                    for (String[] conDataSplit : conDataArray) {
                        if (conDataSplit.length >= 13) {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                            Date date = inputFormat.parse(conDataSplit[0]);

                            Long time = date.getTime();
                            Double microDust_1 = Double.parseDouble(conDataSplit[1]);
                            Double microDust_2 = Double.parseDouble(conDataSplit[2]);
                            Double microDust_3 = Double.parseDouble(conDataSplit[3]);
                            Double microDust_4 = Double.parseDouble(conDataSplit[4]);
                            Double temperature = Double.parseDouble(conDataSplit[5]);
                            Double humidity = Double.parseDouble(conDataSplit[6]);
                            Double ozone = Double.parseDouble(conDataSplit[7]);
                            Double sulfurDioxide = Double.parseDouble(conDataSplit[8]);
                            Double carbonMonoxide = Double.parseDouble(conDataSplit[9]);
                            Double carbonDioxide = Double.parseDouble(conDataSplit[10]);
                            Double windDirection = (Double.parseDouble(conDataSplit[11])* 100) / 360;
                            Double windSpeed = Double.parseDouble(conDataSplit[12]);

                            System.out.println("Time: " + time);
                            System.out.println("Micro Dust 1: " + microDust_1);
                            System.out.println("Micro Dust 2: " + microDust_2);
                            System.out.println("Micro Dust 3: " + microDust_3);
                            System.out.println("Micro Dust 4: " + microDust_4);
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
                                    .addField("microDust_4", microDust_4)
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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("value i: " + i + "value j: " + j);
                i++;
                j = 1 + 1;
            }
        }
    }
}
