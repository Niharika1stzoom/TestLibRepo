package com.firstzoom.bluevision.ui.login;

import static com.firstzoom.bluevision.util.AppConstants.KEY_TOKEN;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.firstzoom.bluevision.MainActivity;
import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.databinding.FragmentLoginBinding;
import com.firstzoom.bluevision.model.User;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;
import com.firstzoom.bluevision.util.BlueVisionUtil;
import com.firstzoom.bluevision.util.SharedPrefUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private String firebaseUrl=null;

    void FullScreenON(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    void FullScreenOff(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreenON();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

       // SharedPrefUtils.delUser(getContext().getApplicationContext());
        //onNewIntent();
        return binding.getRoot();
    }

    private void onNewIntent() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getActivity().getIntent())
                .addOnSuccessListener(getActivity(), new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            try {
                                String token = deepLink.getQueryParameter(KEY_TOKEN);
                                Log.d(AppConstants.TAG,"Got token from link "+token);
                                Log.d(AppConstants.TAG,"Got url from link"+deepLink.getAuthority());
                                if (deepLink.getBooleanQueryParameter(
                                        KEY_TOKEN, false)) {
                                    token = deepLink.getQueryParameter(KEY_TOKEN);
                                    if (!TextUtils.isEmpty(token)) {
                                        String url=BlueVisionUtil.validateBaseUrl(deepLink.getAuthority());
                                        if(url==null || TextUtils.isEmpty(url)) {
                                            showLoginFailed(R.string.login_failed);
                                            return;

                                        }
                                        showLoadingScreen();
                                        SharedPrefUtils.saveUrl(getContext().getApplicationContext(),url);
                                        SharedPrefUtils.saveToken(getContext().getApplicationContext(),token);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginViewModel.validate(url);
                                            }
                                        }, 1000);
                                    }
                                }
                            }catch (Exception e) {
                                Log.d(AppConstants.TAG, "error in login" + e.toString());
                                showLoginFailed(R.string.login_failed);
                            }
                        }
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(AppConstants.TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void showLoadingScreen() {
        binding.loginGroup.setVisibility(View.GONE);
        binding.appLogoCenter.setVisibility(View.VISIBLE);
    }

    private void navigateApp() {
        NavHostFragment.findNavController(this).popBackStack();
        NavHostFragment.findNavController(this).navigate(R.id.monitorsFragment);
    }


    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        onNewIntent();
       // if(SharedPrefUtils.getToken(getContext().getApplicationContext())!=null && !TextUtils.isEmpty(SharedPrefUtils.getToken(getContext().getApplicationContext())))
        //    loginViewModel.validate();
        Log.d(AppConstants.TAG,"Token is"+SharedPrefUtils.getToken(getContext().getApplicationContext()));
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final EditText urlEditText = binding.url;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        binding.loginQR.setOnClickListener(view1 -> showQRLoginDetail());
        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(),
                new Observer<LoginFormState>() {
                    @Override
                    public void onChanged(@Nullable LoginFormState loginFormState) {
                        if (loginFormState == null) {
                            return;
                        }
                        loginButton.setEnabled(loginFormState.isDataValid());
                        if (loginFormState.getUsernameError() != null) {
                            usernameEditText.setError(getString(loginFormState.getUsernameError()));
                        }
                        if (loginFormState.getPasswordError() != null) {
                            passwordEditText.setError(getString(loginFormState.getPasswordError()));
                        }
                        if (loginFormState.getUrlError() != null) {
                            urlEditText.setError(getString(loginFormState.getUrlError()));
                        }
                    }
                });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }
            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),urlEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        urlEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String url=null;
                    if(urlEditText.getVisibility()==View.VISIBLE) {
                        url = loginViewModel.isUrlValid(urlEditText.getText().toString());
                    }
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), url);
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=null;
                if(urlEditText.getVisibility()==View.VISIBLE) {
                    url = loginViewModel.isUrlValid(urlEditText.getText().toString());
                }
                loadingProgressBar.setVisibility(View.VISIBLE);
                        loginViewModel.login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString(), url);
            }
        });

    }


    private void updateUiWithUser(User model) {
        navigateApp();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            AppUtil.showSnackbarLong(getView(),getString(errorString));
           /* Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();*/
          showLoginScreen();
        }
    }

    private void showLoginScreen() {
        binding.loginGroup.setVisibility(View.VISIBLE);
        binding.loading.setVisibility(View.INVISIBLE);
        binding.appLogoCenter.setVisibility(View.GONE);
    }
    private void showQRLoginDetail(){
        String msg="\t1. On a web browser and \n" +
                "\t    go to your BlueVision web app\n" +
                "\t2. Login entering credentials\n" +
                "\t3. Go to the Subscription tab and \n" +
                "\t4. Scan the QR code \n " +
                "\t    from your mobile\n" +
                "\t5. This will auto login you \n" +
                "\t    to the mobile app. ";
        new MaterialAlertDialogBuilder(getContext(),R.style.AlertDialoTheme)
                .setTitle("Login using QR code")
                .setMessage(msg)
                .setNegativeButton("Cancel", /* listener = */ null)
                .show();
    }

    @Override
    public void onDestroyView() {
        FullScreenOff();
        super.onDestroyView();
        binding = null;
    }
}