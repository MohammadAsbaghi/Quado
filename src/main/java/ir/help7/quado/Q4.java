package ir.help7.quado;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import ir.adad.client.Adad;
import ir.help7.quado.interfaces.Go_Launcher;
import ir.help7.quado.interfaces.Go_q3;
import ir.help7.quado.interfaces.Go_q5;
import ir.help7.quado.models.Cube;


/**
 * A simple {@link Fragment} subclass.
 */
public class Q4 extends Fragment implements View.OnClickListener, View.OnTouchListener {

    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13, btn14, btn15, btn16, btn_q4_new;
    TextView q4_bestRecord;
    View red_indicator1, green_indicator1, blue_indicator1, yellow_indicator1, whiteGround;
    Point[] points = new Point[64];
    Cube[] cubes = new Cube[64];
    int[] positions = new int[64];
    int l;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RelativeLayout q4_framelayout;

    Go_Launcher go_launcher;
    Go_q3 go_q3;
    Go_q5 go_q5;
    DisplayMetrics displayMetrics;
    int width;


    // timer things
    TextView timerValue;
    long startTime = 0;
    Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    Runnable updateTimerThread;
    long recordTime;

    int animation_time = 150;


    public Q4() {
        // Required empty public constructor
    }


    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // this is for adad advertise enable that when app certificated
        Adad.showInterstitialAd(getActivity());

