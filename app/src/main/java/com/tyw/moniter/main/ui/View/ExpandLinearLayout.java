package com.tyw.moniter.main.ui.View;

import android.animation.Animator;
import android.animation.ValueAnimator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ExpandLinearLayout extends LinearLayout {
    public ExpandLinearLayout(Context context) {
        this(context, null);
    }

    public ExpandLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private View layoutView;
    private int viewHeight;
    private boolean isExpand;
    private long animationDuration;

    private void initView() {
        layoutView = this;
        isExpand = true;
        animationDuration = 30;
        setViewDimensions();
    }

    /**
     * @param isExpand 初始状态是否折叠
     */
    public void initExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {
            animateToggle(10,null);
        }
    }

    /**
     * 设置动画时间
     *
     * @param animationDuration 动画时间
     */
    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    /**
     * 获取 subView 的总高度
     * View.post() 的 runnable 对象中的方法会在 View 的 measure、layout 等事件后触发
     */
    private void setViewDimensions() {
        layoutView.post(new Runnable() {
            @Override
            public void run() {
                if (viewHeight <= 0) {
                    viewHeight = layoutView.getMeasuredHeight();
                }
            }
        });
    }

    public static void setViewHeight(View view, int height) {
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.requestLayout();
    }

    public static interface ExpandListener {
        void OnExpandEnd();
    }

    /**
     * 切换动画实现
     */
    private void animateToggle(long animationDuration, final ExpandListener _listener) {
        int intw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int inth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layoutView.measure(intw, inth);
        //int intwidth = layoutView.getMeasuredWidth();
        viewHeight = layoutView.getMeasuredHeight();

        ValueAnimator heightAnimation = isExpand ?
                ValueAnimator.ofFloat(0f, viewHeight) : ValueAnimator.ofFloat(viewHeight, 0f);
        heightAnimation.setDuration(animationDuration / 2);
        heightAnimation.setStartDelay(animationDuration / 2);

        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                setViewHeight(layoutView, (int) val);
            }
        });
        if (_listener != null)
            heightAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    _listener.OnExpandEnd();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        heightAnimation.start();
    }

    public boolean isExpand() {
        return isExpand;
    }

    /**
     * 折叠view
     */
    public void collapse(ExpandListener _listener) {
        isExpand = false;
        animateToggle(animationDuration, _listener);
    }

    /**
     * 展开view
     */
    public void expand(ExpandListener _listener) {
        isExpand = true;
        animateToggle(animationDuration, _listener);
    }

    // 设置折叠
    public void SetExpand(boolean _on, boolean _ani, ExpandListener _listener) {
        int action=_on?VISIBLE:GONE;
        if(getVisibility()==action) return;
        setVisibility(action);
        return;
//        //if(_on==isExpand) return;
//        if (!_ani) {
//            isExpand=_on;
//            if (_on) {
//                int intw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                int inth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                layoutView.measure(intw, inth);
//                //int intwidth = layoutView.getMeasuredWidth();
//                viewHeight = layoutView.getMeasuredHeight();
//                setViewHeight(layoutView, viewHeight);
//            } else
//                setViewHeight(layoutView, 0);
//            return;
//        }
//        if (_on) expand(_listener);
//        else collapse(_listener);
    }

    public void toggleExpand() {

        if (isExpand) {
            collapse(null);
        } else {
            expand(null);
        }
    }
}
