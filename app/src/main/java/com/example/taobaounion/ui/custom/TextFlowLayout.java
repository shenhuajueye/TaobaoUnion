package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.taobaounion.R;

import java.util.ArrayList;
import java.util.List;

public class TextFlowLayout extends ViewGroup {
    private List<String> mTextList = new ArrayList<>();

    public static final float DEFAULT_SPACE = 10;
    private float itemHorizontalSpace = DEFAULT_SPACE;
    private float itemVerticalSpace = DEFAULT_SPACE;
    private int selfWidth;
    private int itemHeight;
    private OnFlowTextItemClickListener mItemClickListener = null;

    public float getItemHorizontalSpace() {
        return itemHorizontalSpace;
    }

    public void setItemHorizontalSpace(float itemHorizontalSpace) {
        this.itemHorizontalSpace = itemHorizontalSpace;
    }

    public float getItemVerticalSpace() {
        return itemVerticalSpace;
    }

    public void setItemVerticalSpace(float itemVerticalSpace) {
        this.itemVerticalSpace = itemVerticalSpace;
    }

    public TextFlowLayout(Context context) {
        this(context, null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //去拿到相关属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        itemHorizontalSpace = ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace, DEFAULT_SPACE);
        itemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_verticalSpace, DEFAULT_SPACE);
        ta.recycle();
        //LogUtils.d(this, "itemHorizontalSpace -->" + itemHorizontalSpace);
        //LogUtils.d(this, "itemVerticalSpace -->" + itemVerticalSpace);
    }

    public void setTextList(List<String> textList) {
        this.mTextList = textList;
        //遍历内容
        for (String text : mTextList) {
            //添加子view
            //LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, true);
            //等价于
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFlowItemClick(text);
                    }
                }
            });
            addView(item);
        }
    }

    //描述所有的行
    private List<List<View>> lines = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 0) {
            return;
        }
        //描述单行
        List<View> line = null;
        lines.clear();
        selfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        //LogUtils.d(this, "selfWidth -->" + selfWidth);
        //测量
        //LogUtils.d(this, "onMeasure -->" + getChildCount());
        //测量孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            if (itemView.getVisibility() != VISIBLE) {
                //不需要进行测量
                continue;
            }
            //测量前
            //LogUtils.d(this, "height before measure-->" + itemView.getMeasuredHeight());
            measureChild(itemView, widthMeasureSpec, heightMeasureSpec);
            //测量后
            //LogUtils.d(this, "height after measure-->" + itemView.getMeasuredHeight());
            if (line == null) {
                //说明当前行为空，可以添加
                line = createNewLine(itemView);
            } else {
                //判断是否可以继续添加
                if (canBeAdd(itemView, line)) {
                    //可以添加
                    line.add(itemView);
                } else {
                    //新创建一行
                    line = createNewLine(itemView);
                }
            }
        }
        itemHeight = getChildAt(0).getMeasuredHeight();
        int selfHeight = (int) (lines.size() * itemHeight + itemVerticalSpace * (lines.size() + 1) + 0.5f);
        //测量自己
        setMeasuredDimension(selfWidth, selfHeight);
    }

    private List<View> createNewLine(View itemView) {
        List<View> line = new ArrayList<>();
        line.add(itemView);
        lines.add(line);
        return line;
    }

    /**
     * 判断当前行是否可以继续添加新数据
     *
     * @param itemView
     * @param line
     */
    private boolean canBeAdd(View itemView, List<View> line) {
        //所有已经添加的子view宽度相加+(line.size()+1)*itemHorizontalSpace + itemView.getMeasureWidth()
        //条件：如果小于等于当前控件的宽度，则可以添加，否则不能添加
        int totalWidth = itemView.getMeasuredWidth();
        for (View view : line) {
            //叠加所有已经添加控件的宽度
            totalWidth += view.getMeasuredWidth();
        }
        //水平间距的宽度
        totalWidth += itemHorizontalSpace * (line.size() + 1);
        //LogUtils.d(this, "totalWidth -->" + totalWidth);
        //LogUtils.d(this, "selfWidth -->" + selfWidth);
        //如果小于等于当前控件的宽度，则可以添加，否则不能添加
        return totalWidth <= selfWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //摆放孩子
        //LogUtils.d(this, "onLayout -->" + getChildCount());
        int topOffset = (int) itemVerticalSpace;
        for (List<View> views : lines) {
            //views是每一行
            int leftOffset = (int) itemHorizontalSpace;
            for (View view : views) {
                //每一行里的item
                view.layout(leftOffset, topOffset, leftOffset + view.getMeasuredWidth(), topOffset + view.getMeasuredHeight());
                //
                leftOffset += view.getMeasuredWidth() + itemHorizontalSpace;
            }
            topOffset += itemHeight + itemVerticalSpace;
        }
    }

    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnFlowTextItemClickListener {
        void onFlowItemClick(String text);
    }
}
