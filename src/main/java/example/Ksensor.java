package example;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;

@Measurement(name = "Ksensor")
public class Ksensor {
    @Column
    private Double time;

    @Column
    private Double microDust_1;

    @Column
    private Double microDust_2;

    @Column
    private Double microDust_3;

    @Column
    private Double temperature;

    @Column
    private Double humidity;

    @Column
    private Double ozone;

    @Column
    private Double sulfurDioxide;

    @Column
    private Double carbonMonoxide;

    @Column
    private Double carbonDioxide;

    @Column
    private Double windDirection;

    @Column
    private Double windSpeed;

    // 기본 생성자 필요 (InfluxDB에서 POJO를 생성하기 위함)
    public Ksensor() {
    }

    public Ksensor(Double time, Double microDust_1, Double microDust_2, Double microDust_3, Double temperature,
                   Double humidity, Double ozone, Double sulfurDioxide, Double carbonMonoxide, Double carbonDioxide,
                   Double windDirection, Double windSpeed) {
        this.time = time;
        this.microDust_1 = microDust_1;
        this.microDust_2 = microDust_2;
        this.microDust_3 = microDust_3;
        this.temperature = temperature;
        this.humidity = humidity;
        this.ozone = ozone;
        this.sulfurDioxide = sulfurDioxide;
        this.carbonMonoxide = carbonMonoxide;
        this.carbonDioxide = carbonDioxide;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    // Getter와 Setter 메서드들 (생략)

    public static void main(final String[] args) {
        // InfluxDB 접속 설정
        String url = "http://localhost:8086";
        char[] token = "DcKEp5NVq6hrMfrvjbEBkwW3TdqKtukNrnLoSwijEUxhN90454sSnHGubrOlm8ZJdiJDmb3tPSj5ZylOdIZ1cw==".toCharArray();
        String org = "Ubicomp";
        String bucket = "Ubicomp-Bucket";

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);

        // 임의의 데이터 생성 및 InfluxDB에 저장
        // 원하는 데이터를 변경하여 사용하세요
        Ksensor sensorData1 = new Ksensor(0230720212650.0, 20.0, 20.0, 20.0, 25.0, 10.0, 24.0, 1.002, 0.034, 0.002, 97.5, 1.0);
        Ksensor sensorData2 = new Ksensor(0230720212651.0, 22.0, 21.5, 20.2, 26.0, 12.0, 25.0, 1.004, 0.035, 0.003, 95.0, 1.2);
        Ksensor sensorData3 = new Ksensor(0230720212652.0, 23.5, 21.2, 19.8, 24.5, 11.5, 23.5, 1.001, 0.033, 0.002, 96.0, 1.1);

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, sensorData1);
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, sensorData2);
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, sensorData3);

        influxDBClient.close();
    }
}
