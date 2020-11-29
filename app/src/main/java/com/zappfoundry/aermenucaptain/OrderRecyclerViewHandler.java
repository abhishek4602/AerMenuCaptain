package com.zappfoundry.aermenucaptain;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderRecyclerViewHandler extends RecyclerView.Adapter<OrderRecyclerViewHandler.RecyclerViewHolder> {
    List<Order> orderItemList ;
Context context;
    IAttachOrderItems iAttachOrderItems;
    public  OrderRecyclerViewHandler(List<Order> orderItemListrcvd,Context receivedContext,IAttachOrderItems iAttachOrderItemsrcvd)
    {
        this.orderItemList=orderItemListrcvd;
        this.context=receivedContext;
        this.iAttachOrderItems=iAttachOrderItemsrcvd;
    }
    @NonNull
    @Override
    public OrderRecyclerViewHandler.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view= layoutInflater.inflate(R.layout.order_item,null);
        final RecyclerViewHolder rvHolder=new RecyclerViewHolder(view);
        return rvHolder;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerViewHandler.RecyclerViewHolder holder, int position) {
        final Order ordr =orderItemList.get(position);
        if(ordr.isDelivered()){
            holder.viewForegroundInner.setBackgroundColor(Color.parseColor("#F2F3F4"));
            holder.itemName.setTextColor(Color.parseColor("#212F3C"));
            holder.itemCount.setTextColor(Color.parseColor("#212F3C"));
            holder.itemPrice.setTextColor(Color.parseColor("#212F3C"));
            holder.itemTotal.setTextColor(Color.parseColor("#212F3C"));

        }else
        {
            holder.viewForegroundInner.setBackgroundColor(Color.parseColor("#1c2833"));
            holder.itemName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.itemCount.setTextColor(Color.parseColor("#FFFFFF"));
            holder.itemPrice.setTextColor(Color.parseColor("#FFFFFF"));
            holder.itemTotal.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.itemName.setText(String.valueOf( ordr.getItemName()));
        holder.itemCount.setText("Qty : "+String.valueOf(ordr.getItemCount()));
        holder.itemPrice.setText("Price per Plate : Rs "+ordr.getItemPrice());
        holder.itemTotal.setText("Total Amount Rs :"+ordr.getItemTotal());
        try{holder.itemTime.setText(ordr.getTimeStamp());}catch (Exception mam){}
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }
    public void restoreItem(Order item, int position) {
        orderItemList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
    public void removeItem(int position) {
       // orderItemList.remove(position);
        Order o=orderItemList.get(position);
        Log.e("adapterName",o.getItemName());
        Log.e("adapterID",String.valueOf(o.getItemID()));
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
       // notifyItemRemoved(position);

        if(!o.isDelivered()){
            iAttachOrderItems.delivered(o.getItemID());

        }
       // o.setDelivered(true);
        notifyDataSetChanged();
    }



    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,itemCount,itemPrice,itemTotal,itemTime;
        public RelativeLayout viewBackground, viewForeground;
        public LinearLayout viewForegroundInner;
        public CardView cardViewFG,cardViewBG;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.tvItemName);
            itemCount=itemView.findViewById(R.id.tvOrderQuantity);
            viewBackground=itemView.findViewById(R.id.relativeLayoutBG);
            cardViewFG=itemView.findViewById(R.id.cardViewOrderItem);
            viewForeground=itemView.findViewById(R.id.relativeLayoutFG);
            viewForegroundInner=itemView.findViewById(R.id.llfgC);
            itemPrice=itemView.findViewById(R.id.tvPrice);
            itemTotal=itemView.findViewById(R.id.tvItemTotal);
            itemTime=itemView.findViewById(R.id.tvTimeStamp);
        }


    }
}
