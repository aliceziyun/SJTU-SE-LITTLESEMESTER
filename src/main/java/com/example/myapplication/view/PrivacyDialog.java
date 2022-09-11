package com.example.myapplication.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class PrivacyDialog extends Dialog {
    private Button knowBtn;

    public PrivacyDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_privacy_agreement,null);
        setContentView(view);

        knowBtn = (Button)findViewById(R.id.agree_btn);
        knowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
