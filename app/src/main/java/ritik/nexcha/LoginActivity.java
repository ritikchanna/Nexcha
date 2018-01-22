package ritik.nexcha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends Activity {

    CallbackManager callbackManager;
    LoginButton loginButton;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ritik", "onCreate: LoginActivitu");
        //remove everything from window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set the layout
        setContentView(R.layout.activity_login);


        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Ritik", "onSuccess: ");

                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.d("Ritik", "onCurrentProfileChanged: ");

                            if (currentProfile != null)
                                finishWithResult(true, 1);
                            else
                                finishWithResult(false, 1);
                        }
                    };

                }


            }

            @Override
            public void onCancel() {
                Log.d("Ritik", "onCancel: ");
                finishWithResult(false, 1);
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Ritik", "onError: ");
                Toast.makeText(LoginActivity.this, getText(R.string.login_fail), Toast.LENGTH_SHORT);
                finishWithResult(false, 1);
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 369) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        Log.d("Ritik", "onStart: LoginActivity");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        TextView text = (TextView) (signInButton.getChildAt(0));
        if (account != null) {
            text.setText("Log out");
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finishWithResult(false, 2);
                                }
                            });

                }
            });
        } else {

            text.setText("Sign in with Google");
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 369);

                }
            });
        }
        super.onStart();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("Ritik", "GoogleSignInResult: " + account.getDisplayName());
            finishWithResult(true, 2);

        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, getText(R.string.login_fail), Toast.LENGTH_SHORT);
            finishWithResult(false, 2);

        }
    }

    private void finishWithResult(Boolean success, int account_type) {
        Bundle conData = new Bundle();
        conData.putInt("account_type", account_type);
        Intent intent = new Intent();
        intent.putExtras(conData);
        if (success)
            setResult(RESULT_OK, intent);
        else
            setResult(RESULT_CANCELED, intent);
        finish();
    }
}
