package hmperson1.apps.autosign;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import hmperson1.apps.autosign.sign.Sign;
import hmperson1.apps.autosign.sign.Signs;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignActivity extends Activity {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * The number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * The flags to pass to {@link View#setSystemUiVisibility(int)}.
     */
    private static final int UI_DEFAULT_OPTIONS = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    /**
     * The flags to pass to {@link View#setSystemUiVisibility(int)}.
     */
    private static final int UI_FULLSCREEN_OPTIONS = UI_DEFAULT_OPTIONS
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    int mShortAnimTime;
    Handler mHideHandler;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
            return false;
        }
    };
    private View mControlsView;
    private View mDecorView;
    private ActionBar mActionBar;
    /**
     * Hides the system UI and our controls.
     */
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mControlsView.animate()
                    .alpha(0)
                    .setDuration(mShortAnimTime);
            mDecorView.setSystemUiVisibility(UI_FULLSCREEN_OPTIONS);
            mActionBar.hide();
        }
    };
    /**
     * Shows the system UI and our controls.
     */
    Runnable mShowRunnable = new Runnable() {
        @Override
        public void run() {
            mControlsView.animate()
                    .alpha(1)
                    .setDuration(mShortAnimTime);
            mDecorView.setSystemUiVisibility(UI_DEFAULT_OPTIONS);
            mActionBar.show();
        }
    };
    private TextView mContentView;
    /**
     * The ID of the sign we are showing.
     */
    private int mId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        mHideHandler = new Handler();
        mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mActionBar = getActionBar();
        mContentView = (TextView) findViewById(R.id.fullscreen_content);
        mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(UI_DEFAULT_OPTIONS);

        Intent intent = getIntent();
        if (intent.hasExtra(ARG_ITEM_ID)) {
            mId = intent.getIntExtra(ARG_ITEM_ID, 0);
        } else if (savedInstanceState != null) {
            mId = savedInstanceState.getInt(ARG_ITEM_ID, 0);
        }

        // Show the controls when the system UI is shown.
        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    mHideHandler.post(mShowRunnable);
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    mHideHandler.post(mHideRunnable);
                } else {
                    mHideHandler.post(mShowRunnable);
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.next_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.prev_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSign();

        // Trigger the initial hide() shortly after the activity has been
        // resumed, to briefly hint to the user that UI controls
        // are available.
        delayedHide(1000);
    }

    private void updateSign() {
        Sign sign = Signs.instance(this).getSign(mId);
        mContentView.setText(sign.getText());
        mContentView.setTypeface(Typeface.create(sign.getTypeface(), Typeface.NORMAL));
        mContentView.setTextColor(sign.getFontColor());
        mContentView.setBackgroundColor(sign.getBgColor());
    }

    public void next(View view) {
        int size = Signs.instance(this).size();
        if (++mId >= size) {
            mId -= size;
        }
        updateSign();
    }

    public void prev(View view) {
        if (--mId < 0) {
            mId += Signs.instance(this).size();
        }
        updateSign();
    }

    public void list(View view) {
        Intent intent = new Intent(this, SignListActivity.class);
        intent.putExtra(ARG_ITEM_ID, mId);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ARG_ITEM_ID, mId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
