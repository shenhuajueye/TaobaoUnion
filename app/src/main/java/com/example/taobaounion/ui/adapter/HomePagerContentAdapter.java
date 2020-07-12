package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
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
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePagerContentAdapter extends RecyclerView.Adapter<HomePagerContentAdapter.InnerHolder> {
    private List<HomePagerContent.DataBean> data = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogUtils.d(this,"onCreateViewHolder...");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //LogUtils.d(this,"onBindViewHolder..." + position);
        HomePagerContent.DataBean dataBean = data.get(position);
        //设置数据
        holder.setData(dataBean);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        data.clear();
        data.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<HomePagerContent.DataBean> contents) {
        //添加之前拿到原来数据的size
        int olderSize = data.size();
        data.addAll(contents);
        //更新UI
        notifyItemRangeChanged(olderSize, contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.goods_cover)
        public ImageView cover;

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

        public void setData(HomePagerContent.DataBean dataBean) {
            Context context = itemView.getContext();
            title.setText(dataBean.getTitle());
            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            int coverSize = (width > height ? width : height) / 2;
            //LogUtils.d(this,"url -->" + dataBean.getPict_url());
            String coverPath = UrlUtils.getCoverPath(dataBean.getPict_url(), coverSize);
            //LogUtils.d(this,coverPath);
            Glide.with(context).load(coverPath).into(cover);
            String originalPrice = dataBean.getZk_final_price();
            long couponAmount = dataBean.getCoupon_amount();
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
}
