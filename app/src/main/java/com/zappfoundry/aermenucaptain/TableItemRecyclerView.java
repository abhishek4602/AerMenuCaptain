package com.zappfoundry.aermenucaptain;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TableItemRecyclerView extends RecyclerView.Adapter<TableItemRecyclerView.RecyclerViewHolder> {
    List<String> tableList;
    Context context;
    List<CardView>cardViewList = new ArrayList<>();List<CardView>notifViewList = new ArrayList<>();
    Boolean isFirstTime=true;
    IAttachOrderItems iAttachOrderItems;
    public TableItemRecyclerView(List<String> rcvdTableList, Context receivedContext,IAttachOrderItems iAttachOrderItemsrcvd){
        this.tableList=rcvdTableList;
        this.context=receivedContext;
        this.iAttachOrderItems=iAttachOrderItemsrcvd;
    }
    @NonNull
    @Override
    public TableItemRecyclerView.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view= layoutInflater.inflate(R.layout.table_item,null);
        final TableItemRecyclerView.RecyclerViewHolder rvHolder=new TableItemRecyclerView.RecyclerViewHolder(view);
        return rvHolder;
    }
    public void showNotification(int position,int count) {
        // orderItemList.remove(position);
        CardView cardView=notifViewList.get(position-1);
        cardView.setVisibility(View.VISIBLE);
        ViewGroup viewGroup = ((ViewGroup)cardView.getChildAt(0));
        //  ((View)viewGroup.getChildAt(0)).setBackgroundColor(Color.parseColor("#EFEFEF"));
        ((TextView)viewGroup.getChildAt(0)).setText(""+count);

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        // notifyItemRemoved(position);


        //notifyDataSetChanged();
    }

    public void destroyNotification(int position){
        CardView cardView=notifViewList.get(position);
        cardView.setVisibility(View.GONE);
    }
    @Override
    public void onBindViewHolder(@NonNull final TableItemRecyclerView.RecyclerViewHolder holder, final int position) {
        final String tableNumber =tableList.get(position);
        holder.tableNumber.setText(String.valueOf( tableNumber ));
        if(isFirstTime)
           {
            if(position==0)
            {
                holder.cardViewParent.setBackgroundColor(Color.parseColor("#1c2833"));
                holder.tableNumber.setTextColor(Color.parseColor(("#FFFFFF" )));
                holder.cardViewNotif.setVisibility(View.GONE);
            }
            isFirstTime=false;
           }
        if (!cardViewList.contains(holder.cardViewParent)) {
            cardViewList.add(holder.cardViewParent);
            notifViewList.add(holder.cardViewNotif);
        }
        holder.cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iAttachOrderItems.ReloadMenu(tableNumber);
                destroyNotification(position);
                for(CardView cardView : cardViewList){
                   cardView.setBackgroundColor(Color.parseColor("#EFEFEF"));
                    ViewGroup viewGroup = ((ViewGroup)cardView.getChildAt(0));
                  //  ((View)viewGroup.getChildAt(0)).setBackgroundColor(Color.parseColor("#EFEFEF"));
                    ((TextView)viewGroup.getChildAt(0)).setTextColor(Color.parseColor("#818080"));
                }
                holder.cardViewParent.setBackgroundColor(Color.parseColor("#1c2833"));
                holder.tableNumber.setTextColor(Color.parseColor(("#FFFFFF" )));
                holder.cardViewNotif.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView notifCount,tableNumber;View view;
        CardView cardViewParent,cardViewNotif;
      //  public RelativeLayout viewBackground, viewForeground;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            notifCount=itemView.findViewById(R.id.tvNotif);
            tableNumber=itemView.findViewById(R.id.tvTableNumber);
            cardViewParent=itemView.findViewById(R.id.cardViewTableNumber);
              view=itemView.findViewById(R.id.view);
            cardViewNotif=itemView.findViewById(R.id.cardViewNotif);
           /* viewBackground=itemView.findViewById(R.id.relativeLayoutBG);

            viewForeground=itemView.findViewById(R.id.relativeLayoutFG);*/

        }


    }
}
