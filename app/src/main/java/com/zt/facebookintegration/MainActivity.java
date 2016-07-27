package com.zt.facebookintegration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    TextView textView;
    CallbackManager callbackManager;

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        textView = (TextView) findViewById(R.id.textView);

        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        loginButton.setReadPermissions(permissionNeeds);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");
                GraphRequest request = GraphRequest.newMeRequest
                        (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                System.out.println("Check: " + response.toString());
                                try {
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    String gender = object.getString("gender");
                                    String birthday = object.getString("birthday");
                                    System.out.println(id + ", " + name + ", " + email + ", " + gender + ", " + birthday);
                                    Toast.makeText(getApplicationContext(), "Login Successfully " + email, Toast.LENGTH_SHORT).show();
                                    textView.setText("" + "ID : " + id + "\n\n" + "Name : " + name + "\n\n" + "Email : " + email + "\n\n" + "Gender : " + gender + "\n\n" + "Birthday : " + birthday);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
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
    }
}