
package com.example.campusfind_teamarslan;

import android.content.Intent;
import android.widget.Button;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReportFragment extends Fragment {

    public ReportFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_report,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Button btnReportLost =
                view.findViewById(R.id.btnReportLost);

        Button btnReportFound =
                view.findViewById(R.id.btnReportFound);

        // Report Lost button
        btnReportLost.setOnClickListener(v -> {
            openReportForm("LOST");
        });

        // Report Found button
        btnReportFound.setOnClickListener(v -> {
            openReportForm("FOUND");
        });
    }

    private void openReportForm(String status) {

        Intent intent = new Intent(
                requireContext(),
                ReportItemActivity.class
        );

        intent.putExtra("ITEM_STATUS", status);

        // Get logged-in user's email
        String userEmail = requireActivity()
                .getIntent()
                .getStringExtra("USER_EMAIL");

        intent.putExtra("USER_EMAIL", userEmail);

        startActivity(intent);
    }
}
