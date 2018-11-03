package ir.help7.quado;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import ir.adad.client.Adad;
import ir.help7.quado.interfaces.Go_Quado;
import ir.help7.quado.interfaces.Go_q2;
import ir.help7.quado.interfaces.Go_q3;
import ir.help7.quado.interfaces.Go_q4;
import ir.help7.quado.interfaces.Go_q5;
import ir.help7.quado.interfaces.Go_random_puzzle;


/**
 * A simple {@link Fragment} subclass.
 */
public class LauncherFragment extends Fragment implements View.OnClickListener {

    DisplayMetrics displayMetrics;
    Go_q2 go_q2;
    Go_q3 go_q3;
    Go_q4 go_q4;
    Go_q5 go_q5;
    Go_random_puzzle go_random_puzzle;
    SharedPreferences pref;
    Button btn_q2, btn_q3, btn_q4, btn_q5, btn_random;
    TextView tv_welcome;
    int width, height;
    String language_flag, UPCOMING_FLAG;



    public LauncherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // this is for adad advertise enable that when app certificated
        Adad.prepareInterstitialAd();

        // Inflate the layout for this fragment
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        UPCOMING_FLAG = pref.getString("UPCOMING_FLAG",MainActivity.NEW_GAME_FLAG);
        language_flag = pref.getString("language_flag", MainActivity.ENGLISH);
        View view = inflater.inflate(R.layout.fragment_launcher, container, false);
        Typeface english_typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/english_font.ttf");
        Typeface farsi_typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/farsi_font.ttf");
        get_screen_scale();
        view_init(view, english_typeface, farsi_typeface);

        return view;
    }


    // this method initializes the view components.
    private void view_init(View view, Typeface english_typeface, Typeface farsi_typeface) {

        btn_q2 =  view.findViewById(R.id.btn_q2);
        btn_q3 =  view.findViewById(R.id.btn_q3);
        btn_q4 =  view.findViewById(R.id.btn_q4);
        btn_q5 =  view.findViewById(R.id.btn_q5);
        btn_random =  view.findViewById(R.id.btn_random);
        tv_welcome =  view.findViewById(R.id.tv_welcome);

        tv_welcome.setY((float)height/10);
        int demention = 15*height/100;
        btn_q2.setY((float)3*height/10);
        btn_q2.setX((float)(width/2-7*demention/5));
        btn_q3.setY((float)3*height/10);
        btn_q3.setX((float)(width/2+2*demention/5));
        btn_q4.setY((float)5*height/10+demention/5);
        btn_q4.setX((float)(width/2-7*demention/5));
        btn_q5.setY((float)5*height/10);
        btn_q5.setX((float)(width/2+demention/5));
        btn_q2.getLayoutParams().height = demention;
        btn_q2.getLayoutParams().width = demention;
        btn_q3.getLayoutParams().height = demention;
        btn_q3.getLayoutParams().width = demention;
        btn_q4.getLayoutParams().height = demention;
        btn_q4.getLayoutParams().width = demention;
        btn_q5.getLayoutParams().height = 8*demention/5;
        btn_q5.getLayoutParams().width = 8*demention/5;

        Glide.with(getContext()).load(R.drawable.ic_q2).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_q2.setBackground(resource);
                }
            }
        });
        Glide.with(getContext()).load(R.drawable.ic_q3).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_q3.setBackground(resource);
                }
            }
        });
        Glide.with(getContext()).load(R.drawable.ic_q4).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_q4.setBackground(resource);
                }
            }
        });
        Glide.with(getContext()).load(R.drawable.ic_q5).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_q5.setBackground(resource);
                }
            }
        });
        tv_welcome.setTextSize(getWidthInches()*12);
        if (language_flag.equals(MainActivity.ENGLISH)){
            tv_welcome.setTypeface(english_typeface);
        }
        if (language_flag.equals(MainActivity.FARSI)){
            tv_welcome.setTypeface(farsi_typeface);
        }

        animate2(tv_welcome,0);
        animate1(btn_q2,300);
        animate1(btn_q3,300);
        animate1(btn_q4,300);
        animate1(btn_q5,300);
        btn_q2.setOnClickListener(this);
        btn_q3.setOnClickListener(this);
        btn_q4.setOnClickListener(this);
        btn_q5.setOnClickListener(this);
        btn_random.setOnClickListener(this);
    }

    public void animate1(View view,long d){
        view.setAlpha(0);
        view.animate().alpha(1f).setDuration(500).setStartDelay(d).start();
    }

    //this method gets screen pixels.
    private void get_screen_scale() {
        displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    // this method provides screen size in inches.
    public float getWidthInches(){
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels/displayMetrics.xdpi;
    }

    // this method will animate all things in up coming.
    public void animate2(final View view, long delay){
        float m = view.getY();
        ValueAnimator va = ValueAnimator.ofFloat(m);
        int duration = 700;
        va.setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setY((float) animation.getAnimatedValue());
            }
        });
        va.setStartDelay(delay);
        va.setInterpolator(new DecelerateInterpolator());
        va.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        go_q2 = (Go_q2) context;
        go_q3 = (Go_q3) context;
        go_q4 = (Go_q4) context;
        go_q5 = (Go_q5) context;
        go_random_puzzle = (Go_random_puzzle) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        go_q2 = null;
        go_q3 = null;
        go_q4 = null;
        go_q5 = null;
        go_random_puzzle = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btn_q2.getId()){
            go_q2.onGo_q2_click();
        }
        if (v.getId() == btn_q3.getId()){
            go_q3.onGo_q3_click();
        }
        if (v.getId() == btn_q4.getId()){
            go_q4.onGo_q4_click();
        }
        if (v.getId() == btn_q5.getId()){
            go_q5.onGo_q5_click();
        }
        if (v.getId() == btn_random.getId()){
            go_random_puzzle.onGo_random_puzzle();
        }
    }
}
