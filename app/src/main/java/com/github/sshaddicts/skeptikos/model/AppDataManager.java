package com.github.sshaddicts.skeptikos.model;

public class AppDataManager implements DataManager {

    public DbHelper helper;

    public static class AppDataManagerBuilder{
        public static final AppDataManager INSTANCE = new AppDataManager();
        public static void setDbHelper(DbHelper helper){
            INSTANCE.setDbHelper(helper);
        }

        public static AppDataManager build(){
            return INSTANCE;
        }
    }

    public static AppDataManager getInstance(){
        return AppDataManagerBuilder.INSTANCE;
    }

    private void setDbHelper(DbHelper helper){

    }
}
