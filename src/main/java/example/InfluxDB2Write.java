package example;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxDB2Write {

    private static char[] token = "DcKEp5NVq6hrMfrvjbEBkwW3TdqKtukNrnLoSwijEUxhN90454sSnHGubrOlm8ZJdiJDmb3tPSj5ZylOdIZ1cw==".toCharArray();
    private static String org = "Ubicomp";
    private static String bucket = "Ubicomp-Bucket";

    public static void main(final String[] args) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);

        // 임의의 데이터 생성
        Long time1 = Instant.now().toEpochMilli();
        Double microDust_1_1 = 20.0;
        Double microDust_2_1 = 20.0;
        Double microDust_3_1 = 20.0;
        Double temperature_1 = 25.0;
        Double humidity_1 = 10.0;
        Double ozone_1 = 24.0;
        Double sulfurDioxide_1 = 1.002;
        Double carbonMonoxide_1 = 0.034;
        Double carbonDioxide_1 = 0.002;
        Double windDirection_1 = 97.5;
        Double windSpeed_1 = 1.0;

        Long time2 = Instant.now().toEpochMilli();
        Double microDust_1_2 = 22.0;
        Double microDust_2_2 = 21.5;
        Double microDust_3_2 = 20.2;
        Double temperature_2 = 26.0;
        Double humidity_2 = 12.0;
        Double ozone_2 = 25.0;
        Double sulfurDioxide_2 = 1.004;
        Double carbonMonoxide_2 = 0.035;
        Double carbonDioxide_2 = 0.003;
        Double windDirection_2 = 95.0;
        Double windSpeed_2 = 1.2;

        Long time3 = Instant.now().toEpochMilli();
        Double microDust_1_3 = 23.5;
        Double microDust_2_3 = 21.2;
        Double microDust_3_3 = 19.8;
        Double temperature_3 = 24.5;
        Double humidity_3 = 11.5;
        Double ozone_3 = 23.5;
        Double sulfurDioxide_3 = 1.001;
        Double carbonMonoxide_3 = 0.033;
        Double carbonDioxide_3 = 0.002;
        Double windDirection_3 = 96.0;
        Double windSpeed_3 = 1.1;

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        // 첫 번째 데이터 포인트
        Point point1 = Point
                .measurement("test1")
                .addTag("sensor_id", "TLM0103") // 태그 추가
                .addField("time", time1)
                .addField("microDust_1", microDust_1_1)
                .addField("microDust_2", microDust_2_1)
                .addField("microDust_3", microDust_3_1)
                .addField("temperature", temperature_1)
                .addField("humidity", humidity_1)
                .addField("ozone", ozone_1)
                .addField("sulfurDioxide", sulfurDioxide_1)
                .addField("carbonMonoxide", carbonMonoxide_1)
                .addField("carbonDioxide", carbonDioxide_1)
                .addField("windDirection", windDirection_1)
                .addField("windSpeed", windSpeed_1)
                .time(time1, WritePrecision.MS);

        // 두 번째 데이터 포인트
        Point point2 = Point
                .measurement("test2")
                .addTag("sensor_id", "TLM0200") // 태그 추가
                .addField("time", time2)
                .addField("microDust_1", microDust_1_2)
                .addField("microDust_2", microDust_2_2)
                .addField("microDust_3", microDust_3_2)
                .addField("temperature", temperature_2)
                .addField("humidity", humidity_2)
                .addField("ozone", ozone_2)
                .addField("sulfurDioxide", sulfurDioxide_2)
                .addField("carbonMonoxide", carbonMonoxide_2)
                .addField("carbonDioxide", carbonDioxide_2)
                .addField("windDirection", windDirection_2)
                .addField("windSpeed", windSpeed_2)
                .time(time2, WritePrecision.MS);

        // 세 번째 데이터 포인트
        Point point3 = Point
                .measurement("test3")
                .addTag("sensor_id", "TLM0201") // 태그 추가
                .addField("time", time3)
                .addField("microDust_1", microDust_1_3)
                .addField("microDust_2", microDust_2_3)
                .addField("microDust_3", microDust_3_3)
                .addField("temperature", temperature_3)
                .addField("humidity", humidity_3)
                .addField("ozone", ozone_3)
                .addField("sulfurDioxide", sulfurDioxide_3)
                .addField("carbonMonoxide", carbonMonoxide_3)
                .addField("carbonDioxide", carbonDioxide_3)
                .addField("windDirection", windDirection_3)
                .addField("windSpeed", windSpeed_3)
                .time(time3, WritePrecision.MS);

        // InfluxDB에 데이터 저장

        writeApi.writePoint(bucket, org, point1);
        writeApi.writePoint(bucket, org, point2);
        writeApi.writePoint(bucket, org, point3);

        influxDBClient.close();
    }
}
