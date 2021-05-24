package com.runoob.filerecyclerview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.runoob.filerecyclerview.databinding.FragmentSecondBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private MyListAdapter myListAdapter;
    private ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    private String[] login;
    private int cbVisibility;
    private boolean[] cbHow;//此處新增一個boolean型別的陣列
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //讀入資料與製作列表
        readFile();
        makeData();
        cbHow = new boolean[login.length];      //初始化設置checkbox狀態的陣列
        cbVisibility = View.GONE;                           //初始將checkBox設置為不可見且不保留空間
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        binding.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        binding.mRecyclerView.setAdapter(myListAdapter);
        //下拉刷新
        binding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue_RURI));
        binding.refreshLayout.setOnRefreshListener(()->{
            arrayList.clear();
            readFile();
            makeData();
            myListAdapter.notifyDataSetChanged();
            binding.refreshLayout.setRefreshing(false);
        });
        //浮游刪除按鈕
        binding.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbVisibility == View.GONE){                      //設置切換顯示CheckBox們及確定刪除紐
                    cbVisibility = View.VISIBLE;
                    binding.fabCheckDelete.setVisibility(View.VISIBLE);
                }else if(cbVisibility == View.VISIBLE){
                    cbVisibility = View.GONE;
                    binding.fabCheckDelete.setVisibility(View.INVISIBLE);
                    for(int i = 0;i < cbHow.length;i++){
                        cbHow[i] = false;
                    }
                }
                myListAdapter.notifyDataSetChanged();
            }
        });
        //浮游確定刪除紐
        binding.fabCheckDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String p = getContext().getExternalFilesDir("").getAbsoluteFile().toString() ;  //獲取路徑字串
                                    File path = new File(p); //開啟檔案路徑
                                    File[] file = path.listFiles();    //列出所有目錄裡的資料
                                    for (int i = 0;i < file.length;i++){
                                        if (cbHow[i] == true){
                                            file[i].delete();   //刪除檔案
                                            cbHow[i] = false;   //清空所選取的狀態
                                        }
                                    }
                                }catch (Exception e){
                                    Toast.makeText(getActivity().getApplicationContext(),"未成功刪除檔案",Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                arrayList.clear();
                                readFile();
                                makeData();
                                myListAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNeutralButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //讀取帳密資料存到login陣列
    private void readFile(){
        String p = getContext().getExternalFilesDir("").getAbsoluteFile().toString() ;  //獲取路徑字串
        File path = new File(p); //開啟檔案路徑
        File[] file = path.listFiles();    //列出所有目錄裡的資料
        login = new String[file.length];    //在此處定義陣列大小 否則會閃退
        try{
            for (int i = 0;i < file.length;i++){
                FileInputStream fin = new FileInputStream(file[i]);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
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
    private void makeData() {               //用HashMap來創建列表選項
        for (int i = 0;i<login.length;i++){
            HashMap<String,String> hashMap = new HashMap<>();
            String[] data = login[i].split("\n");
            hashMap.put("Account",data[0]);    //把帳號放到標籤為Account的地方
            hashMap.put("Password",data[1]);
            hashMap.put("Date",data[2]);
            arrayList.add(hashMap);
        }
    }
    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        class ViewHolder extends RecyclerView.ViewHolder{       //可以看成是宣告每一個列表選項所持有的元件
            private TextView tvSub1,tvSub2,tvDate;
            public CheckBox cbSelect;
            private View mView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSub1 = itemView.findViewById(R.id.textView_sub1);
                tvSub2 = itemView.findViewById(R.id.textView_sub2);
                tvDate  = itemView.findViewById(R.id.createDate);
                cbSelect = itemView.findViewById(R.id.cbSelect);
                mView  = itemView;
            }
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {     //由指定的xml創建列表項目
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {        //對列表選項的更動
            holder.tvSub1.setText(arrayList.get(position).get("Account"));
            holder.tvSub2.setText(arrayList.get(position).get("Password"));
            holder.tvDate.setText(arrayList.get(position).get("Date"));
            holder.cbSelect.setVisibility(cbVisibility);
            //in some cases, it will prevent unwanted situations
            holder.cbSelect.setOnCheckedChangeListener(null);//先設定一次CheckBox的選中監聽器，傳入引數null
            //if true, your checkbox will be selected, else unselected
            holder.cbSelect.setChecked(cbHow[position]);//用陣列中的值設定CheckBox的選中狀態
            //再設定一次CheckBox的選中監聽器，當CheckBox的選中狀態發生改變時，把改變後的狀態儲存在陣列中
            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //set your object's last status
                    cbHow[position] = isChecked;
                }
            });
            holder.mView.setOnClickListener((v)->{
                Toast.makeText(getActivity().getBaseContext(),"這個帳號是在" + holder.tvDate.getText() + "時創立的",Toast.LENGTH_SHORT).show();
            });


        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

}