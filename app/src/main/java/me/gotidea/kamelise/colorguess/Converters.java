package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by kamelise on 3/24/18.
 */

public class Converters {
    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }
}