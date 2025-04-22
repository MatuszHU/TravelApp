package hu.matusz.travelapp.databaseUtil.dataClasses;

public class GeoLocation {
    private int coordX;
    private int coordY;
    private String POI_name;

    private long geoId;

    public GeoLocation(int coordX, int coordY, String POI_name, long geoId) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.POI_name = POI_name;
        this.geoId = geoId;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public String getPOI_name() {
        return POI_name;
    }

    public long getGeoId() {
        return geoId;
    }
}
