package com.davstele.models;

/**
 * Created by dx on 5/12/16.
 */
public class Knowledge {

    private String className;
    private int[] bits;
    private String filename;

    public Knowledge(String className, int[] bits, String filename) {
        this.className = className;
        this.bits = bits;
        this.filename = filename;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int[] getBits() {
        return bits;
    }

    public void setBits(int[] bits) {
        this.bits = bits;
    }


    public String getFilename() {
        return filename;
    }
}
