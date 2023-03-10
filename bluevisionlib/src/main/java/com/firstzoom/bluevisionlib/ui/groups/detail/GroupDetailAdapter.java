package com.firstzoom.bluevisionlib.ui.groups.detail;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.firstzoom.bluevisionlib.R;
import com.firstzoom.bluevisionlib.databinding.ListItemGroupDetailBinding;
import com.firstzoom.bluevisionlib.model.CameraInfo;
import com.firstzoom.bluevisionlib.util.AppUtil;
import com.firstzoom.bluevisionlib.util.BlueVisionUtil;
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
            popup.inflate(R.menu.camera_menu);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                   /* case R.id.recordings:
                        navigateRecordings(view, cameraInfo);
                        return true;
                    case R.id.refresh:
                        refreshCamera(cameraInfo);
                        return true;
                    case R.id.feed:
                        navigateVideo(view,cameraInfo);
                        return true;*/
                    default:
                        return false;
                }
            });
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
            playerView.setPlayer(player);
            Uri uri = Uri.parse(filePath);
            MediaItem mediaItem = MediaItem.fromUri(uri);
            player.setMediaItem(mediaItem);
           // player.prepare();
           // player.play();

        }

    }
    public void refreshCamera(CameraInfo info){}
}
