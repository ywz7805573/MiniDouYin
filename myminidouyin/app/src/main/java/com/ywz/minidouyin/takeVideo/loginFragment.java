package com.ywz.minidouyin.takeVideo;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ywz.minidouyin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class loginFragment extends Fragment {


    private TextView name;
    private TextView id;
    private Button button;

    public loginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        name = v.findViewById(R.id.loginName);
        id = v.findViewById(R.id.loginId);
        button = v.findViewById(R.id.login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyHomeActivity.class);
                intent.putExtra(MyHomeActivity.LOGINID,id.getText().toString());
                intent.putExtra(MyHomeActivity.LOGINNAME,name.getText().toString());
                startActivity(intent);
            }
        });

        return v;
    }

}
