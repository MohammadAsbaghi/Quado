package ir.help7.quado;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionSet;

import ir.adad.client.Adad;
import ir.help7.quado.interfaces.Go_Launcher;
import ir.help7.quado.interfaces.Go_Quado;
import ir.help7.quado.interfaces.Go_q2;
import ir.help7.quado.interfaces.Go_q3;
import ir.help7.quado.interfaces.Go_q4;
import ir.help7.quado.interfaces.Go_q5;
import ir.help7.quado.interfaces.Go_random_puzzle;


public class MainActivity extends AppCompatActivity implements Go_Launcher, Go_q2, Go_q3, Go_q4, Go_q5 , Go_random_puzzle{

    public static String LAUNCHER = "launcher";
    public static String ENGLISH = "english";
    public static String FARSI = "farsi";
    public static String NEW_GAME_FLAG = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // this is for adad advertise enable that when app certificated
        Adad.initialize(getApplicationContext());

        LauncherFragment launcherFragment = new LauncherFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            launcherFragment.setEnterTransition(new Fade());
            launcherFragment.setExitTransition(new Fade());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.f_container, launcherFragment, LAUNCHER);
        transaction.commit();

    }



    @Override
    public void on_go_launcher_click() {
        LauncherFragment launcherFragment = new LauncherFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            launcherFragment.setEnterTransition(new Fade());
            launcherFragment.setExitTransition(new Fade());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.f_container, launcherFragment, "launcher");
        transaction.commit();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("q2");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q3");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q4");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q5");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("randomPuzzle");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LAUNCHER);
        if (fragment!=null && fragment.isVisible()){
            this.finish();
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q2");
        if (fragment!=null && fragment.isVisible()){
            on_go_launcher_click();
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q3");
        if (fragment!=null && fragment.isVisible()){
            on_go_launcher_click();
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q4");
        if (fragment!=null && fragment.isVisible()){
            on_go_launcher_click();
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q5");
        if (fragment!=null && fragment.isVisible()){
            on_go_launcher_click();
        }
        fragment = getSupportFragmentManager().findFragmentByTag("randomPuzzle");
        if (fragment!=null && fragment.isVisible()){
            on_go_launcher_click();
        }
    }

    @Override
    public void onGo_q2_click() {
        Q2 q2 = new Q2();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            q2.setEnterTransition(new Fade());
            q2.setExitTransition(new Fade());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.f_container, q2, "q2");
        transaction.commit();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LAUNCHER);
        if (fragment!=null){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q3");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q4");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q5");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("randomPuzzle");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
    }

    @Override
    public void onGo_q3_click() {
        Q3 q3 = new Q3();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            q3.setEnterTransition(new Fade());
            q3.setExitTransition(new Fade());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.f_container, q3, "q3");
        transaction.commit();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LAUNCHER);
        if (fragment!=null){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q2");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q4");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q5");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("randomPuzzle");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
    }

    @Override
    public void onGo_q4_click() {
        Q4 q4 = new Q4();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            q4.setEnterTransition(new Fade());
            q4.setExitTransition(new Fade());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.f_container, q4, "q4");
        transaction.commit();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LAUNCHER);
        if (fragment!=null){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q2");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q3");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q5");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("randomPuzzle");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }

    }


    @Override
    public void onGo_q5_click() {
        Q5 q5 = new Q5();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            q5.setEnterTransition(new Fade());
            q5.setExitTransition(new Fade());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.f_container, q5, "q5");
        transaction.commit();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LAUNCHER);
        if (fragment!=null){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q2");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q3");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q4");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("randomPuzzle");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
    }

    @Override
    public void onGo_random_puzzle() {
        RandomPuzzle randomPuzzle = new RandomPuzzle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            randomPuzzle.setEnterTransition(new Fade());
            randomPuzzle.setExitTransition(new Fade());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.f_container, randomPuzzle, "randomPuzzle");
        transaction.commit();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LAUNCHER);
        if (fragment!=null){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q2");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q3");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q4");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
        fragment = getSupportFragmentManager().findFragmentByTag("q5");
        if (fragment!=null && fragment.isVisible()){
            transaction.remove(fragment);
        }
    }
}
