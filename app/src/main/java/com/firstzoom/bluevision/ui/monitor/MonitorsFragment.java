package com.firstzoom.bluevision.ui.monitor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.hardware.Camera;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firstzoom.bluevision.MainActivity;
import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.databinding.FragmentMonitorsBinding;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;
import com.firstzoom.bluevision.util.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MonitorsFragment extends Fragment {

    private FragmentMonitorsBinding binding;
    private ItemAdapter mAdapter;
    private MonitorsViewModel mViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if (SharedPrefUtils.getUser(getActivity().getApplicationContext()) == null ) {
            NavHostFragment.findNavController(getParentFragment()).popBackStack();
            NavHostFragment.findNavController(getParentFragment()).navigate(R.id.loginFragment);
            return null;
        }
        mViewModel=new ViewModelProvider(getActivity()).get(MonitorsViewModel.class);
        binding = FragmentMonitorsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initRecyclerView();
        displayLoader();
        getList();
        if(mViewModel.getList().getValue()==null) {
            mViewModel.getMonitors();
        }
        String s="https://bluevision.page.link/?link="+"http://www.1stzoom.com/?url=disposafe.1stzoom.com&token=jwtToken"+
                "&apn="+ getContext().getApplicationContext().getPackageName()+
                "&st="+getContext().getString(R.string.app_name)+
                "&sd="+
                "&si=";
        Log.d(AppConstants.TAG,"url "+s);
        return root;
    }

    void showList(List<CameraInfo> camerasList){
        mAdapter.setList(camerasList);
    }

    @Override
    public void onResume() {
        super.onResume();

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
                    }
                });
    }


    private void initRecyclerView() {
        mAdapter =new ItemAdapter(getContext()){
            @Override
            public void refreshCamera(CameraInfo info){
                AppUtil.showSnackbar(binding.recyclerView,"Refreshing");
                mViewModel.refreshCamera(info);
            }
        };
        binding.recyclerView.setAdapter(mAdapter);
        binding.swiperefresh.setOnRefreshListener(
                () -> {
                    mViewModel.getMonitors();}
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
                List<CameraInfo> filteredlist= mViewModel.filter(query);
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