package com.example.nyan.streamoutputnav;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import static java.lang.System.out;

/**
 * Created by NYAN on 09.07.2019.
 */

public class SnapshotFragment extends Fragment{
    private String IP_address="";
    private String login="";
    private String password="";
    private String textIP_address;
    private String TAG ="MyTag";
    private Bundle bundle= null;
    private MyObjectAuthorization authorization;
    private boolean createDilog = false;

    String login_temp = "";
    String password_temp = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        return inflater.inflate(R.layout.fragment_snapshot,null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            final MainActivity activity = (MainActivity) getActivity();
            activity.setTitle("Скриншот");
            Button buttonSnapshot = view.findViewById(R.id.buttonSnapshot);
            final TextView textIP = view.findViewById(R.id.textIP);

            //если IP уже вводили
            if (!IP_address.equals("")) {
                textIP.setText(IP_address);
            }

            buttonSnapshot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
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
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String IP_address_auth = textIP.getText().toString();
                                    String login_auth;
                                    String password_auth;
                                    login_auth = textLogin.getText().toString();
                                    password_auth = textPassword.getText().toString();

                                    login_temp = login_auth;
                                    password_temp = password_auth;

                                    //создадим объект Parcelable для передачи логина и пароля
                                    MyObjectAuthorization myObject = new MyObjectAuthorization(IP_address_auth, login_auth, password_auth);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("auth", myObject);
                                    Intent intent = activity.getIntent();
                                    intent.putExtras(bundle);

                                    if (login_auth.equals("") && password_auth.equals("")) {
                                        stringURL.append("http://").append(IP_address_auth).append("/jpg/image.jpg");
                                    } else {
                                        stringURL.append("http://").append(login).append(":").append(password).append("@")
                                                .append(IP_address_auth).append("/jpg/image.jpg");
                                    }
                                    new DownloadImageTask((ImageView) view.findViewById(R.id.imageView)).execute(stringURL.toString());
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
                            textIP.setText(IP_address);
                            stringURL.append("http://").append(IP_address).append("/jpg/image.jpg");

                            login_temp = login;
                            password_temp = password;
//                            if (login.equals("") && password.equals("")) {
//                                stringURL.append("http://").append(IP_address).append("/jpg/image.jpg");
//                            } else {
//                                stringURL.append("http://").append(login).append(":").append(password).append("@")
//                                        .append(IP_address).append("/jpg/image.jpg");
//                            }
                            new DownloadImageTask((ImageView) view.findViewById(R.id.imageView)).execute(stringURL.toString());
                        }
                    }
                }
            });
        }
    }

    //поток для получения скриншота
    class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage){
            this.bmImage = bmImage;
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlDisplay = strings[0];

            Bitmap bitmap = null;
            if(!(login_temp.equals("")&&password_temp.equals(""))) {
                try {
                    URLConnection connection = null;
                    try {
                        connection = new URL(urlDisplay).openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection httpConn = (HttpURLConnection) connection;

                    String basicAuth = Base64.getEncoder().encodeToString((login_temp + ":" + password_temp).getBytes());
                    httpConn.setRequestProperty("Authorization", "Basic " + basicAuth);

                    InputStream in = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(in);

                } catch (Exception e) {
                    Log.e("Ошибка передачи", e.getMessage());
                    e.printStackTrace();
                }
            }
            else{
                try {
                    InputStream in = new URL(urlDisplay).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            ImageView imageView = (ImageView)getView().findViewById(R.id.imageView);
            imageView.setImageBitmap(result);

//            bmImage.setImageBitmap(result);
        }
    }
}
