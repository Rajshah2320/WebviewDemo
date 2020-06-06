package com.example.webviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private ListView listView;
    ArrayList<String> filePaths;
    ArrayList<String> fileNames;
    ArrayList<File> dirNames;
    private String intFilePath, extFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SecurityHelper securityHelper = new SecurityHelper(this);

        intent = new Intent(MainActivity.this, WebviewActivity.class);

        listView = findViewById(R.id.listView);

        File[] names = getExternalFilesDir("").listFiles();
        fileNames = new ArrayList<>();
        filePaths = new ArrayList<>();
        dirNames = new ArrayList<>();

        intFilePath = getFilesDir().getAbsolutePath();
        extFilePath = getExternalFilesDir("").getAbsolutePath();

        Log.i("FILES", "int file " + intFilePath + " ext file " + extFilePath);

        for (int i = 0; i < names.length; i++) {
            if (names[i].isDirectory()) {
                filePaths.add(names[i].getAbsolutePath());
                fileNames.add(names[i].getName());
                dirNames.add(names[i]);
            }
            Log.i("Files", "name: " + names[i].getName() + " path " + names[i].getAbsolutePath());
        }


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    getFiles(dirNames.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String newPath = intFilePath + filePaths.get(i).substring(62);

                Log.i("NEWPATH", "onItemClick: " + newPath);

                intent.putExtra("path", newPath);
                startActivity(intent);

            }
        });



        /*
        SecurityHelper securityHelper = new SecurityHelper(this);
        Log.i("KEY", "onCreate: " + securityHelper.keyFileExists());
        byte[] bytes = SecurityHelper.decodeFile(getExternalFilesDir("").getAbsoluteFile().getAbsolutePath() + "/test.pdf");

        Log.i("SECURITY HELPER", "onCreate: " + bytes);

        try {
            String path=getExternalFilesDir("").getAbsoluteFile().getAbsolutePath()+"/test.pdf";
            File file=new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(bytes);

            Log.i("FILE", file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }

         */

    }

    private void decryptFile(String path) {

        String intPath = intFilePath + path.substring(getExternalFilesDir("").getAbsolutePath().length());
        byte[] bytes = SecurityHelper.decodeFile(path);
        Log.i("BYTES", "decryptFile: " + bytes + " int path = " + intPath);

        try {
            File file = new File(intPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.close();

            Log.i("FILE decrypted at", intPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFiles(File files) throws IOException {
        if (files.isDirectory()) {
            String intPath = intFilePath + files.getAbsolutePath().substring(getExternalFilesDir("").getAbsolutePath().length());
            File tfile = new File(intPath);
            tfile.mkdir();
            for (File file : files.listFiles()) {

                Log.i("Get file called", file.getAbsolutePath());
                getFiles(file);

            }
        } else {

            decryptFile(files.getAbsolutePath());
            Log.i("Decrypt file called ", files.getAbsolutePath());
        }

    }
}
