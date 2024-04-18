package com.controladad.boutia_pms.network.network_check;

public class NoNetworkException extends RuntimeException{

    public NoNetworkException() {
        //TODO put the String in resources
        super();
        throw new RuntimeException("No Internet Available");
    }
}
