package com.example.cinedex_v2.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinedex_v2.Data.Models.Pelicula;
import com.example.cinedex_v2.Data.Models.Section;
import com.example.cinedex_v2.Data.Models.SectionTop10;
import com.example.cinedex_v2.R;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_STANDARD = 0;
    private static final int VIEW_TYPE_TOP10 = 1;

    private List<Object> sections;
    private Context context;

    public MainAdapter(List<Object> sections, Context context) {
        this.sections = sections;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (sections.get(position) instanceof Section) return VIEW_TYPE_STANDARD;
        else if (sections.get(position) instanceof SectionTop10) return VIEW_TYPE_TOP10;
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_STANDARD) {
            View view = inflater.inflate(R.layout.item_section, parent, false);
            return new SectionViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_section_top10, parent, false);
            return new SectionTop10ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_STANDARD) {
            SectionViewHolder vh = (SectionViewHolder) holder;
            Section section = (Section) sections.get(position);

            vh.sectionTitle.setText(section.getTitle());
            vh.movieRecyclerView.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            );
            MovieAdapter adapter = new MovieAdapter(section.getMovieList(), context);
            vh.movieRecyclerView.setAdapter(adapter);

        } else if (holder.getItemViewType() == VIEW_TYPE_TOP10) {
            SectionTop10ViewHolder vh = (SectionTop10ViewHolder) holder;
            SectionTop10 sectionTop10 = (SectionTop10) sections.get(position);

            vh.sectionTitle.setText(sectionTop10.getTitle());
            vh.sectionSubtitle.setText(sectionTop10.getSubtitle());
            vh.movieRecyclerView.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            );
            MovieAdapterTop10 adapterTop10 = new MovieAdapterTop10(sectionTop10.getMovies(), context);
            vh.movieRecyclerView.setAdapter(adapterTop10);
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public void setSections(List<Object> newSections) {
        this.sections.clear();
        this.sections.addAll(newSections);
        notifyDataSetChanged();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        RecyclerView movieRecyclerView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.section_title);
            movieRecyclerView = itemView.findViewById(R.id.section_recycler_view);
        }
    }

    public static class SectionTop10ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle, sectionSubtitle;
        RecyclerView movieRecyclerView;

        public SectionTop10ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.section_title_top10);
            sectionSubtitle = itemView.findViewById(R.id.section_subtitle_top10);
            movieRecyclerView = itemView.findViewById(R.id.section_recycler_view_top10);
        }
    }
}
