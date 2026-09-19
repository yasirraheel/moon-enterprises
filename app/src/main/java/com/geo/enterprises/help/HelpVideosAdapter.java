package com.geo.enterprises.help;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.enterprises.R;
import com.geo.enterprises.models.HelpVideo;

import java.util.List;

public class HelpVideosAdapter extends RecyclerView.Adapter<HelpVideosAdapter.VideoViewHolder> {

    private List<HelpVideo> videos;
    private OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(HelpVideo video);
    }

    public HelpVideosAdapter(List<HelpVideo> videos, OnVideoClickListener listener) {
        this.videos = videos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_help_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        HelpVideo video = videos.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvVideoType, tvViewCount;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_video_title);
            tvVideoType = itemView.findViewById(R.id.tv_video_type);
            tvViewCount = itemView.findViewById(R.id.tv_view_count);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onVideoClick(videos.get(position));
                }
            });
        }

        public void bind(HelpVideo video) {
            tvTitle.setText(video.getTitle());
            tvVideoType.setText(video.getVideoType() != null ? video.getVideoType().toUpperCase() : "VIDEO");
            tvViewCount.setText(video.getViewCount() + " views");
        }
    }
}
