package com.app.syspoint.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatImageView;

import com.app.syspoint.R;

public class PrettyDialog extends AppCompatDialog {
    Resources resources;
    LinearLayout ll_content;
    LinearLayout ll_buttons;
    PrettyDialogCircularImageView iv_icon;
    RotateAnimation close_rotation_animation;
    boolean icon_animation = true;
    TextView tv_title;
    TextView tv_message;
    Typeface typeface;
    PrettyDialog thisDialog;
    Context context;

    public PrettyDialog(Context context) {
        super(context);
        this.context = context;
        this.getWindow().requestFeature(1);
        this.setContentView(R.layout.pdlg_layout);
        this.setCancelable(true);
        this.resources = context.getResources();
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        DisplayMetrics displayMetrics = this.resources.getDisplayMetrics();
        float pxWidth = (float)displayMetrics.widthPixels;
        this.getWindow().setLayout((int)((double)pxWidth * 0.75D), -2);
        this.getWindow().getAttributes().windowAnimations = R.style.pdlg_default_animation;
        this.thisDialog = this;
        this.setupViews_Base();
    }

    private void setupViews_Base() {
        this.ll_content = (LinearLayout)this.findViewById(R.id.ll_content);
        this.ll_buttons = (LinearLayout)this.findViewById(R.id.ll_buttons);
        this.iv_icon = (PrettyDialogCircularImageView)this.findViewById(R.id.iv_icon);
        LayoutParams lp = new LayoutParams(-1, -2);
        lp.setMargins(0, this.resources.getDimensionPixelSize(R.dimen.pdlg_icon_size) / 2, 0, 0);
        this.ll_content.setLayoutParams(lp);
        this.ll_content.setPadding(0, (int)(1.25D * (double)this.resources.getDimensionPixelSize(R.dimen.pdlg_icon_size) / 2.0D), 0, this.resources.getDimensionPixelSize(R.dimen.pdlg_space_1_0x));
        this.close_rotation_animation = new RotateAnimation(0.0F, 180.0F, 1, 0.5F, 1, 0.5F);
        this.close_rotation_animation.setDuration(300L);
        this.close_rotation_animation.setRepeatCount(0);
        this.close_rotation_animation.setInterpolator(new DecelerateInterpolator());
        this.close_rotation_animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                PrettyDialog.this.thisDialog.dismiss();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.iv_icon.setOnTouchListener((v, event) -> {
            switch(event.getAction()) {
                case 0:
                    v.setAlpha(0.7F);
                    return true;
                case 1:
                    v.setAlpha(1.0F);
                    if (PrettyDialog.this.icon_animation) {
                        v.startAnimation(PrettyDialog.this.close_rotation_animation);
                    }

                    return true;
                default:
                    return false;
            }
        });
        this.tv_title = (TextView)this.findViewById(R.id.tv_title);
        this.tv_title.setVisibility(View.GONE);
        this.tv_message = (TextView)this.findViewById(R.id.tv_message);
        this.tv_message.setVisibility(View.GONE);
    }

    public PrettyDialog setGravity(int gravity) {
        this.getWindow().setGravity(gravity);
        return this;
    }

    public PrettyDialog addButton(String text, Integer textColor, Integer backgroundColor, PrettyDialogCallback callback) {
        PrettyDialogButton button = new PrettyDialogButton(this.context, text, textColor, backgroundColor, this.typeface, callback);
        int margin = this.resources.getDimensionPixelSize(R.dimen.pdlg_space_1_0x);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(margin, margin, margin, 0);
        button.setLayoutParams(lp);
        this.ll_buttons.addView(button);
        return this;
    }

    public PrettyDialog setTitle(String text) {
        if (text.trim().length() > 0) {
            this.tv_title.setVisibility(View.VISIBLE);
            this.tv_title.setText(text);
        } else {
            this.tv_title.setVisibility(View.GONE);
        }

        return this;
    }

    public PrettyDialog setTitleColor(Integer color) {
        this.tv_title.setTextColor(this.context.getResources().getColor(color == null ? R.color.black : color));
        return this;
    }

    public PrettyDialog setMessage(String text) {
        if (text.trim().length() > 0) {
            this.tv_message.setVisibility(View.VISIBLE);
            this.tv_message.setText(text);
        } else {
            this.tv_message.setVisibility(View.GONE);
        }

        return this;
    }

