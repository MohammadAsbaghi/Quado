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
import android.util.Log;
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
import ir.help7.quado.interfaces.Go_q3;
import ir.help7.quado.interfaces.Go_q4;
import ir.help7.quado.models.Cube;


/**
 * A simple {@link Fragment} subclass.
 */
public class Q5 extends Fragment implements View.OnClickListener, View.OnTouchListener {

    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18, btn19, btn20, btn21, btn22, btn23, btn24, btn_q5_new;
    TextView q5_bestRecord;
    View red_indicator1, red_indicator2, green_indicator1, green_indicator2, blue_indicator1, blue_indicator2, yellow_indicator1, whiteGround, whiteGround1;
    Point[] points = new Point[63];
    Cube[] cubes = new Cube[63];
    int[] positions = new int[63];
    int l;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RelativeLayout q5_relativelayout;

    Go_Launcher go_launcher;
    Go_q4 go_q4;
    DisplayMetrics displayMetrics;
    int width;
    int height;


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


    public Q5() {
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
        View view = inflater.inflate(R.layout.fragment_q5, container, false);
        view.setOnTouchListener(this);
        q5_relativelayout = view.findViewById(R.id.q5_relativelayout);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = pref.edit();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        l = width / 15;
        startTimer();
        InitPositions();
        InitPoints();
        InitCubes();
        InitButtons(view);
        InitSeprater();
        locate_cubes();
        initRecordShow(view);

        if (overChecker()) {
            random_rank();
        }
        positionsToPref();
        return view;
    }

    public void initRecordShow(View view){
        q5_bestRecord = view.findViewById(R.id.q5_bestRecord);
        q5_bestRecord.setTextSize(getWidthInches() * 7);
        q5_bestRecord.setTranslationY(points[0].y - 3 * l );
        q5_bestRecord.setTranslationX(points[0].x - l);
        if (pref.getLong("q5bestRecord", 0) > 0) {
            int secs = (int) (pref.getLong("q5bestRecord", 0) / 1000);
            int mins = secs / 60;
            int hours = mins / 60;
            secs = secs % 60;
            int milliseconds = (int) (pref.getLong("q5bestRecord", 0) % 100);
            String s = "" + hours +":"+ mins + ":"
                    + String.format(Locale.US,"%02d", secs) + ":"
                    + String.format(Locale.US,"%02d", milliseconds);
            q5_bestRecord.setText(s);
        } else {
            q5_bestRecord.setVisibility(View.GONE);
        }
    }

    private void InitRunnable() {
        updateTimerThread = new Runnable() {
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

                updatedTime = timeSwapBuff + timeInMilliseconds;

                int secs = (int) (updatedTime / 1000);
                int mins = secs / 60;
                int hours = mins / 60;
                secs = secs % 60;
                int milliseconds = (int) (updatedTime % 100);
                String s = "" + hours + ":" + mins + ":"
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
        go_q4 = (Go_q4) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        go_launcher = null;
        go_q4 = null;
    }

    public float getWidthInches() {
        displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels / displayMetrics.xdpi;
    }

    private void locate_cubes() {
        for (int j = 0; j < cubes.length; j++) {
            q5_relativelayout.addView(cubes[j]);
            cubes[j].getLayoutParams().width = l;
            cubes[j].getLayoutParams().height = l;
            cubes[j].setBackgroundColor(cubes[j].getColor());
            cubes[j].setTranslationX(cubes[j].getPoint().x);
            cubes[j].setTranslationY(cubes[j].getPoint().y);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cubes[j].setElevation(5);
            }
        }
    }

    private void InitPositions() {
        for (int i = 0; i < positions.length; i++) {
            positions[i] = pref.getInt("q5pos" + i, i);
        }
    }

