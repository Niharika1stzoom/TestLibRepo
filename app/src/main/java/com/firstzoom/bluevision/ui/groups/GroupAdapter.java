package com.firstzoom.bluevision.ui.groups;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.databinding.ListItemCameraInfoBinding;
import com.firstzoom.bluevision.databinding.ListItemGroupBinding;
import com.firstzoom.bluevision.model.CameraDatabaseSingleton;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.Group;
import com.firstzoom.bluevision.util.AppUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ItemViewHolder> {
    ListItemGroupBinding mBinding;
    Context mContext;
    private List<Group> mItemList;

    public GroupAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mBinding = ListItemGroupBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ItemViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Group item = mItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        if (mItemList == null) {
            return 0;
        }
        return mItemList.size();

    }

    public void setList(List<Group> itemList) {
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public List<Group> getList() {
        return mItemList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private ListItemGroupBinding mBinding;


        public ItemViewHolder(@NonNull ListItemGroupBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().findViewById(R.id.noCameras).setOnClickListener(this);
        }

        public void bind(Group item) {
            if (item != null && item.getName() != null) {
                mBinding.itemName.setText(AppUtil.capitalize(item.getName()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mBinding.itemName.setTooltipText(AppUtil.capitalize(item.getName()));
                }
            }
            if (item != null && item.getDescription() != null) {
                mBinding.itemDescription.setText(AppUtil.capitalize(item.getDescription()));
            }
            if (item != null && item.getFeeds() != null) {
                mBinding.noCameras.setText(String.valueOf(item.getFeeds().size()));
            }
        }

        @Override
        public void onClick(View view) {
            Group item = mItemList.get(getAbsoluteAdapterPosition());
            if(view.getId()==mBinding.noCameras.getId()){
                showCameraNames(CameraDatabaseSingleton.getCameraNames(item.getFeeds()));
            }
            else
                navigateToDetail(item,view);
        }
    }

    private void navigateToDetail(Group item,View view) {
        Navigation.findNavController(view).navigate
                (R.id.groupDetailFragment, AppUtil.getGroupBundle(item));
    }

    private void showCameraNames(String cameraNames[]) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialoTheme);
        builder.setTitle("Monitoring cameras");
        //String[] arr = new String[cameraInfoList.size()];
        //arr = CameraUtil.getCamerasString(cameraInfoList).toArray(arr);
        builder.setItems(cameraNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //navigateToDetail(cameraInfoList.get(which).getId().toString());
                    }
                })
                .setNeutralButton("Cancel", (dialogInterface, i) -> {
                    // use for loop
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }
}

