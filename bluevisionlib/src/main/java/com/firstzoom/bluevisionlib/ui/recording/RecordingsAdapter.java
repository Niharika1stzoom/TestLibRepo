package com.firstzoom.bluevisionlib.ui.recording;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.firstzoom.bluevisionlib.R;
import com.firstzoom.bluevisionlib.databinding.ListItemCameraInfoBinding;
import com.firstzoom.bluevisionlib.databinding.ListItemRecordingBinding;
import com.firstzoom.bluevisionlib.model.CameraInfo;
import com.firstzoom.bluevisionlib.util.BlueVisionUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.List;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder> {
    ListItemRecordingBinding mBinding;
    Context mContext;
    private List<String> mItemList;
    CameraInfo cameraInfo;

    public RecordingsAdapter(Context context, CameraInfo info) {
        mContext = context;
        cameraInfo = info;
    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mBinding = ListItemRecordingBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new RecordingViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position) {
        String item = mItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        if (mItemList == null) {
            return 0;
        }
        return mItemList.size();

    }

    public void setList(List<String> itemList) {
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public List<String> getList() {
        return mItemList;
    }

    public class RecordingViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public ListItemRecordingBinding mBinding;

        public RecordingViewHolder(@NonNull ListItemRecordingBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().findViewById(R.id.full_screen).setOnClickListener(this);
        }


        public void bind(String item) {
            if (item != null) {
                mBinding.itemName.setText(BlueVisionUtil.getFileName(item));
            }
            setVideoUrl(mBinding.thumbnail, item);
        }

        @Override
        public void onClick(View view) {
            String item = mItemList.get(getAbsoluteAdapterPosition());
            navigateVideo(view, item);
        }

        private void navigateVideo(View view, String fileName) {
            Navigation.findNavController(view).navigate(R.id.exoPlayerActivity,
                    BlueVisionUtil.getBundleUrl(fileName));
        }
    }

    private void setVideoUrl(StyledPlayerView playerView, String url) {
        ExoPlayer player = BlueVisionUtil.getExoPlayer(mContext);
        String filePath = url;
        playerView.setPlayer(player);
        Uri uri = Uri.parse(filePath);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
        // player.play();
    }
}

