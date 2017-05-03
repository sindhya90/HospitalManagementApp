package com.manage.hospital.hmapp.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.utility.ConfigConstant;
import com.manage.hospital.hmapp.utility.FitbitReferences;


public class FitBitDetailsFragment extends Fragment {


    SharedPreferences sharedPref;
    Button btnLogin;

    private CustomTabsClient customTabsClient;
    private CustomTabsIntent customTabsIntent;
    private CustomTabsSession customTabsSession;
    private CustomTabsServiceConnection customTabsServiceConnection;

    Boolean isAuthorized;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_fit_bit_details,container,false);
        btnLogin=(Button)view.findViewById(R.id.fitbit_login_button);


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity());

        customTabsServiceConnection=new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {

                customTabsClient=client;
                customTabsClient.warmup(0L);
                customTabsSession=customTabsClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

                customTabsClient=null;
            }
        };



        CustomTabsClient.bindCustomTabsService(getActivity(), ConfigConstant.PACKAGE_CUSTOM_TAB,customTabsServiceConnection);

        customTabsIntent=new CustomTabsIntent.Builder(customTabsSession)
                .setToolbarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary))
                .setShowTitle(true)
                .build();

        isAuthorized=sharedPref.getBoolean(FitbitReferences.HAS_ACCESS_TOKEN,false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAuthorized){
                    String fitbit_auth_url=ConfigConstant.FITBIT_BASE_URL;
                    customTabsIntent.launchUrl(getActivity(),Uri.parse(fitbit_auth_url));
                }else{
                    Log.d("Log","Already logged in");
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isAuthorized) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        getFragmentManager().popBackStack();
                        return true;
                    }
                    return false;
                }
            });
        }


    }

}