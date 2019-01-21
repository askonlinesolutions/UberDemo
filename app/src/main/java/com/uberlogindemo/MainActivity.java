package com.uberlogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.android.core.auth.LoginButton;
import com.uber.sdk.android.core.auth.LoginCallback;
import com.uber.sdk.android.core.auth.LoginManager;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.AccessTokenStorage;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    public static final String CLIENT_ID = "ZuXuUnnuv-p1DHCFqf3Ysv5nL8hFkJYu";
    public static final String REDIRECT_URI = "IMCooking.com.IMCooking://oauth/consumer";

    private static final String LOG_TAG = "LoginSampleActivity";

    private static final int LOGIN_BUTTON_CUSTOM_REQUEST_CODE = 1112;
    private static final int CUSTOM_BUTTON_REQUEST_CODE = 1113;

    private Button btnLogin;
    private LoginButton blackButton;
    private LoginButton whiteButton;
    private Button customButton;
    private AccessTokenManager accessTokenStorage;
    private LoginManager loginManager;
    private SessionConfiguration configuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configuration = new SessionConfiguration.Builder()
                .setClientId(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
                .build();

        /*validateConfiguration(configuration);*/

        accessTokenStorage = new AccessTokenManager(this);

        //Create a button with a custom request code
        whiteButton = findViewById(R.id.uber_button_white);
        btnLogin = findViewById(R.id.login);

        whiteButton.setCallback(new SampleLoginCallback())
                .setSessionConfiguration(configuration);

        //Create a button using a custom AccessTokenStorage
        //Custom Scopes are set using XML for this button as well in R.layout.activity_sample
        blackButton = findViewById(R.id.uber_button_black);
        blackButton.setAccessTokenManager(accessTokenStorage)
                .setCallback(new SampleLoginCallback())
                .setSessionConfiguration(configuration)
                .setRequestCode(LOGIN_BUTTON_CUSTOM_REQUEST_CODE);


        //Use a custom button with an onClickListener to call the LoginManager directly
        loginManager = new LoginManager(accessTokenStorage,
                new SampleLoginCallback(),
                configuration,
                CUSTOM_BUTTON_REQUEST_CODE);

        customButton = findViewById(R.id.custom_uber_button);
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginManager.login(MainActivity.this);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.login(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, String.format("onActivityResult requestCode:[%s] resultCode [%s]",
                requestCode, resultCode));

        //Allow each a chance to catch it.
        whiteButton.onActivityResult(requestCode, resultCode, data);

        blackButton.onActivityResult(requestCode, resultCode, data);

        loginManager.onActivityResult(this, requestCode, resultCode, data);
    }

    private class SampleLoginCallback implements LoginCallback {

        @Override
        public void onLoginCancel() {
            Toast.makeText(MainActivity.this, R.string.user_cancels_message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLoginError(@NonNull AuthenticationError error) {
            Toast.makeText(MainActivity.this,
                    getString(R.string.login_error_message, error.name()), Toast.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onLoginSuccess(@NonNull AccessToken accessToken) {
            /*loadProfileInfo();*/
        }

        @Override
        public void onAuthorizationCodeReceived(@NonNull String authorizationCode) {
            Toast.makeText(MainActivity.this, getString(R.string.authorization_code_message, authorizationCode),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

}
