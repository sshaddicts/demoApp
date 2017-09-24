package com.github.sshaddicts.skeptikos.model;

public class AppDataManagerHolder {

    public CustomHttpClient nsClient;

    public static class AppDataManager {
        private static final AppDataManagerHolder INSTANCE = new AppDataManagerHolder();

        public static void setNeuralSwarmClient(CustomHttpClient neuralSwarmClient) {
            INSTANCE.setNeuralClient(neuralSwarmClient);
        }

        public static AppDataManagerHolder getInstance() {
            return INSTANCE;
        }

        public static CustomHttpClient getClient(){
            return INSTANCE.nsClient;
        }
    }

    private AppDataManagerHolder(){}

    private void setNeuralClient(CustomHttpClient client) {
        this.nsClient = client;
    }
}
