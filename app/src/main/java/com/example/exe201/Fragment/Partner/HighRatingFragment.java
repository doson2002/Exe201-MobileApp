package com.example.exe201.Fragment.Partner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.Adapter.RatingCommentAdapter;
import com.example.exe201.R;

import java.util.List;

public class HighRatingFragment extends Fragment {

    private RecyclerView recyclerView;
    private RatingCommentAdapter adapter;
    private List<String> comments;

    public HighRatingFragment(List<String> comments) {
        this.comments = comments;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_high_rating, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RatingCommentAdapter(comments);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
