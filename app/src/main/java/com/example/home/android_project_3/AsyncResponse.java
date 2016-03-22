package com.example.home.android_project_3;

import java.util.ArrayList;

/**
 * Created by Home on 3/22/16.
 */
public interface AsyncResponse {
    void processFinish(Integer output);
    void processFinish(ArrayList<Pet> output);
}
