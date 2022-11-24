package com.firstzoom.bluevision.ui.monitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import com.firstzoom.bluevision.databinding.ListItemCameraInfoBinding;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;
import com.firstzoom.bluevision.util.BlueVisionUtil;
import com.firstzoom.bluevision.util.SharedPrefUtils;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    ListItemCameraInfoBinding mBinding;
    Context mContext;
    private List<CameraInfo> mItemList;

    public ItemAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mBinding = ListItemCameraInfoBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ItemViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
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

    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private ListItemCameraInfoBinding mBinding;


        public ItemViewHolder(@NonNull ListItemCameraInfoBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            binding.getRoot().findViewById(R.id.thumbnail).setOnClickListener(this);
            binding.getRoot().getRootView().setOnClickListener(this);
            binding.getRoot().findViewById(R.id.textOptions).setOnClickListener(this);



        }

        public void bind(CameraInfo item) {
            if(item!=null && item.getName()!=null) {
                mBinding.itemName.setText(AppUtil.capitalize(item.getName()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mBinding.itemName.setTooltipText(AppUtil.capitalize(item.getName()));
                }
            }
            if(item!=null && item.getDescription()!=null) {
                mBinding.itemDescription.setText(item.getDescription());
            }
           /* if(item!=null && item.getRecordings()!=null && item.getRecordings().size()>0)
                mBinding.recordings.setVisibility(View.VISIBLE);
            else
                mBinding.recordings.setVisibility(View.GONE);

            */

            Log.d(AppConstants.TAG,"Url is "+item.getImage_url());
            //AppUtil.setImage(mContext,null,mBinding.thumbnail);
            AppUtil.setImage(mContext,item.getImage_url(),mBinding.thumbnail);

        }

        @Override
        public void onClick(View view) {
            CameraInfo item = mItemList.get(getAbsoluteAdapterPosition());
            if(view.getId()==R.id.thumbnail) {
                navigateVideo(view,item);
            }
            else{

                showPopUp(mBinding.thumbnail,item);

            }



        }

        private void showPopUp(View view, CameraInfo cameraInfo) {
            PopupMenu popup = new PopupMenu(mContext, view,Gravity.LEFT ,0,R.style.PopupMenu);
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
            Navigation.findNavController(view).navigate(R.id.recordingsFragment,BlueVisionUtil.getCameraBundle(item));
            //  Navigation.findNavController(view).navigate(R.id.exoPlayerActivity, AppUtil.getBundle(item,false));
        }
        private void navigateVideo( View view,CameraInfo item) {
            Log.d(AppConstants.TAG,"Video Url"+BlueVisionUtil.getFeedUrl(item,mContext));
            Navigation.findNavController(view).navigate(R.id.exoPlayerActivity,
                    BlueVisionUtil.getBundleUrl(BlueVisionUtil.getFeedUrl(item,mContext)));

          //  Navigation.findNavController(view).navigate(R.id.exoPlayerActivity, AppUtil.getBundle(item,false));
        }
    }
    public void refreshCamera(CameraInfo info){}
}

