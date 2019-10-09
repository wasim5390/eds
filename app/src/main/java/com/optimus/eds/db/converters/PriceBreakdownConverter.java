package com.optimus.eds.db.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.Outlet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PriceBreakdownConverter {

    @TypeConverter
    public static List<CartonPriceBreakDown> fromString(String value) {
        if (value == null) {
            return (null);
        }
        Type listType = new TypeToken<ArrayList<CartonPriceBreakDown>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<CartonPriceBreakDown> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
