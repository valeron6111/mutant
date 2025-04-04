package com.alawar.mutant.network;

import android.util.Log;
import com.alawar.mutant.network.http.RequestExecutedCallback;
import com.alawar.mutant.network.http.RestClient;
import com.alawar.mutant.p000ui.common.ProgressBar;
import com.alawar.mutant.util.DeviceUUID;
import com.alawar.mutant.util.ParametrizedRunnable;
import com.alawar.mutant.util.Signer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class UserService {
    private static final String clientKey = "_client";
    private static int balanceCounter = 0;
    private static int prevBalanceValue = 0;
    public static final UserService instance = new UserService();

    public Coins submitGiftCode(String friendCode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("c", friendCode);
        params.put("u", DeviceUUID.getUuid());
        params.put("sig", Signer.md5(friendCode + "_" + DeviceUUID.getUuid() + clientKey));
        final Coins result = new Coins(0);
        createRequest(MutantUrl.giftCode(), params, new ParametrizedRunnable<String>() { // from class: com.alawar.mutant.network.UserService.1
            @Override // com.alawar.mutant.util.ParametrizedRunnable
            public void run(String val) {
                try {
                    int value = Integer.parseInt(val);
                    if (value >= 0 && value < 1000) {
                        result.amount = value;
                    } else {
                        result.amount = -1;
                    }
                } catch (NumberFormatException e) {
                    result.amount = -1;
                }
            }
        }).setProgress(true).invoke();
        if (result.amount != -1) {
            return result;
        }
        return null;
    }

    public Coins submitPromoCode(String giftCode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("c", giftCode);
        params.put("u", DeviceUUID.getUuid());
        params.put("sig", Signer.md5(giftCode + "_" + DeviceUUID.getUuid() + clientKey));
        final Coins result = new Coins(0);
        createRequest(MutantUrl.giftCode(), params, new ParametrizedRunnable<String>() { // from class: com.alawar.mutant.network.UserService.2
            @Override // com.alawar.mutant.util.ParametrizedRunnable
            public void run(String val) {
                try {
                    int value = Integer.parseInt(val);
                    if (value >= 0 && value < 1000) {
                        result.amount = value;
                    } else {
                        result.amount = -1;
                    }
                } catch (NumberFormatException e) {
                    result.amount = -1;
                }
            }
        }).setProgress(false).invoke();
        if (result.amount != -1) {
            return result;
        }
        return null;
    }

    private void executeRequest(String uri, boolean async, HashMap<String, String> params, ParametrizedRunnable<String> callback) {
        createRequest(uri, async, params, callback).invoke();
    }

    public RequestExecutor createRequest(String uri, boolean async, HashMap<String, String> params, ParametrizedRunnable<String> callback) {
        return new RequestExecutor(uri, async, params, callback);
    }

    public RequestExecutor createRequest(String uri, HashMap<String, String> params, ParametrizedRunnable<String> callback) {
        return new RequestExecutor(uri, params, callback);
    }

    public static class RequestExecutor {
        private boolean async;
        private final ParametrizedRunnable<String> callback;
        private HashMap<String, String> params;
        private boolean progress;
        private String uri;

        public RequestExecutor(String uri, HashMap<String, String> params, ParametrizedRunnable<String> callback) {
            this.async = false;
            this.progress = false;
            this.uri = uri;
            this.params = params;
            this.callback = callback;
        }

        public RequestExecutor(String uri, boolean async, HashMap<String, String> params, ParametrizedRunnable<String> callback) {
            this.async = false;
            this.progress = false;
            this.uri = uri;
            this.async = async;
            this.params = params;
            this.callback = callback;
        }

        public RequestExecutor setAsync(boolean async) {
            this.async = async;
            return this;
        }

        public RequestExecutor setProgress(boolean progress) {
            this.progress = progress;
            return this;
        }

        public void invoke() {
            if (!this.async) {
                ProgressBar.showProgress(true);
            }
            try {
                invokeRequest();
            } finally {
                ProgressBar.showProgress(false);
            }
        }

        private void invokeRequest() {
            final RestClient restClient = new RestClient(this.uri);
            for (Map.Entry<String, String> entry : this.params.entrySet()) {
                restClient.addParam(entry.getKey(), entry.getValue());
            }
            RequestExecutedCallback requestCallback = new RequestExecutedCallback() { // from class: com.alawar.mutant.network.UserService.RequestExecutor.1
                @Override // com.alawar.mutant.network.http.RequestExecutedCallback
                public void onSuccess(RestClient request) {
                    String response = null;
                    if (restClient.hasResponse()) {
                        response = restClient.getResponse().trim();
                    }
                    if (restClient.getResponseCode() == 200 && response != null) {
                        Log.i("UserService", "Got " + response + " coins");
                        RequestExecutor.this.callback.run(response);
                    } else {
                        Log.e("UserService", "Got failure response: " + response);
                        RequestExecutor.this.callback.run(null);
                    }
                }

                @Override // com.alawar.mutant.network.http.RequestExecutedCallback
                public void onFailure(RestClient request) {
                    RequestExecutor.this.callback.run(null);
                }
            };
            if (this.async) {
                restClient.executeAsync(RequestMethod.GET, requestCallback);
            } else {
                restClient.execute(RequestMethod.GET);
                requestCallback.onSuccess(restClient);
            }
        }
    }
}
