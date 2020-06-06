package com.example.webviewdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class WebviewActivity extends AppCompatActivity {

    WebView webView;
    ArrayList<String> mimeTypes, dataPaths;
    ArrayList<byte[]> datas;
    static int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mimeTypes = new ArrayList<>();
        dataPaths = new ArrayList<>();
        datas = new ArrayList<>();
        // toolbar
        // setTitle(getString(R.string.sign_in_policy_text));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init webview, keep it hidden

        webView = findViewById(R.id.webview);
        //webView.setVisibility(View.INVISIBLE);

        // start activities
        //loadUrl(getIntent());
        loadUrl();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    private void loadUrl() {
        // url
        //String url = intent.getStringExtra(getString(R.string.app_webview_url_key));
        // String url = "file:///android_asset/subhajit/index.html";

        final String path = getIntent().getStringExtra("path");
       /*
        String pathData = path + "/data";
        File file = new File(pathData);
        try {
            getFiles(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        */

        String url = "file:///" + path + "/index.html";

        //Log.i("Name is", "loadUrl: " + url);


        // settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);

        // progress
        final ProgressDialog progressDialog = new ProgressDialog(WebviewActivity.this);
        progressDialog.hide();

        // web client
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.i("RESOURCE", "onLoadResource: " + url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                // hide toolbar, if necessary
//                webView.loadUrl("javascript:(function() { " +
//                        "document.querySelector('[role=\"toolbar\"]').remove();})()");

                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(getApplicationContext(), error.getDescription(), Toast.LENGTH_SHORT).show();

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

/*
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                InputStream inputStream = new ByteArrayInputStream(datas.get(j));
                String mimeType = mimeTypes.get(j);
                Log.i("DATA LOAD", "shouldInterceptRequest: "+dataPaths.get(j));
                j++;
                if (inputStream != null) {
                    return new WebResourceResponse(mimeType, "base64", inputStream);
                }


                try {
                    byte[] html=getByteArray(path+"/index.html");
                    InputStream inputStream=new ByteArrayInputStream(html);
                    if (inputStream != null) {
                        return new WebResourceResponse("text/html", "utf-8", inputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }



                return super.shouldInterceptRequest(view, request);


            }

 */


        });


        // load url
        if (url != null && url.length() > 0) {
            // progress
            progressDialog.setMessage("Loading... Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
/*

            for (int i = 0; i < datas.size(); i++) {
                String encoded = Base64.encodeToString(datas.get(i), Base64.DEFAULT);
                webView.loadData(encoded, mimeTypes.get(i), "base64");
            }



            try {
                byte[] html=getByteArray(path+"/index.html");
                String encoded=Base64.encodeToString(html,Base64.DEFAULT);
                webView.loadData(encoded,"text/html","base64");
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
            // load
             webView.loadUrl(url);
      
        }
    }

    private void getFiles(File files) throws IOException {
        if (files.isDirectory()) {
            for (File file : files.listFiles()) {
                getFiles(file);
            }
        } else {
            String mimeType = getMimeType(files.getAbsolutePath());
            mimeTypes.add(mimeType);
            dataPaths.add(files.getAbsolutePath());
            datas.add(getByteArray(files.getAbsolutePath()));
            // Log.i("Mimetype", "getFiles: " + mimeType);
        }

    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            if (extension.equals("js")) {
                return "text/javascript";
            } else if (extension.equals("woff")) {
                return "application/font-woff";
            } else if (extension.equals("woff2")) {
                return "application/font-woff2";
            } else if (extension.equals("ttf")) {
                return "application/x-font-ttf";
            } else if (extension.equals("eot")) {
                return "application/vnd.ms-fontobject";
            } else if (extension.equals("svg")) {
                return "image/svg+xml";
            } else if (extension.equals("gif")) {
                return "image/gif";
            } else if (extension.equals("png")) {
                return "image/png";
            } else if (extension.equals("mp3")) {
                return "audio/mpeg";
            } else if (extension.equals("css")) {
                return "text/css";
            } else if (extension.equals("ico")) {
                return "image/vnd.microsoft.icon";
            } else if (extension.equals("html")) {
                return "text/html";
            } else if (extension.equals("jpg")) {
                return "image/jpeg";
            } else if (extension.equals("mp4")) {
                return "video/mp4";
            } else if (extension.equals("cur")) {
                return "application/octet-stream";
            }
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private byte[] getByteArray(String path) throws IOException {

        File file = new File(path);
        byte[] byteArray = FileUtils.readFileToByteArray(file);
        return byteArray;
    }

    private void deleteInternalFiles(){
        File[] files =getFilesDir().listFiles();
        if(files != null)
            for(File file : files) {
                Log.i("FILE DELETED", "deleteInternalFiles: "  + file.getName());
                file.delete();
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "called ");
        deleteInternalFiles();
    }
}
