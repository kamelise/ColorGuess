package me.gotidea.kamelise.colorguess.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by kamelise on 3/23/18.
 */

@Database(entities = {GameResult.class, ResultsArchive.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LocalDatabase extends RoomDatabase {

    private static LocalDatabase instance;

    public static synchronized LocalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LocalDatabase.class, "color-guess").fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract GameResultDao gameResultDao();

    public abstract ResultsArchiveDao resultsArchiveDao();
}