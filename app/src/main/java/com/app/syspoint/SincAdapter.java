package com.app.syspoint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.db.bean.LogSyncGetBean;
import com.app.syspoint.utils.ItemAnimation;

import java.util.List;

public class SincAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LogSyncGetBean> mData;
    private int animation_type = 0;
    private int lastPosition = -1;
    private boolean on_attach = true;


    public SincAdapter(List<LogSyncGetBean> mData, int animation_type) {
        this.mData = mData;
        this.animation_type = animation_type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_items_sucesos, parent ,false);

        vh = new OriginalViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  OriginalViewHolder){
            OriginalViewHolder view = (OriginalViewHolder)holder;
            final LogSyncGetBean row = mData.get(position);
            view.textViewSuceso.setText(row.getRecurso());
            view.textViewItems.setText("Registros sincronizados: " + row.getItems());
            setAnimation(view.itemView, position);
        }
    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        TextView textViewSuceso;
        TextView textViewItems;

        public OriginalViewHolder(View v) {
            super(v);
            textViewSuceso = itemView.findViewById(R.id.tv_recurso);
            textViewItems = itemView.findViewById(R.id.tv_registros);
        }
    }

    @Override
    public int getItemCount() {
        if (mData.isEmpty()) {
            return 0;
        }else {
            return mData.size();
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }


    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}
