package com.kridacreations.mouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    Thread thread1 = null;
    TextView statusInfo, xText, yText, zText;
    String SERVER_IP;
    EditText enterIP;
    Button connect, continueToController, howToUse;
    Boolean connected = false;
    private AdView mAdView;


    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        setAds();

        mAdView = new AdView(this);

        mAdView.setAdSize(AdSize.BANNER);

        mAdView.setAdUnitId(getString(R.string.banner_ads_id));

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        enterIP = (EditText) findViewById(R.id.enterIP);
        connect = (Button) findViewById(R.id.connect);
        continueToController = (Button) findViewById(R.id.continueToController);
        statusInfo = (TextView) findViewById(R.id.statusInfo);
        howToUse = (Button) findViewById(R.id.HowToUse);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 999) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };

        enterIP.setFilters(filters);

        howToUse.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        howToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mInterstitialAd != null){
                    mInterstitialAd.show(MainActivity.this);

                    Log.v("got ads","got ads");

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            Intent intent = new Intent(MainActivity.this, HowToUse.class);
                            startActivity(intent);
                            Log.v("ads dissmiessd","ads dismissed");

                            mInterstitialAd = null;
                            setAds();
                        }
                    });

                }else{
                    Intent intent = new Intent(MainActivity.this, HowToUse.class);
                    startActivity(intent);
                    Log.v("else", "else");
                }



            }
        });

        continueToController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected){
                    connected = false;
                    continueToController.setBackground(getResources().getDrawable(R.drawable.faded_button));

                    boolean addLoaded = false;if(mInterstitialAd != null){
                        mInterstitialAd.show(MainActivity.this);

                        Log.v("got ads","got ads");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Intent intent = new Intent(MainActivity.this, KeyboardController.class);
                                intent.putExtra("IP", SERVER_IP);
                                startActivity(intent);
                                Log.v("ads dissmiessd","ads dismissed");

                                mInterstitialAd = null;
                                setAds();
                            }
                        });

                    }else{
                        Intent intent = new Intent(MainActivity.this, KeyboardController.class);
                        intent.putExtra("IP", SERVER_IP);
                        startActivity(intent);
                        Log.v("else", "else");
                    }




                }
            }
        });



        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Log.v("Main", enterIP.getText().toString());

                SERVER_IP = enterIP.getText().toString();

                thread1 = new Thread(new Thread1());
                thread1.start();


            }
        });
//        initiateSocketConnection();
    }

    public void setAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.interstitial_ads_id), adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    mInterstitialAd = null;
                }
            });
    }


    private PrintWriter output;
    private BufferedReader input;



    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, 5000);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output.print("0 0");
                output.flush();

                connected = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        continueToController.setBackgroundColor(0xFF6200EA);
                        continueToController.setBackground(getResources().getDrawable(R.drawable.button));
                        statusInfo.setTextColor(0xFF109C38);
                        statusInfo.setText("Connected");
                        statusInfo.setVisibility(View.VISIBLE);
                    }
                });

                statusInfo.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusInfo.setVisibility(View.GONE);
                    }
                }, 1500);

            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        continueToController.setBackgroundColor(0x426200EA);
                        continueToController.setBackground(getResources().getDrawable(R.drawable.faded_button));
                        statusInfo.setTextColor(0xFFFB4444);
                        statusInfo.setText("Try Again!!!");
                        statusInfo.setVisibility(View.VISIBLE);
                    }
                });

                statusInfo.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusInfo.setVisibility(View.GONE);
                    }
                }, 1500);
            }
        }
    }
}