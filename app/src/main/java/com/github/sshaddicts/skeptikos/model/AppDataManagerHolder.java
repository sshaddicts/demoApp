package com.github.sshaddicts.skeptikos.model;

public class AppDataManagerHolder implements DataManager {

    public NeuralSwarmClient nsClient;

    public static class AppDataManager {
        private static final AppDataManagerHolder INSTANCE = new AppDataManagerHolder();

        public static void setNeuralSwarmClient(NeuralSwarmClient neuralSwarmClient) {
            INSTANCE.setNeuralClient(neuralSwarmClient);
        }

        public static AppDataManagerHolder getInstance() {
            return INSTANCE;
        }

        public static NeuralSwarmClient getClient(){
            return INSTANCE.nsClient;
        }
    }

    private AppDataManagerHolder(){}

    private void setNeuralClient(NeuralSwarmClient client) {
        this.nsClient = client;
    }
}
