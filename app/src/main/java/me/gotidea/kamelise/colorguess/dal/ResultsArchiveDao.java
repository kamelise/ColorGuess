package me.gotidea.kamelise.colorguess.dal;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import me.gotidea.kamelise.colorguess.db.ResultsArchive;

@Dao
public interface ResultsArchiveDao {

    @Insert
    void insertResultsArchiveRow(ResultsArchive row);
}
