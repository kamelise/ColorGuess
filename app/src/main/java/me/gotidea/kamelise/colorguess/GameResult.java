package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by kamelise on 3/23/18.
 */

@Entity(tableName = "game_results", indices = {@Index(value = "date", unique = true)})
public class GameResult {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private Date date;
    private boolean won;

    @ColumnInfo(name = "time_played")
    private long timePlayed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(long time) {
        this.timePlayed = time;
    }
}
