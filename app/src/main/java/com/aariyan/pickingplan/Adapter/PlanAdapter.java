package com.aariyan.pickingplan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Interface.ToLoadClick;
import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.R;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private Context context;
    private List<PlanModel> list;
    private boolean check = false;
    ToLoadClick toLoadClick;
    private String from;

    public PlanAdapter(Context context, List<PlanModel> list, ToLoadClick toLoadClick, String from) {
        this.context = context;
        this.list = list;
        this.toLoadClick = toLoadClick;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_plan_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlanModel model = list.get(position);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (position == 0) {
                    holder.salesOrderNo.setVisibility(View.VISIBLE);
                    holder.storeName.setVisibility(View.VISIBLE);
                    holder.titleLayout.setVisibility(View.VISIBLE);
                }

                if (position > 0) {
                    if (list.get(position).getStorename().equals(list.get(position - 1).getStorename())) {
                        holder.salesOrderNo.setVisibility(View.GONE);
                        holder.storeName.setVisibility(View.GONE);
                        holder.titleLayout.setVisibility(View.GONE);
                    } else {
                        holder.salesOrderNo.setVisibility(View.VISIBLE);
                        holder.storeName.setVisibility(View.VISIBLE);
                        holder.titleLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
//        if (position == 0) {
//            holder.salesOrderNo.setVisibility(View.VISIBLE);
//            holder.storeName.setVisibility(View.VISIBLE);
//            holder.titleLayout.setVisibility(View.VISIBLE);
//        }
//
//        if (position > 0) {
//            if (list.get(position).getStorename().equals(list.get(position - 1).getStorename())) {
//                holder.salesOrderNo.setVisibility(View.GONE);
//                holder.storeName.setVisibility(View.GONE);
//                holder.titleLayout.setVisibility(View.GONE);
//            } else {
//                holder.salesOrderNo.setVisibility(View.VISIBLE);
//                holder.storeName.setVisibility(View.VISIBLE);
//                holder.titleLayout.setVisibility(View.VISIBLE);
//            }
//        }

        holder.storeName.setText(model.getStorename());
        holder.salesOrderNo.setText(model.getSalesOrderNo());
        holder.lineNo.setText("" + model.getLineNos());
        holder.itemName.setText(model.getDescription());
        holder.qty.setText(model.getQuantity());
        holder.weight.setText(model.getWeights());
//        if (list.contains("toLoad")) {
//            holder.toLoad.setText(model.getToLoad());
//        } else {
//            holder.toLoad.setText("0");
//        }
        if (TextUtils.isEmpty(model.getToLoad())) {
            holder.toLoad.setText("0");
        } else {
            holder.toLoad.setText(model.getToLoad());
        }

        holder.toLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!from.equals(Constant.fromInvoice)) {
                    toLoadClick.onClick(model, position);
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView storeName, salesOrderNo, lineNo, itemName, qty, weight, toLoad;
        private ConstraintLayout titleLayout;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storeName);
            salesOrderNo = itemView.findViewById(R.id.salesOrderNo);
            lineNo = itemView.findViewById(R.id.lineNo);
            itemName = itemView.findViewById(R.id.itemName);
            qty = itemView.findViewById(R.id.qty);
            weight = itemView.findViewById(R.id.weight);
            toLoad = itemView.findViewById(R.id.toLoad);
            titleLayout = itemView.findViewById(R.id.titleLayout);
            view = itemView.findViewById(R.id.view);
        }
    }
}
