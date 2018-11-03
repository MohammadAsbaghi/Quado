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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import ir.adad.client.Adad;
import ir.help7.quado.interfaces.Go_Launcher;
import ir.help7.quado.interfaces.Go_q2;
import ir.help7.quado.interfaces.Go_q4;
import ir.help7.quado.models.Cube;


/**
 * A simple {@link Fragment} subclass.
 */
public class Q3 extends Fragment implements View.OnClickListener, View.OnTouchListener {

    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn_q3_new;
    TextView q3_bestRecord;
    View red_indicator1, green_indicator1, blue_indicator1, yellow_indicator1, whiteGround;
    Point[] points = new Point[36];
    Cube[] cubes = new Cube[36];
    int[] positions = new int[36];
    int l;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RelativeLayout q3_relativelayout;
    DisplayMetrics displayMetrics;
    Go_Launcher go_launcher;
    Go_q4 go_q4;
    Go_q2 go_q2;

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


    public Q3() {
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
        View view = inflater.inflate(R.layout.fragment_q3, container, false);
        view.setOnTouchListener(this);
        q3_relativelayout = view.findViewById(R.id.q3_framelayout);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = pref.edit();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        l = 2*width/19;
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

    public float getWidthInches() {
        displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels/displayMetrics.xdpi;
    }

    private void locate_cubes() {
        for (int j = 0; j<cubes.length; j++) {
            q3_relativelayout.addView(cubes[j]);
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
            positions[i] = pref.getInt("q3pos"+i,i);
        }
    }

    private void positionsToPref() {
        for (int i = 0; i<positions.length; i++){
            editor.putInt("q3pos"+i,positions[i]).commit();
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


    private void InitSeprater(int width) {

        whiteGround = new View(getContext());

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


        indicator_setter(whiteGround, points[0].x - (5*l / 12) , points[0].y - (5*l / 12), 8 * l +(4*l / 12), 8 * l +(4*l / 12), getResources().getColor(R.color.text_color));

        indicator_setter(red_indicator1,points[0].x-l/4,points[0].y-l/4,4*l-width/100,4*l-width/100,getResources().getColor(R.color.red_indicator));
        indicator_setter(blue_indicator1,points[3].x-(l/4)+2*width/200,points[0].y-l/4,4*l-width/100,4*l-width/100,getResources().getColor(R.color.blue_indicator));
        indicator_setter(green_indicator1,points[0].x-l/4,points[18].y-l/4+2*width/200,4*l-width/100,4*l-width/100,getResources().getColor(R.color.green_indicator));
        indicator_setter(yellow_indicator1,points[3].x-(l/4)+2*width/200,points[18].y-l/4+2*width/200,4*l-width/100,4*l-width/100,getResources().getColor(R.color.yellow_indicator));

    }

    public void indicator_setter(View view, float x, float y, int width, int height, int color){
        q3_relativelayout.addView(view);
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.setTranslationY(y);
        view.setTranslationX(x);
        view.setBackgroundColor(color);
    }

    private void InitButtons(View view) {
        q3_bestRecord = view.findViewById(R.id.q3_bestRecord);
        q3_bestRecord.setTextSize(getWidthInches()*7);
        q3_bestRecord.setTranslationY(l/2);
        q3_bestRecord.setTranslationX(points[0].x-l/4);
        if (pref.getLong("q3bestRecord",0) > 0){
            int secs = (int) (pref.getLong("q3bestRecord",0) / 1000);
            int mins = secs / 60;
            int hours = mins/60;
            secs = secs % 60;
            int milliseconds = (int) (pref.getLong("q3bestRecord",0) % 100);
            String s ="" + hours +":"+ mins + ":"
                    + String.format(Locale.US,"%02d", secs) + ":"
                    + String.format(Locale.US,"%03d", milliseconds);
            q3_bestRecord.setText(s);
        } else {
            q3_bestRecord.setVisibility(View.GONE);
        }

        timerValue = (TextView) view.findViewById(R.id.q3_timer);
        timerValue.setTextSize(getWidthInches()*10);
        timerValue.setTranslationY(points[30].y+3*l);
        timerValue.setTranslationX(points[33].x-l/4);


        btn1 = view.findViewById(R.id.q3btn1);
        btn2 =  view.findViewById(R.id.q3btn2);
        btn3 =  view.findViewById(R.id.q3btn3);
        btn4 =  view.findViewById(R.id.q3btn4);
        btn5 =  view.findViewById(R.id.q3btn5);
        btn6 =  view.findViewById(R.id.q3btn6);
        btn7 =  view.findViewById(R.id.q3btn7);
        btn8 = view.findViewById(R.id.q3btn8);
        btn9 =  view.findViewById(R.id.q3btn9);
        btn10 =  view.findViewById(R.id.q3btn10);
        btn11 =  view.findViewById(R.id.q3btn11);
        btn12 = view.findViewById(R.id.q3btn12);

        btn_q3_new =  view.findViewById(R.id.btn_q3_new);
        btn_q3_new.setTextSize(getWidthInches()*7);
        btn_q3_new.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/english_font.ttf"));
        btn_q3_new.setTranslationX(points[3].x-l/4);
        btn_q3_new.setTranslationY(5*l/12);

        btnFixer(btn1,new Point(points[0].x-5*l/4,points[0].y));
        btnFixer(btn2,new Point(points[6].x-5*l/4,points[6].y));
        btnFixer(btn3,new Point(points[12].x-5*l/4,points[12].y));
        btnFixer(btn4,new Point(points[18].x-5*l/4,points[18].y));
        btnFixer(btn5,new Point(points[24].x-5*l/4,points[24].y));
        btnFixer(btn6,new Point(points[30].x-5*l/4,points[30].y));
        btnFixer(btn7,new Point(points[30].x,points[30].y+5*l/4));
        btnFixer(btn8,new Point(points[31].x,points[31].y+5*l/4));
        btnFixer(btn9,new Point(points[32].x,points[32].y+5*l/4));
        btnFixer(btn10,new Point(points[33].x,points[33].y+5*l/4));
        btnFixer(btn11,new Point(points[34].x,points[34].y+5*l/4));
        btnFixer(btn12,new Point(points[35].x,points[35].y+5*l/4));

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
        btn_q3_new.setOnClickListener(this);
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
        cubes[3] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[3]]);
        cubes[4] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[4]]);
        cubes[5] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[5]]);
        cubes[6] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[6]]);
        cubes[7] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[7]]);
        cubes[8] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[8]]);
        cubes[9] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[9]]);
        cubes[10] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[10]]);
        cubes[11] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[11]]);
        cubes[12] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[12]]);
        cubes[13] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[13]]);
        cubes[14] = new Cube(getContext(), getResources().getColor(R.color.red),points[positions[14]]);
        cubes[15] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[15]]);
        cubes[16] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[16]]);
        cubes[17] = new Cube(getContext(), getResources().getColor(R.color.blue),points[positions[17]]);
        cubes[18] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[18]]);
        cubes[19] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[19]]);
        cubes[20] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[20]]);
        cubes[21] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[21]]);
        cubes[22] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[22]]);
        cubes[23] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[23]]);
        cubes[24] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[24]]);
        cubes[25] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[25]]);
        cubes[26] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[26]]);
        cubes[27] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[27]]);
        cubes[28] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[28]]);
        cubes[29] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[29]]);
        cubes[30] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[30]]);
        cubes[31] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[31]]);
        cubes[32] = new Cube(getContext(), getResources().getColor(R.color.green),points[positions[32]]);
        cubes[33] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[33]]);
        cubes[34] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[34]]);
        cubes[35] = new Cube(getContext(), getResources().getColor(R.color.yellow),points[positions[35]]);
    }

    private void InitPoints() {
        points[0] = new Point(3*l/2,2*l);
        points[1] = new Point(11*l/4,2*l);
        points[2] = new Point(4*l,2*l);
        points[3] = new Point(11*l/2,2*l);
        points[4] = new Point(27*l/4,2*l);
        points[5] = new Point(32*l/4,2*l);
        points[6] = new Point(3*l/2,13*l/4);
        points[7] = new Point(11*l/4,13*l/4);
        points[8] = new Point(4*l,13*l/4);
        points[9] = new Point(11*l/2,13*l/4);
        points[10] = new Point(27*l/4,13*l/4);
        points[11] = new Point(32*l/4,13*l/4);
        points[12] = new Point(3*l/2,9*l/2);
        points[13] = new Point(11*l/4,9*l/2);
        points[14] = new Point(4*l,9*l/2);
        points[15] = new Point(11*l/2,9*l/2);
        points[16] = new Point(27*l/4,9*l/2);
        points[17] = new Point(32*l/4,9*l/2);
        points[18] = new Point(3*l/2,6*l);
        points[19] = new Point(11*l/4,6*l);
        points[20] = new Point(4*l,6*l);
        points[21] = new Point(11*l/2,6*l);
        points[22] = new Point(27*l/4,6*l);
        points[23] = new Point(32*l/4,6*l);
        points[24] = new Point(3*l/2,29*l/4);
        points[25] = new Point(11*l/4,29*l/4);
        points[26] = new Point(4*l,29*l/4);
        points[27] = new Point(11*l/2,29*l/4);
        points[28] = new Point(27*l/4,29*l/4);
        points[29] = new Point(32*l/4,29*l/4);
        points[30] = new Point(3*l/2,17*l/2);
        points[31] = new Point(11*l/4,17*l/2);
        points[32] = new Point(4*l,17*l/2);
        points[33] = new Point(11*l/2,17*l/2);
        points[34] = new Point(27*l/4,17*l/2);
        points[35] = new Point(32*l/4,17*l/2);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btn1.getId()){
            pos_exchange(0,1,2,3,4,5);
        } else if (v.getId() == btn2.getId()){
            pos_exchange(6,7,8,9,10,11);
        } else if (v.getId() == btn3.getId()){
            pos_exchange(12,13,14,15,16,17);
        } else if (v.getId() == btn4.getId()){
            pos_exchange(18,19,20,21,22,23);
        }else if (v.getId() == btn5.getId()){
            pos_exchange(24,25,26,27,28,29);
        }else if (v.getId() == btn6.getId()){
            pos_exchange(30,31,32,33,34,35);
        }else if (v.getId() == btn7.getId()){
            pos_exchange(0,6,12,18,24,30);
        }else if (v.getId() == btn8.getId()){
            pos_exchange(1,7,13,19,25,31);
        }else if (v.getId() == btn9.getId()){
            pos_exchange(2,8,14,20,26,32);
        }else if (v.getId() == btn10.getId()){
            pos_exchange(3,9,15,21,27,33);
        }else if (v.getId() == btn11.getId()){
            pos_exchange(4,10,16,22,28,34);
        }else if (v.getId() == btn12.getId()){
            pos_exchange(5,11,17,23,29,35);
        }else if (v.getId() == btn_q3_new.getId()){
            stopTimer();
            editor.putLong("q3timeSwapBuff",0).commit();
            timeSwapBuff = 0;
            random_rank();
            startTimer();
        }
    }



    public void pos_exchange(int a, int b, int c, int d, int e, int f){
        int temp = positions[a];
        positions[a] = positions[f];
        positions[f] = temp;
        temp = positions[b];
        positions[b] = positions[e];
        positions[e] = temp;
        temp = positions[c];
        positions[c] = positions[d];
        positions[d] = temp;

        cubes[positions[a]].setPoint(points[a]);
        cubes[positions[b]].setPoint(points[b]);
        cubes[positions[c]].setPoint(points[c]);
        cubes[positions[d]].setPoint(points[d]);
        cubes[positions[e]].setPoint(points[e]);
        cubes[positions[f]].setPoint(points[f]);

        cubes[positions[a]].animate().translationY(cubes[positions[a]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[b]].animate().translationY(cubes[positions[b]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[c]].animate().translationY(cubes[positions[c]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[d]].animate().translationY(cubes[positions[d]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[e]].animate().translationY(cubes[positions[e]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[f]].animate().translationY(cubes[positions[f]].getPoint().y).setDuration(animation_time).start();

        cubes[positions[a]].animate().translationX(cubes[positions[a]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[b]].animate().translationX(cubes[positions[b]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[c]].animate().translationX(cubes[positions[c]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[d]].animate().translationX(cubes[positions[d]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[e]].animate().translationX(cubes[positions[e]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[f]].animate().translationX(cubes[positions[f]].getPoint().x).setDuration(animation_time).start();

        if (overChecker()){
            showOver();
        }
    }

    public boolean overChecker(){
        if (
                positions[0]==0 && positions[1]==1 && positions[2]==2 && positions[3]==3 &&
                positions[4]==4 && positions[5]==5 && positions[6]==6 && positions[7]==7 &&
                positions[8]==8 && positions[9]==9 && positions[10]==10 && positions[11]==11 &&
                positions[12]==12 && positions[13]==13 && positions[14]==14 && positions[15]==15 &&
                positions[16]==16 && positions[17]==17 && positions[18]==18 && positions[19]==19&&
                positions[20]==20 && positions[21]==21 && positions[22]==22 && positions[23]==23 &&
                positions[24]==24 && positions[25]==25 && positions[26]==26 && positions[27]==27 &&
                positions[28]==28 && positions[29]==29 && positions[30]==30 && positions[31]==31 &&
                positions[32]==32 && positions[33]==33 && positions[34]==34 && positions[35]==35){

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
        editor.putLong("q3timeSwapBuff",0).commit();
        timeSwapBuff = 0;
        if (pref.getLong("q3bestRecord",0)==0 || pref.getLong("q3bestRecord",0)>recordTime){
            editor.putLong("q3bestRecord",recordTime).commit();
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
        Button nextStep =  d.findViewById(R.id.btn_nextStep);
        nextStep.setVisibility(View.VISIBLE);
        Button playAgain=  d.findViewById(R.id.btn_playAgain);
        Button previousStep=  d.findViewById(R.id.btn_previousStep);
        previousStep.setVisibility(View.VISIBLE);
        Typeface english_typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/english_font.ttf");
        dialogRecordTv.setTypeface(english_typeface);
        nextStep.setTypeface(english_typeface);
        playAgain.setTypeface(english_typeface);
        previousStep.setTypeface(english_typeface);
        dialogRecordTv.setTextSize(getWidthInches()*9);
        nextStep.setTextSize(getWidthInches()*9);
        previousStep.setTextSize(getWidthInches()*9);
        playAgain.setTextSize(getWidthInches()*9);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_q4.onGo_q4_click();
                d.dismiss();
            }
        });
        previousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_q2.onGo_q2_click();
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
        while (i<40){
            randomBtn(r).performClick();
            i++;
        }

    }
    public Button randomBtn(Random r){
        int t = r.nextInt(12);
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
            default:
                return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        go_launcher = (Go_Launcher) context;
        go_q4 = (Go_q4) context;
        go_q2 = (Go_q2) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        go_launcher = null;
        go_q4 = null;
        go_q2 = null;
    }

    public void startTimer(){
        timeSwapBuff = pref.getLong("q3timeSwapBuff",0);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer(){
        customHandler.removeCallbacks(updateTimerThread);
        timeSwapBuff = updatedTime;
        editor.putLong("q3timeSwapBuff",timeSwapBuff).commit();
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
        q3_bestRecord.setText(s);
        q3_bestRecord.setVisibility(View.VISIBLE);
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
                        btn7.performClick();
                    }
                }else if (x1>=points[1].x && x1<=points[1].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn8.performClick();
                    }
                }else if (x1>=points[2].x && x1<=points[2].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn9.performClick();
                    }
                }else if (x1>=points[3].x && x1<=points[3].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn10.performClick();
                    }
                }else if (x1>=points[4].x && x1<=points[4].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn11.performClick();
                    }
                }else if (x1>=points[5].x && x1<=points[5].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn12.performClick();
                    }
                }
            }else if (Math.abs(y1-y2)<=l){
                if (y1>=points[0].y && y1<=points[0].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn1.performClick();
                    }
                }else if (y1>=points[6].y && y1<=points[6].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn2.performClick();
                    }
                }else if (y1>=points[12].y && y1<=points[12].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn3.performClick();
                    }
                }else if (y1>=points[18].y && y1<=points[18].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn4.performClick();
                    }
                }else if (y1>=points[24].y && y1<=points[24].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn5.performClick();
                    }
                }else if (y1>=points[30].y && y1<=points[30].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn6.performClick();
                    }
                }
            }
        }
        return true;
    }
}
