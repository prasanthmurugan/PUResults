package com.example.admin.puresults;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.puresults.utils.AlertUtils;
import com.example.admin.puresults.utils.DeviceUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyVideoAd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView webView;
    private String url = "http://result.pondiuni.edu.in/candidate.asp";
    private FloatingActionButton screenShotBtn, shareBtn;
    private FloatingActionMenu menuBtn;
    private View rootView;
    private Animation fabAnimation;
    private TextView txtScreenShot, txtShare, txtNoInternet;
    private ProgressBar progressBar;
    private AnimationDrawable gyroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_main);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
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
        txtNoInternet = (TextView) findViewById(R.id.txt_no_internet);
//        screenShotBtn.setVisibility(View.GONE);
//        shareBtn.setVisibility(View.GONE);
//        txtScreenShot.setVisibility(View.GONE);
//        txtShare.setVisibility(View.GONE);
    }

    private void setUpDefaults() {
        loadUrl();
    }

    private void loadUrl() {
        if (DeviceUtils.isInternetConnected(this)) {
            txtNoInternet.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
            webView.loadUrl(url);
        } else {
            if (gyroAnimation != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gyroAnimation.stop();
                    }
                }, 500);
            }
            txtNoInternet.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
        }
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
                if (webView.getVisibility() == View.VISIBLE) {
                    screenShot(false);
                } else {
                    AlertUtils.showAlert(MainActivity.this, getString(R.string.check_your_internet_connection), false);
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.getVisibility() == View.VISIBLE) {
                    screenShot(true);
                } else {
                    AlertUtils.showAlert(MainActivity.this, getString(R.string.check_your_internet_connection), false);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e(TAG, "onPageFinished: ");
                if (view.getProgress() >= 80) {
                    txtNoInternet.setVisibility(View.GONE);
                    if (gyroAnimation != null) {
                        gyroAnimation.stop();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "onReceivedError: ");
                if (gyroAnimation != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gyroAnimation.stop();
                        }
                    }, 500);
                }
                txtNoInternet.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
//                view.loadUrl("about:blank");
//                AlertUtils.showAlert(MainActivity.this, getString(R.string.check_your_internet_connection), false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        ImageView locButton = (ImageView) menu.findItem(R.id.menu_refresh).getActionView();
        if (locButton != null) {
            locButton.setBackgroundResource(R.drawable.refresh_icon);
            gyroAnimation = (AnimationDrawable) locButton.getBackground();
            locButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gyroAnimation.start();
                    loadUrl();
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if (item.getItemId() == R.id.menu_refresh) {
            loadUrl();
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void screenShot(final boolean shareImage) {
        final String path = this.getExternalCacheDir() /*+File.pathSeparator+getString(R.string.result)*/+ "/" + SystemClock.currentThreadTimeMillis() + ".jpg";
     /*   webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(0);
        webView.buildDrawingCache();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(webView.getDrawingCache());
                webView.setDrawingCacheEnabled(false);
//                webView.getSettings().setLoadWithOverviewMode(false);
//                webView.getSettings().setUseWideViewPort(false);
                webView.destroyDrawingCache();*/
      /*================*/
        webView.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)/*webView.getWidth()*/,/*webView.getHeight()*/
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.d(TAG, "screenShot: current width:"+webView.getWidth()+"  Current Height"+webView.getHeight() +" webView.getScrollX(): "+webView.getScrollX()+" webView.getScrollY(): "+webView.getScrollY());
        Log.d(TAG, "screenShot: Measured width:"+webView.getMeasuredWidth()+"  Measured Height"+webView.getMeasuredHeight());
        webView.layout(0, /*0*/webView.getScrollY(), /*webView.getWidth()*/webView.getMeasuredWidth(),
                webView.getHeight()+webView.getScrollY()/*webView.getMeasuredHeight()*/);
        webView.scrollTo(webView.getScrollX(),webView.getScrollY());
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        Log.d(TAG, "After Layout current width:"+webView.getWidth()+"  Current Height"+(webView.getHeight()+webView.getScrollY()));
        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(),
                webView.getHeight()+webView.getScrollY(), Bitmap.Config.ARGB_8888);

        Canvas bigcanvas = new Canvas(bitmap);
        Paint paint = new Paint();
//        paint.setColor(ContextCompat.getColor(this,R.color.colorAccent));
        int iHeight = bitmap.getHeight();
        bigcanvas.drawBitmap(bitmap, 0, iHeight, paint);
        webView.draw(bigcanvas);
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, webView.getScrollY(), webView.getWidth(), webView.getHeight());
        webView.setDrawingCacheEnabled(false);
//        webView.destroyDrawingCache();
      /*================*/
                File screenShot = new File(path);
//        File screenShot = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES),""+"/"+ SystemClock.currentThreadTimeMillis()+".jpg");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(screenShot);
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    if (shareImage) {
                        openShareOption(screenShot.getAbsolutePath());
                    } else {
                        addToMedia(screenShot.getAbsolutePath());
                    }
//            openScreenShot(screenShot);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
         /*   }
        }, 500);*/
    }

//    private void openScreenShot(File screenShot) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(screenShot),"image/*");
//        startActivity(intent);
//    }

    private void openShareOption(String imgPath) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imgPath)));
        startActivity(Intent.createChooser(shareIntent, "Share Result"));
    }

    private void addToMedia(String filePath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Toast.makeText(this, "ScreenShot added To Gallery", Toast.LENGTH_LONG).show();
    }

    public Bitmap getBitmapByView(WebView webView) {
        int h = 0, w = 0;
        Bitmap bitmap = null;
        //get the actual height of scrollview
        for (int i = 0; i < webView.getChildCount(); i++) {
            h += webView.getChildAt(i).getHeight();
            w += webView.getChildAt(i).getWidth();
//            scrollView.getChildAt(i).setBackgroundResource(R.color.white);
        }
        // create bitmap with target size
        bitmap = Bitmap.createBitmap(/*webView.getWidth()*/w, h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);
     /*   FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/screen_test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }*/
        return bitmap;
    }

    public void openPlayStore(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

}


