package com.aariyan.pickingplan.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.Model.PostModel;
import com.aariyan.pickingplan.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    //private List<PostModel> list;
    private List<PlanModel> list;
    private Context context;

    public PostAdapter(Context context, List<PlanModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_upload_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlanModel model = list.get(position);
        holder.quantity.setText(model.getToLoad());
        holder.itemName.setText(model.getDescription());
        holder.id.setText("" + model.getIntAutoPicking());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView id, itemName, quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.pickingID);
            itemName = itemView.findViewById(R.id.itemNames);
            quantity = itemView.findViewById(R.id.itemQuantity);
        }
    }
}