    public PrettyDialog setMessageColor(Integer color) {
        this.tv_message.setTextColor(this.context.getResources().getColor(color == null ? R.color.black : color));
        return this;
    }

    public PrettyDialog setIcon(Integer icon) {
        this.iv_icon.setImageResource(icon == null ? R.drawable.pdlg_icon_close : icon);
        this.icon_animation = false;
        this.iv_icon.setOnTouchListener((OnTouchListener)null);
        return this;
    }

    public PrettyDialog setIconTint(Integer color) {
        if (color == null) {
            this.iv_icon.setColorFilter((ColorFilter)null);
        } else {
            this.iv_icon.setColorFilter(this.context.getResources().getColor(color), Mode.MULTIPLY);
        }

        return this;
    }

    public PrettyDialog setIconCallback(final PrettyDialogCallback callback) {
        this.iv_icon.setOnTouchListener((OnTouchListener)null);
        if (callback != null) {
            this.iv_icon.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case 0:
                            v.setAlpha(0.7F);
                            return true;
                        case 1:
                            v.setAlpha(1.0F);
                            callback.onClick();
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }

        return this;
    }

    public PrettyDialog setIcon(Integer icon, Integer iconTint, final PrettyDialogCallback callback) {
        this.icon_animation = false;
        this.iv_icon.setImageResource(icon == null ? R.drawable.pdlg_icon_close : icon);
        if (iconTint == null) {
            this.iv_icon.setColorFilter((ColorFilter)null);
        } else {
            this.iv_icon.setColorFilter(this.context.getResources().getColor(iconTint), Mode.MULTIPLY);
        }

        this.iv_icon.setOnTouchListener((OnTouchListener)null);
        if (callback != null) {
            this.iv_icon.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case 0:
                            v.setAlpha(0.7F);
                            return true;
                        case 1:
                            v.setAlpha(1.0F);
                            callback.onClick();
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }

        return this;
    }

    public PrettyDialog setTypeface(Typeface tf) {
        this.typeface = tf;
        this.tv_title.setTypeface(tf);
        this.tv_message.setTypeface(tf);

        for(int i = 0; i < this.ll_buttons.getChildCount(); ++i) {
            PrettyDialogButton button = (PrettyDialogButton)this.ll_buttons.getChildAt(i);
            button.setTypeface(tf);
            button.requestLayout();
        }

        return this;
    }

    public PrettyDialog setAnimationEnabled(boolean enabled) {
        if (enabled) {
            this.getWindow().getAttributes().windowAnimations = R.style.pdlg_default_animation;
        } else {
            this.getWindow().getAttributes().windowAnimations = R.style.pdlg_no_animation;
        }

        return this;
    }

    public static class PrettyDialogCircularImageView extends AppCompatImageView {
        private float borderWidth;
        private int canvasSize;
        private ColorFilter colorFilter;
        private Bitmap image;
        private Drawable drawable;
        private Paint paint;
        private Paint paintBorder;
        private Paint paintBackground;

        public PrettyDialogCircularImageView(Context context) {
            this(context, (AttributeSet)null);
        }

        public PrettyDialogCircularImageView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public PrettyDialogCircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.init(context, attrs, defStyleAttr);
        }

        private void init(Context context, AttributeSet attrs, int defStyleAttr) {
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paintBorder = new Paint();
            this.paintBorder.setAntiAlias(true);
            this.paintBackground = new Paint();
            this.paintBackground.setAntiAlias(true);
            this.setBorderWidth(0.0F);
            this.setBorderColor(-1);
            this.setBackgroundColor(-1);
        }

        public void setBorderWidth(float borderWidth) {
            this.borderWidth = borderWidth;
            this.requestLayout();
            this.invalidate();
        }

        public void setBorderColor(int borderColor) {
            if (this.paintBorder != null) {
                this.paintBorder.setColor(borderColor);
            }

            this.invalidate();
        }

        public void setBackgroundColor(int backgroundColor) {
            if (this.paintBackground != null) {
                this.paintBackground.setColor(backgroundColor);
            }

            this.invalidate();
        }

        public void setColorFilter(ColorFilter colorFilter) {
            if (this.colorFilter != colorFilter) {
                this.colorFilter = colorFilter;
                this.drawable = null;
                this.invalidate();
            }
        }

        public ScaleType getScaleType() {
            ScaleType currentScaleType = super.getScaleType();
            return currentScaleType != null && currentScaleType == ScaleType.CENTER_INSIDE ? currentScaleType : ScaleType.CENTER_CROP;
        }

