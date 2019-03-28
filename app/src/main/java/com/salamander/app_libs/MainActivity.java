package com.salamander.app_libs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.salamander.salamander_base_module.Utils;
import com.salamander.salamander_network.JSON;
import com.salamander.salamander_network.retro.Retro;
import com.salamander.salamander_network.retro.RetroData;
import com.salamander.salamander_network.retro.RetroResp;
import com.salamander.salamander_network.retro.RetroStatus;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
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
        tx_test2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && tx_test2.getText().toString().trim().equals("0"))
                    tx_test2.setText("");
            }
        });

        String json = "{\"nama\":\"benny\",\"nomor\":\"11\"}";
        JSONObject jsonObject = JSON.toJSONObject(json);
        try {
            String umur = jsonObject.getString("umur");
        } catch (Exception exception) {
            Utils.showLog(exception);
        }

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
        @GET("andro_m_data.php")
        Call<ResponseBody> get();
    }

    private void getData(final Retro.OnCB onCB) {
        String url = "http://dtswebapi.datascrip.co.id/demo/rest_api/android_data/";
        url = "http://dtswebapi.datascrip.co.id/demo/rest_api/application/models/";
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