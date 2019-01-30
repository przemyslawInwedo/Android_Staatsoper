package app.nunc.com.staatsoperlivestreaming.Activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.roughike.bottombar.BottomBar;

import app.nunc.com.staatsoperlivestreaming.Fragment.LiveFragment;
import app.nunc.com.staatsoperlivestreaming.Fragment.ProfileFragment;
import app.nunc.com.staatsoperlivestreaming.Fragment.VideothequeFragment;
import app.nunc.com.staatsoperlivestreaming.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        attachBottomBar();
        showActiveBar();
        startScreen();
        actionBar = getActionBar();

    }

    private void showActiveBar() {
        bottomBar.setActiveTabColor(getResources().getColor(R.color.white));
        bottomBar.setInActiveTabColor(getResources().getColor(R.color.beige_light));
    }

    private void attachBottomBar() {

        bottomBar.setOnTabSelectListener(this::selectScreen);
        bottomBar.setOnTabReselectListener(this::selectScreen);

    }

    private void selectScreen(int menuItemId) {

        switch (menuItemId) {
            case R.id.live:

                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new LiveFragment()).commit();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
            case R.id.videotheque:

                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new VideothequeFragment()).commit();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.profile:

                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new ProfileFragment()).commit();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }
    }

    private void startScreen() {
        bottomBar.setVisibility(View.VISIBLE);
        bottomBar.setDefaultTab(R.id.live);
        selectScreen(R.id.live);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LiveFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        bottomBar.selectTabAtPosition(0);
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
