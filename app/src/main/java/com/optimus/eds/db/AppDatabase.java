package com.optimus.eds.db;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.optimus.eds.db.converters.AssetConverter;
import com.optimus.eds.db.converters.DecimalConverter;
import com.optimus.eds.db.converters.MerchandiseItemConverter;
import com.optimus.eds.db.converters.ProductConverter;
import com.optimus.eds.db.dao.CustomerDao;
import com.optimus.eds.db.dao.MerchandiseDao;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.OrderStatusDao;
import com.optimus.eds.db.dao.PriceConditionEntitiesDao;
import com.optimus.eds.db.dao.PricingDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.CustomerInput;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionAvailed;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionClassPriceAccessSequences;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing.PriceScaleBasis;
import com.optimus.eds.db.entities.pricing.PricingGroups;
import com.optimus.eds.db.entities.pricing.PricingLevels;
import com.optimus.eds.ui.order.pricing.ProductQuantity;


@Database(entities = {Route.class, Outlet.class, Merchandise.class, Asset.class,ProductGroup.class,
        Product.class, Package.class, Order.class, OrderStatus.class,
        OrderDetail.class, CartonPriceBreakDown.class,
        UnitPriceBreakDown.class, CustomerInput.class, PriceAccessSequence.class,
        PriceBundle.class, PriceCondition.class, PriceConditionAvailed.class,
        PriceConditionClass.class, PriceConditionClassPriceAccessSequences.class,
        PriceConditionDetail.class, PriceConditionEntities.class,
        PriceConditionScale.class, PriceConditionType.class, PriceScaleBasis.class,
        PricingLevels.class, ProductQuantity.class
}, version =3, exportSchema = false)
@TypeConverters({MerchandiseItemConverter.class, AssetConverter.class, ProductConverter.class})

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract RouteDao routeDao();
    public abstract ProductsDao productsDao();
    public abstract OrderDao orderDao();
    public abstract OrderStatusDao orderStatusDao();
    public abstract MerchandiseDao merchandiseDao();
    public abstract PricingDao pricingDao();
    public abstract PriceConditionEntitiesDao priceConditionEntitiesDao();
    public abstract CustomerDao customerDao();
    public static synchronized AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "eds")
                    .fallbackToDestructiveMigration()
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
