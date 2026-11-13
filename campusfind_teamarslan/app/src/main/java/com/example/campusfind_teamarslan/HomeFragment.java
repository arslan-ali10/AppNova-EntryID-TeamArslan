
package com.example.campusfind_teamarslan;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    ListView lvItems;
    TextView tvEmpty;

    ArrayAdapter<String> adapter;
    ArrayList<String> itemRows;

    DatabaseHelper databaseHelper;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        lvItems = view.findViewById(R.id.lvItems);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        databaseHelper = new DatabaseHelper(requireContext());

        itemRows = new ArrayList<>();

        adapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                itemRows
        ) {

            @NonNull
            @Override
            public View getView(
                    int position,
                    @Nullable View convertView,
                    @NonNull ViewGroup parent) {

                View row = super.getView(
                        position,
                        convertView,
                        parent
                );

                String[] parts = getItem(position).split("\n", 2);

                TextView title = row.findViewById(android.R.id.text1);
                TextView details = row.findViewById(android.R.id.text2);

                title.setText(parts[0]);
                details.setText(parts.length > 1 ? parts[1] : "");

                title.setTextSize(16);
                title.setTypeface(null, android.graphics.Typeface.BOLD);

                details.setTextSize(13);

                return row;
            }
        };

        lvItems.setAdapter(adapter);

        lvItems.setEmptyView(tvEmpty);

        loadItems();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter != null) {
            loadItems();
        }
    }

    private void loadItems() {

        itemRows.clear();

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT item_code, status, item_name, category, " +
                        "location, date_reported " +
                        "FROM items " +
                        "ORDER BY date_reported DESC, id DESC",
                null
        );

        while (cursor.moveToNext()) {

            String code = cursor.getString(0);
            String status = cursor.getString(1);
            String name = cursor.getString(2);
            String category = cursor.getString(3);
            String location = cursor.getString(4);
            String date = formatDate(cursor.getString(5));

            if (code == null) {
                code = "CF-" + cursor.getPosition();
            }

            String firstLine =
                    code + " | " + status + " | " + name;

            String secondLine =
                    category + " • " + location + " • " + date;

            itemRows.add(firstLine + "\n" + secondLine);
        }

        cursor.close();

        adapter.notifyDataSetChanged();
    }

    private String formatDate(String date) {

        try {

            SimpleDateFormat input =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            SimpleDateFormat output =
                    new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            Date parsedDate = input.parse(date);

            if (parsedDate != null) {
                return output.format(parsedDate);
            }

        } catch (ParseException e) {
            return date;
        }

        return date;
    }
}
