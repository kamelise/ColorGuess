package me.gotidea.kamelise.colorguess.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.gotidea.kamelise.colorguess.BuildConfig;
import me.gotidea.kamelise.colorguess.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onRateClick(View view) {

    }
    public void onRemoveAdsClick(View view) {

    }
    public void onDonateClick(View view) {

    }
    public void onContactUsClick(View view) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {"colourguess@gmail.com" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ColorGuess Inquiry");
        String text = "\n\n\n\n";
        text += "SDK API version:" + Build.VERSION.SDK_INT;
        text += "\nBrand: " + Build.MANUFACTURER + " Model: " + Build.MODEL;
        text += "\nVersion name: " + BuildConfig.VERSION_NAME + " version code: "
                + BuildConfig.VERSION_CODE;
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(emailIntent, "Send email to developer"));
    }
}
