package com.salamander.app_libs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.salamander.core.Utils;
import com.salamander.network.retro.Retro;
import com.salamander.network.retro.RetroData;
import com.salamander.network.retro.RetroResp;
import com.salamander.network.retro.RetroStatus;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        initView();
    }

    private void initView() {
        Button bt_test = findViewById(R.id.bt_test);
        final EditText tx_test = findViewById(R.id.tx_test);
        final EditText tx_test2 = findViewById(R.id.tx_test2);

        tx_test.setText("0");

        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(new Retro.OnCB() {
                    @Override
                    public void onCB(RetroStatus retroStatus) {
                        if (retroStatus.isSuccess())
                            tx_test.setText(retroStatus.getMessage());
                        else
                            Retro.showRetroDialog(activity, retroStatus);
                    }
                });
            }
        });
    }

    private interface getData {
        @GET("Packing_List")
        Call<ResponseBody> get();
    }

    private void getData(final Retro.OnCB onCB) {
        String url = "http://dtsnavtest2015.datascrip.co.id:8047/servertest_trace/WS/PT%20DATASCRIP/Page/";
        getData getData = createRetrofit(this, url).create(MainActivity.getData.class);
        getData.get().enqueue(new RetroResp.SuccessCallback<ResponseBody>(activity) {
            @Override
            public void onCall(RetroData retroData) {
                super.onCall(retroData);
                if (Utils.isEmpty(retroData.getRetroStatus().getMessage()))
                    retroData.getRetroStatus().setMessage(retroData.getResult());
                //onCB.onCB(retroData.getRetroStatus());
            }
        });
    }

    private Retrofit createRetrofit(Context context, String URL) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .authenticator(new NTLMAuthenticator("dbadmin", "K@m!sDinIHari7M@r2019"))
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES);
        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(client.build())
                .build();
    }
}