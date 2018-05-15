package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "results_archive")
public class ResultsArchive {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date_archived")
    private Date dateArchived;

    @NonNull
    @ColumnInfo(name = "game_id")
    private int gameId;

    @NonNull
    private Date date;

    private boolean won;

    @ColumnInfo(name = "time_played")
    private long timePlayed;

    @ColumnInfo(name = "moves_taken")
    private byte movesTaken;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(Date dateArchived) {
        this.dateArchived = dateArchived;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
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

    public byte getMovesTaken() {
        return movesTaken;
    }

    public void setMovesTaken(byte movesTaken) {
        this.movesTaken = movesTaken;
    }

    public void convertGameResult(GameResult res, Date dateArchived) {
        this.dateArchived = dateArchived;
        gameId = res.getId();
        date = res.getDate();
        won = res.isWon();
        timePlayed = res.getTimePlayed();
        movesTaken = res.getMovesTaken();
    }
}
