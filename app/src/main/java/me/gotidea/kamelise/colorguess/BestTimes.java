package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by kamelise on 3/23/18.
 */

@Entity
class BestTimes {
    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    @PrimaryKey
    private int place;
}
