package app.nunc.com.staatsoperlivestreaming.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.roughike.bottombar.BottomBar;

import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.Fragment.ProfileFragment;
import app.nunc.com.staatsoperlivestreaming.Fragment.VideothequeFragment;
import app.nunc.com.staatsoperlivestreaming.Fragment.LiveFragment;
import app.nunc.com.staatsoperlivestreaming.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        attachBottomBar();
        showActiveBar();
        startScreen();

        Log.d("SYSTEM_X_DEVICE_MODEL", Keys.X_DEVICE_MODEL);
        Log.d("SYSTEM_X_DEVICE_APP", Keys.X_DEVICE_APP_NAME);
        Log.d("SYSTEM_X_DEVICE_IDENTI", Keys.X_DEVICE_IDENTIFIER);
        Log.d("SYSTEM_X_DEVICE_SYSTEM", Keys.X_DEVICE_SYSTEM_VERSION);
        Log.d("SYSTEM_X_DEVICE_TYPE", Keys.X_DEVICE_TYPE);

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
                break;
            case R.id.videotheque:

                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new VideothequeFragment()).commit();
                break;
            case R.id.profile:

                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new ProfileFragment()).commit();
                break;
        }
    }

    private void startScreen() {
        bottomBar.setVisibility(View.VISIBLE);
        bottomBar.setDefaultTab(R.id.live);
        selectScreen(R.id.live);
    }

}
