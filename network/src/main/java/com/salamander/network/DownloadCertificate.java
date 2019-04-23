package com.salamander.network;

import android.content.Context;
import android.os.AsyncTask;

import com.salamander.core.Utils;
import com.salamander.network.retro.Retro;
import com.salamander.network.utils.CertUtil;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadCertificate extends AsyncTask<String, String, String> {

    private static final String TAG = "CERT_DOWNLOAD";
    private static final String CERT_URL = "http://www.datascrip.co.id/ssl/datascrip_co_id.crt";

    private Context context;
    private PostDownload callback;
    private String aURL = CERT_URL, errorMessage, certText;

    public DownloadCertificate(Context context, PostDownload callback) {
        this.context = context;
        this.callback = callback;
    }

    public DownloadCertificate(Context context, String aURL, PostDownload callback) {
        this.context = context;
        this.callback = callback;
        this.aURL = aURL;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {
            URL url;
            if (aURL != null)
                url = new URL(aURL);
            else url = new URL(CERT_URL);
            URLConnection connection = url.openConnection();
            connection.connect();

            int lenghtOfFile = connection.getContentLength();
            Utils.showLog("DownloadCertificate => Length of the file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            //file = new File(destinationFile);
            certText = CertUtil.streamToString(input);
            /*
            FileOutputStream output = new FileOutputStream(destinationFile); //context.openFileOutput("content.zip", Context.MODE_PRIVATE);
            Utils.showLog("DownloadCertificate => file saved at " + destinationFile.getAbsolutePath());
            fileDescriptor = output.getFD();

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                //publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            */
        } catch (Exception e) {
            errorMessage = e.getClass() + " => " + e.getMessage();
        }
        return certText;
    }

    protected void onProgressUpdate(String... progress) {
        //Log.e(TAG,progress[0]);
        Utils.showLog("DownloadCertificate => progress : " + progress[0]);
    }

    @Override
    protected void onPostExecute(String certText) {
        if (callback != null) {
            if (!Retro.isConnected(context))
                errorMessage = "Not connected to internet.\nCheck your connection and try again";
            else //CertUtil.setCertificate(context, destinationFile);
                CertUtil.setCertificate(context, certText);
            callback.downloadDone(errorMessage);
        }
    }

    public interface PostDownload {
        void downloadDone(String errorMessage);
    }
}