        public void setScaleType(ScaleType scaleType) {
            if (scaleType != ScaleType.CENTER_CROP && scaleType != ScaleType.CENTER_INSIDE) {
                throw new IllegalArgumentException(String.format("ScaleType %s not supported. Just ScaleType.CENTER_CROP & ScaleType.CENTER_INSIDE are available for this library.", scaleType));
            } else {
                super.setScaleType(scaleType);
            }
        }

        public void onDraw(Canvas canvas) {
            this.loadBitmap();
            if (this.image != null) {
                if (!this.isInEditMode()) {
                    this.canvasSize = Math.min(canvas.getWidth(), canvas.getHeight());
                }

                int circleCenter = (int)((float)this.canvasSize - this.borderWidth * 2.0F) / 2;
                float margeWithShadowRadius = 0.0F;
                canvas.drawCircle((float)circleCenter + this.borderWidth, (float)circleCenter + this.borderWidth, (float)circleCenter + this.borderWidth - margeWithShadowRadius, this.paintBorder);
                canvas.drawCircle((float)circleCenter + this.borderWidth, (float)circleCenter + this.borderWidth, (float)circleCenter - margeWithShadowRadius, this.paintBackground);
                canvas.drawCircle((float)circleCenter + this.borderWidth, (float)circleCenter + this.borderWidth, (float)circleCenter - margeWithShadowRadius, this.paint);
            }
        }

        private void loadBitmap() {
            if (this.drawable != this.getDrawable()) {
                this.drawable = this.getDrawable();
                this.image = this.drawableToBitmap(this.drawable);
                this.updateShader();
            }
        }

        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            this.canvasSize = Math.min(w, h);
            if (this.image != null) {
                this.updateShader();
            }

        }

        private void updateShader() {
            if (this.image != null) {
                BitmapShader shader = new BitmapShader(this.image, TileMode.CLAMP, TileMode.CLAMP);
                float scale = 0.0F;
                float dx = 0.0F;
                float dy = 0.0F;
                switch(this.getScaleType()) {
                    case CENTER_CROP:
                        if (this.image.getWidth() * this.getHeight() > this.getWidth() * this.image.getHeight()) {
                            scale = (float)this.getHeight() / (float)this.image.getHeight();
                            dx = ((float)this.getWidth() - (float)this.image.getWidth() * scale) * 0.5F;
                        } else {
                            scale = (float)this.getWidth() / (float)this.image.getWidth();
                            dy = ((float)this.getHeight() - (float)this.image.getHeight() * scale) * 0.5F;
                        }
                        break;
                    case CENTER_INSIDE:
                        if (this.image.getWidth() * this.getHeight() < this.getWidth() * this.image.getHeight()) {
                            scale = (float)this.getHeight() / (float)this.image.getHeight();
                            dx = ((float)this.getWidth() - (float)this.image.getWidth() * scale) * 0.5F;
                        } else {
                            scale = (float)this.getWidth() / (float)this.image.getWidth();
                            dy = ((float)this.getHeight() - (float)this.image.getHeight() * scale) * 0.5F;
                        }
                }

                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                shader.setLocalMatrix(matrix);
                this.paint.setShader(shader);
                this.paint.setColorFilter(this.colorFilter);
            }
        }

        private Bitmap drawableToBitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            } else if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable)drawable).getBitmap();
            } else {
                try {
                    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                    return bitmap;
                } catch (Exception var4) {
                    var4.printStackTrace();
                    return null;
                }
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = this.measureWidth(widthMeasureSpec);
            int height = this.measureHeight(heightMeasureSpec);
            this.setMeasuredDimension(width, height);
        }

        private int measureWidth(int measureSpec) {
            int specMode = MeasureSpec.getMode(measureSpec);
            int specSize = MeasureSpec.getSize(measureSpec);
            int result;
            if (specMode == 1073741824) {
                result = specSize;
            } else if (specMode == -2147483648) {
                result = specSize;
            } else {
                result = this.canvasSize;
            }

            return result;
        }

        private int measureHeight(int measureSpecHeight) {
            int specMode = MeasureSpec.getMode(measureSpecHeight);
            int specSize = MeasureSpec.getSize(measureSpecHeight);
            int result;
            if (specMode == 1073741824) {
                result = specSize;
            } else if (specMode == -2147483648) {
                result = specSize;
            } else {
                result = this.canvasSize;
            }

            return result + 2;
        }
    }
}