        // init timer runnable
        InitRunnable();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_q4, container, false);
        view.setOnTouchListener(this);
        q4_framelayout = view.findViewById(R.id.q4_framelayout);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = pref.edit();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        l = 2*width/25;
        startTimer();
        InitPositions();
        InitPoints();
        InitCubes();
        InitButtons(view);
        InitSeprater(width);
        locate_cubes();

        if (overChecker()){
            random_rank();
        }
        positionsToPref();
        return view;
    }

    private void InitRunnable() {
        updateTimerThread = new Runnable() {
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

                updatedTime = timeSwapBuff + timeInMilliseconds;

                int secs = (int) (updatedTime / 1000);
                int mins = secs / 60;
                int hours = mins/60;
                secs = secs % 60;
                int milliseconds = (int) (updatedTime % 100);
                String s = "" + hours +":"+ mins + ":"
                        + String.format(Locale.US,"%02d", secs) + ":"
                        + String.format(Locale.US,"%02d", milliseconds);
                timerValue.setText(s);
                customHandler.postDelayed(this, 10);
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        go_launcher = (Go_Launcher) context;
        go_q3 = (Go_q3) context;
        go_q5 = (Go_q5) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        go_launcher = null;
        go_q3 = null;
        go_q5 = null;
    }

    public float getWidthInches() {
        displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels/displayMetrics.xdpi;
    }

    private void locate_cubes() {
        for (int j = 0; j<cubes.length; j++) {
            q4_framelayout.addView(cubes[j]);
            cubes[j].getLayoutParams().width = l;
            cubes[j].getLayoutParams().height = l;
            cubes[j].setBackgroundColor(cubes[j].getColor());
            cubes[j].setTranslationX(cubes[j].getPoint().x);
            cubes[j].setTranslationY(cubes[j].getPoint().y);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cubes[j].setElevation(8);
            }
        }
    }

    private void InitPositions() {
        for (int i = 0; i<positions.length; i++){
            positions[i] = pref.getInt("q4pos"+i,i);
        }
    }

    private void positionsToPref() {
        for (int i = 0; i<positions.length; i++){
            editor.putInt("q4pos"+i,positions[i]).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        positionsToPref();
        stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        InitPositions();
        for (int i = 0; i < cubes.length; i++) {
            cubes[positions[i]].setPoint(points[i]);
            cubes[positions[i]].setTranslationY(cubes[positions[i]].getPoint().y);
            cubes[positions[i]].setTranslationX(cubes[positions[i]].getPoint().x);
        }
        startTimer();
    }


    private void InitSeprater( int width) {

        red_indicator1 = new View(getContext());
        green_indicator1 = new View(getContext());
        blue_indicator1 = new View(getContext());
        yellow_indicator1 = new View(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            red_indicator1.setElevation(3);
            green_indicator1.setElevation(3);
            blue_indicator1.setElevation(3);
            yellow_indicator1.setElevation(3);
        }
        whiteGround = new View(getContext());

        indicator_setter(whiteGround, points[0].x - (5*l / 12) , points[0].y - (5*l / 12), 10 * l + 5*l/6 , 10 * l + 5*l/6, getResources().getColor(R.color.text_color));

        indicator_setter(red_indicator1,points[0].x-l/4,points[0].y-l/4,21*l/4-width/100,21*l/4-width/100,getResources().getColor(R.color.red_indicator));
        indicator_setter(blue_indicator1,points[4].x-(l/4)+2*width/200,points[0].y-l/4,21*l/4-width/100,21*l/4-width/100,getResources().getColor(R.color.blue_indicator));
        indicator_setter(green_indicator1,points[0].x-l/4,points[32].y-l/4+2*width/200,21*l/4-width/100,21*l/4-width/100,getResources().getColor(R.color.green_indicator));
        indicator_setter(yellow_indicator1,points[4].x-(l/4)+2*width/200,points[32].y-l/4+2*width/200,21*l/4-width/100,21*l/4-width/100,getResources().getColor(R.color.yellow_indicator));

    }

    public void indicator_setter(View view, float x, float y, int width, int height, int color){
        q4_framelayout.addView(view);
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.setTranslationY(y);
        view.setTranslationX(x);
        view.setBackgroundColor(color);
    }

    private void InitButtons(View view) {

        q4_bestRecord =  view.findViewById(R.id.q4_bestRecord);
        q4_bestRecord.setTextSize(getWidthInches()*7);
        q4_bestRecord.setTranslationY(l/2);
        q4_bestRecord.setTranslationX(points[0].x-l/4);
        if (pref.getLong("q4bestRecord",0) > 0){
            int secs = (int) (pref.getLong("q4bestRecord",0) / 1000);
            int mins = secs / 60;
            int hours = mins/60;
            secs = secs % 60;
            int milliseconds = (int) (pref.getLong("q4bestRecord",0) % 100);
            String s ="" + hours +":"+ mins + ":"
                    + String.format(Locale.US,"%02d", secs) + ":"
                    + String.format(Locale.US,"%02d", milliseconds);
            q4_bestRecord.setText(s);
        } else {
            q4_bestRecord.setVisibility(View.GONE);
        }

        timerValue =  view.findViewById(R.id.q4_timer);
        timerValue.setTextSize(getWidthInches()*10);
        timerValue.setTranslationY(points[60].y+3*l);
        timerValue.setTranslationX(points[4].x-l/4);


        btn1 =  view.findViewById(R.id.q4btn1);
        btn2 =  view.findViewById(R.id.q4btn2);
        btn3 =  view.findViewById(R.id.q4btn3);
        btn4 =  view.findViewById(R.id.q4btn4);
        btn5 =  view.findViewById(R.id.q4btn5);
        btn6 =  view.findViewById(R.id.q4btn6);
        btn7 =  view.findViewById(R.id.q4btn7);
        btn8 =  view.findViewById(R.id.q4btn8);
        btn9 =  view.findViewById(R.id.q4btn9);
        btn10 =  view.findViewById(R.id.q4btn10);
        btn11 =  view.findViewById(R.id.q4btn11);
        btn12 =  view.findViewById(R.id.q4btn12);
        btn13 =  view.findViewById(R.id.q4btn13);
        btn14 =  view.findViewById(R.id.q4btn14);
        btn15 =  view.findViewById(R.id.q4btn15);
        btn16 =  view.findViewById(R.id.q4btn16);

        btn_q4_new =  view.findViewById(R.id.btn_q4_new);
        btn_q4_new.setTextSize(getWidthInches()*7);
        btn_q4_new.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/english_font.ttf"));
        btn_q4_new.setTranslationX(points[4].x-l/4);
        btn_q4_new.setTranslationY(5*l/12);

        btnFixer(btn1,new Point(points[0].x-5*l/4,points[0].y));
        btnFixer(btn2,new Point(points[8].x-5*l/4,points[8].y));
        btnFixer(btn3,new Point(points[16].x-5*l/4,points[16].y));
        btnFixer(btn4,new Point(points[24].x-5*l/4,points[24].y));
        btnFixer(btn5,new Point(points[32].x-5*l/4,points[32].y));
        btnFixer(btn6,new Point(points[40].x-5*l/4,points[40].y));
        btnFixer(btn7,new Point(points[48].x-5*l/4,points[48].y));
        btnFixer(btn8,new Point(points[56].x-5*l/4,points[56].y));
        btnFixer(btn9,new Point(points[56].x,points[56].y+5*l/4));
        btnFixer(btn10,new Point(points[57].x,points[57].y+5*l/4));
        btnFixer(btn11,new Point(points[58].x,points[58].y+5*l/4));
        btnFixer(btn12,new Point(points[59].x,points[59].y+5*l/4));
        btnFixer(btn13,new Point(points[60].x,points[60].y+5*l/4));
        btnFixer(btn14,new Point(points[61].x,points[61].y+5*l/4));
        btnFixer(btn15,new Point(points[62].x,points[62].y+5*l/4));
        btnFixer(btn16,new Point(points[63].x,points[63].y+5*l/4));

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);
        btn12.setOnClickListener(this);
        btn13.setOnClickListener(this);
        btn14.setOnClickListener(this);
        btn15.setOnClickListener(this);
        btn16.setOnClickListener(this);
        btn_q4_new.setOnClickListener(this);
    }

    public void btnFixer(Button btn, Point p){
        btn.setTranslationX(p.x);
        btn.setTranslationY(p.y);
        btn.getLayoutParams().height = l;
        btn.getLayoutParams().width = l;
    }

    private void InitCubes() {
        cubes[0] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[0]]);
        cubes[1] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[1]]);
        cubes[2] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[2]]);
        cubes[3] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[3]]);
        cubes[4] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[4]]);
        cubes[5] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[5]]);
        cubes[6] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[6]]);
        cubes[7] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[7]]);
        cubes[8] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[8]]);
        cubes[9] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[9]]);
        cubes[10] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[10]]);
        cubes[11] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[11]]);
        cubes[12] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[12]]);
        cubes[13] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[13]]);
        cubes[14] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[14]]);
        cubes[15] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[15]]);
        cubes[16] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[16]]);
        cubes[17] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[17]]);
        cubes[18] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[18]]);
        cubes[19] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[19]]);
        cubes[20] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[20]]);
        cubes[21] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[21]]);
        cubes[22] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[22]]);
        cubes[23] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[23]]);
        cubes[24] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[24]]);
        cubes[25] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[25]]);
        cubes[26] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[26]]);
        cubes[27] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[27]]);
        cubes[28] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[28]]);
        cubes[29] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[29]]);
        cubes[30] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[30]]);
        cubes[31] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[31]]);
        cubes[32] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[32]]);
        cubes[33] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[33]]);
        cubes[34] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[34]]);
        cubes[35] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[35]]);
        cubes[36] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[36]]);
        cubes[37] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[37]]);
        cubes[38] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[38]]);
        cubes[39] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[39]]);
        cubes[40] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[40]]);
        cubes[41] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[41]]);
        cubes[42] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[42]]);
        cubes[43] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[43]]);
        cubes[44] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[44]]);
        cubes[45] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[45]]);
        cubes[46] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[46]]);
        cubes[47] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[47]]);
        cubes[48] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[48]]);
        cubes[49] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[49]]);
        cubes[50] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[50]]);
        cubes[51] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[51]]);
        cubes[52] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[52]]);
        cubes[53] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[53]]);
        cubes[54] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[54]]);
        cubes[55] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[55]]);
        cubes[56] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[56]]);
        cubes[57] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[57]]);
        cubes[58] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[58]]);
        cubes[59] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[59]]);
        cubes[60] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[60]]);
        cubes[61] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[61]]);
        cubes[62] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[62]]);
        cubes[63] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[63]]);

    }

    private void InitPoints() {
        points[0] = new Point(2*l-l/4,3*l);
        points[1] = new Point(13*l/4-l/4,3*l);
        points[2] = new Point(9*l/2-l/4,3*l);
        points[3] = new Point(23*l/4-l/4,3*l);
        points[4] = new Point(29*l/4-l/4,3*l);
        points[5] = new Point(17*l/2-l/4,3*l);
        points[6] = new Point(39*l/4-l/4,3*l);
        points[7] = new Point(11*l-l/4,3*l);
        points[8] = new Point(2*l-l/4,17*l/4);
        points[9] = new Point(13*l/4-l/4,17*l/4);
        points[10] = new Point(9*l/2-l/4,17*l/4);
        points[11] = new Point(23*l/4-l/4,17*l/4);
        points[12] = new Point(29*l/4-l/4,17*l/4);
        points[13] = new Point(17*l/2-l/4,17*l/4);
        points[14] = new Point(39*l/4-l/4,17*l/4);
        points[15] = new Point(11*l-l/4,17*l/4);
        points[16] = new Point(2*l-l/4,11*l/2);
        points[17] = new Point(13*l/4-l/4,11*l/2);
        points[18] = new Point(9*l/2-l/4,11*l/2);
        points[19] = new Point(23*l/4-l/4,11*l/2);
        points[20] = new Point(29*l/4-l/4,11*l/2);
        points[21] = new Point(17*l/2-l/4,11*l/2);
        points[22] = new Point(39*l/4-l/4,11*l/2);
        points[23] = new Point(11*l-l/4,11*l/2);
        points[24] = new Point(2*l-l/4,27*l/4);
        points[25] = new Point(13*l/4-l/4,27*l/4);
        points[26] = new Point(9*l/2-l/4,27*l/4);
        points[27] = new Point(23*l/4-l/4,27*l/4);
        points[28] = new Point(29*l/4-l/4,27*l/4);
        points[29] = new Point(17*l/2-l/4,27*l/4);
        points[30] = new Point(39*l/4-l/4,27*l/4);
        points[31] = new Point(11*l-l/4,27*l/4);
        points[32] = new Point(2*l-l/4,33*l/4);
        points[33] = new Point(13*l/4-l/4,33*l/4);
        points[34] = new Point(9*l/2-l/4,33*l/4);
        points[35] = new Point(23*l/4-l/4,33*l/4);
        points[36] = new Point(29*l/4-l/4,33*l/4);
        points[37] = new Point(17*l/2-l/4,33*l/4);
        points[38] = new Point(39*l/4-l/4,33*l/4);
        points[39] = new Point(11*l-l/4,33*l/4);
        points[40] = new Point(2*l-l/4,19*l/2);
        points[41] = new Point(13*l/4-l/4,19*l/2);
        points[42] = new Point(9*l/2-l/4,19*l/2);
        points[43] = new Point(23*l/4-l/4,19*l/2);
        points[44] = new Point(29*l/4-l/4,19*l/2);
        points[45] = new Point(17*l/2-l/4,19*l/2);
        points[46] = new Point(39*l/4-l/4,19*l/2);
        points[47] = new Point(11*l-l/4,19*l/2);
        points[48] = new Point(2*l-l/4,43*l/4);
        points[49] = new Point(13*l/4-l/4,43*l/4);
        points[50] = new Point(9*l/2-l/4,43*l/4);
        points[51] = new Point(23*l/4-l/4,43*l/4);
        points[52] = new Point(29*l/4-l/4,43*l/4);
        points[53] = new Point(17*l/2-l/4,43*l/4);
        points[54] = new Point(39*l/4-l/4,43*l/4);
        points[55] = new Point(11*l-l/4,43*l/4);
        points[56] = new Point(2*l-l/4,12*l);
        points[57] = new Point(13*l/4-l/4,12*l);
        points[58] = new Point(9*l/2-l/4,12*l);
        points[59] = new Point(23*l/4-l/4,12*l);
        points[60] = new Point(29*l/4-l/4,12*l);
        points[61] = new Point(17*l/2-l/4,12*l);
        points[62] = new Point(39*l/4-l/4,12*l);
        points[63] = new Point(11*l-l/4,12*l);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btn1.getId()){
            pos_exchange(0,1,2,3,4,5,6,7);
        } else if (v.getId() == btn2.getId()){
            pos_exchange(8,9,10,11,12,13,14,15);
        } else if (v.getId() == btn3.getId()){
            pos_exchange(16,17,18,19,20,21,22,23);
        } else if (v.getId() == btn4.getId()){
            pos_exchange(24,25,26,27,28,29,30,31);
        }else if (v.getId() == btn5.getId()){
            pos_exchange(32,33,34,35,36,37,38,39);
        }else if (v.getId() == btn6.getId()){
            pos_exchange(40,41,42,43,44,45,46,47);
        }else if (v.getId() == btn7.getId()){
            pos_exchange(48,49,50,51,52,53,54,55);
        }else if (v.getId() == btn8.getId()){
            pos_exchange(56,57,58,59,60,61,62,63);
        }else if (v.getId() == btn9.getId()){
            pos_exchange(0,8,16,24,32,40,48,56);
        }else if (v.getId() == btn10.getId()){
            pos_exchange(1,9,17,25,33,41,49,57);
        }else if (v.getId() == btn11.getId()){
            pos_exchange(2,10,18,26,34,42,50,58);
        }else if (v.getId() == btn12.getId()){
            pos_exchange(3,11,19,27,35,43,51,59);
        }else if (v.getId() == btn13.getId()){
            pos_exchange(4,12,20,28,36,44,52,60);
        }else if (v.getId() == btn14.getId()){
            pos_exchange(5,13,21,29,37,45,53,61);
        }else if (v.getId() == btn15.getId()){
            pos_exchange(6,14,22,30,38,46,54,62);
        }else if (v.getId() == btn16.getId()){
            pos_exchange(7,15,23,31,39,47,55,63);
        }else if (v.getId() == btn_q4_new.getId()){
            stopTimer();
            editor.putLong("q4timeSwapBuff",0).commit();
            timeSwapBuff = 0;
            random_rank();
            startTimer();
        }
    }



    public void pos_exchange(int a, int b, int c, int d, int e, int f, int g, int h){
        int temp = positions[a];
        positions[a] = positions[h];
        positions[h] = temp;
        temp = positions[b];
        positions[b] = positions[g];
        positions[g] = temp;
        temp = positions[c];
        positions[c] = positions[f];
        positions[f] = temp;
        temp = positions[d];
        positions[d] = positions[e];
        positions[e] = temp;

        cubes[positions[a]].setPoint(points[a]);
        cubes[positions[b]].setPoint(points[b]);
        cubes[positions[c]].setPoint(points[c]);
        cubes[positions[d]].setPoint(points[d]);
        cubes[positions[e]].setPoint(points[e]);
        cubes[positions[f]].setPoint(points[f]);
        cubes[positions[g]].setPoint(points[g]);
        cubes[positions[h]].setPoint(points[h]);

        cubes[positions[a]].animate().translationY(cubes[positions[a]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[b]].animate().translationY(cubes[positions[b]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[c]].animate().translationY(cubes[positions[c]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[d]].animate().translationY(cubes[positions[d]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[e]].animate().translationY(cubes[positions[e]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[f]].animate().translationY(cubes[positions[f]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[g]].animate().translationY(cubes[positions[g]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[h]].animate().translationY(cubes[positions[h]].getPoint().y).setDuration(animation_time).start();

        cubes[positions[a]].animate().translationX(cubes[positions[a]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[b]].animate().translationX(cubes[positions[b]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[c]].animate().translationX(cubes[positions[c]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[d]].animate().translationX(cubes[positions[d]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[e]].animate().translationX(cubes[positions[e]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[f]].animate().translationX(cubes[positions[f]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[g]].animate().translationX(cubes[positions[g]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[h]].animate().translationX(cubes[positions[h]].getPoint().x).setDuration(animation_time).start();

        if (overChecker()){
            showOver();
        }
    }

    public boolean overChecker(){
        if (positions[0]==0 && positions[1]==1 && positions[2]==2 && positions[3]==3 &&
            positions[4]==4 && positions[5]==5 && positions[6]==6 && positions[7]==7 &&
            positions[8]==8 && positions[9]==9 && positions[10]==10 && positions[11]==11 &&
            positions[12]==12 && positions[13]==13 && positions[14]==14 && positions[15]==15 &&
            positions[16]==16 && positions[17]==17 && positions[18]==18 && positions[19]==19&&
            positions[20]==20 && positions[21]==21 && positions[22]==22 && positions[23]==23 &&
            positions[24]==24 && positions[25]==25 && positions[26]==26 && positions[27]==27 &&
            positions[28]==28 && positions[29]==29 && positions[30]==30 && positions[31]==31 &&
            positions[32]==32 && positions[33]==33 && positions[34]==34 && positions[35]==35 &&
            positions[36]==36 && positions[37]==37 && positions[38]==38 && positions[39]==39 &&
            positions[40]==40 && positions[41]==41 && positions[42]==42 && positions[43]==43 &&
            positions[44]==44 && positions[45]==45 && positions[46]==46 && positions[47]==47 &&
            positions[48]==48 && positions[49]==49 && positions[50]==50 && positions[51]==51 &&
            positions[52]==52 && positions[53]==53 && positions[54]==54 && positions[55]==55 &&
            positions[56]==56 && positions[57]==57 && positions[58]==58 && positions[59]==59 &&
            positions[60]==60 && positions[61]==61 && positions[62]==62 && positions[63]==63){
            return true;
        } else {
            return false;
        }
    }

    public void showOver(){
        Adad.prepareInterstitialAd();
        stopTimer();
        recordTime = updatedTime;
        updatedTime = 0;
        editor.putLong("q4timeSwapBuff",0L).commit();
        timeSwapBuff = 0L;
        if (pref.getLong("q4bestRecord",0L)==0L || pref.getLong("q4bestRecord",0L)>recordTime){
            editor.putLong("q4bestRecord",recordTime).commit();
            updateRecord(recordTime);
        }

        final Dialog d = new Dialog(getContext());
        d.setCancelable(false);
        d.setContentView(R.layout.over_dialog_layout);
        TextView dialogRecordTv = d.findViewById(R.id.dialog_record_tv);
        int secs = (int) (recordTime / 1000);
        int mins = secs / 60;
        int hours = mins/60;
        secs = secs % 60;
        int milliseconds = (int) (recordTime % 100);
        String s = "" + hours +":"+ mins + ":"
                + String.format(Locale.US,"%02d", secs) + ":"
                + String.format(Locale.US,"%02d", milliseconds);
        dialogRecordTv.setText(s);
        Button previousStep = d.findViewById(R.id.btn_previousStep);
        Button nextStep = d.findViewById(R.id.btn_nextStep);
        previousStep.setVisibility(View.VISIBLE);
        nextStep.setVisibility(View.VISIBLE);
        Button playAgain= d.findViewById(R.id.btn_playAgain);
        Typeface english_typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/english_font.ttf");
        dialogRecordTv.setTypeface(english_typeface);
        previousStep.setTypeface(english_typeface);
        nextStep.setTypeface(english_typeface);
        playAgain.setTypeface(english_typeface);
        dialogRecordTv.setTextSize(getWidthInches()*9);
        previousStep.setTextSize(getWidthInches()*9);
        nextStep.setTextSize(getWidthInches()*9);
        playAgain.setTextSize(getWidthInches()*9);
        previousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_q3.onGo_q3_click();
                d.dismiss();
            }
        });
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_q5.onGo_q5_click();
                d.dismiss();
            }
        });
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                random_rank();
                startTimer();
            }
        });
        if (recordTime!=0) {
            d.show();
        }
    }

    public void random_rank(){
        Random r = new Random();
        int i = 0;
        while (i<100){
            randomBtn(r).performClick();
            i++;
        }

    }
    public Button randomBtn(Random r){
        int t = r.nextInt(16);
        switch (t){
            case 0:
                return btn1;
            case 1:
                return btn2;
            case 2:
                return btn3;
            case 3:
                return btn4;
            case 4:
                return btn5;
            case 5:
                return btn6;
            case 6:
                return btn7;
            case 7:
                return btn8;
            case 8:
                return btn9;
            case 9:
                return btn10;
            case 10:
                return btn11;
            case 11:
                return btn12;
            case 12:
                return btn13;
            case 13:
                return btn14;
            case 14:
                return btn15;
            case 15:
                return btn16;
            default:
                return null;
        }
    }

    public void startTimer(){
        timeSwapBuff = pref.getLong("q4timeSwapBuff",0);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer(){
        customHandler.removeCallbacks(updateTimerThread);
        timeSwapBuff = updatedTime;
        editor.putLong("q4timeSwapBuff",timeSwapBuff).commit();
    }

    public void updateRecord(long record){
        int secs = (int) (record / 1000);
        int mins = secs / 60;
        int hours = mins/60;
        secs = secs % 60;
        int milliseconds = (int) (record % 100);
        String s = "" + hours +":"+ mins + ":"
                + String.format(Locale.US,"%02d", secs) + ":"
                + String.format(Locale.US,"%02d", milliseconds);
        q4_bestRecord.setText(s);
        q4_bestRecord.setVisibility(View.VISIBLE);
    }

    float x1=0, y1=0,x2=0,y2=0;
    boolean isDraging = false;
    boolean secondPulse = false;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            x1 = event.getX();
            y1 = event.getY();
            isDraging = true;
        }
        if (event.getAction()==MotionEvent.ACTION_UP){
            x2 = event.getX();
            y2 = event.getY();
            isDraging = false;
            secondPulse = true;
        }
        if (!isDraging && secondPulse){
            secondPulse = false;
            if (Math.abs(x1-x2)<=l){
                if (x1>=points[0].x && x1<=points[0].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn9.performClick();
                    }
                }else if (x1>=points[1].x && x1<=points[1].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn10.performClick();
                    }
                }else if (x1>=points[2].x && x1<=points[2].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn11.performClick();
                    }
                }else if (x1>=points[3].x && x1<=points[3].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn12.performClick();
                    }
                }else if (x1>=points[4].x && x1<=points[4].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn13.performClick();
                    }
                }else if (x1>=points[5].x && x1<=points[5].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn14.performClick();
                    }
                }else if (x1>=points[6].x && x1<=points[6].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn15.performClick();
                    }
                }else if (x1>=points[7].x && x1<=points[7].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn16.performClick();
                    }
                }
            }else if (Math.abs(y1-y2)<=l){
                if (y1>=points[0].y && y1<=points[0].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn1.performClick();
                    }
                }else if (y1>=points[8].y && y1<=points[8].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn2.performClick();
                    }
                }else if (y1>=points[16].y && y1<=points[16].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn3.performClick();
                    }
                }else if (y1>=points[24].y && y1<=points[24].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn4.performClick();
                    }
                }else if (y1>=points[32].y && y1<=points[32].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn5.performClick();
                    }
                }else if (y1>=points[40].y && y1<=points[40].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn6.performClick();
                    }
                }else if (y1>=points[48].y && y1<=points[48].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn7.performClick();
                    }
                }else if (y1>=points[56].y && y1<=points[56].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn8.performClick();
                    }
                }
            }
        }
        return true;
    }
}
