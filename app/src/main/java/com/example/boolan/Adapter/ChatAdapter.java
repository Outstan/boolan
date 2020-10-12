package com.example.boolan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.beans.Chat;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.WsStatusListener;

import java.util.List;
import java.util.Map;

import okhttp3.Response;
import okio.ByteString;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chat> mMsgList;
    private Context context1;
    private Map<String, String> userdata;

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftMsg = view.findViewById(R.id.left_msg);
            rightMsg = view.findViewById(R.id.right_msg);
        }
    }

    public ChatAdapter(Context context, List<Chat> msgList){
        context1 = context;
        mMsgList = msgList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        userdata = new PreferencesService(context1).getdengluflag();
        Chat msg = mMsgList.get(position);
        if (!msg.getType().equals(userdata.get("id"))){
            //如果是收到消息，则显示在左边，将右边布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());

        }else if(msg.getType().equals(userdata.get("id"))){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }
    @Override
    public int getItemCount(){
        return mMsgList.size();
    }
}
