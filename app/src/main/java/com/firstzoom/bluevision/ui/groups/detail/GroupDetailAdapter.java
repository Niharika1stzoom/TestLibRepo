package com.firstzoom.bluevision.ui.groups.detail;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.databinding.ListItemGroupDetailBinding;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;
import com.firstzoom.bluevision.util.BlueVisionUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.List;

public class GroupDetailAdapter extends RecyclerView.Adapter<GroupDetailAdapter.GroupDetailViewHolder> {
    ListItemGroupDetailBinding mBinding;
    Context mContext;
    private List<CameraInfo> mItemList;

    public GroupDetailAdapter(Context context) {
        mContext = context;

    }

    @NonNull
    @Override
    public GroupDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = ListItemGroupDetailBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new GroupDetailViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupDetailViewHolder holder, int position) {
        CameraInfo item = mItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        if (mItemList == null) {
            return 0;
        }
        return mItemList.size();

    }

    public void setList(List<CameraInfo> itemList) {
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public List<CameraInfo> getList() {
        return mItemList;
    }

    public class GroupDetailViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public ListItemGroupDetailBinding mBinding;


        public GroupDetailViewHolder(@NonNull ListItemGroupDetailBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            //binding.getRoot().findViewById(R.id.recordings).setOnClickListener(this);
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().findViewById(R.id.full_screen).setOnClickListener(this);

        }

        public void bind(CameraInfo item) {
            if (item != null && item.getName() != null) {
                mBinding.itemName.setText(AppUtil.capitalize(item.getName()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mBinding.itemName.setTooltipText(AppUtil.capitalize(item.getName()));
                }
            }
            if (item != null && item.getDescription() != null) {
                mBinding.itemDescription.setText(item.getDescription());
            }
            setVideoUrl(mBinding.thumbnail, BlueVisionUtil.getFeedUrl(item, mContext));


        }

        @Override
        public void onClick(View view) {
            CameraInfo item = mItemList.get(getAbsoluteAdapterPosition());
            if (view.getId() == R.id.full_screen)
                navigateVideo(view, item);
            else
                showPopUp(mBinding.thumbnail,item);
        }
        private void showPopUp(View view, CameraInfo cameraInfo) {
            PopupMenu popup = new PopupMenu(mContext, view, Gravity.LEFT ,0,R.style.PopupMenu);
            //inflating menu from xml resource
            popup.inflate(R.menu.camera_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.recordings:
                            navigateRecordings(view, cameraInfo);
                            return true;
                        case R.id.refresh:
                            refreshCamera(cameraInfo);
                            return true;
                        case R.id.feed:
                            navigateVideo(view,cameraInfo);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            //displaying the popup
            popup.show();
        }

        private void navigateRecordings(View view, CameraInfo item) {
            Navigation.findNavController(view).navigate(R.id.recordingsFragment, BlueVisionUtil.getCameraBundle(item));
        }

        private void navigateVideo(View view, CameraInfo item) {
            Navigation.findNavController(view).navigate(R.id.exoPlayerActivity,
                    BlueVisionUtil.getBundleUrl(BlueVisionUtil.getFeedUrl(item, mContext)));
        }

        private void setVideoUrl(StyledPlayerView playerView, String url) {
            // Build a HttpDataSource.Factory with cross-protocol redirects enabled.
            ExoPlayer player = BlueVisionUtil.getExoPlayer(mContext);
            String filePath = url;
            //ExoPlayer player = new ExoPlayer.Builder(mContext).build();
            playerView.setPlayer(player);
            Uri uri = Uri.parse(filePath);
            MediaItem mediaItem = MediaItem.fromUri(uri);
// Set the media item to be played.
            player.setMediaItem(mediaItem);
// Prepare the player.
            player.prepare();
// Start the playback.
            // player.play();
        }

    }
    public void refreshCamera(CameraInfo info){}
}
