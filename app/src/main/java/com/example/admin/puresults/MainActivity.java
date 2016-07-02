package com.example.admin.puresults;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    WebView webView;
    String url = /*"http://www.pondiuni.edu.in/"*/"http://result.pondiuni.edu.in/candidate.asp";
    Button screenShotBtn;
    View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
        init();
        setUpDefaults();
        setUpEvents();
    }

    private void init() {
        webView = (WebView) findViewById(R.id.web_view);
        screenShotBtn = (Button) findViewById(R.id.screen_shot);
    }

    private void setUpDefaults() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl(url);
    }

    private void setUpEvents() {
        screenShotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenShot();
            }
        });
    }

    public void screenShot(){
        String path = this.getExternalCacheDir()+"/"+ SystemClock.currentThreadTimeMillis()+".jpg";
        View screenView = webView;
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        File screenShot = new File(path);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(screenShot);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            openScreenShot(screenShot);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void openScreenShot(File screenShot) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(screenShot),"image/*");
        startActivity(intent);
    }

    private static void galleryAddPic(Context app, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        app.sendBroadcast(mediaScanIntent);
    }
}
