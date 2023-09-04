package example;

import java.time.Instant;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;

@Measurement(name = "sensor_data")
public class SensorData {
    @Column(tag = true)
    private String location;

    @Column
    private double temperature;

    @Column
    private double humidity;

    @Column(timestamp = true)
    private Instant time;

    // 기본 생성자 필요 (InfluxDB에서 POJO를 생성하기 위함)
    public SensorData() {
    }

    public SensorData(String location, double temperature, double humidity, Instant time) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.time = time;
    }

    // Getter와 Setter 메서드들 (생략)

    public static void main(final String[] args) {
        // InfluxDB 접속 설정
        String url = "http://localhost:8086";
        char[] token = "DcKEp5NVq6hrMfrvjbEBkwW3TdqKtukNrnLoSwijEUxhN90454sSnHGubrOlm8ZJdiJDmb3tPSj5ZylOdIZ1cw==".toCharArray();
        String org = "Ubicomp";
        String bucket = "Ubicomp-Bucket";

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);

        // 센서 데이터 생성 및 InfluxDB에 저장
        SensorData data1 = new SensorData("Room1", 25.5, 60.0, Instant.now());
        SensorData data2 = new SensorData("Room2", 24.8, 55.5, Instant.now().minusSeconds(60));
        SensorData data3 = new SensorData("Room3", 26.2, 58.2, Instant.now().minusSeconds(120));

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, data1);
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, data2);
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, data3);

        influxDBClient.close();
    }
}
