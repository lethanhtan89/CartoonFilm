package tan.le.cartoonfilm.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.squareup.haha.perflib.Main;

import javax.inject.Inject;

import butterknife.ButterKnife;
import tan.le.cartoonfilm.utils.DialogUtils;

public abstract class BaseActivity extends AppCompatActivity {
    @Inject
    public Context contextDagger;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApplication) getApplication()).getAppComponent().inject(this);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        initView();
    }

    public MainApplication getMainApplication() {
        if (this.getApplication() instanceof MainApplication) {
            MainApplication vebraryApplication = (MainApplication) this.getApplication();
            return vebraryApplication;
        } else
            return null;
    }

    protected abstract int getLayoutResource();

    protected abstract void initView();

    public void showProgressDialog() {
        hideProgressDialog();
        progressDialog = DialogUtils.showLoadingDialog(this);
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }
}
