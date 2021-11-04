package com.example.csis4175_group4.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class AppViewModel extends AndroidViewModel {

    private boolean loggedIn = false;

    public AppViewModel(Application app){
        super(app);
    }

    public boolean isLoggedIn(){return this.loggedIn;}

    //we need to add some verification
    public void logIn(){
        this.loggedIn = true;
    }

    public void logOut(){
        this.loggedIn = false;
    }
}
