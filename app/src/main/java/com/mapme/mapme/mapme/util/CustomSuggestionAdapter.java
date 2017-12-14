package com.mapme.mapme.mapme.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mapme.mapme.mapme.R;

/**
 * Created by Sillybob on 12/13/2017.
 */

public class CustomSuggestionAdapter extends SuggestionsAdapter<Suggestion, CustomSuggestionAdapter.SuggestionHolder> {

    private SuggestionsAdapter.OnItemViewClickListener listener;

    public CustomSuggestionAdapter(LayoutInflater layoutInflater) {
        super(layoutInflater);
    }

    @Override
    public void onBindSuggestionHolder(Suggestion suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getMainText());
        holder.subtitle.setText(suggestion.getSecondaryText());
    }

    @Override
    public int getSingleViewHeight() {
        return 60;
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_custom_suggesstion, parent, false);
        return new SuggestionHolder(view);
    }

    public void setListener(SuggestionsAdapter.OnItemViewClickListener listener) {
        this.listener = listener;
    }

    class SuggestionHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView subtitle;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(getSuggestions().get(getAdapterPosition()));
                    listener.OnItemClickListener(getAdapterPosition(), v);
                }
            });
        }
    }
}
