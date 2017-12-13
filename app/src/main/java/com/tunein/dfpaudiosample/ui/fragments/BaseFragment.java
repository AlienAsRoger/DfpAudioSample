package com.tunein.dfpaudiosample.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tunein.dfpaudiosample.application.BaseApplication;
import com.tunein.dfpaudiosample.inject.components.AppComponent;


/**
 * BaseFragment for using Dagger Injections
 */

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(getAppComponent());
    }

    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
    }

    protected AppComponent getAppComponent() {
        return ((BaseApplication) getActivity().getApplication()).getAppComponent();
    }
}