    private void positionsToPref() {
        for (int i = 0; i < positions.length; i++) {
            editor.putInt("q5pos" + i, positions[i]).commit();
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


    private void InitSeprater() {

        red_indicator1 = new View(getContext());
        red_indicator2 = new View(getContext());

        green_indicator1 = new View(getContext());
        green_indicator2 = new View(getContext());

        blue_indicator1 = new View(getContext());
        blue_indicator2 = new View(getContext());

        yellow_indicator1 = new View(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            red_indicator1.setElevation(3);
            red_indicator2.setElevation(3);
            green_indicator1.setElevation(3);
            green_indicator2.setElevation(3);
            blue_indicator1.setElevation(3);
            blue_indicator2.setElevation(3);
            yellow_indicator1.setElevation(3);
        }

        whiteGround = new View(getContext());
        whiteGround1 = new View(getContext());


        double frame = points[2].x - points[0].x + l;

        indicator_setter(whiteGround, points[0].x-l/4  , points[0].y-l/4, 8*l, 8*l, getResources().getColor(R.color.text_color));
        indicator_setter(whiteGround1, points[21].x - l/4   , points[21].y-l/4, 8*l, 8*l, getResources().getColor(R.color.text_color));

        indicator_setter(red_indicator1, points[0].x - l / 5, points[0].y - l / 5, (int) frame + 2 * l / 5, (int) frame + 2 * l / 5, getResources().getColor(R.color.red_indicator));
        indicator_setter(red_indicator2, points[48].x - l / 5, points[48].y - l / 5, (int) frame + 2 * l / 5, (int) frame + 2 * l / 5, getResources().getColor(R.color.red_indicator));

        indicator_setter(blue_indicator1, points[3].x - l / 5, points[3].y - l / 5, (int) frame + 2 * l / 5, (int) frame + 2 * l / 5, getResources().getColor(R.color.blue_indicator));
        indicator_setter(blue_indicator2, points[45].x - l / 5, points[45].y - l / 5, (int) frame + 2 * l / 5, (int) frame + 2 * l / 5, getResources().getColor(R.color.blue_indicator));

        indicator_setter(green_indicator1, points[18].x - l / 5, points[18].y - l / 5, (int) (frame + 2 * l / 5), (int) (frame + 2 * l / 5), getResources().getColor(R.color.green_indicator));
        indicator_setter(green_indicator2, points[24].x - l / 5, points[24].y - l / 5, (int) (frame + 2 * l / 5), (int) (frame + 2 * l / 5), getResources().getColor(R.color.green_indicator));

        indicator_setter(yellow_indicator1, points[21].x - l / 5, points[21].y - l / 5, (int) (frame + 2 * l / 5), (int) (frame + 2 * l / 5), getResources().getColor(R.color.yellow_indicator));

    }

    public void indicator_setter(View view, float x, float y, int width, int height, int color) {
        q5_relativelayout.addView(view);
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.setTranslationY(y);
        view.setTranslationX(x);
        view.setBackgroundColor(color);
    }

    private void InitButtons(View view) {

        timerValue = view.findViewById(R.id.q5_timer);
        timerValue.setTextSize(getWidthInches() * 10);
        timerValue.setTranslationY(points[60].y + 3 * l);
        timerValue.setTranslationX(points[24].x );


        btn1 = view.findViewById(R.id.q5btn1);
        btn2 = view.findViewById(R.id.q5btn2);
        btn3 = view.findViewById(R.id.q5btn3);
        btn4 = view.findViewById(R.id.q5btn4);
        btn5 = view.findViewById(R.id.q5btn5);
        btn6 = view.findViewById(R.id.q5btn6);
        btn7 = view.findViewById(R.id.q5btn7);
        btn8 = view.findViewById(R.id.q5btn8);
        btn9 = view.findViewById(R.id.q5btn9);
        btn10 = view.findViewById(R.id.q5btn10);
        btn11 = view.findViewById(R.id.q5btn11);
        btn12 = view.findViewById(R.id.q5btn12);
        btn13 = view.findViewById(R.id.q5btn13);
        btn14 = view.findViewById(R.id.q5btn14);
        btn15 = view.findViewById(R.id.q5btn15);
        btn16 = view.findViewById(R.id.q5btn16);
        btn17 = view.findViewById(R.id.q5btn17);
        btn18 = view.findViewById(R.id.q5btn18);
        btn19 = view.findViewById(R.id.q5btn19);
        btn20 = view.findViewById(R.id.q5btn20);
        btn21 = view.findViewById(R.id.q5btn21);
        btn22 = view.findViewById(R.id.q5btn22);
        btn23 = view.findViewById(R.id.q5btn23);
        btn24 = view.findViewById(R.id.q5btn24);

        btn_q5_new = view.findViewById(R.id.btn_q5_new);
        btn_q5_new.setTextSize(getWidthInches() * 7);
        btn_q5_new.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/english_font.ttf"));
        btn_q5_new.setTranslationX(points[24].x );
        btn_q5_new.setTranslationY(points[0].y - 3*l);


        btnFixer(btn1, new Point(points[0].x , points[0].y - 5*l/4));
        btnFixer(btn2, new Point(points[1].x , points[1].y - 5*l/4));
        btnFixer(btn3, new Point(points[2].x , points[2].y - 5*l/4));
        btnFixer(btn4, new Point(points[3].x , points[3].y - 5*l/4));
        btnFixer(btn5, new Point(points[4].x , points[4].y - 5*l/4));
        btnFixer(btn6, new Point(points[5].x , points[5].y - 5*l/4));
        btnFixer(btn7, new Point(points[0].x - 5*l/4, points[0].y ));
        btnFixer(btn8, new Point(points[6].x - 5*l/4, points[6].y));
        btnFixer(btn9, new Point(points[12].x - 5*l/4, points[12].y));
        btnFixer(btn10, new Point(points[18].x - 5*l/4, points[18].y));
        btnFixer(btn11, new Point(points[27].x - 5*l/4, points[27].y));
        btnFixer(btn12, new Point(points[36].x - 5*l/4, points[36].y));
        btnFixer(btn13, new Point(points[57].x , points[57].y + 5*l/4));
        btnFixer(btn14, new Point(points[58].x, points[58].y + 5*l/4));
        btnFixer(btn15, new Point(points[59].x, points[59].y + 5*l/4));
        btnFixer(btn16, new Point(points[60].x, points[60].y + 5*l/4));
        btnFixer(btn17, new Point(points[61].x, points[61].y + 5*l/4));
        btnFixer(btn18, new Point(points[62].x, points[62].y + 5*l/4));
        btnFixer(btn19, new Point(points[26].x + 5*l/4, points[26].y ));
        btnFixer(btn20, new Point(points[35].x + 5*l/4, points[35].y));
        btnFixer(btn21, new Point(points[44].x + 5*l/4, points[44].y));
        btnFixer(btn22, new Point(points[50].x + 5*l/4, points[50].y));
        btnFixer(btn23, new Point(points[56].x + 5*l/4, points[56].y));
        btnFixer(btn24, new Point(points[62].x + 5*l/4, points[62].y));

        btn1.setRotation(90);
        btn2.setRotation(90);
        btn3.setRotation(90);
        btn4.setRotation(90);
        btn5.setRotation(90);
        btn6.setRotation(90);
        btn13.setRotation(-90);
        btn14.setRotation(-90);
        btn15.setRotation(-90);
        btn16.setRotation(-90);
        btn17.setRotation(-90);
        btn18.setRotation(-90);
        btn19.setRotation(180);
        btn20.setRotation(180);
        btn21.setRotation(180);
        btn22.setRotation(180);
        btn23.setRotation(180);
        btn24.setRotation(180);



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
        btn17.setOnClickListener(this);
        btn18.setOnClickListener(this);
        btn19.setOnClickListener(this);
        btn20.setOnClickListener(this);
        btn21.setOnClickListener(this);
        btn22.setOnClickListener(this);
        btn23.setOnClickListener(this);
        btn24.setOnClickListener(this);
        btn_q5_new.setOnClickListener(this);
    }

    public void btnFixer(Button btn, Point p) {
        btn.setTranslationX(p.x);
        btn.setTranslationY(p.y);
        btn.getLayoutParams().height = l;
        btn.getLayoutParams().width = l;
    }

    private void InitCubes() {
        cubes[0] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[0]]);
        cubes[1] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[1]]);
        cubes[2] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[2]]);
        cubes[3] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[3]]);
        cubes[4] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[4]]);
        cubes[5] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[5]]);
        cubes[6] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[6]]);
        cubes[7] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[7]]);
        cubes[8] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[8]]);
        cubes[9] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[9]]);
        cubes[10] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[10]]);
        cubes[11] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[11]]);
        cubes[12] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[12]]);
        cubes[13] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[13]]);
        cubes[14] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[14]]);
        cubes[15] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[15]]);
        cubes[16] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[16]]);
        cubes[17] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[17]]);
        cubes[18] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[18]]);
        cubes[19] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[19]]);
        cubes[20] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[20]]);
        cubes[21] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[21]]);
        cubes[22] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[22]]);
        cubes[23] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[23]]);
        cubes[24] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[24]]);
        cubes[25] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[25]]);
        cubes[26] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[26]]);
        cubes[27] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[27]]);
        cubes[28] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[28]]);
        cubes[29] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[29]]);
        cubes[30] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[30]]);
        cubes[31] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[31]]);
        cubes[32] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[32]]);
        cubes[33] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[33]]);
        cubes[34] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[34]]);
        cubes[35] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[35]]);
        cubes[36] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[36]]);
        cubes[37] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[37]]);
        cubes[38] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[38]]);
        cubes[39] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[39]]);
        cubes[40] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[40]]);
        cubes[41] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[41]]);
        cubes[42] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[42]]);
        cubes[43] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[43]]);
        cubes[44] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[44]]);
        cubes[45] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[45]]);
        cubes[46] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[46]]);
        cubes[47] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[47]]);
        cubes[48] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[48]]);
        cubes[49] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[49]]);
        cubes[50] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[50]]);
        cubes[51] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[51]]);
        cubes[52] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[52]]);
        cubes[53] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[53]]);
        cubes[54] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[54]]);
        cubes[55] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[55]]);
        cubes[56] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[56]]);
        cubes[57] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[57]]);
        cubes[58] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[58]]);
        cubes[59] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[59]]);
        cubes[60] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[60]]);
        cubes[61] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[61]]);
        cubes[62] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[62]]);

    }

    private void InitPoints() {
        points[0] = new Point(7*l/4 , height/2 - (6 * l+ 3*l/4));
        points[1] = new Point(points[0].x +  5*l / 4, points[0].y);
        points[2] = new Point(points[1].x +  5*l / 4, points[1].y);
        points[3] = new Point(points[2].x +  3*l / 2, points[2].y);
        points[4] = new Point(points[3].x +  5*l / 4, points[3].y);
        points[5] = new Point(points[4].x +  5*l / 4, points[4].y);
        points[6] = new Point(points[0].x , points[0].y +  5*l / 4);
        points[7] = new Point(points[6].x +  5*l / 4, points[6].y);
        points[8] = new Point(points[7].x +  5*l / 4, points[7].y);
        points[9] = new Point(points[8].x + 3 * l / 2, points[8].y);
        points[10] = new Point(points[9].x + 5*l / 4, points[9].y);
        points[11] = new Point(points[10].x + 5*l / 4, points[9].y);
        points[12] = new Point(points[0].x, points[11].y + 5*l / 4);
        points[13] = new Point(points[12].x + 5*l / 4, points[12].y);
        points[14] = new Point(points[13].x + 5*l / 4, points[13].y);
        points[15] = new Point(points[14].x + 3 * l / 2, points[14].y);
        points[16] = new Point(points[15].x + 5*l / 4, points[15].y);
        points[17] = new Point(points[16].x + 5*l / 4, points[16].y);
        points[18] = new Point(points[0].x , points[17].y + 3 * l / 2);
        points[19] = new Point(points[18].x + 5*l / 4, points[18].y );
        points[20] = new Point(points[19].x + 5*l / 4, points[19].y);
        points[21] = new Point(points[20].x + 3*l/2, points[20].y);
        points[22] = new Point(points[21].x+ 5*l / 4, points[21].y );
        points[23] = new Point(points[22].x + 5*l / 4, points[22].y );
        points[24] = new Point(points[23].x+ 3*l / 2, points[23].y );
        points[25] = new Point(points[24].x + 5*l / 4, points[24].y );
        points[26] = new Point(points[25].x + 5*l / 4, points[25].y );
        points[27] = new Point(points[0].x , points[26].y + 5*l / 4);
        points[28] = new Point(points[27].x+ 5*l / 4, points[27].y);
        points[29] = new Point(points[28].x + 5*l / 4, points[28].y);
        points[30] = new Point(points[29].x + 3 * l / 2, points[29].y);
        points[31] = new Point(points[30].x + 5*l / 4, points[30].y);
        points[32] = new Point(points[31].x + 5*l / 4, points[31].y );
        points[33] = new Point(points[32].x + 3 * l / 2, points[32].y );
        points[34] = new Point(points[33].x + 5*l / 4, points[33].y );
        points[35] = new Point(points[34].x + 5*l / 4, points[34].y );
        points[36] = new Point(points[0].x , points[35].y + 5 * l / 4);
        points[37] = new Point(points[36].x + 5 * l / 4, points[36].y );
        points[38] = new Point(points[37].x + 5 * l / 4, points[37].y );
        points[39] = new Point(points[38].x + 3 * l / 2, points[38].y );
        points[40] = new Point(points[39].x + 5 * l / 4, points[39].y);
        points[41] = new Point(points[40].x + 5*l / 4, points[40].y );
        points[42] = new Point(points[41].x + 3 * l / 2, points[41].y );
        points[43] = new Point(points[42].x + 5 * l / 4, points[42].y );
        points[44] = new Point(points[43].x + 5 * l / 4, points[43].y );
        points[45] = new Point(points[39].x , points[44].y + 3 * l / 2);
        points[46] = new Point(points[45].x + 5*l / 4, points[45].y );
        points[47] = new Point(points[46].x + 5*l / 4, points[46].y);
        points[48] = new Point(points[47].x + 3*l / 2, points[47].y);
        points[49] = new Point(points[48].x + 5 * l / 4, points[48].y );
        points[50] = new Point(points[49].x + 5 * l / 4, points[49].y );
        points[51] = new Point(points[39].x , points[50].y + 5 * l / 4);
        points[52] = new Point(points[51].x + 5*l / 4, points[51].y );
        points[53] = new Point(points[52].x + 5*l / 4, points[52].y );
        points[54] = new Point(points[53].x +3*l/2, points[53].y );
        points[55] = new Point(points[54].x + 5*l / 4, points[54].y );
        points[56] = new Point(points[55].x + 5*l / 4, points[55].y);
        points[57] = new Point(points[39].x , points[56].y + 5 * l / 4);
        points[58] = new Point(points[57].x + 5*l / 4, points[57].y );
        points[59] = new Point(points[58].x + 5*l / 4, points[58].y );
        points[60] = new Point(points[59].x + 3 * l / 2, points[59].y );
        points[61] = new Point(points[60].x + 5 * l / 4, points[60].y );
        points[62] = new Point(points[61].x + 5 * l / 4, points[61].y );
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btn1.getId()) {
            pos_exchange(0, 6, 12, 18, 27, 36);
        } else if (v.getId() == btn2.getId()) {
            pos_exchange(1, 7, 13, 19, 28, 37);
        } else if (v.getId() == btn3.getId()) {
            pos_exchange(2, 8, 14, 20, 29, 38);
        } else if (v.getId() == btn4.getId()) {
            pos_exchange(3, 9, 15, 21, 30, 39);
        } else if (v.getId() == btn5.getId()) {
            pos_exchange(4, 10, 16, 22, 31, 40);
        } else if (v.getId() == btn6.getId()) {
            pos_exchange(5, 11, 17, 23, 32, 41);
        } else if (v.getId() == btn7.getId()) {
            pos_exchange(0, 1, 2, 3, 4, 5);
        } else if (v.getId() == btn8.getId()) {
            pos_exchange(6, 7, 8, 9, 10, 11);
        } else if (v.getId() == btn9.getId()) {
            pos_exchange(12, 13, 14, 15, 16, 17);
        } else if (v.getId() == btn10.getId()) {
            pos_exchange(18, 19, 20, 21, 22, 23);
        } else if (v.getId() == btn11.getId()) {
            pos_exchange(27, 28, 29, 30, 31, 32);
        } else if (v.getId() == btn12.getId()) {
            pos_exchange(36, 37, 38, 39, 40, 41);
        } else if (v.getId() == btn13.getId()) {
            pos_exchange(21, 30, 39, 45, 51, 57);
        } else if (v.getId() == btn14.getId()) {
            pos_exchange(22, 31, 40, 46, 52, 58);
        } else if (v.getId() == btn15.getId()) {
            pos_exchange(23, 32, 41, 47, 53, 59);
        } else if (v.getId() == btn16.getId()) {
            pos_exchange(24, 33, 42, 48, 54, 60);
        } else if (v.getId() == btn17.getId()) {
            pos_exchange(25, 34, 43, 49, 55, 61);
        } else if (v.getId() == btn18.getId()) {
            pos_exchange(26, 35, 44, 50, 56, 62);
        } else if (v.getId() == btn19.getId()) {
            pos_exchange(21, 22, 23, 24, 25, 26);
        } else if (v.getId() == btn20.getId()) {
            pos_exchange(30, 31, 32, 33, 34, 35);
        } else if (v.getId() == btn21.getId()) {
            pos_exchange(39, 40, 41, 42, 43, 44);
        } else if (v.getId() == btn22.getId()) {
            pos_exchange(45, 46, 47, 48, 49, 50);
        } else if (v.getId() == btn23.getId()) {
            pos_exchange(51, 52, 53, 54, 55, 56);
        } else if (v.getId() == btn24.getId()) {
            pos_exchange(57, 58, 59, 60, 61, 62);
        } else if (v.getId() == btn_q5_new.getId()) {
            stopTimer();
            editor.putLong("q5timeSwapBuff", 0).commit();
            timeSwapBuff = 0;
            random_rank();
            startTimer();
        }
    }


    public void pos_exchange(int a, int b, int c, int d, int e, int f) {
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

        if (overChecker()) {
            showOver();
        }
    }

    public boolean overChecker() {
        if ((positions[0] == 0 || positions[0] == 48) && (positions[1] == 1 || positions[1] == 49) && (positions[2] == 2 || positions[2] == 50) &&
                (positions[3] == 3 || positions[3] == 45) && (positions[4] == 4 || positions[4] == 46) && (positions[5] == 5 || positions[5] == 47) &&
                (positions[6] == 6 || positions[6] == 54) && (positions[7] == 7 || positions[7] == 55) &&
                (positions[8] == 8 || positions[8] == 56) && (positions[9] == 9 || positions[9] == 51) && (positions[10] == 10 || positions[10] == 52) &&
                (positions[11] == 11 || positions[11] == 53) && (positions[12] == 12 || positions[12] == 60) && (positions[13] == 13 || positions[13] == 61) &&
                (positions[14] == 14 || positions[14] == 62) && (positions[15] == 15 || positions[15] == 57) &&
                (positions[16] == 16 || positions[16] == 58) && (positions[17] == 17 || positions[17] == 59) &&
                (positions[18] == 18 || positions[18] == 24) && (positions[19] == 19 || positions[19] == 25) &&
                (positions[20] == 20 || positions[20] == 26) && positions[21] == 21 && positions[22] == 22 && positions[23] == 23 &&
                (positions[24] == 24 || positions[24] == 18) && (positions[25] == 25 || positions[25] == 19) &&
                (positions[26] == 26 || positions[26] == 20) && (positions[27] == 27 || positions[27] == 33) &&
                (positions[28] == 28 || positions[28] == 34) && (positions[29] == 29 || positions[29] == 35) &&
                positions[30] == 30 && positions[31] == 31 && positions[32] == 32 && (positions[33] == 33 || positions[33] == 27) &&
                (positions[34] == 34 || positions[34] == 28) && (positions[35] == 35 || positions[35] == 29) &&
                (positions[36] == 36 || positions[36] == 42) && (positions[37] == 37 || positions[37] == 43) &&
                (positions[38] == 38 || positions[38] == 44) && positions[39] == 39 && positions[40] == 40 && positions[41] == 41 &&
                (positions[42] == 42 || positions[42] == 36) && (positions[43] == 43 || positions[43] == 37) &&
                (positions[44] == 44 || positions[44] == 38) && (positions[45] == 45 || positions[45] == 3) && (positions[46] == 46 || positions[46] == 4) &&
                (positions[47] == 47 || positions[47] == 5) && (positions[48] == 48 || positions[48] == 0) && (positions[49] == 49 || positions[49] == 1) &&
                (positions[50] == 50 || positions[50] == 2) && (positions[51] == 51 || positions[51] == 9) &&
                (positions[52] == 52 || positions[52] == 10) && (positions[53] == 53 || positions[53] == 11) && (positions[54] == 54 || positions[54] == 6) &&
                (positions[55] == 55 || positions[55] == 7) && (positions[56] == 56 || positions[56] == 8) && (positions[57] == 57 || positions[57] == 15) &&
                (positions[58] == 58 || positions[58] == 16) && (positions[59] == 59 || positions[59] == 17) &&
                (positions[60] == 60 || positions[60] == 12) && (positions[61] == 61 || positions[61] == 13) && (positions[62] == 62 || positions[62] == 14)) {
            return true;
        } else {
            return false;
        }
    }

    public void showOver() {
        Adad.prepareInterstitialAd();
        stopTimer();
        recordTime = updatedTime;
        updatedTime = 0;
        editor.putLong("q5timeSwapBuff", 0L).commit();
        timeSwapBuff = 0L;
        if (pref.getLong("q5bestRecord", 0L) == 0L || pref.getLong("q5bestRecord", 0L) > recordTime) {
            editor.putLong("q5bestRecord", recordTime).commit();
            updateRecord(recordTime);
        }

        final Dialog d = new Dialog(getContext());
        d.setCancelable(false);
        d.setContentView(R.layout.over_dialog_layout);
        TextView dialogRecordTv = d.findViewById(R.id.dialog_record_tv);
        int secs = (int) (recordTime / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        int milliseconds = (int) (recordTime % 100);
        String s = "" + hours +":"+ mins + ":"
                + String.format(Locale.US,"%02d", secs) + ":"
                + String.format(Locale.US,"%02d", milliseconds);
        dialogRecordTv.setText(s);
        Button previousStep = d.findViewById(R.id.btn_previousStep);
        previousStep.setVisibility(View.VISIBLE);
        Button playAgain = d.findViewById(R.id.btn_playAgain);
        Typeface english_typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/english_font.ttf");
        dialogRecordTv.setTypeface(english_typeface);
        previousStep.setTypeface(english_typeface);
        playAgain.setTypeface(english_typeface);
        dialogRecordTv.setTextSize(getWidthInches() * 9);
        previousStep.setTextSize(getWidthInches() * 9);
        playAgain.setTextSize(getWidthInches() * 9);
        previousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_q4.onGo_q4_click();
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
        if (recordTime != 0) {
            d.show();
        }
    }

    public void random_rank() {
        Random r = new Random();
        int i = 0;
        while (i < 100) {
            randomBtn(r).performClick();
            i++;
        }

    }

    public Button randomBtn(Random r) {
        int t = r.nextInt(24);
        switch (t) {
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
            case 16:
                return btn17;
            case 17:
                return btn18;
            case 18:
                return btn19;
            case 19:
                return btn20;
            case 20:
                return btn21;
            case 21:
                return btn22;
            case 22:
                return btn23;
            case 23:
                return btn24;
            default:
                return null;
        }
    }

    public void startTimer() {
        timeSwapBuff = pref.getLong("q5timeSwapBuff", 0);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        customHandler.removeCallbacks(updateTimerThread);
        timeSwapBuff = updatedTime;
        editor.putLong("q5timeSwapBuff", timeSwapBuff).commit();
    }


    public void updateRecord(long record) {
        int secs = (int) (record / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        int milliseconds = (int) (record % 100);
        String s = "" + hours +":"+ mins + ":"
                + String.format(Locale.US,"%02d", secs) + ":"
                + String.format(Locale.US,"%02d", milliseconds);
        q5_bestRecord.setText(s);
        q5_bestRecord.setVisibility(View.VISIBLE);
    }

    float x1 = 0, y1 = 0, x2 = 0, y2 = 0;
    boolean isDraging = false;
    boolean secondPulse = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            y1 = event.getY();
            isDraging = true;
            Log.i("test", "" + x1 + "   " + y1);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            x2 = event.getX();
            y2 = event.getY();
            isDraging = false;
            secondPulse = true;
        }
        if (!isDraging && secondPulse) {
            secondPulse = false;
            if (Math.abs(x1 - x2) < l) {
                if (x1>=points[0].x && x1<=points[0].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn1.performClick();
                    }
                }else if (x1>=points[1].x && x1<=points[1].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn2.performClick();
                    }
                }else if (x1>=points[2].x && x1<=points[2].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn3.performClick();
                    }
                }else if (x1>=points[3].x && x1<=points[3].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        if (y1>=points[30].y+l/2)
                            btn13.performClick();
                        else
                            btn4.performClick();
                    }
                }else if (x1>=points[4].x && x1<=points[4].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        if (y1>=points[31].y+l/2)
                            btn14.performClick();
                        else
                            btn5.performClick();
                    }
                }else if (x1>=points[5].x && x1<=points[5].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        if (y1>=points[30].y+l/2)
                            btn15.performClick();
                        else
                            btn6.performClick();
                    }
                }else if (x1>=points[5].x && x1<=points[5].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn6.performClick();
                    }
                }else if (x1>=points[24].x && x1<=points[24].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn16.performClick();
                    }
                }else if (x1>=points[25].x && x1<=points[25].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn17.performClick();
                    }
                }else if (x1>=points[26].x && x1<=points[26].x+l){
                    if (Math.abs(y1-y2)>3*l/2){
                        btn18.performClick();
                    }
                }
            } else if (Math.abs(y1-y2)<=l){
                if (y1>=points[0].y && y1<=points[0].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn7.performClick();
                    }
                }else if (y1>=points[6].y && y1<=points[6].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn8.performClick();
                    }
                }else if (y1>=points[12].y && y1<=points[12].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn9.performClick();
                    }
                }else if (y1>=points[18].y && y1<=points[18].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        if (x1>=points[22].x+l/2)
                            btn19.performClick();
                        else
                            btn10.performClick();
                    }
                }else if (y1>=points[27].y && y1<=points[27].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        if (x1>=points[31].x+l/2)
                            btn20.performClick();
                        else
                            btn11.performClick();
                    }
                }else if (y1>=points[36].y && y1<=points[36].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        if (x1>=points[40].x+l/2)
                            btn21.performClick();
                        else
                            btn12.performClick();
                    }
                }else if (y1>=points[45].y && y1<=points[45].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn22.performClick();
                    }
                }else if (y1>=points[51].y && y1<=points[51].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn23.performClick();
                    }
                }else if (y1>=points[57].y && y1<=points[57].y+l){
                    if (Math.abs(x1-x2)>3*l/2){
                        btn24.performClick();
                    }
                }
            }
        }
        return true;
    }
}
