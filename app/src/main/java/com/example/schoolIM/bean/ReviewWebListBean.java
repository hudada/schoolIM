package com.example.schoolIM.bean;

import java.util.ArrayList;

public class ReviewWebListBean extends BaseResponse {

    private ArrayList<ReviewBean> data;

    public ArrayList<ReviewBean> getData() {
        return data;
    }

    public void setData(ArrayList<ReviewBean> data) {
        this.data = data;
    }
}
