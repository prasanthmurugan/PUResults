package com.example.admin.puresults;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    WebView webView;
    String url = /*"http://www.pondiuni.edu.in/"*/"http://result.pondiuni.edu.in/candidate.asp";
    FloatingActionButton screenShotBtn,shareBtn,menuBtn;
    View rootView;
    Animation fabAnimation;
    TextView txtScreenShot,txtShare;
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
        screenShotBtn = (FloatingActionButton) findViewById(R.id.screen_shot);
        shareBtn = (FloatingActionButton) findViewById(R.id.share);
        menuBtn = (FloatingActionButton) findViewById(R.id.fab_menu);
        txtScreenShot = (TextView) findViewById(R.id.label_screenshot);
        txtShare= (TextView)findViewById(R.id.label_share);
        fabAnimation = AnimationUtils.loadAnimation(this,R.anim.clockwise);
        screenShotBtn.setVisibility(View.GONE);
        shareBtn.setVisibility(View.GONE);
        txtScreenShot.setVisibility(View.GONE);
        txtShare.setVisibility(View.GONE);
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
                screenShot(false);
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenShot(true);
            }
        });
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtShare.setVisibility(View.VISIBLE);
                txtScreenShot.setVisibility(View.VISIBLE);
                shareBtn.setVisibility(View.VISIBLE);
                screenShotBtn.setVisibility(View.VISIBLE);

                shareBtn.setAnimation(fabAnimation);
                screenShotBtn.setAnimation(fabAnimation);
                txtScreenShot.setAnimation(fabAnimation);
                txtShare.setAnimation(fabAnimation);
            }
        });
    }

    public void screenShot(boolean shareImage){
        String path = this.getExternalCacheDir()+"/"+ SystemClock.currentThreadTimeMillis()+".jpg";
        View screenView = webView;
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        File screenShot = new File(path);
//        File screenShot = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES),""+"/"+ SystemClock.currentThreadTimeMillis()+".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(screenShot);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            if (shareImage){
                openShareOption(screenShot.getAbsolutePath());
            }else {
//                addPicToGallery(this,screenShot.getAbsolutePath());
//                addPicUsingMount(screenShot.getAbsolutePath());
                addToMedia(screenShot.getAbsolutePath());
            }
//            openScreenShot(screenShot);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    private void openScreenShot(File screenShot) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(screenShot),"image/*");
//        startActivity(intent);
//    }

    private void addPicToGallery(Context app, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        app.sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"ScreenShot added To Gallery",Toast.LENGTH_LONG).show();
    }

    private void addPicUsingMount(String path){
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://"+path)));
        Toast.makeText(this,"ScreenShot added To Gallery",Toast.LENGTH_LONG).show();
        MediaScannerConnection.scanFile(this, new String[] {

                        path},

                null, new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri)
                    {


                    }
                });
    }

    private void openShareOption(String imgPath){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(imgPath)));
        startActivity(Intent.createChooser(shareIntent,"Share Result"));

    }

    private void addToMedia(String filePath){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Toast.makeText(this,"ScreenShot added To Gallery",Toast.LENGTH_LONG).show();
    }
}
