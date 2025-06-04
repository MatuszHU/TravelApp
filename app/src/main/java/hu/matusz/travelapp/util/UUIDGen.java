package hu.matusz.travelapp.util;

import java.util.UUID;
/**
 * Litteraly just to quickly grab a <b style="color: aqua; ">UUID</b> for all the different IDs I have implemented. <br> Using UUIDv4
 * @author Matusz
 * @version v1
 */
public class UUIDGen {
    public UUIDGen(){}
    private static String test = null;
    public String getUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     * <b style="color: red; font-size: 40px">For testing only. <br> DO NOT FORGET TO REMOVE</b>
     * @author Matusz
     *
     */
    public void setGlobalUUID(){
        test = getUUID();
    }
}
