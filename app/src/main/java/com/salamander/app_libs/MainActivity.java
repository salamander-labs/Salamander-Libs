package com.salamander.app_libs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.salamander.salamander_base_module.Utils;
import com.salamander.salamander_network.Retro;
import com.salamander.salamander_network.RetroCallback;
import com.salamander.salamander_network.RetroData;
import com.salamander.salamander_network.RetroResp;
import com.salamander.salamander_network.RetroStatus;

import okhttp3.ResponseBody;
import retrofit2.Call;
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
        final TextView tv_test = findViewById(R.id.tv_test);

        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(new RetroCallback.OnCB() {
                    @Override
                    public void OnCB(RetroStatus retroStatus) {
                        if (retroStatus.isSuccess())
                            tv_test.setText(retroStatus.getMessage());
                        else
                            RetroResp.showRetroDialog(activity, retroStatus, false);
                    }
                });
            }
        });
    }

    private interface getData {
        @GET("andro_m_data.php")
        Call<ResponseBody> get();
    }

    private void getData(final RetroCallback.OnCB onCB) {
        String url = "http://dtswebapi.datascrip.co.id/demo/rest_api/android_data/";
        url = "http://dtswebapi.datascrip.co.id/demo/rest_api/application/models/";
        getData getData = Retro.createRetrofit(this, url).create(MainActivity.getData.class);
        getData.get().enqueue(new RetroResp.SuccessCallback<ResponseBody>(activity) {
            @Override
            public void onCall(RetroData retroData) {
                super.onCall(retroData);
                if (Utils.isEmpty(retroData.getRetroStatus().getMessage()))
                    retroData.getRetroStatus().setMessage(retroData.getResult());
                onCB.OnCB(retroData.getRetroStatus());
            }
        });
    }
}