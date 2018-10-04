package com.salamander.salamander_network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.salamander.salamander_base_module.DialogUtils;
import com.salamander.salamander_base_module.Utils;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;

import okhttp3.FormBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetroResp {

    public static RetroStatus getRetroStatus(String json) {
        RetroStatus status = new RetroStatus();
        JSONObject jsonObject = JSON.toJSONObject(json);
        String statusTxt = JSON.getString(jsonObject, "status");
        String msgTxt = JSON.getString(jsonObject, "msg");
        String qryTxt = JSON.getString(jsonObject, "qry");
        status.setSuccess(!Utils.isEmpty(statusTxt) && statusTxt.equals(Retro.STATUS_SUCCESS));
        if (!Utils.isEmpty(msgTxt))
            status.setMessage(msgTxt);
        if (!Utils.isEmpty(qryTxt))
            status.setQuery(qryTxt);
        return status;
    }

    public static abstract class BaseCallBack<T> {
        public abstract void onCall(RetroData retroData);
    }

    public static abstract class SuccessCallback<T> extends BaseCallBack<T> implements Callback<T> {

        private Context context;
        private RetroData retroData = new RetroData();

        protected SuccessCallback(Context context) {
            setRetroData(context);
        }

        private void setRetroData(Context context) {
            this.context = context;
            retroData.setActivityName(context.getClass().getName());
            StackTraceElement[] stackTraceElementList = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElementList) {
                String packageName = context.getApplicationContext().getPackageName();
                if (stackTraceElement.toString().contains(packageName) && !stackTraceElement.toString().contains("<init>")) {
                    retroData.setClassName(stackTraceElement.getClassName());
                    retroData.setMethodName(stackTraceElement.getMethodName());
                    break;
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            Response<ResponseBody> responseBody = null;

            if (call.request().body() instanceof FormBody) {
                FormBody formBody = ((FormBody) call.request().body());
                if (Utils.isEmpty(retroData.getParameter()) && formBody != null) {
                    String parameter = "";
                    int paramSize = formBody.size();
                    for (int i = 0; i < paramSize; i++)
                        parameter = parameter + formBody.name(i) + " => " +
                                URLDecoder.decode(formBody.value(i)) + "; ";
                    retroData.setParameter(parameter);
                }
            }

            ByteArrayInputStream byteArrayInputStreamError = null;
            if (response.body() instanceof RetroData)
                retroData = (RetroData) response.body();
            else {
                responseBody = (Response<ResponseBody>) response;
                try {
                    //ByteArrayInputStream byteArrayInputStreamBody = new ByteArrayInputStream(Retro.getBytesFromInputStream(Retro.getResponseBody(responseBody).byteStream()));
                    byteArrayInputStreamError = new ByteArrayInputStream(Retro.getBytesFromInputStream(Retro.getErrorBody(responseBody).byteStream()));
                    } catch (Exception e) {
                    Log.d(e.getClass().getSimpleName(), e.getMessage());
                } finally {
                    retroData.setResult(Retro.getString(responseBody));
                }
            }
            RetroStatus status = Retro.getRetroStatus(responseBody, byteArrayInputStreamError, retroData.getResult());
            status.setHeader(response.raw().toString());
            status.setURL(response.raw().request().url().toString());
            status.setStatusCode(response.code());
            retroData.setRetroStatus(status);

            onCall(retroData);
        }

        @Override
        public void onFailure(Call<T> call, Throwable throwable) {

            if (call.request().body() instanceof FormBody) {
                FormBody formBody = ((FormBody) call.request().body());
                if (Utils.isEmpty(retroData.getParameter()) && formBody != null) {
                    String parameter = "";
                    int paramSize = formBody.size();
                    for (int i = 0; i < paramSize; i++)
                        parameter = parameter + formBody.name(i) + " => " + URLDecoder.decode(formBody.value(i)) + "; ";
                    retroData.setParameter(parameter);
                }
            }

            RetroStatus status = Retro.getRetroStatus(null, null, throwable.getMessage());
            if (!Retro.isConnected(context))
                status.setMessage("Not connected to internet.\nCheck your connection and try again");
            status.setHeader(throwable.getClass().getSimpleName());
            retroData.setRetroStatus(status);
            onCall(retroData);
        }

        @Override
        public void onCall(RetroData retroData) {
            dismissDialog(null);
            //new NetworkLogSQLite(context).Post(new NetworkLog(retroData));
            /*
            Crashlytics.log(retroData.getClassName());
            Crashlytics.log(retroData.getMethodName());
            Crashlytics.log(retroData.getParameter());
            Crashlytics.log(retroData.getRetroStatus().getHeader());
            Crashlytics.log(retroData.getResult());
            if (!retroData.isSuccess())
                Crashlytics.log(retroData.getRetroStatus().getMessage());
            Crashlytics.logException(new Exception("onCall"));
            */
        }
    }

    public static void dismissDialog(ProgressDialog progressDialog) {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public static void showRetroDialog(Context context, RetroStatus retroStatus, boolean finishOnDismiss) {
        DialogUtils.showErrorNetwork(context, retroStatus.getTitle(), retroStatus.getMessage(), finishOnDismiss);
    }
}