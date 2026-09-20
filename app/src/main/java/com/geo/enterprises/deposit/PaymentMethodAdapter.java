package com.geo.enterprises.deposit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.models.PaymentMethod;

import java.util.List;

public class PaymentMethodAdapter extends ArrayAdapter<PaymentMethod> {
    
    private final Context context;
    private final List<PaymentMethod> methods;
    private final boolean showPlaceholder;
    
    public PaymentMethodAdapter(@NonNull Context context, List<PaymentMethod> methods, boolean showPlaceholder) {
        super(context, R.layout.spinner_withdrawal_method_item, methods);
        this.context = context;
        this.methods = methods;
        this.showPlaceholder = showPlaceholder;
    }
    
    @Override
    public int getCount() {
        // Add 1 for placeholder if needed
        return showPlaceholder ? methods.size() + 1 : methods.size();
    }
    
    @Nullable
    @Override
    public PaymentMethod getItem(int position) {
        if (showPlaceholder && position == 0) {
            return null; // Placeholder item
        }
        int actualPosition = showPlaceholder ? position - 1 : position;
        return methods.get(actualPosition);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent, R.layout.spinner_withdrawal_method_item);
    }
    
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent, R.layout.spinner_withdrawal_method_dropdown);
    }
    
    private View createView(int position, View convertView, ViewGroup parent, int layoutResource) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.ivMethodIcon);
            holder.textView = convertView.findViewById(R.id.tvMethodName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        PaymentMethod method = getItem(position);
        
        if (method == null) {
            // Placeholder item - hide the image
            holder.textView.setText("Select Payment Method");
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setText(method.getBankOrAccountName());
            
            // Load image with Glide
            if (method.getBankImage() != null && !method.getBankImage().isEmpty()) {
                String imageUrl = method.getBankImage();
                
                android.util.Log.d("PaymentMethodAdapter", "Loading spinner image: " + imageUrl);
                
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_orders)
                        .error(R.drawable.ic_orders)
                        .circleCrop() // Make it circular for better appearance
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_orders);
            }
        }
        
        return convertView;
    }
    
    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
