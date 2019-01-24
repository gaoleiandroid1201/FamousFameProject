package com.android.learn.base.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaolei.basemodule.R;

public class TitleView extends FrameLayout {
 
	private ImageView iv_back;
 
	private TextView title;
 
	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.title_view, this);
		iv_back =findViewById(R.id.iv_back);
		title =findViewById(R.id.title);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Activity) getContext()).finish();
			}
		});
	}
 
	public void setTitleText(String text) {
		title.setText(text);
	}
 
}
