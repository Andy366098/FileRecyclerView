package com.runoob.filerecyclerview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.runoob.filerecyclerview.databinding.FragmentFirstBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private String[] login;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });*/
        binding.btnOK.setOnClickListener(listener);
        binding.btnRegister.setOnClickListener(listener);
        binding.btnReset.setOnClickListener(listener);

        binding.btnWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //以下這段可以讓你根據應用程式的包名來開啟外部APP，但在API版本30不管用，因此目前是用29
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.cnn.mobile.android.phone");
                if (intent != null) {
                    // We found the activity now start the activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=" + "com.cnn.mobile.android.phone"));
                    startActivity(intent);
                }
            }
        });
        requestStoragePermission();     //自定義的方法判斷是否取得驗證

    }

    private void requestStoragePermission(){
        if(Build.VERSION.SDK_INT >= 23){        //Android6.0以上
            //判斷是否已取得驗證
            int hasPermission = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if(hasPermission != PackageManager.PERMISSION_GRANTED){     //未取得驗證的話
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }
        }

    }
    //requestPermissions 觸發的事件
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){   //按允許鈕
                //readFile();   //我把它移到按鈕時才觸發
            }else {
                Toast.makeText(getActivity(),"未取得權限！",Toast.LENGTH_SHORT).show();
                getActivity().finish(); //結束應用程式
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    //讀取帳密資料存到login陣列
    private void readFile(){
        String p = getContext().getExternalFilesDir("").getAbsoluteFile().toString() ;//獲取包下的路徑字串
        File path = new File(p); //開啟檔案路徑
        File[] file = path.listFiles();    //列出所有目錄裡的資料放到陣列裡
        login = new String[file.length];    //在此處定義所要存入的陣列大小 否則會因無宣告初始值而閃退
        try{
            for (int i = 0;i < file.length;i++){
                FileInputStream fin = new FileInputStream(file[i]);         //用這個來讀取
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin)); //加速讀取，使其能夠一次讀取一行
                String line = "",wholedata = "";
                while ((line = reader.readLine()) != null){     //當還有資料可以讀入時
                    wholedata = wholedata + line + "\n";        //總資料+行+換行
                }
                login[i] = wholedata;   //把資料存入陣列裡
                reader.close();
                fin.close();

            }

        }catch (Exception e){
            Toast.makeText(getActivity().getApplicationContext(),"error!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case (R.id.btnOK):      //登入按鈕
                    readFile();     //讀取帳密資料存到login陣列
                    //檢查帳號及密碼是否都有輸入
                    if (binding.editAccount.getText().toString().equals("")){
                        Toast.makeText(getActivity(),"帳號不可為空！",Toast.LENGTH_SHORT).show();
                        break;
                    }else if(binding.editPassword.getText().toString().equals("")){
                        Toast.makeText(getActivity(),"密碼不可為空！",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    boolean flag = false;
                    for(int i = 0;i < login.length;i++){
                        String[] data = login[i].split("\n");
                        if(binding.editAccount.getText().toString().equals(data[0])){    //帳號存在
                            flag = true;
                            if (binding.editPassword.getText().toString().equals(data[1])){ //密碼正確
                                new AlertDialog.Builder(getActivity())  //跳窗提示訊息
                                        .setTitle("登入")
                                        .setMessage("登入成功！\n歡迎使用本應用程式")
                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //轉換到應用程式起始頁面程式碼置於此處
                                                NavHostFragment.findNavController(FirstFragment.this)
                                                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                                            }
                                        })
                                        .show();
                            }else {
                                Toast.makeText(getActivity().getApplicationContext(),"密碼不正確！",Toast.LENGTH_LONG).show();
                                binding.editPassword.setText("");
                                break;
                            }
                        }
                    }
                    if(!flag){
                        Toast.makeText(getActivity().getApplicationContext(),"不存在的帳號",Toast.LENGTH_LONG).show();
                        binding.editAccount.setText("");
                        binding.editPassword.setText("");
                    }
                    break;
                case (R.id.btnReset):   //重新輸入
                    binding.editAccount.setText("");
                    binding.editPassword.setText("");
                    break;
                case (R.id.btnRegister):    //註冊按鈕
                    readFile();

                    //檢查帳號及密碼是否都有輸入
                    if (binding.editAccount.getText().toString().equals("")){
                        Toast.makeText(getActivity(),"帳號不可為空！",Toast.LENGTH_SHORT).show();
                        break;
                    }else if(binding.editPassword.getText().toString().equals("")){
                        Toast.makeText(getActivity(),"密碼不可為空！",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    boolean flagHaveA = false;
                    for(int i = 0;i < login.length;i++){
                        String[] data = login[i].split("\n");
                        if(binding.editAccount.getText().toString().equals(data[0])) {    //帳號存在
                            Toast.makeText(getActivity().getApplicationContext(), "此帳號已存在", Toast.LENGTH_LONG).show();
                            flagHaveA = true;
                            break;
                        }
                    }
                    if(!flagHaveA){
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"); //存入資料夾的格式
                        String date = sDateFormat.format(new Date(System.currentTimeMillis()));
                        File path = getContext().getExternalFilesDir("").getAbsoluteFile();     //路徑為外部儲存的包下
                        File file = new File(path, binding.editAccount.getText().toString() + "_login.txt");   //前面為路徑後面為檔名
                        try {
                            FileOutputStream fout = new FileOutputStream(file,true);        //用這個來寫入
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fout));//用這個來加速，否則原本只能寫入以byte為單位的資料
                            writer.write(binding.editAccount.getText().toString());
                            writer.write("\n");     //換行
                            writer.write(binding.editPassword.getText().toString());
                            writer.write("\n");
                            writer.write(date);
                            writer.write("\n");
                            writer.close();
                            fout.close();
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("註冊成功！")
                                    .setMessage("帳號：" + binding.editAccount.getText().toString() +
                                            "\n密碼：" + binding.editPassword.getText().toString() +
                                            "\n註冊日期：" + date )
                                    .show();
                        }catch (Exception e){
                            Toast.makeText(getActivity().getApplicationContext(),"未成功註冊",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}