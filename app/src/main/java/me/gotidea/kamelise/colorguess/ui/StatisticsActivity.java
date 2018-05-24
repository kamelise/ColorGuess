package me.gotidea.kamelise.colorguess.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.gotidea.kamelise.colorguess.AppExecutors;
import me.gotidea.kamelise.colorguess.R;
import me.gotidea.kamelise.colorguess.db.GameResult;
import me.gotidea.kamelise.colorguess.db.LocalDatabase;
import me.gotidea.kamelise.colorguess.db.ResultsArchive;

public class StatisticsActivity extends AppCompatActivity {

    private AppExecutors appExecutors;
    private LocalDatabase db;

    LinearLayout parentLL;
    LinearLayout iconsLL;
    TextView gamesPlayedTV;
    TextView winRatioTV;
    TextView winsRowTV;
    TextView winsRowMaxTV;
    TextView bestTimeTV;
    TextView bestTime2TV;
    TextView bestTime3TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        gamesPlayedTV = (TextView) findViewById(R.id.games_played);
        winRatioTV = (TextView) findViewById(R.id.win_ratio);
        winsRowTV = (TextView) findViewById(R.id.wins_row);
        winsRowMaxTV = (TextView) findViewById(R.id.wins_row_max);
        bestTimeTV = (TextView) findViewById(R.id.best_time);
        bestTime2TV = (TextView) findViewById(R.id.best_time_2);
        bestTime3TV = (TextView) findViewById(R.id.best_time_3);

        parentLL = (LinearLayout) findViewById(R.id.parentLL);
        iconsLL = (LinearLayout) findViewById(R.id.stars);

        db = LocalDatabase.getInstance(this);
        appExecutors = new AppExecutors();

        initData();
    }

    private void initData() {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final int gamesPlayedCount = db.gameResultDao().getGamesTotalCount();
                final int gamesLostCount = db.gameResultDao().getGamesLostCount();
                int winRatio = 0;
                if (gamesPlayedCount > 0)
                    winRatio = Math.round(100 * (1 - (float) gamesLostCount / gamesPlayedCount));

                int starsNum = (winRatio - 40) / 10;
                if (starsNum < 1) starsNum = 1;
                if (gamesPlayedCount == 0)
                    starsNum = 0;
                final int finalStarsNum = starsNum;

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < iconsLL.getChildCount(); i++) {
                            ImageView star = (ImageView) iconsLL.getChildAt(i);
                            if (finalStarsNum >= i + 1)
                                star.setColorFilter(getResources().getColor(R.color.yellow));
                            else
                                star.setColorFilter(getResources().getColor(R.color.light_grey));
                        }
                    }
                });


                int winsRowCurrent;
                int winsRowMax;
                if (gamesLostCount != 0) {
                    winsRowCurrent = db.gameResultDao().getCurrentConsequentWins();
                    winsRowMax = db.gameResultDao().getMaxConsequentWins();
                } else {
                    winsRowCurrent = gamesPlayedCount;
                    winsRowMax = gamesPlayedCount;
                }
                final int winsRowCurr = winsRowCurrent;
                final int winsRowMax_ = winsRowMax;

                final List<Long> bestTimes = db.gameResultDao().getBestTimes();

                long min, sec;
                String[] bestTime = new String[3];
                for (int i = 0; i < 3; i++) {
                    bestTime[i] = "--:--";
                }
                if (bestTimes.size() > 0) {
                    min = TimeUnit.MILLISECONDS.toMinutes(bestTimes.get(0));
                    sec = TimeUnit.MILLISECONDS.toSeconds(bestTimes.get(0)) % 60;
                    bestTime[0] = String.format(GameActivity.TIME_FORMAT, min, sec);
                    if (bestTimes.size() > 1) {
                        min = TimeUnit.MILLISECONDS.toMinutes(bestTimes.get(1));
                        sec = TimeUnit.MILLISECONDS.toSeconds(bestTimes.get(1)) % 60;
                        bestTime[1] = String.format(GameActivity.TIME_FORMAT, min, sec);
                        if (bestTimes.size() > 2) {
                            min = TimeUnit.MILLISECONDS.toMinutes(bestTimes.get(2));
                            sec = TimeUnit.MILLISECONDS.toSeconds(bestTimes.get(2)) % 60;
                            bestTime[2] = String.format(GameActivity.TIME_FORMAT, min, sec);
                        }
                    }
                }
                final String bestTime1 = bestTime[0];
                final String bestTime2 = bestTime[1];
                final String bestTime3 = bestTime[2];
                final String winRatioStr = winRatio + "%";

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        gamesPlayedTV.setText(String.valueOf(gamesPlayedCount));
                        winRatioTV.setText(winRatioStr);
                        winsRowTV.setText(String.valueOf(winsRowCurr));
                        winsRowMaxTV.setText(String.valueOf(winsRowMax_));
                        bestTimeTV.setText(bestTime1);
                        bestTime2TV.setText(bestTime2);
                        bestTime3TV.setText(bestTime3);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        /* Hide both the navigation bar and the status bar.
         * SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
         * a general rule, you should design your app to hide the status bar whenever you
         * hide the navigation bar.
         */
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void onPlayClick(View view) {
        super.finish();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void onResetClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Reset Statistics");
        builder.setMessage("All the history data of games played will be deleted. Do you want to proceed?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                appExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        GameResult[] results = db.gameResultDao().getGameResults();
                        Date dateArchived = Calendar.getInstance().getTime();
                        for (GameResult res: results) {
                            ResultsArchive archiveRow = new ResultsArchive();
                            archiveRow.convertGameResult(res, dateArchived);
                            db.resultsArchiveDao().insertResultsArchiveRow(archiveRow);
                            db.gameResultDao().delete(res);
                        }
                    }
                });

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.consequent_wins_key), 0);
                editor.putLong(getString(R.string.best_time_key), 0L);
                editor.apply();

                initData();
                parentLL.invalidate();

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
