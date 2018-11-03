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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import ir.adad.client.Adad;
import ir.help7.quado.interfaces.Go_Launcher;
import ir.help7.quado.models.Cube;


/**
 * A simple {@link Fragment} subclass.
 */
public class RandomPuzzle extends Fragment implements View.OnClickListener
 {

    TextView q5_bestRecord;
    View ground, ground_hint, ground_hint2;
    Point[] board_points = new Point[25];
    Point[] pattern_points = new Point[9];
    Cube[] cubes = new Cube[25];
    Cube[] pattern_cubs = new Cube[9];
    @SuppressLint("UseSparseArrays")
    Map<Integer, Integer> master_pattern = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    Map<Integer, Integer> win_pattern = new HashMap<>();
    ArrayList<Integer> game_pattern = new ArrayList<>();
    ArrayList<Integer> temp_pattern = new ArrayList<>();
    int[] positions = new int[25];
    int l;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RelativeLayout RandomPuzzle_Relativelayout;

    Go_Launcher go_launcher;

    DisplayMetrics displayMetrics;
    int width;
    int height;

    Button btn_new_game;


    // timer things
    TextView timerValue;
    long startTime = 0;
    Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    Runnable updateTimerThread;
    long recordTime;

    int animation_time = 100;


    public RandomPuzzle() {
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
        View view = inflater.inflate(R.layout.fragment_random_puzzel, container, false);
        //        view.setOnTouchListener(this);
        btn_new_game = view.findViewById(R.id.btn_new_game);
        btn_new_game.setOnClickListener(this);
        RandomPuzzle_Relativelayout = view.findViewById(R.id.RandomPuzzle_relativelayout);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = pref.edit();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        l = 5*width / 39;
        startTimer();
        InitPositions();
        InitPoints();
        InitMasterPattern();
        InitPattern();
        InitGround();
        InitCubes();
        InitTimerText(view);
        locate_cubes();
        for (int i = 0; i<positions.length; i++){
            positions[i] = i;
        }
//        initRecordShow(view);

        if (overChecker()) {
            //
        }
        positionsToPref();
        return view;
    }

     private void InitMasterPattern() {
        master_pattern.put(0, getResources().getColor(R.color.red));
        master_pattern.put(1, getResources().getColor(R.color.red));
        master_pattern.put(2, getResources().getColor(R.color.red));
        master_pattern.put(3, getResources().getColor(R.color.red));
        master_pattern.put(4, getResources().getColor(R.color.blue));
        master_pattern.put(5, getResources().getColor(R.color.blue));
        master_pattern.put(6, getResources().getColor(R.color.blue));
        master_pattern.put(7, getResources().getColor(R.color.blue));
        master_pattern.put(8, getResources().getColor(R.color.yellow));
        master_pattern.put(9, getResources().getColor(R.color.yellow));
        master_pattern.put(10, getResources().getColor(R.color.yellow));
        master_pattern.put(11, getResources().getColor(R.color.yellow));
        master_pattern.put(12, getResources().getColor(R.color.green));
        master_pattern.put(13, getResources().getColor(R.color.green));
        master_pattern.put(14, getResources().getColor(R.color.green));
        master_pattern.put(15, getResources().getColor(R.color.green));
        master_pattern.put(16, getResources().getColor(R.color.seprator));
        master_pattern.put(17, getResources().getColor(R.color.seprator));
        master_pattern.put(18, getResources().getColor(R.color.seprator));
        master_pattern.put(19, getResources().getColor(R.color.seprator));
        master_pattern.put(20, getResources().getColor(R.color.purple));
        master_pattern.put(21, getResources().getColor(R.color.purple));
        master_pattern.put(22, getResources().getColor(R.color.purple));
        master_pattern.put(23, getResources().getColor(R.color.purple));
        master_pattern.put(24, 0);
         Log.i("mamad", master_pattern.toString());
     }

     private void InitPattern() {
        Random r = new Random();
        for (int i = 0; i<24; i++){
            game_pattern.add(i, i);
        }
        temp_pattern = game_pattern;
        for (int i = 0; i<9; i++){
            int m = r.nextInt(temp_pattern.size());
            win_pattern.put(i, master_pattern.get(m));
            temp_pattern.remove(m);
        }

     }

     public void initRecordShow(View view){
        q5_bestRecord = view.findViewById(R.id.q5_bestRecord);
        q5_bestRecord.setTextSize(getWidthInches() * 7);
        q5_bestRecord.setTranslationY(board_points[0].y - 3 * l );
        q5_bestRecord.setTranslationX(board_points[0].x - l);
        if (pref.getLong("randomPuzzle_bestRecord", 0) > 0) {
            int secs = (int) (pref.getLong("randomPuzzle_bestRecord", 0) / 1000);
            int mins = secs / 60;
            int hours = mins / 60;
            secs = secs % 60;
            int milliseconds = (int) (pref.getLong("randomPuzzle_bestRecord", 0) % 100);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        go_launcher = null;
    }

    public float getWidthInches() {
        displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels / displayMetrics.xdpi;
    }

    private void locate_cubes() {
        for (Cube cube : cubes) {
            RandomPuzzle_Relativelayout.addView(cube);
            cube.getLayoutParams().width = l;
            cube.getLayoutParams().height = l;
            cube.setBackgroundColor(cube.getColor());
            cube.setTranslationX(cube.getPoint().x);
            cube.setTranslationY(cube.getPoint().y);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cube.setElevation(5);
            }
        }
        for (Cube cube : pattern_cubs){
            RandomPuzzle_Relativelayout.addView(cube);
            cube.getLayoutParams().width = l/2;
            cube.getLayoutParams().height = l/2;
            cube.setBackgroundColor(cube.getColor());
            cube.setTranslationX(cube.getPoint().x);
            cube.setTranslationY(cube.getPoint().y);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cube.setElevation(5);
            }
        }
    }

    private void InitPositions() {
        for (int i = 0; i < positions.length; i++) {
            positions[i] = pref.getInt("random_puzzle_pos" + i, i);
        }
    }

    private void positionsToPref() {
        for (int i = 0; i < positions.length; i++) {
            editor.putInt("random_puzzle_pos" + i, positions[i]).commit();
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
            cubes[i].setPoint(board_points[i]);
            cubes[i].setTranslationY(cubes[i].getPoint().y);
            cubes[i].setTranslationX(cubes[i].getPoint().x);
        }
        startTimer();
    }


    private void InitGround() {

        ground = new View(getContext());
        ground_hint = new View(getContext());
        ground_hint2 = new View(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ground.setElevation(3);
            ground_hint.setElevation(3);
            ground_hint2.setElevation(3);
        }

        double frame = board_points[4].x - board_points[0].x + 2*l;
        double hint_frame = board_points[8].x - board_points[6].x + 6*l/5;
        double hint2_frame = board_points[8].x - board_points[6].x + 6*l/5 - l/10;

        indicator_setter(ground, board_points[0].x - l / 2, board_points[0].y - l / 2, (int) frame , (int) frame , getResources().getColor(R.color.black));
        indicator_setter(ground_hint, board_points[6].x - l / 10, board_points[6].y - l / 10, (int) hint_frame , (int) hint_frame , getResources().getColor(R.color.seprator));
        indicator_setter(ground_hint2, board_points[6].x - l / 10 + l/20, board_points[6].y - l / 10 + l/20, (int) hint2_frame , (int) hint2_frame , getResources().getColor(R.color.black));

    }

    public void indicator_setter(View view, float x, float y, int width, int height, int color) {
        RandomPuzzle_Relativelayout.addView(view);
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.setTranslationY(y);
        view.setTranslationX(x);
        view.setBackgroundColor(color);
    }

    private void InitTimerText(View view) {

        timerValue = view.findViewById(R.id.timerValue);
        timerValue.setTextSize(getWidthInches() * 10);
        timerValue.setTranslationY(board_points[24].y + 2*l);
        timerValue.setTranslationX(board_points[21].x + l/2);

    }

    public void btnFixer(Button btn, Point p) {
        btn.setTranslationX(p.x);
        btn.setTranslationY(p.y);
        btn.getLayoutParams().height = l;
        btn.getLayoutParams().width = l;
    }

    private void InitCubes() {
        cubes[0] = new Cube(getContext(), getResources().getColor(R.color.red), board_points[0]);
        cubes[1] = new Cube(getContext(), getResources().getColor(R.color.red), board_points[1]);
        cubes[2] = new Cube(getContext(), getResources().getColor(R.color.red), board_points[2]);
        cubes[3] = new Cube(getContext(), getResources().getColor(R.color.red), board_points[3]);
        cubes[4] = new Cube(getContext(), getResources().getColor(R.color.blue), board_points[4]);
        cubes[5] = new Cube(getContext(), getResources().getColor(R.color.blue), board_points[5]);
        cubes[6] = new Cube(getContext(), getResources().getColor(R.color.blue), board_points[6]);
        cubes[7] = new Cube(getContext(), getResources().getColor(R.color.blue), board_points[7]);
        cubes[8] = new Cube(getContext(), getResources().getColor(R.color.yellow), board_points[8]);
        cubes[9] = new Cube(getContext(), getResources().getColor(R.color.yellow), board_points[9]);
        cubes[10] = new Cube(getContext(), getResources().getColor(R.color.yellow), board_points[10]);
        cubes[11] = new Cube(getContext(), getResources().getColor(R.color.yellow), board_points[11]);
        cubes[12] = new Cube(getContext(), getResources().getColor(R.color.green), board_points[12]);
        cubes[13] = new Cube(getContext(), getResources().getColor(R.color.green), board_points[13]);
        cubes[14] = new Cube(getContext(), getResources().getColor(R.color.green), board_points[14]);
        cubes[15] = new Cube(getContext(), getResources().getColor(R.color.green), board_points[15]);
        cubes[16] = new Cube(getContext(), getResources().getColor(R.color.texts_color), board_points[16]);
        cubes[17] = new Cube(getContext(), getResources().getColor(R.color.texts_color), board_points[17]);
        cubes[18] = new Cube(getContext(), getResources().getColor(R.color.texts_color), board_points[18]);
        cubes[19] = new Cube(getContext(), getResources().getColor(R.color.texts_color), board_points[19]);
        cubes[20] = new Cube(getContext(), getResources().getColor(R.color.purple), board_points[20]);
        cubes[21] = new Cube(getContext(), getResources().getColor(R.color.purple), board_points[21]);
        cubes[22] = new Cube(getContext(), getResources().getColor(R.color.purple), board_points[22]);
        cubes[23] = new Cube(getContext(), getResources().getColor(R.color.purple), board_points[23]);
        cubes[24] = new Cube(getContext(), getResources().getColor(R.color.blue), board_points[24]);

        int p = 1;
        for (Cube cube : cubes){
            cube.setId(p);
            p++;
        }

        for (Cube cube : cubes){
            cube.setOnClickListener(this);
        }

        for (int i=0; i<9; i++) {
            pattern_cubs[i] = new Cube(getContext(), win_pattern.get(i), pattern_points[i]);
        }

        btn_new_game.setTranslationX(board_points[3].x);
        btn_new_game.setTranslationY(pattern_points[0].y);
    }

    private void InitPoints() {
        board_points[0] = new Point(l , height - (7 * l+ 4*l/5));
        board_points[1] = new Point(board_points[0].x +  5*l / 4, board_points[0].y);
        board_points[2] = new Point(board_points[1].x +  5*l / 4, board_points[1].y);
        board_points[3] = new Point(board_points[2].x +  5*l / 4, board_points[2].y);
        board_points[4] = new Point(board_points[3].x +  5*l / 4, board_points[3].y);
        board_points[5] = new Point(board_points[0].x , board_points[0].y +  5*l / 4);
        board_points[6] = new Point(board_points[5].x +  5*l / 4, board_points[5].y);
        board_points[7] = new Point(board_points[6].x +  5*l / 4, board_points[6].y);
        board_points[8] = new Point(board_points[7].x +  5*l / 4, board_points[7].y);
        board_points[9] = new Point(board_points[8].x + 5*l / 4, board_points[8].y);
        board_points[10] = new Point(board_points[0].x, board_points[9].y + 5*l / 4);
        board_points[11] = new Point(board_points[10].x + 5*l / 4, board_points[10].y );
        board_points[12] = new Point(board_points[11].x  + 5*l / 4, board_points[11].y );
        board_points[13] = new Point(board_points[12].x + 5*l / 4, board_points[12].y);
        board_points[14] = new Point(board_points[13].x + 5*l / 4, board_points[13].y);
        board_points[15] = new Point(board_points[0].x , board_points[14].y + 5 * l / 4);
        board_points[16] = new Point(board_points[15].x + 5*l / 4, board_points[15].y);
        board_points[17] = new Point(board_points[16].x + 5*l / 4, board_points[16].y);
        board_points[18] = new Point(board_points[17].x + 5 * l / 4, board_points[17].y);
        board_points[19] = new Point(board_points[18].x + 5*l / 4, board_points[18].y );
        board_points[20] = new Point(board_points[0].x , board_points[19].y+ 5*l / 4);
        board_points[21] = new Point(board_points[20].x + 5*l / 4, board_points[20].y);
        board_points[22] = new Point(board_points[21].x+ 5*l / 4, board_points[21].y );
        board_points[23] = new Point(board_points[22].x + 5*l / 4, board_points[22].y );
        board_points[24] = new Point(board_points[23].x+ 5*l / 4, board_points[23].y );

        int m = 7*l/10;
        pattern_points[0] = new Point(board_points[0].x , board_points[0].y - 29*l/10);
        pattern_points[1] = new Point(pattern_points[0].x + m, pattern_points[0].y);
        pattern_points[2] = new Point(pattern_points[1].x + m, pattern_points[1].y);
        pattern_points[3] = new Point(pattern_points[0].x , pattern_points[2].y+ m);
        pattern_points[4] = new Point(pattern_points[3].x + m, pattern_points[3].y);
        pattern_points[5] = new Point(pattern_points[4].x + m, pattern_points[4].y);
        pattern_points[6] = new Point(pattern_points[0].x , pattern_points[5].y+ m);
        pattern_points[7] = new Point(pattern_points[6].x + m, pattern_points[6].y);
        pattern_points[8] = new Point(pattern_points[7].x + m, pattern_points[7].y);
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), ""+v.getId(), Toast.LENGTH_SHORT).show();

        if (v.getId() == btn_new_game.getId()){
            stopTimer();
            editor.putLong("randomPuzzle_timeSwapBuff", 0).commit();
            timeSwapBuff = 0;
            startTimer();
        }

        if (v.getId()==cubes[positions[0]].getId()){
            if (master_pattern.get(1).equals(0)){
                cubes[positions[0]].animate().translationX(board_points[1].x).setDuration(animation_time).start();
                pos_modifyer(0, 1);
            }
            if (master_pattern.get(5).equals(0)){
                cubes[positions[0]].animate().translationY(board_points[5].y).setDuration(animation_time).start();
                pos_modifyer(0, 5);
            }
        }
        if (v.getId()==cubes[positions[5]].getId()){
            if (master_pattern.get(0)==0){
                cubes[positions[5]].animate().translationY(board_points[0].y).setDuration(animation_time).start();
                pos_modifyer(5, 0);
            }
            if (master_pattern.get(6)==0){
                cubes[positions[5]].animate().translationX(board_points[6].x).setDuration(animation_time).start();
                pos_modifyer(5, 6);
            }
            if (master_pattern.get(10)==0){
                cubes[positions[5]].animate().translationY(board_points[10].y).setDuration(animation_time).start();
                pos_modifyer(5, 10);
            }
        }
        if (v.getId()==cubes[positions[10]].getId()){
            if (master_pattern.get(5)==0){
                cubes[positions[10]].animate().translationY(board_points[5].y).setDuration(animation_time).start();
                pos_modifyer(10, 5);
            }
            if (master_pattern.get(11)==0){
                cubes[positions[10]].animate().translationX(board_points[11].x).setDuration(animation_time).start();
                pos_modifyer(10, 11);
            }
            if (master_pattern.get(15)==0){
                cubes[positions[10]].animate().translationY(board_points[15].y).setDuration(animation_time).start();
                pos_modifyer(10, 15);
            }
        }
        if (v.getId()==cubes[positions[15]].getId()){
            if (master_pattern.get(10)==0){
                cubes[positions[15]].animate().translationY(board_points[10].y).setDuration(animation_time).start();
                pos_modifyer(15, 10);
            }
            if (master_pattern.get(16)==0){
                cubes[positions[15]].animate().translationX(board_points[16].x).setDuration(animation_time).start();
                pos_modifyer(15, 16);
            }
            if (master_pattern.get(20)==0){
                cubes[positions[15]].animate().translationY(board_points[20].y).setDuration(animation_time).start();
                pos_modifyer(15, 20);
            }
        }
        if (v.getId()==cubes[positions[20]].getId()){
            if (master_pattern.get(15)==0){
                cubes[positions[20]].animate().translationY(board_points[15].y).setDuration(animation_time).start();
                pos_modifyer(20, 15);
            }
            if (master_pattern.get(21)==0){
                cubes[positions[20]].animate().translationX(board_points[21].x).setDuration(animation_time).start();
                pos_modifyer(20, 21);
            }
        }

        if (v.getId()==cubes[positions[1]].getId()){
            if (master_pattern.get(0)==0){
                cubes[positions[1]].animate().translationX(board_points[0].x).setDuration(animation_time).start();
                pos_modifyer(1, 0);
            }
            if (master_pattern.get(6)==0){
                cubes[positions[1]].animate().translationY(board_points[6].y).setDuration(animation_time).start();
                pos_modifyer(1, 6);
            }
            if (master_pattern.get(2)==0){
                cubes[positions[1]].animate().translationX(board_points[2].x).setDuration(animation_time).start();
                pos_modifyer(1, 2);
            }

        }
        if (v.getId()==cubes[positions[6]].getId()){
            if (master_pattern.get(1)==0){
                cubes[positions[6]].animate().translationY(board_points[1].y).setDuration(animation_time).start();
                pos_modifyer(6, 1);
            }
            if (master_pattern.get(5)==0){
                cubes[positions[6]].animate().translationX(board_points[5].x).setDuration(animation_time).start();
                pos_modifyer(6, 5);
            }
            if (master_pattern.get(7)==0){
                cubes[positions[6]].animate().translationX(board_points[7].x).setDuration(animation_time).start();
                pos_modifyer(6, 7);
            }
            if (master_pattern.get(11)==0){
                cubes[positions[6]].animate().translationY(board_points[11].y).setDuration(animation_time).start();
                pos_modifyer(6, 11);
            }
        }
        if (v.getId()==cubes[positions[11]].getId()){
            if (master_pattern.get(6)==0){
                cubes[positions[11]].animate().translationY(board_points[6].y).setDuration(animation_time).start();
                pos_modifyer(11, 6);
            }
            if (master_pattern.get(10)==0){
                cubes[positions[11]].animate().translationX(board_points[10].x).setDuration(animation_time).start();
                pos_modifyer(11, 10);
            }
            if (master_pattern.get(12)==0){
                cubes[positions[11]].animate().translationX(board_points[12].x).setDuration(animation_time).start();
                pos_modifyer(11, 12);
            }
            if (master_pattern.get(16)==0){
                cubes[positions[11]].animate().translationY(board_points[16].y).setDuration(animation_time).start();
                pos_modifyer(11, 16);
            }
        }
        if (v.getId()==cubes[positions[16]].getId()){
            if (master_pattern.get(11)==0){
                cubes[positions[16]].animate().translationY(board_points[11].y).setDuration(animation_time).start();
                pos_modifyer(16, 11);
            }
            if (master_pattern.get(15)==0){
                cubes[positions[16]].animate().translationX(board_points[15].x).setDuration(animation_time).start();
                pos_modifyer(16, 15);
            }
            if (master_pattern.get(17)==0){
                cubes[positions[16]].animate().translationX(board_points[17].x).setDuration(animation_time).start();
                pos_modifyer(16, 17);
            }
            if (master_pattern.get(21)==0){
                cubes[positions[16]].animate().translationY(board_points[21].y).setDuration(animation_time).start();
                pos_modifyer(16, 21);
            }
        }
        if (v.getId()==cubes[positions[21]].getId()){
            if (master_pattern.get(16)==0){
                cubes[positions[21]].animate().translationY(board_points[16].y).setDuration(animation_time).start();
                pos_modifyer(21, 16);
            }
            if (master_pattern.get(20)==0){
                cubes[positions[21]].animate().translationX(board_points[20].x).setDuration(animation_time).start();
                pos_modifyer(21, 20);
            }
            if (master_pattern.get(22)==0){
                cubes[positions[21]].animate().translationX(board_points[22].x).setDuration(animation_time).start();
                pos_modifyer(21, 22);
            }

        }

        if (v.getId()==cubes[positions[2]].getId()){
            if (master_pattern.get(1)==0){
                cubes[positions[2]].animate().translationX(board_points[1].x).setDuration(animation_time).start();
                pos_modifyer(2, 1);
            }
            if (master_pattern.get(7)==0){
                cubes[positions[2]].animate().translationY(board_points[7].y).setDuration(animation_time).start();
                pos_modifyer(2, 7);
            }
            if (master_pattern.get(3)==0){
                cubes[positions[2]].animate().translationX(board_points[3].x).setDuration(animation_time).start();
                pos_modifyer(2, 3);
            }

        }
        if (v.getId()==cubes[positions[7]].getId()){
            if (master_pattern.get(2)==0){
                cubes[positions[7]].animate().translationY(board_points[2].y).setDuration(animation_time).start();
                pos_modifyer(7, 2);
            }
            if (master_pattern.get(6)==0){
                cubes[positions[7]].animate().translationX(board_points[6].x).setDuration(animation_time).start();
                pos_modifyer(7, 6);
            }
            if (master_pattern.get(8)==0){
                cubes[positions[7]].animate().translationX(board_points[8].x).setDuration(animation_time).start();
                pos_modifyer(7, 8);
            }
            if (master_pattern.get(12)==0){
                cubes[positions[7]].animate().translationY(board_points[12].y).setDuration(animation_time).start();
                pos_modifyer(7, 12);
            }
        }
        if (v.getId()==cubes[positions[12]].getId()){
            if (master_pattern.get(7)==0){
                cubes[positions[12]].animate().translationY(board_points[7].y).setDuration(animation_time).start();
                pos_modifyer(12, 7);
            }
            if (master_pattern.get(11)==0){
                cubes[positions[12]].animate().translationX(board_points[11].x).setDuration(animation_time).start();
                pos_modifyer(12, 11);
            }
            if (master_pattern.get(17)==0){
                cubes[positions[12]].animate().translationY(board_points[17].y).setDuration(animation_time).start();
                pos_modifyer(12, 17);
            }
            if (master_pattern.get(13)==0){
                cubes[positions[12]].animate().translationX(board_points[13].x).setDuration(animation_time).start();
                pos_modifyer(12, 13);
            }
        }
        if (v.getId()==cubes[positions[17]].getId()){
            if (master_pattern.get(12)==0){
                cubes[positions[17]].animate().translationY(board_points[12].y).setDuration(animation_time).start();
                pos_modifyer(17, 12);
            }
            if (master_pattern.get(16)==0){
                cubes[positions[17]].animate().translationX(board_points[16].x).setDuration(animation_time).start();
                pos_modifyer(17, 16);
            }
            if (master_pattern.get(18)==0){
                cubes[positions[17]].animate().translationX(board_points[18].x).setDuration(animation_time).start();
                pos_modifyer(17, 18);
            }
            if (master_pattern.get(22)==0){
                cubes[positions[17]].animate().translationY(board_points[22].y).setDuration(animation_time).start();
                pos_modifyer(17, 22);
            }
        }
        if (v.getId()==cubes[positions[22]].getId()){
            if (master_pattern.get(17)==0){
                cubes[positions[22]].animate().translationY(board_points[17].y).setDuration(animation_time).start();
                pos_modifyer(22, 17);
            }
            if (master_pattern.get(21)==0){
                cubes[positions[22]].animate().translationX(board_points[21].x).setDuration(animation_time).start();
                pos_modifyer(22, 21);
            }
            if (master_pattern.get(23)==0){
                cubes[positions[22]].animate().translationX(board_points[23].x).setDuration(animation_time).start();
                pos_modifyer(22, 23);
            }

        }

        if (v.getId()==cubes[positions[3]].getId()){
            if (master_pattern.get(2).equals(0)){
                cubes[positions[3]].animate().translationX(board_points[2].x).setDuration(animation_time).start();
                pos_modifyer(3, 2);
            }
            if (master_pattern.get(8).equals(0)){
                cubes[positions[3]].animate().translationY(board_points[8].y).setDuration(animation_time).start();
                pos_modifyer(3, 8);
            }
            if (master_pattern.get(4).equals(0)){
                cubes[positions[3]].animate().translationY(board_points[4].x).setDuration(animation_time).start();
                pos_modifyer(3, 4);
            }

        }
        if (v.getId()==cubes[positions[8]].getId()){
            if (master_pattern.get(3).equals(0)){
                cubes[positions[8]].animate().translationY(board_points[3].y).setDuration(animation_time).start();
                pos_modifyer(8, 3);
            }
            if (master_pattern.get(7).equals(0)){
                cubes[positions[8]].animate().translationX(board_points[7].x).setDuration(animation_time).start();
                pos_modifyer(8, 7);
            }
            if (master_pattern.get(9).equals(0)){
                cubes[positions[8]].animate().translationX(board_points[9].x).setDuration(animation_time).start();
                pos_modifyer(8, 9);
            }
            if (master_pattern.get(13).equals(0)){
                cubes[positions[8]].animate().translationY(board_points[13].y).setDuration(animation_time).start();
                pos_modifyer(8, 13);
            }
        }
        if (v.getId()==cubes[positions[13]].getId()){
            if (master_pattern.get(8).equals(0)){
                cubes[positions[13]].animate().translationY(board_points[8].y).setDuration(animation_time).start();
                pos_modifyer(13, 8);
            }
            if (master_pattern.get(12).equals(0)){
                cubes[positions[13]].animate().translationX(board_points[12].x).setDuration(animation_time).start();
                pos_modifyer(13, 12);
            }
            if (master_pattern.get(14).equals(0)){
                cubes[positions[13]].animate().translationX(board_points[14].x).setDuration(animation_time).start();
                pos_modifyer(13, 14);
            }
            if (master_pattern.get(18).equals(0)){
                cubes[positions[13]].animate().translationY(board_points[18].y).setDuration(animation_time).start();
                pos_modifyer(13, 18);
            }
        }
        if (v.getId()==cubes[positions[18]].getId()){
            Log.i("mamad", "pos18");
            if (master_pattern.get(13).equals(0)){
                cubes[positions[18]].animate().translationY(board_points[13].y).setDuration(animation_time).start();
                pos_modifyer(18, 13);
            }
            if (master_pattern.get(17).equals(0)){
                cubes[positions[18]].animate().translationX(board_points[17].x).setDuration(animation_time).start();
                pos_modifyer(18, 17);
            }
            if (master_pattern.get(19).equals(0)){
                cubes[positions[18]].animate().translationX(board_points[19].x).setDuration(animation_time).start();
                pos_modifyer(18, 19);
            }
            if (master_pattern.get(23).equals(0)){
                cubes[positions[18]].animate().translationY(board_points[23].y).setDuration(animation_time).start();
                pos_modifyer(18, 23);
            }
        }
        if (v.getId()==cubes[positions[23]].getId()){
            if (master_pattern.get(22).equals(0)){
                cubes[positions[23]].animate().translationX(board_points[22].x).setDuration(animation_time).start();
                pos_modifyer(23, 22);
            }
            if (master_pattern.get(18).equals(0)){
                cubes[positions[23]].animate().translationY(board_points[18].y).setDuration(animation_time).start();
                pos_modifyer(23, 18);
            }
            if (master_pattern.get(24).equals(0)){
                cubes[positions[23]].animate().translationX(board_points[24].x).setDuration(animation_time).start();
                pos_modifyer(23, 24);
            }

        }

        if (v.getId()==cubes[positions[4]].getId()){
            if (master_pattern.get(3).equals(0)){
                cubes[positions[4]].animate().translationX(board_points[3].x).setDuration(animation_time).start();
                pos_modifyer(4, 3);
            }
            if (master_pattern.get(9).equals(0)){
                cubes[positions[4]].animate().translationY(board_points[9].y).setDuration(animation_time).start();
                pos_modifyer(4, 9);
            }

        }
        if (v.getId()==cubes[positions[9]].getId()){
            if (master_pattern.get(4).equals(0)){
                cubes[positions[9]].animate().translationY(board_points[4].y).setDuration(animation_time).start();
                pos_modifyer(9, 4);
            }
            if (master_pattern.get(8).equals(0)){
                cubes[positions[9]].animate().translationX(board_points[8].x).setDuration(animation_time).start();
                pos_modifyer(9, 8);
            }
            if (master_pattern.get(14).equals(0)){
                cubes[positions[9]].animate().translationY(board_points[14].y).setDuration(animation_time).start();
                pos_modifyer(9, 14);
            }
        }
        if (v.getId()==cubes[positions[14]].getId()){
            if (master_pattern.get(9).equals(0)){
                cubes[positions[14]].animate().translationY(board_points[9].y).setDuration(animation_time).start();
                pos_modifyer(14, 9);
            }
            if (master_pattern.get(13).equals(0)){
                cubes[positions[14]].animate().translationX(board_points[13].x).setDuration(animation_time).start();
                pos_modifyer(14, 13);
            }
            if (master_pattern.get(19).equals(0)){
                cubes[positions[14]].animate().translationY(board_points[19].y).setDuration(animation_time).start();
                pos_modifyer(14, 19);
            }
        }
        if (v.getId()==cubes[positions[19]].getId()){
            if (master_pattern.get(14).equals(0)){
                cubes[positions[19]].animate().translationY(board_points[14].y).setDuration(animation_time).start();
                pos_modifyer(19, 14);
            }
            if (master_pattern.get(18).equals(0)){
                cubes[positions[19]].animate().translationX(board_points[18].x).setDuration(animation_time).start();
                pos_modifyer(19, 18);
            }
            if (master_pattern.get(24).equals(0)){
                cubes[positions[19]].animate().translationY(board_points[24].y).setDuration(animation_time).start();
                pos_modifyer(19, 24);
            }
        }
        Log.i("mamad", master_pattern.toString());
    }

    public void pos_modifyer(int a, int b){
        int temp = positions[a];
        positions[a] = positions[b];
        positions[b] = temp;
        temp = master_pattern.get(a);
        master_pattern.put(a, master_pattern.get(b));
        master_pattern.put(b, temp);
        cubes[24].setTranslationX(cubes[positions[b]].getX());
        cubes[24].setTranslationY(cubes[positions[b]].getY());
    }

    public boolean overChecker() {
        if ((master_pattern.get(6).equals(win_pattern.get(0))) && (master_pattern.get(7).equals(win_pattern.get(1))) && (master_pattern.get(8).equals(win_pattern.get(2))) &&
                (master_pattern.get(11).equals(win_pattern.get(3))) && (master_pattern.get(12).equals(win_pattern.get(4))) && (master_pattern.get(13).equals(win_pattern.get(5))) &&
                (master_pattern.get(16).equals(win_pattern.get(6))) && (master_pattern.get(17).equals(win_pattern.get(7))) &&
                (master_pattern.get(18).equals(win_pattern.get(8)))) {
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
        editor.putLong("randomPuzzle_timeSwapBuff", 0L).commit();
        timeSwapBuff = 0L;
        if (pref.getLong("randomPuzzle_bestRecord", 0L) == 0L || pref.getLong("randomPuzzle_bestRecord", 0L) > recordTime) {
            editor.putLong("randomPuzzle_bestRecord", recordTime).commit();
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

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                startTimer();
            }
        });
        if (recordTime != 0) {
            d.show();
        }
    }


    public void startTimer() {
        timeSwapBuff = pref.getLong("randomPuzzle_timeSwapBuff", 0);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        customHandler.removeCallbacks(updateTimerThread);
        timeSwapBuff = updatedTime;
        editor.putLong("randomPuzzle_timeSwapBuff", timeSwapBuff).commit();
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

}

