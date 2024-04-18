package com.controladad.boutia_pms.models;

public interface CallBackMethod<String> {

    void callBackIfSuccessFull(String callback, Boolean isMission);

    void callBackIfFailed(String callback, Boolean isMission);

}
