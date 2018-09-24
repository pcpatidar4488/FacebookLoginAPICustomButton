package com.example.pc.facebookintegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    com.facebook.login.widget.LoginButton loginButton;
    Button mFb;

    CallbackManager callbackManager;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    private String id, name, email, gender, birthday;
    Profile profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        mFb = (Button) findViewById(R.id.fb);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissionNeeds = Arrays.asList("id","user_photos", "email",
                "user_birthday", "public_profile", "AccessToken");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mFb.setText("Logout");
                        System.out.println("onSuccess");
                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {
                                        try {
                                            id = object.getString("id");
                                            try {
                                                URL profile_pic = new URL(
                                                        "http://graph.facebook.com/" + id + "/picture?type=large");
                                                Log.i("profile_pic",
                                                        profile_pic + "");

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            name = object.getString("name");
                                            email = object.getString("email");
                                            gender = object.getString("gender");
                                            birthday = object.getString("birthday");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("pppppppp ",
                                                id + " " + name + " " + email + " " + gender + " " + birthday);
                                    }


                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                        GraphRequest request1 = GraphRequest.newGraphPathRequest(loginResult.getAccessToken(), "/100028914904929/friends",new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                // Insert your code here
                                System.out.println(response.getJSONArray());
                            }
                        });

                        request1.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

        profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            // user has logged in
            mFb.setText("Facebook");
        } else {
            // user has not logged in
            mFb.setText("Facebook");
        }

        accessToken = AccessToken.getCurrentAccessToken();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                if (AccessToken.getCurrentAccessToken()!=null){
                    Toast.makeText(LoginActivity.this, "AccessToken", Toast.LENGTH_SHORT).show();
                }else {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(LoginActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    mFb.setText("Facebook");
                }
            }
        };
        // If the access token is available already assign it.
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
                if (Profile.getCurrentProfile()!=null){
                    Toast.makeText(LoginActivity.this, Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    @Override
    protected void onStop() {
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v) {
        if (v == mFb) {
            loginButton.performClick();
        }/*else if (v == mLogout){
            loginButton.performClick();
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        }*/
    }
}
