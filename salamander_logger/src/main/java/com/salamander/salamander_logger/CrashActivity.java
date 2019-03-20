package com.salamander.salamander_logger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.salamander.salamander_base_module.Utils;

public class CrashActivity extends AppCompatActivity {

    private Context context;
    private TextView tx_message, tx_title, tx_logcat;
    private Button bt_report, bt_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        context = this;
        startService(new Intent(this, ReportingService.class));
        initView();
    }

    private void initView() {
        tx_title = findViewById(R.id.tx_title);
        tx_message = findViewById(R.id.tx_message);
        tx_logcat = findViewById(R.id.tx_logcat);
        bt_report = findViewById(R.id.bt_report);
        bt_report.setVisibility(View.GONE);
        bt_exit = findViewById(R.id.bt_exit);
        tx_logcat.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent() != null ? getIntent() : new Intent();

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String logcat = intent.getStringExtra("logcat");

        if (!Utils.isEmpty(title))
            tx_title.setText(title);
        if (!Utils.isEmpty(message))
            tx_message.setText(message);
        if (!Utils.isEmpty(logcat))
            tx_logcat.setText(Utils.textToHtml(logcat));

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }
}
