package com.boozefy.android.helper;

/**
 * Created by Mauricio Giordano on 1/17/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.boozefy.android.model.Beverage;
import com.boozefy.android.model.Order;
import com.boozefy.android.model.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by mauricio on 3/14/15.
 */
public class ModelHelper extends OrmLiteSqliteOpenHelper
{
    public static final int DATABASE_VERSION = 3;

    private Dao<User, Integer> userDao = null;
    private Dao<Beverage, Integer> beverageDao = null;
    private Dao<Order, Integer> orderDao = null;

    public static ConnectionSource cs;

    public static ModelHelper getModelHelper(Context context) {
        return OpenHelperManager.getHelper(context, ModelHelper.class);
    }

    public ModelHelper(Context context) {
        super(context, "booze.db", null, DATABASE_VERSION);
    }

    public void clearDatabase() {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "WARN");

        try {
            ConnectionSource cs = getConnectionSource();
            TableUtils.dropTable(cs, User.class, true);
            TableUtils.dropTable(cs, Beverage.class, true);
            TableUtils.dropTable(cs, Order.class, true);

            TableUtils.createTable(cs, User.class);
            TableUtils.createTable(cs, Beverage.class);
            TableUtils.createTable(cs, Order.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource cs) {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "WARN");

        try {
            TableUtils.createTable(cs, User.class);
            TableUtils.createTable(cs, Beverage.class);
            TableUtils.createTable(cs, Order.class);
            ModelHelper.cs = cs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource cs, int oldVersion,
                          int newVersion) {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "WARN");

        try {
            TableUtils.dropTable(cs, User.class, true);
            TableUtils.dropTable(cs, Beverage.class, true);
            TableUtils.dropTable(cs, Order.class, true);

            onCreate(database, cs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<User, Integer> getUserDao() {
        if (userDao == null) {
            try {
                userDao = getDao(User.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userDao;
    }

    public Dao<Beverage, Integer> getBeverageDao() {
        if (beverageDao == null) {
            try {
                beverageDao = getDao(Beverage.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return beverageDao;
    }

    public Dao<Order, Integer> getOrderDao() {
        if (orderDao == null) {
            try {
                orderDao = getDao(Order.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return orderDao;
    }

}
