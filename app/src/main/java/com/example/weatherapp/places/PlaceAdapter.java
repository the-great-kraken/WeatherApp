package com.example.weatherapp.places;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.weatherapp.App;
import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.data.CityModel;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private SortedList<CityModel> sortedList;

    public PlaceAdapter() {

        sortedList = new SortedList<>(CityModel.class, new SortedList.Callback<CityModel>() {

            @Override
            public int compare(CityModel o1, CityModel o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(CityModel oldItem, CityModel newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(CityModel item1, CityModel item2) {
                return item1.id == item2.id;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaceViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void setItems(List<CityModel> places) {
        sortedList.replaceAll(places);
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        View delete;
        CityModel city;
        TextView cityName;

        public PlaceViewHolder(@NonNull final View itemView) {
            super(itemView);

            cityName = itemView.findViewById(R.id.city_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    MainActivity.cityName = cityName.getText().toString();
                    view.getContext().startActivity(intent);
                }
            });

            delete = itemView.findViewById(R.id.delete_city);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getInstance().getCityDao().delete(city);
                }
            });
        }

        public void bind(CityModel city) {
            this.city = city;
            this.cityName.setText(city.city);
        }
    }
}