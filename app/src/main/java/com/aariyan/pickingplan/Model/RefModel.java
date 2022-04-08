package com.aariyan.pickingplan.Model;

public class RefModel {

    private int intAutoPickingHeader;
    private String strUnickReference,strPickingNickname;

    public RefModel() {}

    public RefModel(int intAutoPickingHeader, String strUnickReference, String strPickingNickname) {
        this.intAutoPickingHeader = intAutoPickingHeader;
        this.strUnickReference = strUnickReference;
        this.strPickingNickname = strPickingNickname;
    }

    public int getIntAutoPickingHeader() {
        return intAutoPickingHeader;
    }

    public void setIntAutoPickingHeader(int intAutoPickingHeader) {
        this.intAutoPickingHeader = intAutoPickingHeader;
    }

    public String getStrUnickReference() {
        return strUnickReference;
    }

    public void setStrUnickReference(String strUnickReference) {
        this.strUnickReference = strUnickReference;
    }

    public String getStrPickingNickname() {
        return strPickingNickname;
    }

    public void setStrPickingNickname(String strPickingNickname) {
        this.strPickingNickname = strPickingNickname;
    }
}
