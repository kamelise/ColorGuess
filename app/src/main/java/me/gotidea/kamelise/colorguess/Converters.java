package me.gotidea.kamelise.colorguess;

import android.arch.persistence.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kamelise on 3/24/18.
 */

public class Converters {
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static DateFormat df = new SimpleDateFormat(TIME_STAMP_FORMAT);

    @TypeConverter
    public static Long fromTimestamp(String value) {
        if (value != null) {
            try {
                return df.parse(value).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToTimestamp(Long value) {

        return value == null ? null : df.format(new Date(value));
    }

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