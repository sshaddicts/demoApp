package com.github.sshaddicts.skeptikos.model;

import android.util.Base64;

import com.github.sshaddicts.neuralclient.AuthenticatedClient;
import com.github.sshaddicts.neuralclient.Client;
import com.github.sshaddicts.neuralclient.ConnectedClient;
import com.github.sshaddicts.neuralclient.data.ProcessedData;
import com.github.sshaddicts.neuralclient.encoding.Base64Coder;
import com.github.sshaddicts.skeptikos.view.CustomView;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DeferredObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

import rx.functions.Action1;
import ws.wamp.jawampa.WampClient;

public class NeuralSwarmClient {
    private Client client;

    private String username;
    private String password;

    private final CustomView view;

    private Deferred<AuthenticatedClient, Void, Void> deferredClient = new DeferredObject<>();

    public NeuralSwarmClient(String username, String password, CustomView view) {
        this.username = username;
        this.password = password;
        this.view = view;
        client = new Client("ws://neuralswarm.sshaddicts.ml/", "api", new Base64Coder() {
            @NotNull
            @Override
            public String encode(@NotNull byte[] bytes) {
                byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
                return new String(encode);
            }

            @NotNull
            @Override
            public byte[] decode(@NotNull String s) {
                return Base64.decode(s, Base64.DEFAULT);
            }
        });
    }

    public void registerClient() {
        client.getConnected().subscribe(new Action1<ConnectedClient>() {
            @Override
            public void call(ConnectedClient connectedClient) {
                connectedClient.register(username, password).subscribe(new Action1<AuthenticatedClient>() {
                    @Override
                    public void call(AuthenticatedClient authenticatedClient) {
                        deferredClient.resolve(authenticatedClient);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
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
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
            }
        });
        System.out.println("imageProcessingFinished");
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
