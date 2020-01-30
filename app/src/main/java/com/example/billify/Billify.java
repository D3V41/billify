package com.example.billify;

import android.app.Application;

import java.util.ArrayList;

public class Billify extends Application
{
    String fb_flag;
    MyAdapter adpt;
    ContactAdapter cadpt;
    ArrayList<Friend> selected;
    Friend you;

    public Friend getYou() {
        return you;
    }

    public void setYou(Friend you) {
        this.you = you;
    }

    public String getFb_flag() {
        return fb_flag;
    }

    public void setFb_flag(String fb_flag) {
        this.fb_flag = fb_flag;
    }

    public MyAdapter getAdpt() {
        return adpt;
    }

    public ArrayList<Friend> getSelected() {
        return selected;
    }

    public void setSelected(ArrayList<Friend> selected) {
        this.selected = selected;
    }

    public ContactAdapter getCadpt() {
        return cadpt;
    }

    public void setCadpt(ContactAdapter cadpt) {
        this.cadpt = cadpt;
    }

    public void setAdpt(MyAdapter adpt) {
        this.adpt = adpt;
    }

    public Billify() {
    }
}
