//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.app.syspoint.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.syspoint.R;

class PrettyDialogButton extends LinearLayout {
    Context context;
    Resources resources;
    PrettyDialogCallback callback;
    Integer default_background_color;
    Integer background_color;
    Integer default_text_color;
    Integer text_color;
    String text;
    TextView tv;
    ImageView iv;
    Typeface tf;

    public PrettyDialogButton(Context context, String text, int textColor, int background_color, Typeface tf, PrettyDialogCallback callback) {
        super(context);
        this.default_background_color = R.color.pdlg_color_blue;
        this.default_text_color = R.color.white;
        this.context = context;
        this.resources = context.getResources();
        this.text = text;
        this.text_color = textColor;
        this.background_color = background_color;
        this.tf = tf;
        this.callback = callback;
        this.init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.pdlg_button, this);
        }

        this.tv = (TextView)this.findViewById(R.id.tv_button);
        this.tv.setText(this.text);
        this.tv.setTextColor(this.resources.getColor(this.text_color == null ? this.default_text_color : this.text_color));
        if (this.tf != null) {
            this.tv.setTypeface(this.tf);
        }

        this.setBackground();
        this.setOnClickListener(v -> {
            this.setEnabled(false);
            this.setOnClickListener(null);
            if (PrettyDialogButton.this.callback != null) {
                v.postDelayed(() -> PrettyDialogButton.this.callback.onClick(), 150L);
            }
            this.setEnabled(true);
        });
    }

    public void setTypeface(Typeface tf) {
        this.tf = tf;
        this.tv.setTypeface(tf);
    }

    private void setBackground() {
        this.setBackgroundDrawable(this.makeSelector(this.resources.getColor(this.background_color == null ? this.default_background_color : this.background_color)));
    }

    private int getLightenColor(int color) {
        double fraction = 0.2D;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = (int)Math.min((double)red + (double)red * fraction, 255.0D);
        green = (int)Math.min((double)green + (double)green * fraction, 255.0D);
        blue = (int)Math.min((double)blue + (double)blue * fraction, 255.0D);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, red, green, blue);
    }

    private StateListDrawable makeSelector(int color) {
        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(150);
        GradientDrawable pressed_drawable = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{this.getLightenColor(color), this.getLightenColor(color)});
        pressed_drawable.setCornerRadius((float)this.resources.getDimensionPixelSize(R.dimen.pdlg_corner_radius));
        GradientDrawable default_drawable = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{color, color});
        default_drawable.setCornerRadius((float)this.resources.getDimensionPixelSize(R.dimen.pdlg_corner_radius));
        res.addState(new int[]{16842919}, pressed_drawable);
        res.addState(new int[0], default_drawable);
        return res;
    }
}
