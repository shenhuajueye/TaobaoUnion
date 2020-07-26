package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.model.domain.ILinearItemInfo;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinearItemContentAdapter extends RecyclerView.Adapter<LinearItemContentAdapter.InnerHolder> {
    private List<ILinearItemInfo> data = new ArrayList<>();
    private OnListItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LogUtils.d(this,"onCreateViewHolder...");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linear_goods_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //LogUtils.d(this,"onBindViewHolder..." + position);
        ILinearItemInfo dataBean = data.get(position);
        //设置数据
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(dataBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<? extends ILinearItemInfo> contents) {
        data.clear();
        data.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<? extends ILinearItemInfo> contents) {
        //添加之前拿到原来数据的size
        int olderSize = data.size();
        data.addAll(contents);
        //更新UI
        notifyItemRangeChanged(olderSize, contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.goods_cover)
        public ImageView coverIv;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_price)
        public TextView offPriseTv;

        @BindView(R.id.goods_after_off_price)
        public TextView finalPriceTv;

        @BindView(R.id.goods_original_price)
        public TextView originalPriceTv;

        @BindView(R.id.goods_sell_count)
        public TextView sellCountTv;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(ILinearItemInfo dataBean) {
            Context context = itemView.getContext();
            title.setText(dataBean.getTitle());
            //ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            //int width = layoutParams.width;
            //int height = layoutParams.height;
            //int coverSize = (width > height ? width : height) / 2;
            //LogUtils.d(this,"url -->" + dataBean.getPict_url());
            String cover = dataBean.getCover();
            if (!TextUtils.isEmpty(cover)) {
                String coverPath = UrlUtils.getCoverPath(dataBean.getCover());
                Glide.with(context).load(coverPath).into(this.coverIv);
            }else{
                coverIv.setImageResource(R.mipmap.ic_launcher);
            }
            //LogUtils.d(this,coverPath);
            String originalPrice = dataBean.getFinalPrice();
            long couponAmount = dataBean.getCouponAmount();
            long sellCount = dataBean.getVolume();
            //LogUtils.d(this,"original price -->" + originalPrice );
            float finalPrice = Float.parseFloat(originalPrice) - couponAmount;
            //LogUtils.d(this,"final price -->" + finalPrice );
            finalPriceTv.setText(String.format("%.2f", finalPrice));
            offPriseTv.setText(String.format(context.getString(R.string.text_goods_off_price), couponAmount));
            originalPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            originalPriceTv.setText(String.format(context.getString(R.string.text_goods_original_price), originalPrice));
            sellCountTv.setText(String.format(context.getString(R.string.text_goods_sell_count), sellCount));
        }
    }


    public void setOnListItemClickListener(OnListItemClickListener listItemClickListener) {
        this.mItemClickListener = listItemClickListener;
    }

    public interface OnListItemClickListener {
        void onItemClick(IBaseInfo item);
    }
}
