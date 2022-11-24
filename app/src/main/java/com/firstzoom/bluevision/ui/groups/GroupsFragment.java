package com.firstzoom.bluevision.ui.groups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.databinding.FragmentGroupsBinding;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.Group;
import com.firstzoom.bluevision.ui.monitor.ItemAdapter;

import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;
    private GroupAdapter mAdapter;
    private GroupsViewModel mViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mViewModel=new ViewModelProvider(this).get(GroupsViewModel.class);
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initRecyclerView();
        displayLoader();
        getList();
        if(mViewModel.getList().getValue()==null) {
            mViewModel.getMonitorGroups();
        }
        return root;
    }

    void showList(List<Group> camerasList){
        mAdapter.setList(camerasList);
    }

    private void getList() {
        mViewModel.getList()
                .observe(getViewLifecycleOwner(), list -> {
                    hideLoader();
                    if(binding.swiperefresh.isRefreshing())
                        binding.swiperefresh.setRefreshing(false);
                    if (list == null || list.size() == 0) {
                        if (!AppUtil.isNetworkAvailableAndConnected(getContext()))
                            AppUtil.showSnackbar2(getView(),((AppCompatActivity) getActivity()).findViewById(R.id.nav_view), getString(R.string.network_err));
                        displayEmptyView();
                    } else {
                        hideEmptyView();
                        mAdapter.setList(list);

                        //  else
                        //    mAdapter.setList(mViewModel.getLocationFilterList(mViewModel.getLocationsValue()
                        //          .get(mViewModel.getLocationPositionValue()-1)));
                    }
                });
    }


    private void initRecyclerView() {
        mAdapter =new GroupAdapter(getContext());
        binding.recyclerView.setAdapter(mAdapter);
        binding.swiperefresh.setOnRefreshListener(
                () -> {
                    mViewModel.getMonitorGroups();}
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
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.search_menu, menu);
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
                List<Group> filteredlist= mViewModel.filter(query);
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