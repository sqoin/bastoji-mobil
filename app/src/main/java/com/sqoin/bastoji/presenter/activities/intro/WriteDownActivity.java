package com.sqoin.bastoji.presenter.activities.intro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.sqoin.bastoji.R;
import com.sqoin.bastoji.presenter.activities.util.BRActivity;
import com.sqoin.bastoji.presenter.interfaces.BRAuthCompletion;
import com.sqoin.bastoji.tools.animation.BRAnimator;
import com.sqoin.bastoji.tools.security.AuthManager;
import com.sqoin.bastoji.tools.security.PostAuth;
import com.sqoin.bastoji.tools.util.BRConstants;

public class WriteDownActivity extends BRActivity {
    private static final String TAG = WriteDownActivity.class.getName();
    private Button writeButton;
    private ImageButton close;
    public static boolean appVisible = false;
    private static WriteDownActivity app;

    public static WriteDownActivity getApp() {
        return app;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_down);
        Log.i(TAG, "onCreate : setContentView");
        writeButton = (Button) findViewById(R.id.button_write_down);
        close = (ImageButton) findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        ImageButton faq = (ImageButton) findViewById(R.id.faq_button);
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                Log.i(TAG, "onCreate : writeButton(!BRAnimator,isClikAllowed)");
                BRAnimator.showSupportFragment(app, BRConstants.paperKey);
            }
        });
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                AuthManager.getInstance().authPrompt(WriteDownActivity.this, null, getString(R.string.VerifyPin_continueBody), true, false, new BRAuthCompletion() {
                    @Override
                    public void onComplete() {
                        PostAuth.getInstance().onPhraseCheckAuth(WriteDownActivity.this, false);
                        Log.i(TAG, "on complete");
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "on Cancel");

                    }
                });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;
        Log.i(TAG, "on Resume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        appVisible = false;
        Log.i(TAG , "on Pause");
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            Log.v(TAG, "onBackPressed:getFragmentManager.getBackStackEntryCount");
            close();
        } else {
            Log.v(TAG, "onBackPressed:getFragmentManager.popBackStack");
            getFragmentManager().popBackStack();
        }
    }

    private void close() {
        Log.e(TAG, "close: ");
        BRAnimator.startBreadActivity(this, false);
        overridePendingTransition(R.anim.fade_up, R.anim.exit_to_bottom);
        if (!isDestroyed())
            Log.v(TAG, "finish");
            finish();
        //additional code
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
    }

}
