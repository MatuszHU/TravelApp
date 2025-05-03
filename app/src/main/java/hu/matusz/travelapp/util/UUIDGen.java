package hu.matusz.travelapp.util;

import java.util.UUID;
/**
 * Litteraly just to quickly grab a <b style="color: aqua; ">UUID</b> for all the differents IDs I have implemented.
 * @author Matusz
 * @version v1
 */
public class UUIDGen {
    public UUIDGen(){}

    public String getUUID(){
        return UUID.randomUUID().toString();
    }
}
