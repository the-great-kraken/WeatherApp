package com.example.weatherapp.places;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.CityModel;
import com.example.weatherapp.home.HomeFragment;

import java.util.List;

public class PlacelistFragment extends Fragment {

    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_placelist, container, false);

        final NavController navController = NavHostFragment.findNavController(this);
        final PlaceAdapter placeAdapter = new PlaceAdapter();

        recyclerView = (RecyclerView) root.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(placeAdapter);

        PlacelistViewModel mainViewModel = ViewModelProviders.of(this).get(PlacelistViewModel.class);
        mainViewModel.getNoteLiveData().observe(getViewLifecycleOwner(), new Observer<List<CityModel>>() {
            @Override
            public void onChanged(List<CityModel> cities) {
                placeAdapter.setItems(cities);
            }
        });

        ImageView searchButton = root.findViewById(R.id.search_button_pl);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByCityName(navController);
            }
        });

        return root;
    }

    private void searchByCityName(NavController navController) {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alert.setTitle(this.getString(R.string.search_title));
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setMaxLines(1);
        input.setSingleLine(true);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                if (!result.isEmpty()) {
                    String tmp = result.toLowerCase();
                    result = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
                    HomeFragment.currentCity = result;
                    navController.navigate(R.id.navigation_home);
                }
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancelled
            }
        });
        alert.show();
    }

}