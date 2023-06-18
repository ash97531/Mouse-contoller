package com.kridacreations.mouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class KeyboardController extends AppCompatActivity {

    // Setting up keyboard buttons
    String [] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    String [] alphabetShifted = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    String [] number = {"1","2","3","4","5","6","7","8","9","0"};
    String [] numberShifted = {"!","@","#","$","%","^","&","*","(",")"};

    String [] sign = {"`","-","=","[","]","\\",";","\'",",",".","/"};
    String [] signShifted = {"~","_","+","{","}","|",":","\"","<",">","?"};

    String [] funArray = {"F1", "F2", "F3","F4","F5","F6","F7","F8","F9","F10","F11","F12"};

    Button [] alphabetBtnArr;
    Button [] numberBtnArr;
    Button [] signBtnArr;

    Button backspace,tab,capslock,shift1,shift2, func,enter,alt1,alt2,ctrl1,ctrl2, spacebar;

    ImageButton window ,upArrow,downArrow ,leftArrow ,rightArrow, switchToMouse;

    String SERVER_IP, keyInfo;

    boolean alphaBtnShift = false, numberBtnShift = false, signBtnShift = false, functionBtnShift = false;
    boolean shiftDown = false, ctrlDown = false, altDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_controller);


        getSupportActionBar().hide();

        Intent intent = getIntent();
        SERVER_IP = intent.getStringExtra("IP");

        // Getting keyboard buttons from XML
        alphabetBtnArr = new Button[]{
                (Button) findViewById(R.id.a_btn),
                (Button) findViewById(R.id.b_btn),
                (Button) findViewById(R.id.c_btn),
                (Button) findViewById(R.id.d_btn),
                (Button) findViewById(R.id.e_btn),
                (Button) findViewById(R.id.f_btn),
                (Button) findViewById(R.id.g_btn),
                (Button) findViewById(R.id.h_btn),
                (Button) findViewById(R.id.i_btn),
                (Button) findViewById(R.id.j_btn),
                (Button) findViewById(R.id.k_btn),
                (Button) findViewById(R.id.l_btn),
                (Button) findViewById(R.id.m_btn),
                (Button) findViewById(R.id.n_btn),
                (Button) findViewById(R.id.o_btn),
                (Button) findViewById(R.id.p_btn),
                (Button) findViewById(R.id.q_btn),
                (Button) findViewById(R.id.r_btn),
                (Button) findViewById(R.id.s_btn),
                (Button) findViewById(R.id.t_btn),
                (Button) findViewById(R.id.u_btn),
                (Button) findViewById(R.id.v_btn),
                (Button) findViewById(R.id.w_btn),
                (Button) findViewById(R.id.x_btn),
                (Button) findViewById(R.id.y_btn),
                (Button) findViewById(R.id.z_btn)
        };

        numberBtnArr = new Button[]{
                (Button) findViewById(R.id.one),
                (Button) findViewById(R.id.two),
                (Button) findViewById(R.id.switch_to_keyboard),
                (Button) findViewById(R.id.four),
                (Button) findViewById(R.id.five),
                (Button) findViewById(R.id.six),
                (Button) findViewById(R.id.seven),
                (Button) findViewById(R.id.eight),
                (Button) findViewById(R.id.nine),
                (Button) findViewById(R.id.zero)
        };

        signBtnArr = new Button[]{
                (Button) findViewById(R.id.back_tick),
                (Button) findViewById(R.id.minus),
                (Button) findViewById(R.id.equal),
                (Button) findViewById(R.id.left_sq_brac_btn),
                (Button) findViewById(R.id.right_sq_brac_btn),
                (Button) findViewById(R.id.back_slash),
                (Button) findViewById(R.id.colon),
                (Button) findViewById(R.id.apostrophe),
                (Button) findViewById(R.id.comma),
                (Button) findViewById(R.id.dot),
                (Button) findViewById(R.id.forward_slash)
        };

        backspace = (Button) findViewById(R.id.backspace);
        tab = (Button) findViewById(R.id.tab);
        capslock = (Button) findViewById(R.id.caps_lock);
        shift1 = (Button) findViewById(R.id.shift_1);
        shift2 = (Button) findViewById(R.id.shift_2);
        func = (Button) findViewById(R.id.function);
        enter = (Button) findViewById(R.id.enter);
        alt1 = (Button) findViewById(R.id.alt_1);
        alt2 = (Button) findViewById(R.id.alt_2);
        ctrl1 = (Button) findViewById(R.id.ctrl_1);
        ctrl2 = (Button) findViewById(R.id.ctrl_2);
        spacebar = (Button) findViewById(R.id.spacebar);

        window = (ImageButton) findViewById(R.id.windows);
        upArrow = (ImageButton) findViewById(R.id.up_arrow);
        downArrow = (ImageButton) findViewById(R.id.down_arrow);
        leftArrow = (ImageButton) findViewById(R.id.left_arrow);
        rightArrow = (ImageButton) findViewById(R.id.right_arrow);
        switchToMouse = (ImageButton) findViewById(R.id.switch_to_mouse);

        // There can be two possibilities for shift key: single or hold
        // Single: When shift key is pressed and released
        shift1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shiftKeyPressed();
            }
        });
        // Hold: When shift key is pressed and not released
        shift1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!shiftDown) {
                    keyInfo = "shiftDown";
                    shift1.setText("SHIFT ^");
                    shift2.setText("SHIFT ^");
                } else {
                    keyInfo = "shiftUp";
                    shift1.setText("SHIFT");
                    shift2.setText("SHIFT");
                }

                new Thread(new SendKeyboardCommand()).start();

                shiftDown = !shiftDown;
                return true;
            }
        });

        // Same as shift1
        shift2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shiftKeyPressed();
                Log.v("ip address", SERVER_IP);
            }
        });
        shift2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!shiftDown) {
                    keyInfo = "shiftDown";
                    shift1.setText("SHIFT ^");
                    shift2.setText("SHIFT ^");
                } else {
                    keyInfo = "shiftUp";
                    shift1.setText("SHIFT");
                    shift2.setText("SHIFT");
                }

                new Thread(new SendKeyboardCommand()).start();

                shiftDown = !shiftDown;
                return true;
            }
        });

        // There can be two possibilities for control key: single or hold
        // Single: When ctrl key is pressed and released
        ctrl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "ctrl";
                new Thread(new SendKeyboardCommand()).start();
            }
        });
        // Hold: When ctrl key is pressed and not released
        ctrl1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!ctrlDown){
                    keyInfo = "ctrlDown";
                    ctrl1.setText("CTRL ^");
                    ctrl2.setText("CTRL ^");
                }else{
                    keyInfo = "ctrlUp";
                    ctrl1.setText("CTRL");
                    ctrl2.setText("CTRL");
                }

                new Thread(new SendKeyboardCommand()).start();

                ctrlDown = !ctrlDown;
                return true;
            }
        });

        // Same as ctrl1
        ctrl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "ctrl";
                new Thread(new SendKeyboardCommand()).start();
            }
        });
        ctrl2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!ctrlDown){
                    keyInfo = "ctrlDown";
                    ctrl1.setText("CTRL ^");
                    ctrl2.setText("CTRL ^");
                }else{
                    keyInfo = "ctrlUp";
                    ctrl1.setText("CTRL");
                    ctrl2.setText("CTRL");
                }

                new Thread(new SendKeyboardCommand()).start();

                ctrlDown = !ctrlDown;
                return true;
            }
        });

        alt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "alt";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        alt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "alt";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "backspace";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "enter";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "tab";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        spacebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "space";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        capslock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capsKeyPressed();
            }
        });

        window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "win";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "up";
                new Thread(new SendKeyboardCommand()).start();
            }
        });
        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "down";
                new Thread(new SendKeyboardCommand()).start();
            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "right";
                new Thread(new SendKeyboardCommand()).start();
            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyInfo = "left";
                new Thread(new SendKeyboardCommand()).start();
            }
        });

        func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functionKeyPressed();
            }
        });

        // This function to switch to mouse movement page
        // It will swithc to MouseController activity with the same IP address
        switchToMouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyInfo = "mouse";
                new java.lang.Thread(new SendKeyboardCommand()).start();

                Intent i = new Intent(KeyboardController.this, MouseController.class);
                i.putExtra("IP", SERVER_IP);
                startActivity(i);
                finish();
            }
        });

        // Setting key for alphabets, numbers and signs
        for (int i=0; i<26; i++){
            int finalI = i;

            alphabetBtnArr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    keyInfo = alphabetBtnArr[finalI].getText().toString();
                    new java.lang.Thread(new SendKeyboardCommand()).start();
                }
            });

            if(i<10){
                numberBtnArr[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keyInfo = numberBtnArr[finalI].getText().toString();
                        new java.lang.Thread(new SendKeyboardCommand()).start();
                    }
                });
            }

            if(i<11){
                signBtnArr[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keyInfo = signBtnArr[finalI].getText().toString();
                        new java.lang.Thread(new SendKeyboardCommand()).start();
                    }
                });
            }

        }
    }

    // Creating a thread to send the key pressed to the server
    // The server will then send the key pressed to the computer
    // And the server will execute the command
    class SendKeyboardCommand implements Runnable{
        @Override
        public void run() {
            try {
                Socket socket = new Socket(SERVER_IP, 5000);
                PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                outToServer.print(keyInfo);

                outToServer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    void functionKeyPressed(){
        functionBtnShift = !functionBtnShift;

        if(functionBtnShift){
            for (int i=0;i<10;i++)
                numberBtnArr[i].setText(funArray[i]);
            signBtnArr[1].setText(funArray[10]);
            signBtnArr[2].setText(funArray[11]);
        }else{
            if(signBtnShift){
                for (int i=0; i<10; i++)
                    numberBtnArr[i].setText(numberShifted[i]);
                signBtnArr[1].setText(signShifted[1]);
                signBtnArr[2].setText(signShifted[2]);
            }else{
                for (int i=0; i<10; i++)
                    numberBtnArr[i].setText(number[i]);
                signBtnArr[1].setText(sign[1]);
                signBtnArr[2].setText(sign[2]);
            }
        }
    }

    void capsKeyPressed(){
        alphaBtnShift = !alphaBtnShift;

        if(alphaBtnShift){
            for (int i=0; i<26; i++)
                alphabetBtnArr[i].setText(alphabetShifted[i]);
        }else{
            for (int i=0; i<26; i++)
                alphabetBtnArr[i].setText(alphabet[i]);
        }
    }

    void shiftKeyPressed(){
        signBtnShift = !signBtnShift;
        numberBtnShift = !numberBtnShift;
        capsKeyPressed();

        if(signBtnShift){
            for (int i=0; i<10; i++)
                numberBtnArr[i].setText(numberShifted[i]);
            for (int i=0; i<11; i++)
                signBtnArr[i].setText(signShifted[i]);
        }else{
            for (int i=0; i<10; i++)
                numberBtnArr[i].setText(number[i]);
            for (int i=0; i<11; i++)
                signBtnArr[i].setText(sign[i]);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        keyInfo = "exit";
        new Thread(new SendKeyboardCommand()).start();
    }
}