package com.salamander.salamander_network.retro;

import android.content.Context;

import com.salamander.salamander_base_module.SalamanderProgressDialog;
import com.salamander.salamander_base_module.Utils;
import com.salamander.salamander_network.JSON;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
        String statusTxt = JSON.getString(jsonObject, RetroConst.RETRO_STATUS);
        String msgTxt = JSON.getString(jsonObject, RetroConst.RETRO_MSG);
        String qryTxt = JSON.getString(jsonObject, RetroConst.RETRO_QRY);
        status.setSuccess(!Utils.isEmpty(statusTxt) && statusTxt.equals(RetroConst.STATUS_SUCCESS));
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
                    for (int i = 0; i < paramSize; i++) {
                        String decodedValue = "";
                        try {
                            decodedValue = URLDecoder.decode(formBody.encodedValue(i), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            Utils.showLog(e);
                        }
                        parameter = parameter + formBody.name(i) + " => " + decodedValue + "; ";
                        retroData.getParameters().add(new Parameter(formBody.name(i), decodedValue));
                    }
                    retroData.setParameter(parameter);
                }
            }

            if (response.body() instanceof RetroData)
                retroData = (RetroData) response.body();
            else {
                responseBody = (Response<ResponseBody>) response;
                retroData.setResult(Retro.getString(responseBody));
            }
            retroData.setCode(response.code());
            retroData.setHeader(response.raw().toString());
            retroData.setURL(response.raw().request().url().toString());
            retroData.setStatus(response.raw().message());
            RetroStatus status = Retro.getRetroStatus(responseBody, retroData.getResult());
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
                    for (int i = 0; i < paramSize; i++) {
                        String decodedValue = "";
                        try {
                            decodedValue = URLDecoder.decode(formBody.encodedValue(i), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            Utils.showLog(e);
                        }
                        parameter = parameter + formBody.name(i) + " => " + decodedValue + "; ";
                        retroData.getParameters().add(new Parameter(formBody.name(i), decodedValue));
                    }
                    retroData.setParameter(parameter);
                }
            }

            RetroStatus status = Retro.getRetroStatus(null, throwable.getMessage());
            if (!Retro.isConnected(context))
                status.setMessage("Tidak terkoneksi ke internet.\nSilakan cek koneksi anda dan coba lagi.");
            retroData.setHeader(throwable.getClass().getSimpleName());
            retroData.setRetroStatus(status);
            onCall(retroData);
        }

        @Override
        public void onCall(RetroData retroData) {
            //new NetworkLogSQLite(context).Post(new NetworkLog(retroData));
            //Crashlytics.log(retroData.getClassName());
            //Crashlytics.log(retroData.getMethodName());
            //Crashlytics.log(retroData.getParameter());
            //Crashlytics.log(retroData.getRetroStatus().getHeader());
            //Crashlytics.log(retroData.getResult());
            //if (!retroData.isSuccess())
            //Crashlytics.log(retroData.getRetroStatus().getMessage());
            //Crashlytics.logException(new Exception("onCall"));
        }
    }

    public static void dismissDialog(SalamanderProgressDialog salamanderProgressDialog) {
        try {
            if (salamanderProgressDialog != null)
                salamanderProgressDialog.cancel();
        } catch (Exception e) {
            //Crashlytics.log(e.getMessage());
            //Crashlytics.logException(new Exception("Close Dialog"));
        }
    }
}
