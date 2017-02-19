package com.lizhe.devtools.itemview;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.R;
import com.lizhe.devtools.accessibility.PermissionRequestMgr;
import com.lizhe.devtools.accessibility.Utils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by lz on 2/18/17.
 */

public class ItemDragAdapter extends RecyclerView.Adapter<ItemDragAdapter.Holder> implements OnItemCallbackListener {

    private List<ItemModel> mData;
    private Context mContext;

    public ItemDragAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<ItemModel> data) {
        mData = data;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final ItemModel model = mData.get(position);

        holder.iv.setImageResource(model.resIcon);
        holder.tv.setText(model.resTitle);
        if (model.hasIcon) {
            holder.btnOn.setVisibility(View.VISIBLE);
            holder.btnOff.setVisibility(View.VISIBLE);
            holder.btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.doThingsWithAccessibilityCheck(new Runnable() {
                        @Override
                        public void run() {
                            PermissionRequestMgr.getInstance().startRequest(EnumSet.of(model.onType));
                        }
                    });
                }
            });
            holder.btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.doThingsWithAccessibilityCheck(new Runnable() {
                        @Override
                        public void run() {
                            PermissionRequestMgr.getInstance().startRequest(EnumSet.of(model.offType));
                        }
                    });
                }
            });
            holder.itemView.setOnClickListener(null);
        } else {
            holder.btnOn.setVisibility(View.GONE);
            holder.btnOff.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.doThingsWithAccessibilityCheck(new Runnable() {
                        @Override
                        public void run() {
                            switch (model.mainType) {
                                case TYPE_DEVELOPPER_OPTIONS:
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    DevToolsApplication.getContext().startActivity(intent);
                                    break;
                                case TYPE_SYSTEM_SETTINGS:
                                    intent = new Intent(Settings.ACTION_SETTINGS);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    DevToolsApplication.getContext().startActivity(intent);
                                    break;
                                default:
                                    PermissionRequestMgr.getInstance().startRequest(EnumSet.of(model.mainType));
                                    break;
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        /**
         * 在这里进行给原数组数据的移动
         */
        Collections.swap(mData, fromPosition, toPosition);
        /**
         * 通知数据移动
         */
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwipe(int position) {
        /**
         * 原数据移除数据
         */
        mData.remove(position);
        /**
         * 通知移除
         */
        notifyItemRemoved(position);
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        Button btnOn;
        Button btnOff;

        public Holder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            tv = (TextView) itemView.findViewById(R.id.tv);
            btnOn = (Button) itemView.findViewById(R.id.btn_on);
            btnOff = (Button) itemView.findViewById(R.id.btn_off);
        }
    }
}
