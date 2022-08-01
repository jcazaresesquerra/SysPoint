package com.app.syspoint.ui.empleados;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.db.bean.EmpleadoBean;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListaEmpleados extends RecyclerView.Adapter<AdapterListaEmpleados.Holder> implements Filterable {

    private List<EmpleadoBean> mData;
    private List<EmpleadoBean> mDataFilter;
    private OnItemClickListener onItemClickListener;


    public AdapterListaEmpleados(List<EmpleadoBean> mData, OnItemClickListener onItemClickListener) {
        this.mData = mData;
        this.mDataFilter = mData;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_empleados, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mDataFilter.get(position),onItemClickListener);
    }

    @Override
    public int getItemCount() {

        return mDataFilter.size()>0 ? mDataFilter.size() : 0;
    }

    public void setEmpleados(List<EmpleadoBean> data) {
        this.mDataFilter = data;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filtro = constraint.toString();

                if (filtro.isEmpty()){
                    mDataFilter = mData;
                }else {

                    //TODO filtro productos
                    List<EmpleadoBean> filtroEmpleados = new ArrayList<>();

                    for (EmpleadoBean row : mData){
                        if (row.nombre.toLowerCase().contains(filtro) || row.identificador.toLowerCase().contains(filtro) ){
                            filtroEmpleados.add(row);
                        }
                    }
                    mDataFilter = filtroEmpleados;
                }

                FilterResults results = new FilterResults();
                results.values = mDataFilter;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataFilter = (ArrayList<EmpleadoBean>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textViewNombre;
        TextView textViewID;
        CircleImageView circleImageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            this.textViewNombre = itemView.findViewById(R.id.textView_lista_empleado_nombre);
            this.textViewID = itemView.findViewById(R.id.textView_lista_empleado_identificador);
            this.circleImageView = itemView.findViewById(R.id.img_empleado);

        }

        public void bind (EmpleadoBean empleado, final OnItemClickListener onItemClickListener){
            this.textViewNombre.setText(""+ empleado.getNombre());
            this.textViewID.setText("" + empleado.getIdentificador());

            if (empleado.getPath_image() != null){
                byte[] decodedString = Base64.decode(empleado.getPath_image(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                circleImageView.setImageBitmap(decodedByte);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }

    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
