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
import ir.help7.quado.interfaces.Go_q3;
import ir.help7.quado.models.Cube;


/**
 * A simple {@link Fragment} subclass.
 */
public class Q2 extends Fragment implements View.OnClickListener, View.OnTouchListener {

    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn_q2_new;
    TextView q2_bestRecord;
    View red_indicator1, green_indicator1, blue_indicator1, yellow_indicator1, whiteGround;
    Point[] points = new Point[16];
    Cube[] cubes = new Cube[16];
    int[] positions = new int[16];
    int l;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RelativeLayout q2_relativelayout;
    Go_Launcher go_launcher;
    Go_q3 go_q3;
    Typeface english_typeface;
    float inchWidth;
    int width;
    DisplayMetrics displayMetrics;


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

    public Q2() {
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

        View view = inflater.inflate(R.layout.fragment_q2, container, false);
        view.setOnTouchListener(this);
        english_typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/english_font.ttf");
        q2_relativelayout = view.findViewById(R.id.q2_relativelayout);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = pref.edit();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;
        inchWidth = width / displayMetrics.xdpi;
        l = 2 * width / 17;
        startTimer();
        InitPositions();
        InitPoints();
        InitCubes();
        InitButtons(view);
        InitSeprater(width);
        locate_cubes();

        if (overChecker()) {
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
                int hours = mins / 60;
                secs = secs % 60;
                int milliseconds = (int) (updatedTime % 100);
                String s = "" + hours + ":" + mins + ":"
                        + String.format(Locale.US, "%02d", secs) + ":"
                        + String.format(Locale.US, "%02d", milliseconds);
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
        return displayMetrics.widthPixels / displayMetrics.xdpi;
    }

    private void locate_cubes() {
        for (int j = 0; j < cubes.length; j++) {
            q2_relativelayout.addView(cubes[j]);
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

        for (int i = 0; i < positions.length; i++) {
            positions[i] = pref.getInt("q2pos" + i, i);
        }

    }

    private void positionsToPref() {
        for (int i = 0; i < positions.length; i++) {
            editor.putInt("q2pos" + i, positions[i]).commit();
        }
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

        indicator_setter(whiteGround, points[0].x - (l / 3) , points[0].y - l / 3, 5 * l + 2*l/3 , 5 * l + 2*l/3, getResources().getColor(R.color.text_color));

        indicator_setter(red_indicator1, points[0].x - l / 4, points[0].y - l / 4, 5 * l / 2 + l / 4 - width / 100, 5 * l / 2 + l / 4 - width / 100, getResources().getColor(R.color.red_indicator));

        indicator_setter(blue_indicator1, points[2].x - (l / 4) + 2 * width / 200, points[0].y - l / 4, 5 * l / 2 + l / 4 - width / 100, 5 * l / 2 + l / 4 - width / 100, getResources().getColor(R.color.blue_indicator));

        indicator_setter(green_indicator1, points[0].x - l / 4, points[8].y - l / 4 + 2 * width / 200, 5 * l / 2 + l / 4 - width / 100, 5 * l / 2 + l / 4 - width / 100, getResources().getColor(R.color.green_indicator));

        indicator_setter(yellow_indicator1, points[2].x - (l / 4) + 2 * width / 200, points[8].y - l / 4 + 2 * width / 200, 5 * l / 2 + l / 4 - width / 100, 5 * l / 2 + l / 4 - width / 100, getResources().getColor(R.color.yellow_indicator));



    }

    public void indicator_setter(View view, float x, float y, int width, int height, int color) {
        q2_relativelayout.addView(view);
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.setTranslationY(y);
        view.setTranslationX(x);
        view.setBackgroundColor(color);
    }

    private void InitButtons(View view) {

        q2_bestRecord =  view.findViewById(R.id.q2_bestRecord);
        q2_bestRecord.setTextSize(getWidthInches() * 7);
        q2_bestRecord.setTranslationY(l / 2);
        q2_bestRecord.setTranslationX(points[0].x - l / 4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            q2_bestRecord.setElevation(10);
        }
        if (pref.getLong("q2bestRecord", 0) > 0) {
            int secs = (int) (pref.getLong("q2bestRecord", 0) / 1000);
            int mins = secs / 60;
            int hours = mins / 60;
            secs = secs % 60;
            int milliseconds = (int) (pref.getLong("q2bestRecord", 0) % 100);
            String s = "" + hours + ":" + mins + ":"
                    + String.format(Locale.US, "%02d", secs) + ":"
                    + String.format(Locale.US, "%02d", milliseconds);
            q2_bestRecord.setText(s);
        } else {
            q2_bestRecord.setVisibility(View.GONE);
        }

        timerValue = view.findViewById(R.id.q2_timer);
        timerValue.setTextSize(getWidthInches() * 10);
        timerValue.setTranslationY(points[12].y + 3 * l);
        timerValue.setTranslationX(points[2].x - (l / 4) - (width / 200));

        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);
        btn7 = view.findViewById(R.id.btn7);
        btn8 = view.findViewById(R.id.btn8);

        btn_q2_new = view.findViewById(R.id.btn_q2_new);
        btn_q2_new.setTextSize(getWidthInches() * 7);
        btn_q2_new.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/english_font.ttf"));
        btn_q2_new.setTranslationX(points[2].x - l / 4);
        btn_q2_new.setTranslationY(5 * l / 12);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn_q2_new.setElevation(10);
            btn1.setElevation(10);
            btn2.setElevation(10);
            btn3.setElevation(10);
            btn4.setElevation(10);
            btn5.setElevation(10);
            btn6.setElevation(10);
            btn7.setElevation(10);
            btn8.setElevation(10);
        }


        btnFixer(btn1, new Point(points[0].x - 5 * l / 4, points[0].y));
        btnFixer(btn2, new Point(points[4].x - 5 * l / 4, points[4].y));
        btnFixer(btn3, new Point(points[8].x - 5 * l / 4, points[8].y));
        btnFixer(btn4, new Point(points[12].x - 5 * l / 4, points[12].y));
        btnFixer(btn5, new Point(points[12].x, points[12].y + 5 * l / 4));
        btnFixer(btn6, new Point(points[13].x, points[13].y + 5 * l / 4));
        btnFixer(btn7, new Point(points[14].x, points[14].y + 5 * l / 4));
        btnFixer(btn8, new Point(points[15].x, points[15].y + 5 * l / 4));

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn_q2_new.setOnClickListener(this);

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
        cubes[2] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[2]]);
        cubes[3] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[3]]);
        cubes[4] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[4]]);
        cubes[5] = new Cube(getContext(), getResources().getColor(R.color.red), points[positions[5]]);
        cubes[6] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[6]]);
        cubes[7] = new Cube(getContext(), getResources().getColor(R.color.blue), points[positions[7]]);
        cubes[8] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[8]]);
        cubes[9] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[9]]);
        cubes[10] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[10]]);
        cubes[11] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[11]]);
        cubes[12] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[12]]);
        cubes[13] = new Cube(getContext(), getResources().getColor(R.color.green), points[positions[13]]);
        cubes[14] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[14]]);
        cubes[15] = new Cube(getContext(), getResources().getColor(R.color.yellow), points[positions[15]]);
    }

    private void InitPoints() {
        points[0] = new Point(3 * l / 2 + l / 2, 3 * l);
        points[1] = new Point(11 * l / 4 + l / 2, 3 * l);
        points[2] = new Point(17 * l / 4 + l / 2, 3 * l);
        points[3] = new Point(11 * l / 2 + l / 2, 3 * l);
        points[4] = new Point(3 * l / 2 + l / 2, 17 * l / 4);
        points[5] = new Point(11 * l / 4 + l / 2, 17 * l / 4);
        points[6] = new Point(17 * l / 4 + l / 2, 17 * l / 4);
        points[7] = new Point(11 * l / 2 + l / 2, 17 * l / 4);
        points[8] = new Point(3 * l / 2 + l / 2, 23 * l / 4);
        points[9] = new Point(11 * l / 4 + l / 2, 23 * l / 4);
        points[10] = new Point(17 * l / 4 + l / 2, 23 * l / 4);
        points[11] = new Point(11 * l / 2 + l / 2, 23 * l / 4);
        points[12] = new Point(3 * l / 2 + l / 2, 7 * l);
        points[13] = new Point(11 * l / 4 + l / 2, 7 * l);
        points[14] = new Point(17 * l / 4 + l / 2, 7 * l);
        points[15] = new Point(11 * l / 2 + l / 2, 7 * l);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btn1.getId()) {
            pos_exchange(0, 1, 2, 3);
        } else if (v.getId() == btn2.getId()) {
            pos_exchange(4, 5, 6, 7);
        } else if (v.getId() == btn3.getId()) {
            pos_exchange(8, 9, 10, 11);
        } else if (v.getId() == btn4.getId()) {
            pos_exchange(12, 13, 14, 15);
        } else if (v.getId() == btn5.getId()) {
            pos_exchange(0, 4, 8, 12);
        } else if (v.getId() == btn6.getId()) {
            pos_exchange(1, 5, 9, 13);
        } else if (v.getId() == btn7.getId()) {
            pos_exchange(2, 6, 10, 14);
        } else if (v.getId() == btn8.getId()) {
            pos_exchange(3, 7, 11, 15);
        } else if (v.getId() == btn_q2_new.getId()) {
            stopTimer();
            editor.putLong("q2timeSwapBuff", 0).commit();
            timeSwapBuff = 0;
            random_rank();
            startTimer();
        }
    }


    public void pos_exchange(int a, int b, int c, int d) {
        int temp = positions[a];
        positions[a] = positions[d];
        positions[d] = temp;
        temp = positions[b];
        positions[b] = positions[c];
        positions[c] = temp;


        cubes[positions[a]].setPoint(points[a]);
        cubes[positions[b]].setPoint(points[b]);
        cubes[positions[c]].setPoint(points[c]);
        cubes[positions[d]].setPoint(points[d]);

        cubes[positions[a]].animate().translationY(cubes[positions[a]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[b]].animate().translationY(cubes[positions[b]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[c]].animate().translationY(cubes[positions[c]].getPoint().y).setDuration(animation_time).start();
        cubes[positions[d]].animate().translationY(cubes[positions[d]].getPoint().y).setDuration(animation_time).start();

        cubes[positions[a]].animate().translationX(cubes[positions[a]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[b]].animate().translationX(cubes[positions[b]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[c]].animate().translationX(cubes[positions[c]].getPoint().x).setDuration(animation_time).start();
        cubes[positions[d]].animate().translationX(cubes[positions[d]].getPoint().x).setDuration(animation_time).start();

        if (overChecker()) {
            showOver();
        }
    }

    public boolean overChecker() {
        if (
                positions[0] == 0 &&
                        positions[1] == 1 &&
                        positions[2] == 2 &&
                        positions[3] == 3 &&
                        positions[4] == 4 &&
                        positions[5] == 5 &&
                        positions[6] == 6 &&
                        positions[7] == 7 &&
                        positions[8] == 8 &&
                        positions[9] == 9 &&
                        positions[10] == 10 &&
                        positions[11] == 11 &&
                        positions[12] == 12 &&
                        positions[13] == 13 &&
                        positions[14] == 14 &&
                        positions[15] == 15) {

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
        editor.putLong("q2timeSwapBuff", 0).commit();
        timeSwapBuff = 0;
        if (pref.getLong("q2bestRecord", 0) == 0 || pref.getLong("q2bestRecord", 0) > recordTime) {
            editor.putLong("q2bestRecord", recordTime).commit();
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
        String s = "" + hours + ":" + mins + ":"
                + String.format(Locale.US, "%02d", secs) + ":"
                + String.format(Locale.US, "%02d", milliseconds);
        dialogRecordTv.setText(s);
        Button nextStep = d.findViewById(R.id.btn_nextStep);
        nextStep.setVisibility(View.VISIBLE);
        Button playAgain = d.findViewById(R.id.btn_playAgain);
        Typeface english_typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/english_font.ttf");
        dialogRecordTv.setTypeface(english_typeface);
        nextStep.setTypeface(english_typeface);
        playAgain.setTypeface(english_typeface);
        dialogRecordTv.setTextSize(getWidthInches() * 9);
        nextStep.setTextSize(getWidthInches() * 9);
        playAgain.setTextSize(getWidthInches() * 9);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_q3.onGo_q3_click();
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
        while (i < 12) {
            randomBtn(r).performClick();
            i++;
        }

    }

    public Button randomBtn(Random r) {
        int t = r.nextInt(8);
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
            default:
                return null;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        go_launcher = (Go_Launcher) context;
        go_q3 = (Go_q3) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        go_launcher = null;
        go_q3 = null;
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

    public void startTimer() {
        timeSwapBuff = pref.getLong("q2timeSwapBuff", 0);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        customHandler.removeCallbacks(updateTimerThread);
        timeSwapBuff = updatedTime;
        editor.putLong("q2timeSwapBuff", timeSwapBuff).commit();
    }

    public void updateRecord(long record) {
        int secs = (int) (record / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        int milliseconds = (int) (record % 100);
        String s = "" + hours + ":" + mins + ":"
                + String.format(Locale.US, "%02d", secs) + ":"
                + String.format(Locale.US, "%02d", milliseconds);
        q2_bestRecord.setText(s);
        q2_bestRecord.setVisibility(View.VISIBLE);
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
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            x2 = event.getX();
            y2 = event.getY();
            isDraging = false;
            secondPulse = true;
        }
        if (!isDraging && secondPulse) {
            secondPulse = false;
            if (Math.abs(x1 - x2) <= l) {
                if (x1 >= points[0].x && x1 <= points[0].x + l) {
                    if (Math.abs(y1 - y2) > 3 * l / 2) {
                        btn5.performClick();
                    }
                } else if (x1 >= points[1].x && x1 <= points[1].x + l) {
                    if (Math.abs(y1 - y2) > 3 * l / 2) {
                        btn6.performClick();
                    }
                } else if (x1 >= points[2].x && x1 <= points[2].x + l) {
                    if (Math.abs(y1 - y2) > 3 * l / 2) {
                        btn7.performClick();
                    }
                } else if (x1 >= points[3].x && x1 <= points[3].x + l) {
                    if (Math.abs(y1 - y2) > 3 * l / 2) {
                        btn8.performClick();
                    }
                }
            } else if (Math.abs(y1 - y2) <= l) {
                if (y1 >= points[0].y && y1 <= points[0].y + l) {
                    if (Math.abs(x1 - x2) > 3 * l / 2) {
                        btn1.performClick();
                    }
                } else if (y1 >= points[4].y && y1 <= points[4].y + l) {
                    if (Math.abs(x1 - x2) > 3 * l / 2) {
                        btn2.performClick();
                    }
                } else if (y1 >= points[8].y && y1 <= points[8].y + l) {
                    if (Math.abs(x1 - x2) > 3 * l / 2) {
                        btn3.performClick();
                    }
                } else if (y1 >= points[12].y && y1 <= points[12].y + l) {
                    if (Math.abs(x1 - x2) > 3 * l / 2) {
                        btn4.performClick();
                    }
                }
            }
        }
        return true;
    }
}
