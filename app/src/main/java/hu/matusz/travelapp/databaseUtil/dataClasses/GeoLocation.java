package hu.matusz.travelapp.databaseUtil.dataClasses;

public class GeoLocation {
    private double coordX;
    private double coordY;
    private String POI_name;
    private String geoId;  // String típusúra változtattuk

    // Üres konstruktor Firebase-hez
    public GeoLocation() {
        // Szükséges a Firebase-hez
    }

    public GeoLocation(double coordX, double coordY, String POI_name, String geoId) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.POI_name = POI_name;
        this.geoId = geoId;
    }

    public double getCoordX() {
        return coordX;
    }

    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    public String getPOI_name() {
        return POI_name;
    }

    public void setPOI_name(String POI_name) {
        this.POI_name = POI_name;
    }

    public String getGeoId() {
        return geoId;
    }

    public void setGeoId(String geoId) {
        this.geoId = geoId;
    }
}