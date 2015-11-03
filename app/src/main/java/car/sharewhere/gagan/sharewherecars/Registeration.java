package car.sharewhere.gagan.sharewherecars;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;


public class Registeration extends FragmentActivity
{


    LoginButton btnLogin;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            FacebookSdk.sdkInitialize(Registeration.this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_registeration);



        isAlreadyLogin(isLoggedIn());


        btnLogin = (LoginButton) findViewById(R.id.loginbutton);
        FacebookSdk.setApplicationId("1049198945111554");

        callbackManager = CallbackManager.Factory.create();
        btnLogin.setReadPermissions(Arrays.asList("email"));


        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Toast.makeText(Registeration.this, "success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Registeration.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancel()
            {
                Toast.makeText(Registeration.this, "fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception)
            {
                Toast.makeText(Registeration.this, exception.toString(), Toast.LENGTH_SHORT).show(); startActivity(new Intent(Registeration.this, MainActivity.class));
            }
        });


    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void isAlreadyLogin(boolean currentAccessToken)
    {

        if (currentAccessToken )
        {
            Intent i = new Intent(Registeration.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
