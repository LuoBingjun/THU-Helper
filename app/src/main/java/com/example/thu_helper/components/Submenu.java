package com.example.thu_helper.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thu_helper.R;

import java.util.jar.Attributes;

import butterknife.BindView;
import butterknife.ButterKnife;
public class Submenu extends RelativeLayout {
    @BindView(R.id.sub_menu_icon)
    ImageView sub_menu_icon;
    @BindView(R.id.sub_menu_text)
    TextView sub_menu_text;

    private View mView;
    public Submenu(Context context){ this(context,null); }
    public Submenu(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context,attrs);
    }

    public  Submenu(Context context, AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        mView = LayoutInflater.from(context).inflate(R.layout.component_submenu,this,true);
        ButterKnife.bind(mView);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.submenu_view);
        sub_menu_icon.setImageResource(a.getResourceId(R.styleable.submenu_view_src,R.drawable.ic_social_circle));
        sub_menu_text.setText(a.getString(R.styleable.submenu_view_text));
    }
}
