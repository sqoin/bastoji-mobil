
package com.sqoin.bastoji.presenter.activities.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.platform.APIClient;
import com.sqoin.bastoji.BuildConfig;
import com.sqoin.bastoji.BuildConfig;
import com.sqoin.bastoji.R;
import com.sqoin.bastoji.presenter.activities.HomeActivity;
import com.sqoin.bastoji.presenter.activities.SetPinActivity;
import com.sqoin.bastoji.presenter.activities.util.BRActivity;
import com.sqoin.bastoji.tools.animation.BRAnimator;
import com.sqoin.bastoji.tools.manager.BRReportsManager;
import com.sqoin.bastoji.tools.security.BRKeyStore;
import com.sqoin.bastoji.tools.security.PostAuth;
import com.sqoin.bastoji.tools.security.SmartValidator;
import com.sqoin.bastoji.tools.threads.executor.BRExecutor;
import com.sqoin.bastoji.tools.util.BRConstants;
import com.sqoin.bastoji.tools.util.Utils;
import com.sqoin.bastoji.wallet.WalletsMaster;

import java.io.Serializable;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 8/4/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class IntroActivity extends BRActivity implements Serializable {
    private static final String TAG = IntroActivity.class.getName();
    public Button newWalletButton;
    public Button recoverWalletButton;
    public static IntroActivity introActivity;
    public static boolean appVisible = false;
    private static IntroActivity app;
    private View splashScreen;
    private ImageButton faq;

    public static IntroActivity getApp() {
        return app;
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.i(TAG , "on Restart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Log.i(TAG, "onCreate : setContentView");
        newWalletButton = (Button) findViewById(R.id.button_new_wallet);
        recoverWalletButton = (Button) findViewById(R.id.button_recover_wallet);
        splashScreen = findViewById(R.id.splash_screen);
        setListeners();
        updateBundles();
//        SyncManager.getInstance().updateAlarms(this);
        faq = (ImageButton) findViewById(R.id.faq_button);
            faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                Log.i(TAG, "onCreate : !BRAnimator,isClickAllowed");
                BRAnimator.showSupportFragment(app, BRConstants.startView);
            }


        });


        if (!BuildConfig.DEBUG && BRKeyStore.AUTH_DURATION_SEC != 300) {
            Log.e(TAG, "onCreate: BRKeyStore.AUTH_DURATION_SEC != 300");
            BRReportsManager.reportBug(new RuntimeException("AUTH_DURATION_SEC should be 300"), true);
        }
        introActivity = this;

        getWindowManager().getDefaultDisplay().getSize(screenParametersPoint);

        if (Utils.isEmulatorOrDebug(this))
            Utils.printPhoneSpecs();

        byte[] masterPubKey = BRKeyStore.getMasterPublicKey(this);

        boolean isFirstAddressCorrect = false;
        if (masterPubKey != null && masterPubKey.length != 0) {
            Log.e(TAG , "onCreate: masterPubey !=null && masterPubKey,length !=0");
            isFirstAddressCorrect = SmartValidator.checkFirstAddress(this, masterPubKey);
        }
        if (!isFirstAddressCorrect) {
            Log.e(TAG , "onCreate: !isFirstAddressCorrect");
            WalletsMaster.getInstance(this).wipeWalletButKeystore(this);


        }

        PostAuth.getInstance().onCanaryCheck(this, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashScreen.setVisibility(View.GONE);
             Log.e(TAG , "onCreate:  splashScreen");
            }
        }, 1000);
Log.i("introActivity","end oncreate");
    }

    private void updateBundles() {
        BRExecutor.getInstance().forBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("updateBundle");
                final long startTime = System.currentTimeMillis();
                APIClient apiClient = APIClient.getInstance(IntroActivity.this);
                apiClient.updateBundle();
                long endTime = System.currentTimeMillis();
                Log.e(TAG, "updateBundle DONE in " + (endTime - startTime) + "ms");
            }
        });
    }


    private void setListeners() {
        newWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                Log.d(TAG, "newWalle(setListeners onclick: !BRAnimator.isClickAllowed)");
                HomeActivity bApp = HomeActivity.getApp();
                Intent intent = new Intent(IntroActivity.this, SetPinActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                if (bApp != null) bApp.finish();
                Log.e(TAG, "newWalle(setListeners: onClick bApp != null)" );
            }
        });

        recoverWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                Log.e(TAG, "recoverWalle(setListeners onclick: !BRAnimator.isClickAllowed)");
                HomeActivity bApp = HomeActivity.getApp();
                if (bApp != null) bApp.finish();
                Intent intent = new Intent(IntroActivity.this, RecoverActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                if (bApp != null) bApp.finish();
                Log.e(TAG, "recoverWalle(setListeners: onClick bApp != null)" );

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
        Log.i(TAG, "on Pause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "on Save Instance State");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG , "on Stop");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG , "on Back Pressed");

    }

}
