package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{

    ImageView tempBtn, homeBtn, newsBtn, eventsBtn, publishBtn, aboutBtn;
    Button LoginPopUpBtn, LanguageBtn;
    int container, inactiveColor, activeColor;
    FrameLayout containerLayout;
    ArrayList<UserH> userHS;

    FirebaseUser fbUser;
    FirebaseAuth mAuth;
    public static UserH currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize Firebase Authentication variable
        mAuth = FirebaseAuth.getInstance();

        //initialize users array
        userHS = new ArrayList<UserH>();

        //set the custom action bar layout as the action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View actionBarView = getSupportActionBar().getCustomView();

        //set button variables to their corresponding button views
        homeBtn = findViewById(R.id.home_btn);
        newsBtn = findViewById(R.id.news_btn);
        eventsBtn = findViewById(R.id.events_btn);
        publishBtn = findViewById(R.id.publish_btn);
        aboutBtn = findViewById(R.id.about_btn);
        LoginPopUpBtn = actionBarView.findViewById(R.id.login_popup_btn);
        LanguageBtn = actionBarView.findViewById(R.id.language_switch);


        //set the inactive and active color variables
        inactiveColor = getResources().getColor((R.color.primary_light));
        activeColor = getResources().getColor((R.color.primary_grey));

        //set a different color to the currently open tab button
        tempBtn = homeBtn;
        tempBtn.getBackground().setTint(activeColor);

        //set the container variable to its corresponding container view id
        container = R.id.fragment_container;
        containerLayout = findViewById(container);

        //display the home tab by default when the app starts
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, new Home());
        ft.commit();

        homeBtn.setOnClickListener(view ->
        {
            //switch to the home tab when the home button is pressed
            FragmentTransaction homeFT = getSupportFragmentManager().beginTransaction();
            homeFT.replace(container, new Home());
            homeFT.commit();

            //change the shaded tab button
            tempBtn.getBackground().setTint(inactiveColor);
            homeBtn.getBackground().setTint(activeColor);
            tempBtn = homeBtn;
        });

        newsBtn.setOnClickListener(view ->
        {
                //switch to the news tab when the news button is pressed
                FragmentTransaction newsFT = getSupportFragmentManager().beginTransaction();
                newsFT.replace(container, new News());
                newsFT.commit();

                //change the shaded tab button
                tempBtn.getBackground().setTint(inactiveColor);
                newsBtn.getBackground().setTint(activeColor);
                tempBtn = newsBtn;
        });

        eventsBtn.setOnClickListener(view ->
        {
                //switch to the news tab when the news button is pressed
                FragmentTransaction eventsFT = getSupportFragmentManager().beginTransaction();
                eventsFT.replace(container, new Events());
                eventsFT.commit();

                //change the shaded tab button
                tempBtn.getBackground().setTint(inactiveColor);
                eventsBtn.getBackground().setTint(activeColor);
                tempBtn = eventsBtn;
        });

        publishBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //switch to the publish tab when the publish button is pressed
                FragmentTransaction publishFT = getSupportFragmentManager().beginTransaction();
                publishFT.replace(container, new Publish());
                publishFT.commit();

                //change the shaded tab button
                tempBtn.getBackground().setTint(inactiveColor);
                publishBtn.getBackground().setTint(activeColor);
                tempBtn = publishBtn;
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //switch to the about us tab when the about us button is pressed
                FragmentTransaction aboutFT = getSupportFragmentManager().beginTransaction();
                aboutFT.replace(container, new AboutUs());
                aboutFT.commit();

                //change the shaded tab button
                tempBtn.getBackground().setTint(inactiveColor);
                aboutBtn.getBackground().setTint(activeColor);
                tempBtn = aboutBtn;
            }
        });

        LoginPopUpBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //show the login pop up
                callLoginDialog();
            }
        });

        LanguageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(LanguageBtn.getText() == getText(R.string.ar))
                {
                    setLocal(MainActivity.this,"ar");
                    finish();
                    startActivity(getIntent());
                }
                else if (LanguageBtn.getText() == getText(R.string.en))
                {
                    setLocal(MainActivity.this,"");
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Log.d("not signed in", "not signed in");
        }
        //else changeLoginBtn();
    }

    //login pop up method
    private void callLoginDialog()
    {
        Dialog loginDialog;
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.login_popup);

        Button loginBtn = loginDialog.findViewById(R.id.login_btn);
        Button registerBtn = loginDialog.findViewById(R.id.register_btn);

        EditText email = loginDialog.findViewById(R.id.email_et);
        EditText password = loginDialog.findViewById(R.id.password_et);

        TextView errorTxt = loginDialog.findViewById(R.id.incorrect_message);

        loginDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loginDialog.show();

        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                errorTxt.setVisibility(View.INVISIBLE);
                String tempEmail = null;

                //Login Firebase user if the email and password text fields are not empty, and the password matches the email. Otherwise display error messages accordingly
                if(!email.getText().toString().matches("") && !password.getText().toString().matches(""))
                {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        //get an instance of the registered user
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        //store the information of the current user in a global variable
                                        currentUser = new UserH(email.getText().toString(), password.getText().toString(), user.getDisplayName(), R.drawable.auass);
                                        //check if the email belongs to an admin and set to admin if the condition is true
                                        if (currentUser.getEmail() == "hibaeinea@gmail.com" || currentUser.getEmail() == "madyan2435@gmail.com" || currentUser.getEmail() == "m.shwaiki@gmail.com")
                                            currentUser.setAdmin(true);

                                        loginDialog.dismiss();

                                        Toast.makeText(MainActivity.this, "welcome " + currentUser.getUserName() + "!", Toast.LENGTH_SHORT).show();

                                        changeLoginBtn();

                                        //put boolean and make it true for staying logged in the next time the application starts
                                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean("USER_LOGGED", true); // false on logout
                                        editor.commit();
                                    } else
                                    {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                                        errorTxt.setText(task.getException().getMessage());
                                        errorTxt.setVisibility(View.VISIBLE);

                                        //Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }
                                }
                            });
                }
                else if(!password.getText().toString().matches(""))
                {
                    errorTxt.setText("Please enter your email");
                    errorTxt.setVisibility(View.VISIBLE);
                }
                else if(!email.getText().toString().matches(""))
                {
                    errorTxt.setText("Please enter your password");
                    errorTxt.setVisibility(View.VISIBLE);
                }
                else
                {
                    errorTxt.setText("Please enter your email and password");
                    errorTxt.setVisibility(View.VISIBLE);
                }

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //close login pop up
                loginDialog.cancel();
                //open register pop up
                callRegisterDialog();
            }
        });
    }



    //register pop up method
    private void callRegisterDialog()
    {
        Dialog registerDialog;
        registerDialog = new Dialog(this);
        registerDialog.setContentView(R.layout.register_popup);

        Button rRegisterBtn = registerDialog.findViewById(R.id.r_register_btn);

        EditText rName = registerDialog.findViewById(R.id.r_name_et);
        EditText rEmail = registerDialog.findViewById(R.id.r_email_et);
        EditText rPassword = registerDialog.findViewById(R.id.r_password_et);

        TextView registerErrorTxt = registerDialog.findViewById(R.id.register_error_text);

        registerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        registerDialog.show();

        rRegisterBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerErrorTxt.setVisibility(View.INVISIBLE);
                String tempEmail = null;

                //Create Firebase user if the email and password text fields are not empty, otherwise display error messages accordingly
                if(!rEmail.getText().toString().matches("") && !rPassword.getText().toString().matches(""))
                {
                    mAuth.createUserWithEmailAndPassword(rEmail.getText().toString(), rPassword.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        // Get an instance of the registered user
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        if (user != null) {
                                            // Update the display name
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(rName.getText().toString())
                                                    .build();

                                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                // Display name updated successfully
                                                                registerDialog.dismiss();
                                                                Toast.makeText(MainActivity.this, "welcome " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                                                                changeLoginBtn();
                                                            }
                                                        }
                                                    });
                                        }

                                        // Store the information of the current user in a global variable
                                        currentUser = new UserH(rEmail.getText().toString(), rPassword.getText().toString(), rName.getText().toString(), R.drawable.auass);
                                        // Check if the email belongs to an admin and set to admin if the condition is true
                                        if (currentUser.getEmail() == "hibaeinea@gmail.com" || currentUser.getEmail() == "madyan2435@gmail.com" || currentUser.getEmail() == "m.shwaiki@gmail.com")
                                            currentUser.setAdmin(true);

                                        //put boolean and make it true for staying logged in the next time the application starts
                                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean("USER_LOGGED", true); // false on logout
                                        editor.commit();
                                    } else
                                    {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        registerErrorTxt.setText(task.getException().getMessage());
                                        registerErrorTxt.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
                else if(!rPassword.getText().toString().matches(""))
                {
                    registerErrorTxt.setText("Please enter your email");
                    registerErrorTxt.setVisibility(View.VISIBLE);
                }
                else if(!rEmail.getText().toString().matches(""))
                {
                    registerErrorTxt.setText("Please enter your password");
                    registerErrorTxt.setVisibility(View.VISIBLE);
                }
                else
                {
                    registerErrorTxt.setText("Please enter your email and password");
                    registerErrorTxt.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void getCurrentUser()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // Name, email address, and profile photo Url
            changeLoginBtn();
        }
    }
    public void changeLoginBtn()
    {
        LoginPopUpBtn.setBackgroundTintList(MainActivity.this.getResources().getColorStateList(R.color.primary_light));
        LoginPopUpBtn.setText(currentUser.getUserName());
        LoginPopUpBtn.setTextColor(getColor(R.color.dark));
        LoginPopUpBtn.setClickable(false);
    }

    public void setLocal(Activity activity, String langCode)
    {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}