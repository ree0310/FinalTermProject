package com.example.finaltermproject;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // 웹뷰
    private WebView mWebView;
    private EditText url_String;


    // DB
    TextView idView;
    EditText productBox;
    EditText quantityBox;

    // 프로그레스바
    ProgressBar bar;
    TextView textView;
    boolean isRunning = false;

    ProgressHandler handler;

    // tabHost
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // 웹뷰
//
//        // 웹뷰를 생성해줌. id가 webv인 웹뷰 컨텐츠에 연결.
//        mWebView = (WebView) findViewById(R.id.webv);
//
//        // 페이지를 이동해주는 요소들 생성. edittext와 button을 사용해서
//        Button pm = (Button)findViewById(R.id.pm);
//        url_String = (EditText)findViewById(R.id.url);
//
//
//        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true); // 자바스크립드를 실행할 수 있도록 설정,
//        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND); // 플러그인을 사용할 수 있도록 설정
//        webSettings.setSupportMultipleWindows(true); // 여러개의 윈도우를 사용할 수 있도록 설정.
//        webSettings.setSupportZoom(true); // 페이지 확대 축소 기능을 사용할 수 있도록 설정
//
//        // 출처: http://jaehoon0210.tistory.com/entry/안드로이드-WebSettings를-사용하여-WebView-설정
//
//
//        //
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            // 새로운 창을 만들지 않고 웹뷰 안에서 다른 페이지를 로딩한다.
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//
//            // http://webnautes.tistory.com/473 에서 페이지 이동방법 가져옴.
//            // onPageStarted : 페이지로드가 시작되었음을 호스트 응용 프로그램에 알립니다.
//            // Bitmap: 데이터베이스에 이미있는 경우이 페이지에 대한 favicon입니다.
//            @Override
//            public void onPageStarted (WebView view, String url, Bitmap favicon){
//                String urlString = mWebView.getUrl().toString();
//                url_String.setText(urlString);
//            }
//        });
//
//        mWebView.loadUrl("http://www.google.com");
//
//        pm.setOnKeyListener(new View.OnKeyListener(){
//
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (i == KeyEvent.KEYCODE_ENTER)
//                {
//                    String urlString = url_String.getText().toString();
//
//                    if (urlString.startsWith("http") != true)
//                        urlString = "http://" + urlString;
//                    mWebView.loadUrl(urlString);
//                    return true;
//                }
//                return false;
//            }
//        });




        mWebView = (WebView) findViewById(R.id.webv);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl("http://www.google.com");



        // DB
        idView = (TextView) findViewById(R.id.productID);
        productBox = (EditText) findViewById(R.id.productName);
        quantityBox =
                (EditText) findViewById(R.id.productQuantity);


        // 스레드
        bar = (ProgressBar) findViewById(R.id.progress);
        textView = (TextView) findViewById(R.id.textView5);

        handler = new ProgressHandler();




        // tabHost
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Thread");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("DB");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Web View");
        host.addTab(spec);



    } // onCreate() end


    // DB
    public void newProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int quantity =
                Integer.parseInt(quantityBox.getText().toString());
        Product product =
                new Product(productBox.getText().toString(), quantity);
        dbHandler.addProduct(product);
        productBox.setText("");
        quantityBox.setText("");
    }

    public void lookupProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        Product product =
                dbHandler.findProduct(productBox.getText().toString());
        if (product != null) {
            idView.setText(String.valueOf(product.getID()));
            quantityBox.setText(String.valueOf(product.getQuantity()));
        } else {
            idView.setText("No Match Found");
        }
    }

    public void removeProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null,
                null, 1);
        boolean result = dbHandler.deleteProduct(
                productBox.getText().toString());
        if (result)
        {
            idView.setText("Record Deleted");
            productBox.setText("");
            quantityBox.setText("");
        }
        else
            idView.setText("No Match Found");
    }


    // 스레드
    public void onStart() {
        super.onStart();

        bar.setProgress(0);
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 20 && isRunning; i++) {
                        Thread.sleep(1000);

                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    }
                } catch (Exception ex) {
                    Log.e("MainActivity", "Exception in processing message.", ex);
                }
            }
        });

        isRunning = true;
        thread1.start();
    }

    public void onStop() {
        super.onStop();

        isRunning = false;
    }


    public class ProgressHandler extends Handler {

        public void handleMessage(Message msg) {

            bar.incrementProgressBy(5);

            if (bar.getProgress() == bar.getMax()) {
                textView.setText("Done");
            } else {
                textView.setText("Working ..." + bar.getProgress());
            }

        }


    }


} // class end
