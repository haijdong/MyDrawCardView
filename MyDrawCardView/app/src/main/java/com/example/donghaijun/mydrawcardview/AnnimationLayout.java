package com.example.donghaijun.mydrawcardview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by donghaijun on 2016/9/21.
 */

public class AnnimationLayout extends FrameLayout implements View.OnClickListener {

    public static final int DURATION = 500;
    private static int HEIGHT_SHRINK = 120;
    private static int expandHeight;

    private boolean isInit = true;
    private long lastTime = 0;
    private boolean isCreate=true;

    public AnnimationLayout(Context context) {
        super(context,null,-1);

    }

    public AnnimationLayout(Context context, AttributeSet attrs) {
        super(context, attrs,-1);
    }

    public AnnimationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //设置 child 点击事件
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setOnClickListener(this);
            getChildAt(i).setTag(R.id.tv_item_two, i);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        HEIGHT_SHRINK = height / 11;
        //计算每个子view的测量约束
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY); //子 view 和 AnnimationLayout 一样大
        expandHeight = getMeasuredHeight() - (getChildCount() - 1) * HEIGHT_SHRINK;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(expandHeight, MeasureSpec.EXACTLY); //子 view 的高为展开之后的高

        //调用子 view 的 measure 方法, 传入测量约束, 测量每个子 view 的大小
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            if (isCreate) {
                getChildAt(i).setTag(R.id.tv_item, i);
            }
        }
        isCreate=false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //给每个 child 定位
        int childTop;
        int childBottom;

        //在点击之前给每个子 view 进行布局, 在Z轴最上面的 view 排在 Y 轴最上面
        if (isInit) {
            for (int i = 0; i < getChildCount(); i++) {
                childBottom = getMeasuredHeight() - HEIGHT_SHRINK * i;
                childTop = childBottom - HEIGHT_SHRINK;
                getChildAt(i).layout(0, childTop, getMeasuredWidth(), childBottom);
            }
        }
    }

    @Override
    public void onClick(final View v) {
        isInit = false;
        final int top_origin = v.getTop();
        final int bottom_origin = v.getBottom();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < 1000) {
            return;
        }
        lastTime = System.currentTimeMillis();
        annimatorOtherView(top_origin);
        final int position = (int) v.getTag(R.id.tv_item);
        if (v.getMeasuredHeight() != v.getHeight()) {
            // 展开动画
            annimatorClickView(v, top_origin, bottom_origin);
            switch (position) {
                case 0:
                    v.setBackgroundResource(R.drawable.ic_approve_big);
                    break;
                case 1:
                    v.setBackgroundResource(R.drawable.ic_chosemate_big);
                    break;
                case 2:
                    v.setBackgroundResource(R.drawable.ic_tetailsinformation_big);
                    break;
                case 3:
                    v.setBackgroundResource(R.drawable.ic_self_big);
                    break;
            }

        } else {
            // 收缩动画
            annimatorContractView(v, top_origin, bottom_origin);
            switch (position) {
                case 0:
                    v.setBackgroundResource(R.drawable.ic_approve_small);
                    break;
                case 1:
                    v.setBackgroundResource(R.drawable.ic_chose_small);
                    break;
                case 2:
                    v.setBackgroundResource(R.drawable.ic_tetailsinformation_small);
                    break;
                case 3:
                    v.setBackgroundResource(R.drawable.ic_self_samll);
                    break;
            }
        }
    }

    /**
     * 收缩动画
     *
     * @param v
     * @param top_origin
     * @param bottom_origin
     */
    private void annimatorContractView(final View v, final int top_origin, final int bottom_origin) {
        //bringChildToFront(v);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (Float) animation.getAnimatedValue();
                Float bottom = evaluate(fraction, bottom_origin, expandHeight);
                // 点中的 View, top 的结束值为 0, bottom 的结束值应该是展开之后的高度
                Float top = evaluate(fraction, top_origin, bottom - HEIGHT_SHRINK);
                v.layout(0, top.intValue(), v.getRight(), bottom.intValue());
            }
        });
        valueAnimator.setDuration(DURATION);
        valueAnimator.start();
    }

    /**
     * 使用属性动画给非点击的 View 重新布局
     *
     * @param topOfClickView
     */
    private void annimatorOtherView(int topOfClickView) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final int top_origin = child.getTop();
            final int bottom_origin = child.getBottom();
            if ((topOfClickView - child.getTop() > 0)) {
                final int position = (int) child.getTag(R.id.tv_item);
                switch (position) {
                    case 0:
                        child.setBackgroundResource(R.drawable.ic_approve_small);
                        break;
                    case 1:
                        child.setBackgroundResource(R.drawable.ic_chose_small);
                        break;
                    case 2:
                        child.setBackgroundResource(R.drawable.ic_tetailsinformation_small);
                        break;
                    case 3:
                        child.setBackgroundResource(R.drawable.ic_self_samll);
                        break;
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = (Float) animation.getAnimatedValue();
                        //通过百分比计算 view 的 top 值和 bottom 值,对于没被点击的 view, 动画结束的时候 View 的 bottom 应该是在原来的基础上加 收缩后的高度
                        Float bottom = evaluate(fraction, bottom_origin, bottom_origin + HEIGHT_SHRINK);
                        Float top = evaluate(fraction, top_origin, bottom - HEIGHT_SHRINK);
                        child.layout(0, top.intValue(), child.getRight(), bottom.intValue());
                    }
                });
                valueAnimator.setDuration(DURATION);
                valueAnimator.start();
            }
        }
    }

    /**
     * 使用属性动画给点击 View 重新布局
     *
     * @param v
     * @param top_origin
     * @param bottom_origin
     */
    private void annimatorClickView(final View v, final int top_origin, final int bottom_origin) {
        bringChildToFront(v);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (Float) animation.getAnimatedValue();
                // 点中的 View, top 的结束值为 0, bottom 的结束值应该是展开之后的高度
                Float top = evaluate(fraction, top_origin, 0);
                Float bottom = evaluate(fraction, bottom_origin, expandHeight);
                v.layout(0, top.intValue(), v.getRight(), bottom.intValue());
            }
        });
        valueAnimator.setDuration(DURATION);
        valueAnimator.start();
    }

    /**
     * 给定开始值和结束值,计算开始值和结束值之间某个百分比对应的值
     *
     * @param fraction   百分比
     * @param startValue 开始值
     * @param endValue   终点值
     * @return
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
}
