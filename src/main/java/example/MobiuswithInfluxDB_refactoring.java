package example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MobiuswithInfluxDB_refactoring {

    private static char[] token = "DcKEp5NVq6hrMfrvjbEBkwW3TdqKtukNrnLoSwijEUxhN90454sSnHGubrOlm8ZJdiJDmb3tPSj5ZylOdIZ1cw==".toCharArray();
    private static String org = "Ubicomp";
    private static String bucket = "Ubicomp-Bucket";

    public static void main(String[] args) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.AUGUST, 31); // 지정한 날짜

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        while (calendar.get(Calendar.HOUR_OF_DAY) < 24) {
            String startTime = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            String endTime = dateFormat.format(calendar.getTime());

            String url = "http://114.71.220.59:7579/Mobius/Ksensor_project/Data?fu=1&ty=4&cra=" + startTime + "&crb=" + endTime;

            fetchDataAndStoreInInfluxDB(url);
        }
    }

    private static void fetchDataAndStoreInInfluxDB(String url) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("X-M2M-RI", "12345");
            httpGet.setHeader("X-M2M-Origin", "S9arsI6_8fm");

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);

                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray urilArray = json.getAsJsonArray("m2m:uril");

                // Fetch and process data for each URI
                for (int i = 0; i < urilArray.size(); i++) {
                    String uril = urilArray.get(i).getAsString();
                    String dataUrl = "http://114.71.220.59:7579/" + uril;

                    processAndStoreData(dataUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void processAndStoreData(String dataUrl) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet(dataUrl);
            getRequest.setHeader("Accept", "application/json");
            getRequest.setHeader("X-M2M-RI", "12345");
            getRequest.setHeader("X-M2M-Origin", "S9arsI6_8fm");

            try (CloseableHttpResponse getResponse = httpclient.execute(getRequest)) {
                String responseContent = EntityUtils.toString(getResponse.getEntity());
                System.out.println(responseContent);

                JsonObject responseJson = JsonParser.parseString(responseContent).getAsJsonObject();
                String conData = responseJson.getAsJsonObject("m2m:cin").get("con").getAsString();

                String[] conDataSplit = conData.split(",");
                if (conDataSplit.length <= 13) {
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
                    Double windDirection = (Double.parseDouble(conDataSplit[11]) * 100) / 360;
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

                    System.out.println(time + microDust_1 + microDust_2 + microDust_3 + microDust_4 + temperature + humidity + ozone + sulfurDioxide + carbonMonoxide + carbonDioxide + windDirection + windSpeed);

                    InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);
                    WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

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

                    influxDBClient.close();
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
