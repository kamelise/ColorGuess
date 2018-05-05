package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface ResultsArchiveDao {

    @Insert
    void insertResultsArchiveRow(ResultsArchive row);
}
