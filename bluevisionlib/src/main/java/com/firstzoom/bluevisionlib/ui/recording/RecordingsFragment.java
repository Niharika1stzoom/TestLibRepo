package com.firstzoom.bluevisionlib.ui.recording;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firstzoom.bluevisionlib.R;
import com.firstzoom.bluevisionlib.databinding.FragmentRecordingsBinding;
import com.firstzoom.bluevisionlib.model.CameraInfo;
import com.firstzoom.bluevisionlib.model.SliderTime;

import com.firstzoom.bluevisionlib.util.AppConstants;
import com.firstzoom.bluevisionlib.util.AppUtil;
import com.firstzoom.bluevisionlib.util.DateUtil;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.util.Date;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecordingsFragment extends Fragment {

    private FragmentRecordingsBinding binding;
    private RecordingsAdapter mAdapter;
    private RecordingsViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mViewModel = new ViewModelProvider(this).get(RecordingsViewModel.class);
        binding = FragmentRecordingsBinding.inflate(inflater, container, false);
        if (getArguments() != null && getArguments().containsKey(AppConstants.KEY_CAMERA_INFO)) {
            CameraInfo cameraInfo = (CameraInfo) getArguments().getSerializable(AppConstants.KEY_CAMERA_INFO);
            mViewModel.setCamera(cameraInfo);
        }
        View root = binding.getRoot();
        initRecyclerView();
        displayLoader();
        getList();
        setSlider();
        binding.dateButton.setOnClickListener(view -> {
            showDateSelection();
        });
        if (mViewModel.getList().getValue() == null && mViewModel.getCameraInfo() != null) {
            mViewModel.getRecordings();
        }
        mViewModel.getDate().observe(getViewLifecycleOwner(), date -> {
            binding.dateButton.setText(AppUtil.getDisplayDate(date));
        });
        mViewModel.getSliderTime().observe(getViewLifecycleOwner(), sliderTime -> {
            binding.rangeSlider.setValues(new Float[]{Float.valueOf(DateUtil.getTimeInMins(sliderTime.getStart())), Float.valueOf(DateUtil.getTimeInMins(sliderTime.getEnd()))});
        });

        return root;
    }

    private void getList() {
        mViewModel.getList()
                .observe(getViewLifecycleOwner(), recordingInfo -> {
                    hideLoader();
                    if (binding.swiperefresh.isRefreshing())
                        binding.swiperefresh.setRefreshing(false);
                    if (recordingInfo == null || recordingInfo.getRecordings() == null || recordingInfo.getRecordings().size() == 0) {
                        if (!AppUtil.isNetworkAvailableAndConnected(getContext()))
                            AppUtil.showSnackbar2(getView(), ((AppCompatActivity) getActivity()).findViewById(R.id.nav_view), getString(R.string.network_err));
                        displayEmptyView();
                    } else {
                        hideEmptyView();
                        mAdapter.setList(recordingInfo.getRecordings());
                       // mViewModel.getDurationVideo().observe(getViewLifecycleOwner(),duration-> Log.d(AppConstants.TAG,"Duration is"+duration));
                        mViewModel.getDuration();

                    }
                });
    }

    public void showDateSelection() {
        MaterialDatePicker datePicker = AppUtil.getDatePicker();
        datePicker.show(getParentFragmentManager(), "Select Date");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            mViewModel.setDate(new Date(datePicker.getHeaderText()));
            mViewModel.getRecordings();
            displayLoader();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle();
    }

    private void setTitle() {
        if (mViewModel.getCameraInfo() != null) {
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                ((AppCompatActivity) getActivity()).getSupportActionBar()
                        .setTitle(AppUtil.capitalize(mViewModel.getCameraInfo().getName()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.pausePlayers(binding.recyclerView);
    }

    void setSlider() {
        binding.rangeSlider.setLabelBehavior(LabelFormatter.LABEL_VISIBLE);
       // binding.rangeSlider.setStepSize();
        binding.rangeSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int hours = (int) Math.floor(Math.round(value) / 60);
                int mins = (int) (Math.round(value) % 60);
                if(hours==23 && mins==59)
                   return "24:00";
                else
                return hours + ":" + mins;
            }
        });
        binding.rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                SliderTime temp=new SliderTime(DateUtil.getDateFromMins(slider.getValues().get(0),mViewModel.getDate().getValue()),
                        DateUtil.getDateFromMins(slider.getValues().get(1),mViewModel.getDate().getValue()));
                mViewModel.setSliderTime(temp);
                mAdapter.setList(mViewModel.getFilterByTimeList());
            }
        });

    }

    private void initRecyclerView() {
        mAdapter = new RecordingsAdapter(getContext(), mViewModel.getCameraInfo());
        binding.recyclerView.setAdapter(mAdapter);
        binding.swiperefresh.setOnRefreshListener(
                () -> {
                    mViewModel.getRecordings();
                }
        );

    }

    private void displayEmptyView() {
        //hideLoader();
        binding.swiperefresh.setVisibility(View.GONE);
        binding.rangeSlider.setVisibility(View.GONE);
        binding.viewEmpty.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        //hideLoader();
        binding.group.setVisibility(View.VISIBLE);
        binding.viewEmpty.setVisibility(View.GONE);
    }

    private void hideLoader() {
        binding.viewLoader.rootView.setVisibility(View.GONE);
        binding.group.setVisibility(View.VISIBLE);
    }

    private void displayLoader() {
        binding.viewLoader.rootView.setVisibility(View.VISIBLE);
        binding.group.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        mViewModel.releasePlayers(binding.recyclerView);
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // below line is to call set on query text listener method.
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                List<String> filteredlist = mViewModel.filter(query);
                if (!searchView.isIconified())
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
  