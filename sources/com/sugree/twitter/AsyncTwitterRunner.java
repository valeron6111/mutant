package com.sugree.twitter;

import android.content.Context;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/* loaded from: classes.dex */
public class AsyncTwitterRunner {

    /* renamed from: tw */
    Twitter f297tw;

    public interface RequestListener {
        void onComplete(String str);

        void onFileNotFoundException(FileNotFoundException fileNotFoundException);

        void onIOException(IOException iOException);

        void onMalformedURLException(MalformedURLException malformedURLException);

        void onTwitterError(TwitterError twitterError);
    }

    public AsyncTwitterRunner(Twitter tw) {
        this.f297tw = tw;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.sugree.twitter.AsyncTwitterRunner$1] */
    public void logout(final Context context, final RequestListener listener) {
        new Thread() { // from class: com.sugree.twitter.AsyncTwitterRunner.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    String response = AsyncTwitterRunner.this.f297tw.logout(context);
                    if (response.length() == 0 || response.equals("false")) {
                        listener.onTwitterError(new TwitterError("auth.expireSession failed"));
                    } else {
                        listener.onComplete(response);
                    }
                } catch (FileNotFoundException e) {
                    listener.onFileNotFoundException(e);
                } catch (MalformedURLException e2) {
                    listener.onMalformedURLException(e2);
                } catch (IOException e3) {
                    listener.onIOException(e3);
                }
            }
        }.start();
    }
}
