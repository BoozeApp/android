package com.boozefy.android.helper;

import android.content.Context;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by Mauricio Giordano on 1/13/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class RealmHelper {

    private static Realm realm = null;
    private static final RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            RealmSchema schema = realm.getSchema();

            if (newVersion == 0) {
                schema.create("User")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class)
                    .addField("email", String.class)
                    .addField("facebookId", String.class)
                    .addField("picture", String.class)
                    .addField("level", String.class)
                    .addField("accessToken", String.class)
                    .addField("latitude", double.class)
                    .addField("longitude", double.class);

                schema.create("Beverage")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class)
                    .addField("picture", String.class)
                    .addField("price", double.class)
                    .addField("max", int.class);
                newVersion++;
            }

            if (newVersion == 1) {
                schema.create("Order")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("client", schema.get("User"))
                    .addRealmObjectField("staff", schema.get("User"))
                    .addField("amount", double.class)
                    .addField("change", double.class)
                    .addField("status", String.class)
                    .addField("statusReason", String.class)
                    .addField("address", String.class)
                    .addField("latitude", double.class)
                    .addField("longitude", double.class)
                    .addRealmListField("beverages", schema.get("Beverage"));
            }

        }
    };

    public static Realm getInstance(Context context) {
        if (realm != null) return realm;

        RealmConfiguration config = new RealmConfiguration.Builder(context)
            .schemaVersion(1)
            .migration(migration)
            .build();

        realm = Realm.getInstance(config);

        return realm;
    }

}
