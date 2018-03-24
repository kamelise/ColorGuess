package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

/**
 * Created by kamelise on 3/23/18.
 */

@Dao
interface GameResultDao {
    @Insert
    public long insertGameRes(GameResult gameResult);
}
