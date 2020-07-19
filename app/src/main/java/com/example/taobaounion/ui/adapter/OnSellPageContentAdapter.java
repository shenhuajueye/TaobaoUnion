package com.example.taobaounion.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.Paint.STRIKE_THRU_TEXT_FLAG;

public class OnSellPageContentAdapter extends RecyclerView.Adapter<OnSellPageContentAdapter.InnerHolder> {

    private List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData = new ArrayList<>();
    private OnSellPageItemClickListener mContentItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_sell_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //绑定数据
        OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean = mData.get(position);
        holder.setData(mapDataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentItemClickListener != null) {
                    mContentItemClickListener.onSellItemClick(mapDataBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(OnSellContent result) {
        this.mData.clear();
        this.mData.addAll(result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
//        LogUtils.d(this,"mData size is --> " + mData.size());
        notifyDataSetChanged();
    }

    /**
     * 加载更多内容
     *
     * @param moreResult
     */
    public void onMoreLoaded(OnSellContent moreResult) {
        List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> moreData = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
        //原数据的长度
        int oldDataSize = this.mData.size();

        this.mData.addAll(moreData);
        ToastUtils.showToast("成功加载了"+ moreData.size() + "个宝贝。");
        notifyItemRangeChanged(oldDataSize - 1, moreData.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.on_sell_cover)
        public ImageView cover;

        @BindView(R.id.on_sell_content_title_tv)
        public TextView titleTv;

        @BindView(R.id.on_sell_original_price_tv)
        public TextView originalPriceTv;

        @BindView(R.id.on_sell_off_price_tv)
        public TextView offPriceTv;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void setData(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean data) {
            titleTv.setText(data.getTitle());
//            LogUtils.d(this,"pic url -->" + data.getPict_url());
            String coverPath = UrlUtils.getCoverPath(data.getPict_url());
            Glide.with(cover.getContext()).load(coverPath).into(cover);
            String originalPrice = data.getZk_final_price();
            originalPriceTv.setText("￥" + originalPrice + " ");
            originalPriceTv.setPaintFlags(STRIKE_THRU_TEXT_FLAG);
            int couponAmount = data.getCoupon_amount();
            float originalPriceFloat = Float.parseFloat(originalPrice);
            float finalPrice = originalPriceFloat - couponAmount;
            offPriceTv.setText(String.format("券后价:%.2f", finalPrice));
        }
    }


    public void setOnSellPageItemClickListener(OnSellPageItemClickListener listener){
        this.mContentItemClickListener = listener;
    }
    public interface OnSellPageItemClickListener{
        void onSellItemClick(IBaseInfo data);
    }
}
