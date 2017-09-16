package com.github.sshaddicts.skeptikos.model;

import com.github.sshaddicts.neuralclient.Client;

public class AppDataManager implements DataManager {

    public Client neuralSwarmClient;

    public static class AppDataManagerBuilder{
        public static final AppDataManager INSTANCE = new AppDataManager();
        public static void setNeuralSwarmClient(Client neuralSwarmClient){
            INSTANCE.setDbHelper(neuralSwarmClient);
        }

        public static AppDataManager build(){
            return INSTANCE;
        }
    }

    public static AppDataManager getInstance(){
        return AppDataManagerBuilder.INSTANCE;
    }

    private void setDbHelper(Client client){
        this.neuralSwarmClient = client;
    }
}
