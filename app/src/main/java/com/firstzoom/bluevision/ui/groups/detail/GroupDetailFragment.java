package com.firstzoom.bluevision.ui.groups.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.databinding.FragmentGroupDetailBinding;
import com.firstzoom.bluevision.databinding.FragmentRecordingsBinding;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.Group;
import com.firstzoom.bluevision.ui.recording.RecordingsAdapter;
import com.firstzoom.bluevision.ui.recording.RecordingsViewModel;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;

import java.util.List;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupDetailFragment extends Fragment {

    private FragmentGroupDetailBinding binding;
    private GroupDetailAdapter mAdapter;
    private GroupDetailViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mViewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        if (getArguments() != null && getArguments().containsKey(AppConstants.KEY_CAMERA_GROUP)) {
            Group group = (Group) getArguments().getSerializable(AppConstants.KEY_CAMERA_GROUP);
            mViewModel.setGroup(group);
        }
        binding = FragmentGroupDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initRecyclerView();
        displayLoader();
        displayCameras();

        return root;
    }

    void showList(List<CameraInfo> camerasList) {
        mAdapter.setList(camerasList);
    }

    private void displayCameras() {
        hideLoader();
        if (binding.swiperefresh.isRefreshing())
            binding.swiperefresh.setRefreshing(false);
        List list=mViewModel.getList();
        if ( list== null || list.size() == 0) {
            if (!AppUtil.isNetworkAvailableAndConnected(getContext()))
                AppUtil.showSnackbar2(getView(), ((AppCompatActivity) getActivity()).findViewById(R.id.nav_view), getString(R.string.network_err));
            displayEmptyView();
        } else {
            hideEmptyView();
            mAdapter.setList(list);
        }
}

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.pausePlayers(binding.recyclerView);
    }



    @Override
    public void onResume() {
        super.onResume();
        setTitle();
    }


        private void setTitle() {
            if (mViewModel.getGroup()!= null) {
                if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                    ((AppCompatActivity) getActivity()).getSupportActionBar()
                            .setTitle(AppUtil.capitalize(mViewModel.getGroup().getName()));
            }
        }


    private void initRecyclerView() {
        mAdapter = new GroupDetailAdapter(getContext()){
            @Override
            public void refreshCamera(CameraInfo info){
                AppUtil.showSnackbar(binding.recyclerView,"Refreshing");
                mViewModel.refreshCamera(info);
            }
        };
        binding.recyclerView.setAdapter(mAdapter);
        binding.swiperefresh.setOnRefreshListener(
                () -> {

                }
        );

    }

    private void displayEmptyView() {
        //hideLoader();
        binding.recyclerView.setVisibility(View.GONE);
        binding.viewEmpty.emptyContainer.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        //hideLoader();
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.viewEmpty.emptyContainer.setVisibility(View.INVISIBLE);
    }

    private void hideLoader() {
        binding.viewLoader.rootView.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void displayLoader() {
        binding.viewLoader.rootView.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        mViewModel.releasePlayers(binding.recyclerView);
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.actionSpan) {
            GridLayoutManager manager= (GridLayoutManager) binding.recyclerView.getLayoutManager();
            if(manager.getSpanCount()==2) {
                manager.setSpanCount(1);
                item.setIcon(R.drawable.ic_action_linear);
            }
            else{
                manager.setSpanCount(2);
                item.setIcon(R.drawable.ic_action_grid);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.span_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                List<CameraInfo> filteredlist = mViewModel.filter(query);
                if(!searchView.isIconified())
                if (filteredlist.isEmpty()) {
                    Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.setList(filteredlist);
                }
                return false;
            }
        });

    }
}
