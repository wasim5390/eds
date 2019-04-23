package com.optimus.eds.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.optimus.eds.db.converters.AssetConverter;
import com.optimus.eds.db.converters.OutletConverter;
import com.optimus.eds.db.dao.MerchandiseDao;
import com.optimus.eds.db.dao.OutletDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;


@Database(entities = {Route.class, Outlet.class, Merchandise.class}, version = 1, exportSchema = false)
@TypeConverters({OutletConverter.class, AssetConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract RouteDao routeDao();
    public abstract OutletDao outletDao();
    public abstract MerchandiseDao merchandiseDao();

    public static synchronized AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "eds")
                    .build();
        }
        return INSTANCE;
    }

    public static AppDatabase getMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
