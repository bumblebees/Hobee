package bumblebees.hobee.objects;

/**
 * Deals represent the items received from the GoGoDeals service.
 * The class contains the fields as presented in "RFC 16 - Get Deals" in the "data" field.
 */

import java.util.Date;


public class Deal {
    private String client_id;
    private String client_name;
    private int count;
    private String description;
    private Date duration;
    private String filters;
    private String id;
    private double latitude;
    private double longitude;
    private String name;
    private String picture;
    private int price;
    private String status;


    public Deal(String client_id, String client_name, int count, String description, Date duration, String filters, String id, double latitude, double longitude,
                String name, String picture, int price, String status) {
        this.client_id = client_id;
        this.client_name = client_name;
        this.count = count;
        this.description = description;
        this.duration = duration;
        this.filters = filters;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.picture = picture;
        this.price = price;
        this.status = status;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public Date getDuration() {
        return duration;
    }

    public String getFilters() {
        return filters;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Deal{" +
                "client_id='" + client_id + '\'' +
                ", client_name='" + client_name + '\'' +
                ", count=" + count +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", filters='" + filters + '\'' +
                ", id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }



}
