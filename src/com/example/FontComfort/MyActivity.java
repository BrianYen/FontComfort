package com.example.FontComfort;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private String[] mTypefaceTable = {
            "fonts/arjingxiheiscaling80.ttf",
            "fonts/gjxh0db_noh.ttf"

    };

    public final static int DEFAULT_FONT_SIZE = 24;
    public final static int MAX_FONT_SIZE = 72;
    public final static int MIN_FONT_SIZE = 8;
    private static int mSize = DEFAULT_FONT_SIZE;

    private final static boolean IsEnableUserFont = true;

    private static final int MAX_AVAILABLE = 100;
    private final Semaphore available = new Semaphore(MAX_AVAILABLE, true);

    private ArrayList<Typeface> mTypefaces = new ArrayList<Typeface>(
            mTypefaceTable.length);

    private TextView txt;
    private TextView fontSizeMsg;
    private ZoomControls fontSize;

    private static String filePath = "texts/FontComfort_example.html";


    //Typeface initializer
    public void init(AssetManager am) {

        // initialize Util for TypeFace
        Util.init(am);

        mTypefaces.add(null);
        mTypefaces.add(null);
        for (int i = 0; i < mTypefaceTable.length; i++) {
            if (i == 0) {
                new Thread(new Runnable() {
                    public void run() {
                        if (IsEnableUserFont) {
                            Typeface tf = Util
                                    .getTypeface(mTypefaceTable[0]);
                            try {
                                available.acquire();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            mTypefaces.set(0, tf);
                            available.release();
                        }
                    }
                }).start();
            } else {
                new Thread(new Runnable() {
                    public void run() {
                        if (IsEnableUserFont) {
                            Typeface tf = Util
                                    .getTypeface(mTypefaceTable[1]);
                            try {
                                available.acquire();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            mTypefaces.set(1, tf);
                            available.release();
                        }
                    }
                }).start();
            }
        }
    }

    private void loadFile(final String path) {

        //File file = new File(path);

        String line, text = "";
        try {

            InputStream inStream = getAssets().open(path);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(inStream, "UTF-8"));

            while ((line = br.readLine()) != null) {
                text += line;
            }
        } catch (IOException e) {

            Toast.makeText(
                    MyActivity.this,
                    "Load File Error",
                    Toast.LENGTH_LONG
            ).show();
        }

        //設定顯示內容
        txt.setText(Html.fromHtml(text));
    }

    private synchronized void setFontSize(int size) {
        String sizeTitle;
        if (size == MAX_FONT_SIZE) {
            size = MAX_FONT_SIZE;
            fontSize.setIsZoomInEnabled(false);
            sizeTitle = "Font Size : " + size + " (MAX)";
        } else if (size == MIN_FONT_SIZE) {
            size = MIN_FONT_SIZE;
            fontSize.setIsZoomOutEnabled(false);
            sizeTitle = "Font Size : " + size + " (MIN)";
        } else
            sizeTitle = "Font Size : " + size;

        fontSizeMsg.setText(sizeTitle);
        txt.setTextSize(size);
        mSize = size;

    }

    private ZoomControls.OnClickListener mFontSizeAddListener = new ZoomControls.OnClickListener() {

        @Override
        public void onClick(View v) {
            fontSize.setIsZoomOutEnabled(true);
            setFontSize(mSize + 2);
        }
    };

    private ZoomControls.OnClickListener mFontSizeSubListener = new ZoomControls.OnClickListener() {

        @Override
        public void onClick(View v) {
            fontSize.setIsZoomInEnabled(true);
            setFontSize(mSize - 2);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化Typeface
        init(getAssets());

        DisplayMetrics dm = getResources().getDisplayMetrics();

        //取得螢幕顯示的資料
        int ScreenWidth = dm.widthPixels;
        int ScreenHeight = dm.heightPixels;

        if(ScreenHeight > ScreenWidth) {
            setContentView(R.layout.portrait_main);
            //
            txt = (TextView) findViewById(R.id.textView);
            //讀取檔案並顯示
            loadFile(filePath);

            txt.setTypeface(mTypefaces.get(0));
        } else {
            setContentView(R.layout.landscape_main);

            //
            txt = (TextView) findViewById(R.id.textView);
            //讀取檔案並顯示
            loadFile(filePath);

            txt.setTypeface(mTypefaces.get(1));
        }

        txt.setMovementMethod(ScrollingMovementMethod.getInstance());

        fontSizeMsg = (TextView)findViewById(R.id.textView2);
        fontSize = (ZoomControls)findViewById(R.id.zoomControls);

        fontSize.setOnZoomInClickListener(mFontSizeAddListener);
        fontSize.setOnZoomOutClickListener(mFontSizeSubListener);

        setFontSize(DEFAULT_FONT_SIZE);


    }
}
