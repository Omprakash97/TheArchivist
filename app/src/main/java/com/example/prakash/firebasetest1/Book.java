package com.example.prakash.firebasetest1;

public class Book {
private String aname;
private String bname;
private String des;
private String edision;
private String bid;
private String isFav;
    public Book() {

    }

    public String getIsFav() {
        return isFav;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getEdision() {
        return edision;
    }

    public void setEdision(String edision) {
        this.edision = edision;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

}
