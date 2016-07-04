package com.boozefy.android.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Mauricio Giordano on 1/21/16.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Booze, 2016 - All rights reserved.
 */
public class GeocoderHelper {
    private static Service service = null;

    public interface Service {
        @GET("json")
        Call<JsonElement> parse(@Query("address") String address);
    }

    public static Service getService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://maps.googleapis.com/maps/api/geocode/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(Service.class);
        }

        return service;
    }

    public interface OnLocationGathered {
        void onGathered(Location location);
    }

    public static void gatherFromAddress(String address, final OnLocationGathered onLocationGathered) {

        Call<JsonElement> parsed = GeocoderHelper.getService().parse(address);
        parsed.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Response<JsonElement> response) {
                if (response.body() == null) {
                    if (onLocationGathered != null) {
                        onLocationGathered.onGathered(null);
                    }

                    return;
                }

                Location location = new Location();

                try {
                    JsonObject json = response.body().getAsJsonObject();
                    JsonObject result = json.getAsJsonArray("results").get(0).getAsJsonObject();
                    JsonArray address = result.getAsJsonArray("address_components");

                    for (int i = 0; i < address.size(); i++) {
                        JsonObject obj = address.get(i).getAsJsonObject();
                        JsonArray types = obj.getAsJsonArray("types");

                        for (int j = 0; j < types.size(); j++) {
                            String type = types.get(j).getAsString();

                            if (type.equals("street_number")) {
                                location.number = obj.get("long_name").getAsString();
                                break;
                            } else if (type.equals("route")) {
                                location.street = obj.get("long_name").getAsString();
                                break;
                            } else if (type.equals("postal_code")) {
                                location.zipCode = obj.get("long_name").getAsString();
                                break;
                            }
                        }
                    }

                    JsonObject geometry = result.getAsJsonObject("geometry");
                    JsonObject loc = geometry.getAsJsonObject("location");

                    location.latitude = loc.get("lat").getAsDouble();
                    location.longitude = loc.get("lng").getAsDouble();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (onLocationGathered != null) {
                    onLocationGathered.onGathered(location);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (onLocationGathered != null) {
                    onLocationGathered.onGathered(null);
                }
            }
        });

    }

    public static class Location {
        public String street = null;
        public String number = null;
        public String zipCode = null;
        public String description = null;
        public double latitude;
        public double longitude;

        public static Location fromJson(JsonElement jsonElement) {
            Location location = new Location();
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            location.street = jsonObject.get("street").getAsString();
            location.number = jsonObject.get("number").getAsString();
            location.zipCode = jsonObject.get("zipCode").getAsString();
            location.latitude = jsonObject.get("latitude").getAsDouble();
            location.longitude = jsonObject.get("longitude").getAsDouble();
            location.description = jsonObject.get("description").getAsString();

            return location;
        }

        public String toString() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("street", street);
            jsonObject.addProperty("number", number);
            jsonObject.addProperty("zipCode", zipCode);
            jsonObject.addProperty("latitude", latitude);
            jsonObject.addProperty("longitude", longitude);
            jsonObject.addProperty("description", description);

            return jsonObject.toString();
        }

        public String addressLine() {
            return street + ", " + number + " - La Paz, Bolivia - " + zipCode;
        }
    }
}
