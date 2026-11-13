
package com.example.campusfind_teamarslan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );

        TextView tvEmail =
                view.findViewById(R.id.tvProfileEmail);

        Button btnLogout =
                view.findViewById(R.id.btnLogout);

        String email = requireActivity()
                .getIntent()
                .getStringExtra("USER_EMAIL");

        if (email != null) {
            tvEmail.setText(email);
        }

        btnLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireActivity(),
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });

        return view;
    }
}
