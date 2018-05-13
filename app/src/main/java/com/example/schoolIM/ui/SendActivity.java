package com.example.schoolIM.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.schoolIM.bean.BaseResponse;
import com.example.schoolIM.net.ApiManager;
import com.example.schoolIM.net.BaseCallBack;
import com.example.schoolIM.net.OkHttpTools;
import com.example.schoolIM.utils.SpUtils;
import com.hyphenate.schoolIM.R;

public class SendActivity extends BaseActivity {

    private EditText msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        msg = (EditText) findViewById(R.id.et_msg);
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.tv_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAct();
            }
        });


    }

    private void sendAct() {
        String review = msg.getText().toString().trim();
        if (TextUtils.isEmpty(review)) {
            showToast("请输入内容");
            return;
        }
        OkHttpTools.sendPost(mContext, ApiManager.REVIEW_ADD)
                .addParams("uid", SpUtils.getUserBean(mContext).getId() + "")
                .addParams("msg", review)
                .build()
                .execute(new BaseCallBack<BaseResponse>(mContext, BaseResponse.class) {
                    @Override
                    public void onResponse(BaseResponse baseResponse) {
                        showToast("发布成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                });
    }
}
