package com.github.sshaddicts.skeptikos.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.sshaddicts.skeptikos.R;
import com.github.sshaddicts.skeptikos.model.AppDataManagerHolder.AppDataManager;

public class LogInFragment extends Fragment {
    private LoginInteractionListener mListener;

    private TextView info;
    private TextView account;
    private boolean isRegistrationRequested = false;
    Button button;

    public LogInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        info = (TextView) view.findViewById(R.id.loginInfoText);
        account = (TextView) view.findViewById(R.id.loginRegister);
        button = (Button) view.findViewById(R.id.loginConnectButton);

        final EditText usernameField = (EditText) view.findViewById(R.id.loginUsernameEdit);
        final EditText passwordField = (EditText) view.findViewById(R.id.loginPasswordEdit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                onCredentialsEntered(username, password, isRegistrationRequested);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRegistrationRequested){
                    account.setText(R.string.label_no_account_message);
                    info.setText(R.string.welcoming_message);
                    button.setText(R.string.connect_button_text);
                }else{
                    account.setText(R.string.label_misstap_message);
                    info.setText(R.string.register_message);
                    button.setText(R.string.register_button_text);
                }

                isRegistrationRequested = !isRegistrationRequested;
            }
        });

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode){
                    case KeyEvent.KEYCODE_BACK:{
                        info.setText(R.string.welcoming_message);
                        button.setText(R.string.connect_button_text);
                        return true;
                    }
                    default: return false;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    public void onCredentialsEntered(String username, String password, boolean register) {
        try {
            AppDataManager.getClient().setUsername(username);
            AppDataManager.getClient().setPassword(password);

            if (register) {
                AppDataManager.getClient().registerClient();
            } else {
                AppDataManager.getClient().authenticateClient();
            }

            if (mListener != null) {
                mListener.onAuthenticated();
            }
        } catch (Throwable e) {
            info.setText(R.string.incorrect_credentials_message);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LoginInteractionListener) {
            mListener = (LoginInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement LoginInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface LoginInteractionListener {
        void onAuthenticated();
    }
}
