package com.kridacreations.mouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MouseController extends AppCompatActivity implements SensorEventListener {
    // This text shows any error or warning
    EditText enterMessage;
    // Getting click and scroll buttons
    Button leftClick, rightClick, middleClick, scrollDown, scrollUp;
    ImageButton switchToKeyboard;
    String SERVER_IP, clickInfo;
    TextView gyroscopeAlert, holdTextView, tapToUnhold, connectionDot1, connectionDot2, connectionDot3;
    LinearLayout holdLayout;

    // Current value and coordinates of gyroscope
    float curX = 0, curY = 0, curZ, setX = 0, setY = 0, setZ = 0;

    Boolean isHold = false, isScrollingUp = false, isScrollingDown = false;

    // Socket and printwriter for sending data to server
    Thread thread2 = null;

    // Getting sensor manager and gyroscope sensor for rotation
    Sensor gyroscopeSensor;
    SensorManager SM;

    Runnable mouseMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse_controller);

        Log.v("mouse controller", "mouse controller");

        getSupportActionBar().setTitle("Controller");

        Log.v("mouse controller", "mouse controller");


        Intent intent = getIntent();
        SERVER_IP = intent.getStringExtra("IP");
        Log.v("mouse controller", "mouse controller");

        leftClick = (Button) findViewById(R.id.leftClick);
        rightClick = (Button) findViewById(R.id.rightClick);
        middleClick = (Button) findViewById(R.id.middleClick);
        scrollDown = (Button) findViewById(R.id.scrollDOWN);
        scrollUp = (Button) findViewById(R.id.scrollUP);
        switchToKeyboard = (ImageButton) findViewById(R.id.switch_to_keyboard);
        gyroscopeAlert = (TextView) findViewById(R.id.gyroscope_alert);
        holdTextView = (TextView) findViewById(R.id.hold);
        tapToUnhold = (TextView) findViewById(R.id.tapToUnhold);
        holdLayout = (LinearLayout) findViewById(R.id.hold_layout);
        connectionDot1 = (TextView) findViewById(R.id.connection_dot_1);
        connectionDot2 = (TextView) findViewById(R.id.connection_dot_2);
        connectionDot3 = (TextView) findViewById(R.id.connection_dot_3);

        // Getting sensor manager and gyroscope sensor for rotation
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        // If gyroscope sensor is not present, show alert
        if(gyroscopeSensor == null){
            gyroscopeAlert.setVisibility(View.VISIBLE);
        }
        Log.v("mouse controller", "mouse controller");

        // Registering gyroscope sensor
        SM.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);

        switchToKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInfo = "keyboard";
                new java.lang.Thread(new KeyboardSwitch()).start();

                Intent intent = new Intent(MouseController.this, KeyboardController.class);
                intent.putExtra("IP", SERVER_IP);
                startActivity(intent);

                finish();
            }
        });

        // This button will hold event to server
        // Which means mouse will not move when we are on hold
        // Press again to unhold
        holdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHold = !isHold;
                if(isHold){
                    holdTextView.setVisibility(View.VISIBLE);
                    tapToUnhold.setVisibility(View.VISIBLE);
                    holdLayout.setBackground(getResources().getDrawable(R.drawable.hold_view));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holdLayout.setElevation(6f);
                        tapToUnhold.setElevation(6f);
                        holdTextView.setElevation(6f);
                    }
                    Log.v("point pressed", "point pressed");
                }else{
                    holdTextView.setVisibility(View.INVISIBLE);
                    tapToUnhold.setVisibility(View.INVISIBLE);
                    holdLayout.setBackground(getResources().getDrawable(R.drawable.invisible_layout));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holdLayout.setElevation(2f);
                        tapToUnhold.setElevation(2f);
                        holdTextView.setElevation(2f);
                    }
                    Log.v("point up", "point up");
                }
            }
        });

        mouseMove = new Runnable() {
            @Override
            public void run() {
                        if(!isHold) {
                            try {
                                Socket socket = new Socket(SERVER_IP, 5000);
                                PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                                outToServer.print(curX * 60);
                                outToServer.print(" ");
                                outToServer.print(curZ * 60);
                                outToServer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
            }
        };

        thread2 = new Thread(mouseMove);

        Log.v("mouse controller", "mouse controller");

        leftClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInfo = "L";
                new java.lang.Thread(new SendMouseCommand()).start();
            }
        });

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInfo = "R";
                new java.lang.Thread(new SendMouseCommand()).start();
            }
        });

        middleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInfo = "M";
                new java.lang.Thread(new SendMouseCommand()).start();
            }
        });

        scrollUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInfo = "U";
                new java.lang.Thread(new SendMouseCommand()).start();
                scrollDown.setText("DOWN");
                scrollUp.setText("UP");
            }
        });

        scrollUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickInfo = "LongScrollUp";
                new java.lang.Thread(new SendMouseCommand()).start();

                isScrollingUp = !isScrollingUp;

                if(isScrollingUp){
                    scrollUp.setText("UP ^");
                    scrollDown.setText("DOWN");
                    isScrollingDown = false;
                }else{
                    scrollUp.setText("UP");
                }

                return true;
            }
        });

        scrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInfo = "D";
                new java.lang.Thread(new SendMouseCommand()).start();
                scrollDown.setText("DOWN");
                scrollUp.setText("UP");
            }
        });

        scrollDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickInfo = "LongScrollDown";
                new java.lang.Thread(new SendMouseCommand()).start();

                isScrollingDown = !isScrollingDown;

                if(isScrollingDown){
                    scrollDown.setText("DOWN ^");
                    scrollUp.setText("UP");
                    isScrollingUp = false;
                }else{
                    scrollDown.setText("DOWN");
                }

                return true;
            }
        });

        Log.v("mouse controller", "mouse controller");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        curX = (float) (((int)(event.values[0] * 100)) / 100.0);
        curY = (float) (((int)(event.values[1] * 100)) / 100.0);
        curZ = (float) (((int)(event.values[2] * 100)) / 100.0);

        new java.lang.Thread(new Thread2()).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No use
    }

    class KeyboardSwitch implements Runnable{
        @Override
        public void run() {
            try {
                Socket socket = new Socket(SERVER_IP, 5000);
                PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                outToServer.print(clickInfo);

                outToServer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendMouseCommand implements Runnable{
        @Override
        public void run() {
            if(!isHold) {
                try {
                    Socket socket = new Socket(SERVER_IP, 5000);
                    PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                    outToServer.print(clickInfo);

                    outToServer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread2 implements Runnable{
        @Override
        public void run() {

            if(!isHold) {
                try {
                    Socket socket = new Socket(SERVER_IP, 5000);
                    PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                    outToServer.print(curX * 60);
                    outToServer.print(" ");
                    outToServer.print(curZ * 60);


                    outToServer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v("MouseController", "On pause");
        SM.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("MouseController", "On destroy");
        SM.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("MouseController", "On resume");
        SM.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clickInfo = "exit";
        new java.lang.Thread(new SendMouseCommand()).start();
    }
}