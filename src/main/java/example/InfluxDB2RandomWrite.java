package example;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxDB2RandomWrite {

    private static char[] token = "DcKEp5NVq6hrMfrvjbEBkwW3TdqKtukNrnLoSwijEUxhN90454sSnHGubrOlm8ZJdiJDmb3tPSj5ZylOdIZ1cw==".toCharArray();
    private static String org = "Ubicomp";
    private static String bucket = "Ubicomp-Bucket";

    public static void main(final String[] args) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        while (true) {
            // 임의의 데이터 생성
            Long time = Instant.now().toEpochMilli();
            Double microDust_1 = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
            Double microDust_2 = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
            Double microDust_3 = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
            Double temperature = ThreadLocalRandom.current().nextDouble(20.0, 30.0);
            Double humidity = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
            Double ozone = ThreadLocalRandom.current().nextDouble(0.0, 50.0);
            Double sulfurDioxide = ThreadLocalRandom.current().nextDouble(0.0, 5.0);
            Double carbonMonoxide = ThreadLocalRandom.current().nextDouble(0.0, 0.1);
            Double carbonDioxide = ThreadLocalRandom.current().nextDouble(0.0, 0.01);
            Double windDirection = ThreadLocalRandom.current().nextDouble(0.0, 270.0);
            Double windSpeed = ThreadLocalRandom.current().nextDouble(0.0, 10.0);

            // 데이터 포인트 생성
            Point point = Point
                    .measurement("random")
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

            // 1초 대기
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
