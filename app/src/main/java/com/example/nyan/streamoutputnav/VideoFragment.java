package com.example.nyan.streamoutputnav;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;

/**
 * Created by NYAN on 07.07.2019.
 */

public class VideoFragment extends android.support.v4.app.Fragment {

    private Bundle bundle= null;
    private MyObjectAuthorization authorization;
    private boolean createDilog = false;

    private String IP_address="";
    private String login="";
    private String password="";
    private String textIP_address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        if(getActivity().getIntent().getParcelableExtra("auth")!=null){
            bundle = getActivity().getIntent().getExtras();
            authorization = bundle.getParcelable("auth");
            IP_address = authorization.getIP_address();
            login = authorization.getLogin();
            password = authorization.getPassword();
        }
        else{
            createDilog = true;
        }

        return inflater.inflate(R.layout.fragment_video,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity()!=null) {
            final MainActivity activity = (MainActivity) getActivity();
            activity.setTitle("Видеонаблюдение");
            final EditText textIP = view.findViewById(R.id.textIP);
            Button setConnection = view.findViewById(R.id.buttonSetConnect);
            final VideoView myVideoView = view.findViewById(R.id.videoView);

            if(!IP_address.equals("")){
                textIP.setText(IP_address);
            }
            setConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textIP_address = textIP.getText().toString();
                    boolean validate = activity.validateIP_address(textIP_address);
                    if(!validate){
                        Toast toast = Toast.makeText(activity,"Неверный ip-адрес",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                    else {
                        final StringBuilder stringURL = new StringBuilder();
                        if (!IP_address.equals(textIP_address)) {
                            createDilog = true;
                        }
                        if (createDilog) {
                            LayoutInflater layoutInflater = LayoutInflater.from(activity);
                            View authView = layoutInflater.inflate(R.layout.toast_authorization, null);

                            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(activity);
                            mDialogBuilder.setView(authView);
                            final EditText textLogin = authView.findViewById(R.id.textLogin);
                            final EditText textPassword = authView.findViewById(R.id.textPassword);

                            mDialogBuilder.setCancelable(false).setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    IP_address = textIP.getText().toString();
                                    login = textLogin.getText().toString();
                                    password = textPassword.getText().toString();

                                    //создадим объект Parcelable для передачи логина и пароля
                                    authorization = new MyObjectAuthorization(IP_address, login, password);
                                    bundle = new Bundle();
                                    bundle.putParcelable("auth", authorization);
                                    Intent intent = getActivity().getIntent();
                                    intent.putExtras(bundle);

                                    if (login.equals("") && password.equals("")) {
                                        stringURL.append("rtsp://").append(IP_address).append("/axis-media/media.amp");
                                    } else {
                                        stringURL.append("rtsp://").append(login)
                                                .append(":").append(password)
                                                .append("@").append(IP_address).append("/axis-media/media.amp");
                                    }
//                                    stringURL.append("rtsp://").append(login).append(":").append(password).append("@")
//                                            .append(IP_address).append("/mpeg4/media.amp");
                                    myVideoView.setVideoURI(Uri.parse(stringURL.toString()));
                                    //индикатор загрузки
                                    activity.setLoadIndicator(myVideoView);
                                    //myVideoView.start();
                                }
                            }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog alertDialog = mDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            //IP_address = textIP.getText().toString();
                            if (login.equals("") && password.equals("")) {
                                stringURL.append("rtsp://").append(IP_address).append("/axis-media/media.amp");
                            } else {
                                stringURL.append("rtsp://").append(login)
                                        .append(":").append(password)
                                        .append("@").append(IP_address).append("/axis-media/media.amp");
                            }
                            myVideoView.setVideoURI(Uri.parse(stringURL.toString()));
                            //индикатор загрузки
                            activity.setLoadIndicator(myVideoView);
                           // myVideoView.start();
                        }
                    }
                }
            });
        }
    }



}
