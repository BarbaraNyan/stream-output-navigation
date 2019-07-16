package com.example.nyan.streamoutputnav;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NYAN on 09.07.2019.
 */

public class MyObjectAuthorization implements Parcelable {

    public String IP_address;
    public String login;
    public String password;

    public MyObjectAuthorization(String IP_address, String login, String password) {
        this.IP_address = IP_address;
        this.login = login;
        this.password = password;
    }

    protected MyObjectAuthorization(Parcel in) {
        IP_address = in.readString();
        login = in.readString();
        password = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(IP_address);
        parcel.writeString(login);
        parcel.writeString(password);
    }
    public String getIP_address(){return IP_address;}
    public String getLogin(){
        return login;
    }
    public String getPassword(){return password;}

    // Статический метод с помощью которого создаем обьект
    public static final Parcelable.Creator<MyObjectAuthorization> CREATOR = new Parcelable.Creator<MyObjectAuthorization>() {
        public MyObjectAuthorization createFromParcel(Parcel in) {
            return new MyObjectAuthorization(in);
        }

        @Override
        public MyObjectAuthorization[] newArray(int size) {
            return new MyObjectAuthorization[size];
        }
    };
}
