package example;

import okhttp3.*;

import java.io.IOException;

public class MobiusCntPostMethod {
    public static void main(String[] args) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/vnd.onem2m-res+json; ty=3");
            RequestBody body = RequestBody.create(mediaType, "{\n  \"m2m:cnt\": {\n    \"rn\": \"Data\",\n    \"lbl\": [\"ss\"],\n    \"mbs\": 16384\n  }\n}");
            Request request = new Request.Builder()
                    .url("http://114.71.220.59:7579/Mobius/gpm")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("X-M2M-RI", "12345")
                    .addHeader("X-M2M-Origin", "SYRSxt5-D5q")
                    .addHeader("Content-Type", "application/vnd.onem2m-res+json; ty=3")
                    .build();
            Response response = client.newCall(request).execute();

            System.out.println("Response Code: " + response.code());
            System.out.println("Response Body: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
