package com.example.nyan.streamoutputnav;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by NYAN on 11.07.2019.
 */

public class SplitScreenFragment extends Fragment {

    private String IP_address="";
    private String login="";
    private String password="";

    VideoView videoViewOne = null;
    VideoView videoViewTwo = null;
    VideoView videoViewThree = null;
    VideoView videoViewFour = null;

    MainActivity mainActivity = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //при возвращении на вкладку видео воспроизводится
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        addVideoStateChanged(videoViewOne,"cameraOne");
        addVideoStateChanged(videoViewTwo,"cameraTwo");
        addVideoStateChanged(videoViewThree,"cameraThree");
        addVideoStateChanged(videoViewFour,"cameraFour");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_split_screen,null);
    }
    boolean isChecked = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Полиэкран");

        videoViewOne = view.findViewById(R.id.videoOne);
        videoViewTwo = view.findViewById(R.id.videoTwo);
        videoViewThree = view.findViewById(R.id.videoThree);
        videoViewFour = view.findViewById(R.id.videoFour);
        final Switch switchMode = view.findViewById(R.id.switchMode);

        videoViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideo(videoViewOne,"cameraOne");
            }
        });
        videoViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideo(videoViewTwo,"cameraTwo");
            }
        });
        videoViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideo(videoViewThree,"cameraThree");
            }
        });
        videoViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideo(videoViewFour,"cameraFour");
            }
        });

        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //меняем цвет рамки(белый-красный)
                if(b){
                    Toast toast = Toast.makeText(getActivity(),"Режим 'Просмотр'",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();

                    getView().findViewById(R.id.linearCameraOne).setBackground(getResources().getDrawable(R.drawable.red_line));
                    getView().findViewById(R.id.linearCameraTwo).setBackground(getResources().getDrawable(R.drawable.red_line));
                    getView().findViewById(R.id.linearCameraThree).setBackground(getResources().getDrawable(R.drawable.red_line));
                    getView().findViewById(R.id.linearCameraFour).setBackground(getResources().getDrawable(R.drawable.red_line));
                }
                else{
                    Toast toast = Toast.makeText(getActivity(),"Режим 'Добавление'",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();

                    getView().findViewById(R.id.linearCameraOne).setBackground(getResources().getDrawable(R.drawable.white_line));
                    getView().findViewById(R.id.linearCameraTwo).setBackground(getResources().getDrawable(R.drawable.white_line));
                    getView().findViewById(R.id.linearCameraThree).setBackground(getResources().getDrawable(R.drawable.white_line));
                    getView().findViewById(R.id.linearCameraFour).setBackground(getResources().getDrawable(R.drawable.white_line));
                }
                isChecked = b ? true : false;
            }
        });
    }

    //метод для возвращения состояния видепотоков, которые уже работали
    private void addVideoStateChanged(final VideoView videoView, String parcelableName){
        if(getActivity().getIntent().getParcelableExtra(parcelableName)!=null) {
            StringBuilder stringURL = new StringBuilder();
            Bundle bundle = getActivity().getIntent().getExtras();
            MyObjectAuthorization myObject = bundle.getParcelable(parcelableName);
            IP_address = myObject.getIP_address();
            login = myObject.getLogin();
            password = myObject.getPassword();
            if (login.equals("") && password.equals("")) {
                stringURL.append("rtsp://").append(IP_address).append("/axis-media/media.amp");
            } else {
                stringURL.append("rtsp://").append(login)
                        .append(":").append(password)
                        .append("@").append(IP_address).append("/axis-media/media.amp");
            }
            videoView.setVideoURI(Uri.parse(stringURL.toString()));
            //индикатор загрузки
            mainActivity.setLoadIndicator(videoView);
        }
    }

    private void addVideo(final VideoView videoView, final String parcelableName){
        final StringBuilder stringURL = new StringBuilder();
        //для режима "Добавление", открывает всплывающее окно для ввода Ip, логина и пароля
        if(!isChecked) {
            final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View authView = layoutInflater.inflate(R.layout.new_video_split_screen, null);

            //всплывающее окно
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
            mDialogBuilder.setView(authView);
            final EditText textIP_address = authView.findViewById(R.id.textIP_address);
            final EditText textLogin = authView.findViewById(R.id.textLogin);
            final EditText textPassword = authView.findViewById(R.id.textPassword);

            mDialogBuilder.setCancelable(false).setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    IP_address = textIP_address.getText().toString();
                    login = textLogin.getText().toString();
                    password = textPassword.getText().toString();

                    if (login.equals("") && password.equals("")) {
                        stringURL.append("rtsp://").append(IP_address).append("/axis-media/media.amp");
                    } else {
                        stringURL.append("rtsp://").append(login)
                                .append(":").append(password)
                                .append("@").append(IP_address).append("/axis-media/media.amp");
                    }
                    videoView.setVideoURI(Uri.parse(stringURL.toString()));
                    //индикатор загрузки
                    mainActivity.setLoadIndicator(videoView);
//                    videoView.start();

                    MyObjectAuthorization myObjectAuthorization = new MyObjectAuthorization(IP_address,login,password);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(parcelableName, myObjectAuthorization);
                    Intent intent = getActivity().getIntent();
                    intent.putExtras(bundle);
//                    stringURLforView = stringURL.toString();
                }
            }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.show();
        }
        //для режима "Просмотр", открывает видеопоток во всплывающем окне
        else {
            if(getActivity().getIntent().getParcelableExtra(parcelableName)!=null) {
                Bundle bundle = getActivity().getIntent().getExtras();
                MyObjectAuthorization myObject = bundle.getParcelable(parcelableName);
                IP_address = myObject.getIP_address();
                login = myObject.getLogin();
                password = myObject.getPassword();

                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View cameraView = layoutInflater.inflate(R.layout.camera_view, null);
                final VideoView cameraVideoView = cameraView.findViewById(R.id.cameraView);

                if (login.equals("") && password.equals("")) {
                    stringURL.append("rtsp://").append(IP_address).append("/axis-media/media.amp");
                } else {
                    stringURL.append("rtsp://").append(login)
                            .append(":").append(password)
                            .append("@").append(IP_address).append("/axis-media/media.amp");
                }
                cameraVideoView.setVideoURI(Uri.parse(stringURL.toString()));
//                cameraVideoView.start();

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
                mDialogBuilder.setView(cameraView);
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                //индикатор загрузки
                mainActivity.setLoadIndicator(cameraVideoView);
            }
            else{
                Toast toast = Toast.makeText(getActivity(),"Выбрано пустое окно",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }

}
