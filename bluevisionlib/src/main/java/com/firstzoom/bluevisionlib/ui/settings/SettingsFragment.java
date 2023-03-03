package com.firstzoom.bluevisionlib.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firstzoom.bluevisionlib.MainActivity;
import com.firstzoom.bluevisionlib.R;
import com.firstzoom.bluevisionlib.databinding.FragmentSettingsBinding;
import com.firstzoom.bluevisionlib.model.User;
import com.firstzoom.bluevisionlib.util.AppUtil;
import com.firstzoom.bluevisionlib.util.SharedPrefUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.share.setOnClickListener(view -> {
            AppUtil.share(getContext());
        });
        binding.help.setOnClickListener(view -> {
            AppUtil.openBrowser(getContext());
        });

        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        binding.signout.setOnClickListener(view ->
                {
                    SharedPrefUtils.delUser(getContext().getApplicationContext());
                    SharedPrefUtils.delToken(getContext().getApplicationContext());
                    //SharedPrefUtils.delUrl(getContext().getApplicationContext());
                    navigate();
                });
        setDetails();
        return root;
    }

    private void setDetails() {
        User user=SharedPrefUtils.getUser(getActivity().getApplicationContext());
        binding.email.setText("Email:  "+ user.getEmail());
        binding.role.setText("Role:  "+ user.getRole());
        binding.name.setText("Name:  "+ user.getFirstName());
    }

    private void navigate() {
        getActivity().finish();
        Intent intent=new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
      //  NavHostFragment.findNavController(this).popBackStack();
       // NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}