package com.sqoin.bastoji.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sqoin.bastoji.R;
import com.sqoin.bastoji.presenter.activities.util.BRActivity;
import com.sqoin.bastoji.tools.animation.BRAnimator;
import com.sqoin.bastoji.tools.animation.SpringAnimator;
import com.sqoin.bastoji.tools.manager.BRSharedPrefs;
import com.sqoin.bastoji.tools.security.AuthManager;
import com.sqoin.bastoji.tools.util.BRConstants;

import java.util.Locale;


public class DisabledActivity extends BRActivity {
    private static final String TAG = DisabledActivity.class.getName();
    private TextView untilLabel;
    private TextView disabled;
    //    private TextView attempts;
    private ConstraintLayout layout;
    private Button resetButton;
    private CountDownTimer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disabled);
        Log.i(TAG, "onCreate : setContentView");

        untilLabel = (TextView) findViewById(R.id.until_label);
        layout = (ConstraintLayout) findViewById(R.id.layout);
        disabled = (TextView) findViewById(R.id.disabled);
//        attempts = (TextView) findViewById(R.id.attempts_label);
        resetButton = (Button) findViewById(R.id.reset_button);

        ImageButton faq = (ImageButton) findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                Log.i(TAG, "onCreate : !BRAnimator,isClickAllowed");
                BRAnimator.showSupportFragment(DisabledActivity.this, BRConstants.walletDisabled);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisabledActivity.this, InputWordsActivity.class);
                intent.putExtra("resetPin", true);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        untilLabel.setText("");

    }

    private void refresh() {
        if (AuthManager.getInstance().isWalletDisabled(DisabledActivity.this)) {
            Log.i(TAG , " refresh : AuthManager.getInstance.isWalletDisabled" );
            SpringAnimator.failShakeAnimation(DisabledActivity.this, disabled);
        } else {
            Log.i(TAG, "refresh :BRAnmator,startBreadActivity");
            BRAnimator.startBreadActivity(DisabledActivity.this, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "on Stop");
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        long disabledUntil = AuthManager.getInstance().disabledUntil(this);
        Log.e(TAG, "onResume: disabledUntil: " + disabledUntil + ", diff: " + (disabledUntil - BRSharedPrefs.getSecureTime(this)));
        long disabledTime = disabledUntil - System.currentTimeMillis();
        Log.e(TAG, "onResume: disabledTime");
        int seconds = (int) disabledTime / 1000;
        timer = new CountDownTimer(seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long durationSeconds = (millisUntilFinished / 1000);
                Log.e(TAG , "onResume: durationSeconds");
                untilLabel.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", durationSeconds / 3600,
                        (durationSeconds % 3600) / 60, (durationSeconds % 60)));
            }

            public void onFinish() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 2000);
                long durationSeconds = 0;
                Log.e(TAG,"onFinish: durationSeconds=0");
                untilLabel.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", durationSeconds / 3600,
                        (durationSeconds % 3600) / 60, (durationSeconds % 60)));
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "on Pause");
        timer.cancel();

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            Log.d(TAG , "onBackPressed: getFragmentManger,getBackStackEntryCount>0");
            getFragmentManager().popBackStack();
        } else if (AuthManager.getInstance().isWalletDisabled(DisabledActivity.this)) {
            Log.d(TAG , "onBackPressed : AuthManager.getInstance.isWalletDisabled");
            SpringAnimator.failShakeAnimation(DisabledActivity.this, disabled);
        } else {
            Log.d(TAG , "onBackPressed: BRAnimator,startBreadActivity");
            BRAnimator.startBreadActivity(DisabledActivity.this, true);
        }
        overridePendingTransition(R.anim.fade_up, R.anim.fade_down);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG ,"on Save Instance State");

    }
}
