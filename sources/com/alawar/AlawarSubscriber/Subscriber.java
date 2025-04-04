package com.alawar.AlawarSubscriber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.alawar.mutant.C0019R;

/* loaded from: classes.dex */
public class Subscriber implements View.OnClickListener {
    private static final int DIALOG_INVALID_EMAIL = 1;
    private static final int DIALOG_REQUEST_FAILED = 3;
    private static final int DIALOG_REQUEST_PROGRESS = 2;
    private static final int FLOW_ACCEPTED = 3;
    private static final int FLOW_CHECKING_INPUT = 2;
    private static final int FLOW_NONE = 0;
    private static final int FLOW_WAIT_INPUT = 1;
    private Activity mActivity;
    private EditText mEmailEditText;
    private EditText mNameEditText;
    private ViewGroup mSubscribeGroup;
    private Submitter mSubscriber;
    private View mSubscriberView;
    private ViewGroup mThankYouGroup;
    private SubscriberListener mListener = null;
    private SubscribeTask mSubscribeTask = null;
    private Dialog mProgressDialog = null;
    private int mFlowState = 0;

    public interface SubscriberListener {
        void OnSubscriberHide();

        void OnSubscriberShow();
    }

    public Subscriber(String gameName, Activity activity, ViewGroup rootView) {
        this.mActivity = null;
        this.mSubscriberView = null;
        this.mSubscribeGroup = null;
        this.mThankYouGroup = null;
        this.mNameEditText = null;
        this.mEmailEditText = null;
        this.mSubscriber = null;
        this.mActivity = activity;
        if (gameName == null) {
            gameName = "Unknown";
            Log.w("AlawarSubscriberActivity", "EXTRA_GAMENAME_INFO is not specified.");
        }
        this.mSubscriber = new Submitter(gameName);
        this.mSubscriberView = this.mActivity.getLayoutInflater().inflate(C0019R.layout.alawar_subscriber_layout, rootView, true);
        this.mSubscriberView.setVisibility(4);
        this.mSubscriberView.setOnKeyListener(new View.OnKeyListener() { // from class: com.alawar.AlawarSubscriber.Subscriber.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case 3:
                    case 4:
                        Subscriber.this.hideView();
                        return true;
                    default:
                        return false;
                }
            }
        });
        TextView privacyText = (TextView) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_PrivacyTextView);
        if (privacyText != null) {
            privacyText.setText(Html.fromHtml(this.mActivity.getString(C0019R.string.alawar_subscriber_privacy)));
            privacyText.setMovementMethod(LinkMovementMethod.getInstance());
        }
        Button btn = (Button) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_SubscribeButton);
        if (btn != null) {
            btn.setOnClickListener(this);
        }
        Button btn2 = (Button) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_ThankButton);
        if (btn2 != null) {
            btn2.setOnClickListener(this);
        }
        ImageButton imgbtn = (ImageButton) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_BackButton);
        if (imgbtn != null) {
            imgbtn.setOnClickListener(this);
        }
        this.mSubscribeGroup = (ViewGroup) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_SubscribeLayout);
        this.mSubscribeGroup.setVisibility(0);
        this.mThankYouGroup = (ViewGroup) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_ThankLayout);
        this.mThankYouGroup.setVisibility(8);
        this.mNameEditText = (EditText) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_NameEditText);
        this.mEmailEditText = (EditText) this.mSubscriberView.findViewById(C0019R.id.alawar_subscriber_EmailEditText);
        if (this.mEmailEditText != null) {
            this.mEmailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.alawar.AlawarSubscriber.Subscriber.2
                @Override // android.widget.TextView.OnEditorActionListener
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null && event.getKeyCode() == 66) {
                        InputMethodManager in = (InputMethodManager) Subscriber.this.mActivity.getSystemService("input_method");
                        in.hideSoftInputFromWindow(Subscriber.this.mEmailEditText.getApplicationWindowToken(), 2);
                        Subscriber.this.subscribe();
                        return false;
                    }
                    return false;
                }
            });
        }
        setFlowState(1);
    }

    public void setSubscriberListener(SubscriberListener listener) {
        this.mListener = listener;
    }

    public void showView() {
        if (this.mListener != null) {
            this.mListener.OnSubscriberShow();
        }
        this.mSubscriberView.setVisibility(0);
        this.mSubscriberView.setFocusable(true);
        this.mSubscriberView.setFocusableInTouchMode(true);
        this.mSubscriberView.bringToFront();
    }

    public void hideView() {
        this.mSubscriberView.setVisibility(8);
        this.mSubscriberView.setFocusable(false);
        if (this.mListener != null) {
            if (this.mListener instanceof Activity) {
                Activity activity = (Activity) this.mListener;
                InputMethodManager imm = (InputMethodManager) activity.getSystemService("input_method");
                imm.hideSoftInputFromWindow(this.mNameEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(this.mEmailEditText.getWindowToken(), 0);
            }
            this.mListener.OnSubscriberHide();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case C0019R.id.alawar_subscriber_BackButton /* 2131296256 */:
                hideView();
                break;
            case C0019R.id.alawar_subscriber_SubscribeButton /* 2131296261 */:
                subscribe();
                break;
            case C0019R.id.alawar_subscriber_ThankButton /* 2131296263 */:
                hideView();
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlowState(int s) {
        if (this.mFlowState != s) {
            this.mFlowState = s;
            switch (this.mFlowState) {
                case 1:
                    this.mSubscribeGroup.setVisibility(0);
                    this.mThankYouGroup.setVisibility(8);
                    break;
                case 2:
                case 3:
                    this.mSubscribeGroup.setVisibility(8);
                    this.mThankYouGroup.setVisibility(0);
                    break;
            }
        }
    }

    private boolean checkCredentials() {
        return (this.mEmailEditText == null || this.mEmailEditText.getText().length() == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void subscribe() {
        if (this.mFlowState == 1) {
            setFlowState(2);
            if (checkCredentials()) {
                setFlowState(3);
                this.mSubscribeTask = new SubscribeTask();
                this.mSubscribeTask.execute(this.mEmailEditText.getText().toString(), this.mNameEditText.getText().toString());
                return;
            }
            showDialog(1);
        }
    }

    protected Dialog showDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
                builder.setTitle(C0019R.string.alawar_subscriber_error_title);
                builder.setMessage(C0019R.string.alawar_subscriber_invalid_email_text);
                builder.setPositiveButton(C0019R.string.alawar_subscriber_ok, new DialogInterface.OnClickListener() { // from class: com.alawar.AlawarSubscriber.Subscriber.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int whichButton) {
                        Subscriber.this.setFlowState(1);
                    }
                });
                dialog = builder.create();
                break;
            case 2:
                ProgressDialog pdialog = new ProgressDialog(this.mActivity);
                pdialog.setProgressStyle(0);
                pdialog.setMessage(this.mActivity.getString(C0019R.string.alawar_subscriber_wait));
                pdialog.setIndeterminate(true);
                pdialog.setCancelable(true);
                pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.alawar.AlawarSubscriber.Subscriber.4
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialog2) {
                        Subscriber.this.onSubscriptionCancelled();
                    }
                });
                dialog = pdialog;
                this.mProgressDialog = dialog;
                break;
            case 3:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this.mActivity);
                builder2.setTitle(C0019R.string.alawar_subscriber_error_title);
                builder2.setMessage(C0019R.string.alawar_subscriber_request_failed);
                builder2.setPositiveButton(C0019R.string.alawar_subscriber_ok, new DialogInterface.OnClickListener() { // from class: com.alawar.AlawarSubscriber.Subscriber.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int whichButton) {
                        Subscriber.this.setFlowState(1);
                    }
                });
                dialog = builder2.create();
                break;
        }
        dialog.setOwnerActivity(this.mActivity);
        dialog.show();
        return dialog;
    }

    public void onSubscriptionCancelled() {
        if (this.mSubscribeTask != null) {
            this.mSubscribeTask.cancel(true);
            this.mSubscribeTask = null;
            if (this.mProgressDialog != null) {
                this.mProgressDialog.dismiss();
                this.mProgressDialog = null;
            }
        }
        setFlowState(1);
    }

    public void onSubscriptionCompleted(Boolean result) {
        if (this.mSubscribeTask != null) {
            this.mSubscribeTask = null;
            if (this.mProgressDialog != null) {
                this.mProgressDialog.dismiss();
                this.mProgressDialog = null;
            }
            setFlowState(3);
        }
    }

    private class SubscribeTask extends AsyncTask<String, Integer, Boolean> {
        private SubscribeTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(String... args) {
            if (Subscriber.this.mSubscriber != null) {
                return Subscriber.this.mSubscriber.subscribe(args[0], args[1]);
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            Subscriber.this.onSubscriptionCompleted(result);
        }
    }
}
