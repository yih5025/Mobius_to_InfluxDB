package example;

import okhttp3.*;

import java.io.IOException;

public class MobiusAePostMethod {
    public static void main(String[] args) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/vnd.onem2m-res+json;ty=2");
            RequestBody body = RequestBody.create(mediaType, "{\n  \"m2m:ae\" : {\n    \"rn\": \"gpm\",\n      \"api\": \"0.2.481.2.0001.001.000111\",\n      \"lbl\": [\"key1\", \"key2\"],\n      \"rr\": true,\n      \"poa\": [\"http://203.254.173.104:9727\"]\n    }\n}");
            Request request = new Request.Builder()
                    .url("http://114.71.220.59:7579/Mobius")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("X-M2M-RI", "12345")
                    .addHeader("X-M2M-Origin", "S")
                    .addHeader("Content-Type", "application/vnd.onem2m-res+json;ty=2")
                    .build();
            Response response = client.newCall(request).execute();

            System.out.println("Response Code: " + response.code());
            System.out.println("Response Body: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
