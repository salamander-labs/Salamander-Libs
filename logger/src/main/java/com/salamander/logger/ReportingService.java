package com.salamander.logger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class ReportingService extends IntentService {

    private Context context = this;

    public ReportingService() {
        super("ReportingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.reportError(this, 0);
    }
}
