package com.github.sshaddicts.skeptikos.model;

import android.util.Base64;

import com.github.sshaddicts.neuralclient.AuthenticatedClient;
import com.github.sshaddicts.neuralclient.Client;
import com.github.sshaddicts.neuralclient.ConnectedClient;
import com.github.sshaddicts.neuralclient.data.ProcessedData;
import com.github.sshaddicts.neuralclient.encoding.Base64Coder;
import com.github.sshaddicts.skeptikos.fragments.CustomView;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DeferredObject;

import java.lang.reflect.Field;

import rx.functions.Action1;
import ws.wamp.jawampa.WampClient;

public class NeuralSwarmClient {
    private Client client;

    private String username;
    private String password;

    private final CustomView view;

    private Deferred<AuthenticatedClient, Void, Void> deferredClient = new DeferredObject<>();

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public NeuralSwarmClient(CustomView view) {
        this.view = view;
        this.client = new Client("ws://neuralswarm.sshaddicts.ml/", "api",new Base64Coder() {
            @Override
            public String encode(byte[] bytes) {
                byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
                return new String(encode);
            }

            @Override
            public byte[] decode(String s) {
                return Base64.decode(s, Base64.DEFAULT);
            }
        });
    }

    private Action1<Throwable> defaultExceptionHandler = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {throwable.printStackTrace();}
    };

    public void registerClient() {
        client.getConnected().subscribe(new Action1<ConnectedClient>() {
            @Override
            public void call(ConnectedClient connectedClient) {
                connectedClient.register(username, password).subscribe(new Action1<AuthenticatedClient>() {
                    @Override
                    public void call(AuthenticatedClient authenticatedClient) {
                        deferredClient.resolve(authenticatedClient);
                    }
                }, defaultExceptionHandler);
            }
        }, defaultExceptionHandler);
    }

    public void authenticateClient() {
        client.getConnected().subscribe(new Action1<ConnectedClient>() {
            @Override
            public void call(ConnectedClient connectedClient) {
                connectedClient.auth(username, password).subscribe(new Action1<AuthenticatedClient>() {
                    @Override
                    public void call(AuthenticatedClient authenticatedClient) {
                        deferredClient.resolve(authenticatedClient);
                    }
                }, defaultExceptionHandler);
            }
        }, defaultExceptionHandler);
    }

    public void requestImageProcessing(final byte[] data, final int width, final int height) {
        System.out.println("imageProcessingStarted");
        deferredClient.then(new DoneCallback<AuthenticatedClient>() {
            @Override
            public void onDone(AuthenticatedClient result) {
                result.processImage(data, width, height).subscribe(
                            new Action1<ProcessedData>() {
                            @Override
                            public void call(ProcessedData processedData) {
                                view.receiveData(processedData);
                                System.out.println("imageProcessingFinished");
                            }
                        },
                        defaultExceptionHandler
                );
            }
        });
    }

    public void connect() throws NoSuchFieldException, IllegalAccessException {
        Field wamp = Client.class.getDeclaredField("wamp");

        wamp.setAccessible(true);

        WampClient cl = (WampClient) wamp.get(client);

        cl.statusChanged().subscribe(new Action1<WampClient.State>() {
            @Override
            public void call(WampClient.State state) {
                System.out.println("State: " + state);
            }
        });

        client.connect();
    }
}
