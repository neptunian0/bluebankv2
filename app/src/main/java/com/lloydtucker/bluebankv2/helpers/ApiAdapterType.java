package com.lloydtucker.bluebankv2.helpers;

import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;

/**
 * Created by lloydtucker on 17/10/2016.
 */

public enum ApiAdapterType {
    BLUE(0, "Blue Bank", new BlueBankApiAdapter());
    //GREEN(1, "Green Bank", new GreenBankApiAdapter()),
    //MONZO(2, "Monzo", new MonzoApiAdapter());

    private final String name;
    private final int index;
    public static final int count = ApiAdapterType.values().length;
    public final ApiAdapter apiAdapter;

    ApiAdapterType(int index, String name, ApiAdapter apiAdapter){
        this.name = name;
        this.index = index;
        this.apiAdapter = apiAdapter;
    }

    public String getName(){
        return name;
    }

    public int getIndex(){
        return index;
    }
}
