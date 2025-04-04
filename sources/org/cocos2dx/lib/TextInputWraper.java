package org.cocos2dx.lib;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.alawar.mutant.jni.MutantMessages;

/* compiled from: Cocos2dxGLSurfaceView.java */
/* loaded from: classes.dex */
class TextInputWraper implements TextWatcher, TextView.OnEditorActionListener {
    private static final Boolean debug = false;
    private Cocos2dxGLSurfaceView mMainView;
    private String mOriginText;
    private String mText;

    private void LogD(String msg) {
        if (debug.booleanValue()) {
            Log.d("TextInputFilter", msg);
        }
    }

    private Boolean isFullScreenEdit() {
        InputMethodManager imm = (InputMethodManager) this.mMainView.getTextField().getContext().getSystemService("input_method");
        return Boolean.valueOf(imm.isFullscreenMode());
    }

    public TextInputWraper(Cocos2dxGLSurfaceView view) {
        this.mMainView = view;
    }

    public void setOriginText(String text) {
        this.mOriginText = text;
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
        if (!isFullScreenEdit().booleanValue()) {
            LogD("afterTextChanged: " + ((Object) s));
            int nModified = s.length() - this.mText.length();
            if (nModified > 0) {
                String insertText = s.subSequence(this.mText.length(), s.length()).toString();
                this.mMainView.insertText(insertText);
                LogD("insertText(" + insertText + ")");
            } else {
                while (nModified < 0) {
                    this.mMainView.deleteBackward();
                    LogD("deleteBackward");
                    nModified++;
                }
            }
            this.mText = s.toString();
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        LogD("beforeTextChanged(" + ((Object) s) + ")start: " + start + ",count: " + count + ",after: " + after);
        this.mText = s.toString();
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (this.mMainView.getTextField() == v && isFullScreenEdit().booleanValue()) {
            for (int i = this.mOriginText.length(); i > 0; i--) {
                this.mMainView.deleteBackward();
                LogD("deleteBackward");
            }
            String text = v.getText().toString();
            if (text.compareTo(MutantMessages.sEmpty) == 0) {
                text = "\n";
            }
            if ('\n' != text.charAt(text.length() - 1)) {
                text = text + '\n';
            }
            String insertText = text;
            this.mMainView.insertText(insertText);
            LogD("insertText(" + insertText + ")");
            return false;
        }
        return false;
    }
}
