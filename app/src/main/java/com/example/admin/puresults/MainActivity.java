package com.example.admin.puresults;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyVideoAd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String url = /*"http://www.pondiuni.edu.in/"*/"http://result.pondiuni.edu.in/candidate.asp";
    private FloatingActionButton screenShotBtn,shareBtn;
    private FloatingActionMenu menuBtn;
    private View rootView;
    private Animation fabAnimation;
    private TextView txtScreenShot,txtShare;
    private String clientOption = "version:1.0,store:google",app_Id="appa4b5cbd6ddec4e1bab",zone_id="vzc968fd674a92443b9b";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
        AdColony.configure(this,clientOption,app_Id,zone_id);
        init();
        setUpDefaults();
        setUpEvents();
    }

    private void init() {
        webView = (WebView) findViewById(R.id.web_view);
        menuBtn = (FloatingActionMenu) findViewById(R.id.fab_menu);
        screenShotBtn = (FloatingActionButton) findViewById(R.id.screen_shot);
        shareBtn = (FloatingActionButton) findViewById(R.id.share);
        progressBar = (ProgressBar) findViewById(R.id.progrees_bar);
//        screenShotBtn.setVisibility(View.GONE);
//        shareBtn.setVisibility(View.GONE);
//        txtScreenShot.setVisibility(View.GONE);
//        txtShare.setVisibility(View.GONE);
    }

    private void setUpDefaults() {
        progressBar.setVisibility(View.VISIBLE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl(url);
        //*AdColony
        AdColonyVideoAd adColonyVideoAd = new AdColonyVideoAd();
        adColonyVideoAd.show();
    }

    private void setUpEvents() {
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showCustomDailog();
            }
        });

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
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (view.getProgress() >= 80){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdColony.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdColony.resume(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
                    public void onScanCompleted(String path, Uri uri) {
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

//    public void showCustomDailog(){
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_layout);
//        dialog.setCanceledOnTouchOutside(true);
//
//        Window window = dialog.getWindow();
//        window.setBackgroundDrawable(new ColorDrawable(0));
//        window.setGravity(Gravity.TOP|Gravity.END);
//
//        FloatingActionMenu menuBtn = (FloatingActionMenu) dialog.findViewById(R.id.fab_menu);
//        screenShotBtn = (FloatingActionButton) dialog.findViewById(R.id.screen_shot);
//        shareBtn = (FloatingActionButton) dialog.findViewById(R.id.share);
//        txtScreenShot = (TextView) dialog.findViewById(R.id.label_screenshot);
//        txtShare = (TextView) dialog.findViewById(R.id.label_share);
//
//        txtShare.setVisibility(View.VISIBLE);
//        txtScreenShot.setVisibility(View.VISIBLE);
//        shareBtn.setVisibility(View.VISIBLE);
//        screenShotBtn.setVisibility(View.VISIBLE);
//
//        fabAnimation = AnimationUtils.loadAnimation(this,R.anim.clockwise);
//        shareBtn.setAnimation(fabAnimation);
//        screenShotBtn.setAnimation(fabAnimation);
//        txtScreenShot.setAnimation(fabAnimation);
//        txtShare.setAnimation(fabAnimation);
//
//        screenShotBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                screenShot(false);
//            }
//        });
//
//        shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                screenShot(true);
//            }
//        });
//
//        menuBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//
//    }
}


