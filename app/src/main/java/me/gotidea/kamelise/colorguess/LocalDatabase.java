package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * Created by kamelise on 3/23/18.
 */

@Database(entities = {GameResult.class, BestTimes.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LocalDatabase extends RoomDatabase {
    public abstract GameResultDao gameResultDao();
    public abstract BestTimesDao bestTimestDao();
}
