package com.github.sshaddicts.skeptikos.fragments;


import okhttp3.Response;

public interface CustomView {
    void receiveData(Response data);
    void receiveError(Throwable error);
    void onAuthorized();
}
