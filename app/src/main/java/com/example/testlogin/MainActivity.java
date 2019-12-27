package com.example.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.api2pdf.client.Api2PdfClient;
import com.api2pdf.models.Api2PdfResponse;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;



import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private static String FACEBOOK_FIELD_PROFILE_IMAGE = "picture.type(large)";
    private static String FACEBOOK_FIELDS = "fields";

    private void getFacebookData() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> {
                    updateAvatar(getImageUrl(response));
                });
        Bundle parameters = new Bundle();
        parameters.putString(FACEBOOK_FIELDS, FACEBOOK_FIELD_PROFILE_IMAGE);
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void updateAvatar(String imageUrl) {

    }

    private static String FACEBOOK_FIELD_PICTURE = "picture";
    private static String FACEBOOK_FIELD_DATA = "data";
    private static String FACEBOOK_FIELD_URL = "url";
    private String getImageUrl(GraphResponse response) {
        String url = null;
        try {
            url = response.getJSONObject()
                    .getJSONObject(FACEBOOK_FIELD_PICTURE)
                    .getJSONObject(FACEBOOK_FIELD_DATA)
                    .getString(FACEBOOK_FIELD_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions("email");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }
        else {
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d("Login result", loginResult.getAccessToken().getToken());
                            if(loginResult.getAccessToken().isExpired()) {
                                Toast.
                                        makeText(
                                                MainActivity.this,
                                                loginResult.getAccessToken().getSource().toString(),
                                                Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.d("FacebookException", exception.getMessage());
                        }
                    });
        }

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Api2PdfClient a2pClient = new Api2PdfClient("2677ef5d-022c-470b-ae4c-72cddf2fe81e");

                        Api2PdfResponse pdfResponse = null;
                        try {
                            pdfResponse = a2pClient.headlessChromeFromHtml("<p>test</p>", true, "test.pdf");
                            Log.e("PDF",pdfResponse.getPdf());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
