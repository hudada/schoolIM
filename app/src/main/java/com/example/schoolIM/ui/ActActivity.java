package com.example.schoolIM.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.schoolIM.adapter.BaseAdapter;
import com.example.schoolIM.bean.LikeObjBean;
import com.example.schoolIM.bean.ReviewBean;
import com.example.schoolIM.bean.ReviewListBean;
import com.example.schoolIM.bean.ReviewListObjBean;
import com.example.schoolIM.bean.ReviewWebListBean;
import com.example.schoolIM.net.ApiManager;
import com.example.schoolIM.net.BaseCallBack;
import com.example.schoolIM.net.OkHttpTools;
import com.example.schoolIM.utils.SpUtils;
import com.hyphenate.schoolIM.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ActActivity extends BaseActivity {

    private ArrayList<ReviewBean> mData = new ArrayList<>();
    private MyAdapter adapter;
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);
        inflater = LayoutInflater.from(mContext);
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.tv_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(mContext, SendActivity.class), 521);
            }
        });

        RecyclerView rvList = (RecyclerView) findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(mContext, R.layout.item_act, mData);
        rvList.setAdapter(adapter);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadWebData();
        }
    }

    private void loadWebData() {
        OkHttpTools.sendPost(mContext, ApiManager.REVIEW_LIST)
                .addParams("uid", SpUtils.getUserBean(mContext).getId() + "")
                .build()
                .execute(new BaseCallBack<ReviewWebListBean>(mContext, ReviewWebListBean.class) {
                    @Override
                    public void onResponse(ReviewWebListBean reviewWebListBean) {
                        mData = reviewWebListBean.getData();
                        adapter.notifyDataSetChanged(mData);
                    }
                });
    }

    private void loadData() {
        loadWebData();
    }

    private class MyAdapter extends BaseAdapter<ReviewBean> {

        public MyAdapter(Context context, int layoutId, ArrayList<ReviewBean> data) {
            super(context, layoutId, data);
        }

        @Override
        public void initItemView(final BaseViewHolder holder, final ReviewBean reviewBean, final int position) {
            if (TextUtils.isEmpty(reviewBean.getUserBean().getHead())) {
                ((ImageView) holder.getView(R.id.iv_head)).setImageResource(R.drawable.ease_default_avatar);
            } else {
                Glide.with(mContext).load(ApiManager.HEAD_PATH + reviewBean.getUserBean().getHead())
                        .into((ImageView) holder.getView(R.id.iv_head));
            }
            if (TextUtils.isEmpty(reviewBean.getUserBean().getNic())) {
                holder.setText(R.id.tv_name, reviewBean.getUserBean().getNumber());
            } else {
                holder.setText(R.id.tv_name, reviewBean.getUserBean().getNic());
            }

            holder.setText(R.id.tv_time, format.format(reviewBean.getTime()));
            ImageView iv_like = (ImageView) holder.getView(R.id.iv_like);
            if (reviewBean.isLike()) {
                iv_like.setImageResource(R.drawable.ic_favorite_red_300_24dp);
            } else {
                iv_like.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
            holder.setText(R.id.tv_sum, reviewBean.getLikeSum() + "");
            holder.setText(R.id.tv_msg, reviewBean.getMsg());

            initList((LinearLayout) holder.getView(R.id.ll_list), reviewBean);
            holder.getView(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendReview((EditText) holder.getView(R.id.et_msg), reviewBean, position);
                }
            });

            holder.getView(R.id.ll_like).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doLike(reviewBean, position);
                }
            });
        }
    }

    private void doLike(ReviewBean reviewBean, final int position) {
        OkHttpTools.sendPost(mContext, ApiManager.LIKE_ADD)
                .addParams("uid", SpUtils.getUserBean(mContext).getId() + "")
                .addParams("rid", reviewBean.getId() + "")
                .build()
                .execute(new BaseCallBack<LikeObjBean>(mContext, LikeObjBean.class) {
                    @Override
                    public void onResponse(LikeObjBean likeObjBean) {
                        int sum = mData.get(position).getLikeSum();
                        if (likeObjBean.getData() == null) {
                            mData.get(position).setLike(false);
                            mData.get(position).setLikeSum(sum - 1);
                        } else {
                            mData.get(position).setLike(true);
                            mData.get(position).setLikeSum(sum + 1);
                        }
                        adapter.notifyItemChanged(position, "one");
                    }
                });
    }

    private void initList(LinearLayout view, ReviewBean reviewBean) {
        view.removeAllViews();
        for (ReviewListBean reviewListBean : reviewBean.getList()) {
            View view1 = inflater.inflate(R.layout.item_review_list, null, false);
            if (TextUtils.isEmpty(reviewListBean.getUserBean().getNic())) {
                ((TextView) view1.findViewById(R.id.tv_name)).setText(reviewListBean.getUserBean().getNumber() + "评论：");
            } else {
                ((TextView) view1.findViewById(R.id.tv_name)).setText(reviewListBean.getUserBean().getNic() + "评论：");
            }
            ((TextView) view1.findViewById(R.id.tv_msg)).setText(reviewListBean.getMsg());
            view.addView(view1);
        }
    }

    private void sendReview(final EditText view, ReviewBean reviewBean, final int position) {
        final String review = view.getText().toString().trim();
        if (TextUtils.isEmpty(review)) {
            showToast("请输入内容");
            return;
        }
        OkHttpTools.sendPost(mContext, ApiManager.REVIEWLIST_ADD)
                .addParams("uid", SpUtils.getUserBean(mContext).getId() + "")
                .addParams("rid", reviewBean.getId() + "")
                .addParams("msg", review)
                .build()
                .execute(new BaseCallBack<ReviewListObjBean>(mContext, ReviewListObjBean.class) {
                    @Override
                    public void onResponse(ReviewListObjBean reviewListObjBean) {
                        showToast("评论成功");
                        view.setText("");
                        mData.get(position).getList().add(reviewListObjBean.getData());
                        adapter.notifyItemChanged(position, "one");
                    }
                });
    }
}
