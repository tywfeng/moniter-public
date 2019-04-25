package com.tyw.moniter.main.ui;


import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyw.moniter.main.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkOrderFragment extends Fragment {


    public WorkOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workorder, container, false);
        findViews(view);
        return view;
    }

    // bind controls
    private void findViews(View _rootView)
    {
        _rootView.findViewById(R.id.btn_workorder_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 提交提示
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setMessage("工单已提交(模拟提交)")
                                .setPositiveButton(R.string.ok,null)
                                .show();
                    }
                });
    }
}
