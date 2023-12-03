package com.jihyun.mobilesoftwareproject;

public class Menudata {
    String time;
    String mn;
    String date;
    int kcal;
    int id;
    String price;

    public Menudata(String time, String mn, String date, int kcal, int id) {
        this.time = time;
        this.mn= mn;
        this.date = date;
        this.kcal = kcal;
        this.id = id;
    }

    public String gettime(){
        return time;
    }

    public String getmn(){
        return mn;
    }

    public String getdate(){
        return date;
    }

    public int getkcal(){
        return kcal;
    }

    public int getid(){
        return id;
    }
    public String getPrice() {return price;}

    public void settime(String time){
        this.time = time;
    }

    public void setmn(String mnn){
        this.mn = mn;
    }

    public void setdate(String date){
        this.date = date;
    }

    public void setkcal(int kcal){
        this.kcal = kcal;
    }

    public void setid(int id){
        this.id = id;
    }
    public void setprice(String price){
        this.price = price;
    }
}
