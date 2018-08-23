package vn.com.tdns.qlsc.entities;

public class DAddress {
    private double longtitude;
    private double latitude;
    private String subAdminArea;
    private String location;

    public DAddress(double longtitude, double latitude, String subAdminArea, String location) {
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.subAdminArea = subAdminArea;
        this.location = location;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public String getLocation() {
        return location;
    }

}
