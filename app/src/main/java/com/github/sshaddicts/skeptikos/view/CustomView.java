package com.github.sshaddicts.skeptikos.view;

import com.github.sshaddicts.neuralclient.data.ProcessedData;

public interface CustomView {
    void receiveData(ProcessedData data);
}
