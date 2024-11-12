package com.taskapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.taskapp.ui.TaskUI;
import com.taskapp.exception.AppException;
import java.io.IOException;
import com.taskapp.model.User;

public class App {

    public static void main(String[] args) {
        TaskUI ui = new TaskUI();
        ui.displayMenu();
    }
}
