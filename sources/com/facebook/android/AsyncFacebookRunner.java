package com.facebook.android;

import android.content.Context;
import android.os.Bundle;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/* loaded from: classes.dex */
public class AsyncFacebookRunner {

    /* renamed from: fb */
    Facebook f2fb;

    public interface RequestListener {
        void onComplete(String str, Object obj);

        void onFacebookError(FacebookError facebookError, Object obj);

        void onFileNotFoundException(FileNotFoundException fileNotFoundException, Object obj);

        void onIOException(IOException iOException, Object obj);

        void onMalformedURLException(MalformedURLException malformedURLException, Object obj);
    }

    public AsyncFacebookRunner(Facebook fb) {
        this.f2fb = fb;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.facebook.android.AsyncFacebookRunner$1] */
    public void logout(final Context context, final RequestListener listener, final Object state) {
        new Thread() { // from class: com.facebook.android.AsyncFacebookRunner.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    String response = AsyncFacebookRunner.this.f2fb.logout(context);
                    if (response.length() == 0 || response.equals("false")) {
                        listener.onFacebookError(new FacebookError("auth.expireSession failed"), state);
                    } else {
                        listener.onComplete(response, state);
                    }
                } catch (FileNotFoundException e) {
                    listener.onFileNotFoundException(e, state);
                } catch (MalformedURLException e2) {
                    listener.onMalformedURLException(e2, state);
                } catch (IOException e3) {
                    listener.onIOException(e3, state);
                }
            }
        }.start();
    }

    public void logout(Context context, RequestListener listener) {
        logout(context, listener, null);
    }

    public void request(Bundle parameters, RequestListener listener, Object state) {
        request(null, parameters, "GET", listener, state);
    }

    public void request(Bundle parameters, RequestListener listener) {
        request(null, parameters, "GET", listener, null);
    }

    public void request(String graphPath, RequestListener listener, Object state) {
        request(graphPath, new Bundle(), "GET", listener, state);
    }

    public void request(String graphPath, RequestListener listener) {
        request(graphPath, new Bundle(), "GET", listener, null);
    }

    public void request(String graphPath, Bundle parameters, RequestListener listener, Object state) {
        request(graphPath, parameters, "GET", listener, state);
    }

    public void request(String graphPath, Bundle parameters, RequestListener listener) {
        request(graphPath, parameters, "GET", listener, null);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.facebook.android.AsyncFacebookRunner$2] */
    public void request(final String graphPath, final Bundle parameters, final String httpMethod, final RequestListener listener, final Object state) {
        new Thread() { // from class: com.facebook.android.AsyncFacebookRunner.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    String resp = AsyncFacebookRunner.this.f2fb.request(graphPath, parameters, httpMethod);
                    listener.onComplete(resp, state);
                } catch (FileNotFoundException e) {
                    listener.onFileNotFoundException(e, state);
                } catch (MalformedURLException e2) {
                    listener.onMalformedURLException(e2, state);
                } catch (IOException e3) {
                    listener.onIOException(e3, state);
                }
            }
        }.start();
    }
}
