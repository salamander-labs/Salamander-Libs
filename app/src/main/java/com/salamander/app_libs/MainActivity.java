package com.salamander.app_libs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.salamander.core.Utils;
import com.salamander.core.utils.DialogUtils;
import com.salamander.network.DownloadCertificate;
import com.salamander.network.retro.Retro;
import com.salamander.network.retro.RetroData;
import com.salamander.network.retro.RetroResp;
import com.salamander.network.retro.RetroStatus;

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
        final EditText tx_test = findViewById(R.id.tx_test);
        final EditText tx_test2 = findViewById(R.id.tx_test2);

        tx_test.setText("0");
        tx_test2.setText("0");

        new DownloadCertificate(this, new DownloadCertificate.PostDownload() {
            @Override
            public void downloadDone(String errorMessage) {
                if (errorMessage != null) {
                    DialogUtils.showDialog(activity, errorMessage, true);
                }
            }
        }).execute();

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
        @GET("check_version")
        Call<ResponseBody> get();
    }

    private void getData(final Retro.OnCB onCB) {
        String url = "https://intraweb.datascrip.co.id/go-sam/android_data/";
        //url = "http://dtswebapi.datascrip.co.id/demo/rest_api/application/models/";
        getData getData = Retro.createRetrofit(this, url).create(MainActivity.getData.class);
        getData.get().enqueue(new RetroResp.SuccessCallback<ResponseBody>(activity) {
            @Override
            public void onCall(RetroData retroData) {
                super.onCall(retroData);
                if (Utils.isEmpty(retroData.getRetroStatus().getMessage()))
                    retroData.getRetroStatus().setMessage(retroData.getResult());
                onCB.onCB(retroData.getRetroStatus());
            }
        });
    }
}