package me.gotidea.kamelise.colorguess.dal;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import me.gotidea.kamelise.colorguess.db.GameResult;

/**
 * Created by kamelise on 3/23/18.
 */

@Dao
public interface GameResultDao {
    @Insert
    long insertGameRes(GameResult gameResult);

    @Query("SELECT * FROM game_results")
    GameResult[] getGameResults();

    @Query("SELECT COUNT(*) FROM game_results")
    int getGamesTotalCount();

    @Query("SELECT COUNT(*) FROM game_results WHERE won = 0")
    int getGamesLostCount();

    String maxConsecutiveWins =
            "select max(wins_in_a_row) from (\n" +
                    "select count(*)  as wins_in_a_row\n" +
                    "from (\n" +
                    "\tselect id-1 as group_id\n" +
                    "\tfrom game_results \n" +
                    "\twhere id = 1) as tmp\n" +
                    "join game_results res1\n" +
                    "on res1.id > tmp.group_id and res1.id < \n" +
                    "\t(select id\n" +
                    "\tfrom game_results\n" +
                    "\twhere won = 0 and id > tmp.group_id \n" +
                    "\tlimit 1)\n" +
                    "union\n" +
                    "select count(*) \n" +
                    "from \n" +
                    "\t(select id as group_id\n" +
                    "\tfrom game_results \n" +
                    "\twhere won = 0) as tmp\n" +
                    "join game_results res1\n" +
                    "on res1.id > tmp.group_id and res1.id < \n" +
                    "\t(select id \n" +
                    "\tfrom game_results \n" +
                    "\twhere won = 0 and id> tmp.group_id \n" +
                    "\tlimit 1) group by tmp.group_id\n" +
                    "union\n" +
                    "select count(*) \n" +
                    "from (\n" +
                    "\tselect id as group_id\n" +
                    "\tfrom game_results \n" +
                    "\twhere won = 0\n" +
                    "\torder by group_id desc\n" +
                    "\tlimit 0, 1) as tmp\n" +
                    "join game_results res1\n" +
                    "on res1.id > tmp.group_id\n" +
                    "group by group_id)";

    @Query(maxConsecutiveWins)
    int getMaxConsequentWins();

    String currentConsecutiveWins =
            "select count(*) \n" +
                    "from (\n" +
                    "\tselect won, max(id) as id_limit\n" +
                    "\tfrom game_results\n" +
                    "\tgroup by won \n" +
                    "\torder by id_limit desc) as tmp\n" +
                    "join game_results\n" +
                    "on tmp.id_limit < game_results.id and game_results.won = 1";

    @Query(currentConsecutiveWins)
    int getCurrentConsequentWins();

    String bestTimes = "select time_played from game_results where won = 1 order by time_played asc limit 0, 3";
    @Query(bestTimes)
    List<Long> getBestTimes();

    @Delete
    void delete(GameResult res);
}
