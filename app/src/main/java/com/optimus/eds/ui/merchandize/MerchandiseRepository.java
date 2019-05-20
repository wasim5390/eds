package com.optimus.eds.ui.merchandize;

import android.app.Application;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.MerchandiseDao;
import com.optimus.eds.db.entities.Merchandise;

/**
 * Created By apple on 4/23/19
 */
public class MerchandiseRepository {

    private MerchandiseDao merchandiseDao;


    public MerchandiseRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        merchandiseDao = appDatabase.merchandiseDao();

    }

    public void insertIntoDb(Merchandise merchandise) {
            merchandiseDao.insertMerchandise(merchandise);
    }


}
