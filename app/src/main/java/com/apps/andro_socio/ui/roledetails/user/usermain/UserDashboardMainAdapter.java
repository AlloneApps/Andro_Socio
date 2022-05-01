package com.apps.andro_socio.ui.roledetails.user.usermain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserDashboardMainAdapter extends RecyclerView.Adapter<UserDashboardMainAdapter.MnIssuesMainAdapterViewHolder> {

    private static final String TAG = UserDashboardMainAdapter.class.getSimpleName();
    /**
     * ArrayList of type MnIssueMaster
     */
    private List<MnIssueMaster> mnIssueMasterMainItemList;
    private Context context;

    private MnIssueMasterClickListener listener;
    // endregion

    public UserDashboardMainAdapter(Context context, List<MnIssueMaster> mnIssueMasterMainItemList, MnIssueMasterClickListener listener) {
        this.context = context;
        this.mnIssueMasterMainItemList = mnIssueMasterMainItemList;
        this.listener = listener;
    }

    @Override
    public MnIssuesMainAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issues_main, parent, false);
        return new MnIssuesMainAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MnIssuesMainAdapterViewHolder holder, int position) {
        try {
            int olderPosition = holder.getAbsoluteAdapterPosition();
            if (mnIssueMasterMainItemList.size() > 0) {
                holder.setItem(mnIssueMasterMainItemList.get(olderPosition));
                MnIssueMaster mnIssueMaster = mnIssueMasterMainItemList.get(olderPosition);
                if (mnIssueMaster != null) {
                    holder.textTitle.setText(mnIssueMaster.getMnIssueHeader());

                    holder.imageIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.mnIssueMasterClicked(olderPosition, holder.imageIcon, holder.textTitle, mnIssueMaster);
                        }
                    });

                    holder.textTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.mnIssueMasterClicked(olderPosition, holder.imageIcon, holder.textTitle, mnIssueMaster);
                        }
                    });

                    holder.mnIssueMasterCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.mnIssueMasterClicked(olderPosition, holder.imageIcon, holder.textTitle, mnIssueMaster);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mnIssueMasterMainItemList.size();
    }

    public interface MnIssueMasterClickListener {
        void mnIssueMasterClicked(int position, ImageView imageIssue, TextView textIssueHeader, MnIssueMaster mnIssueMaster);
    }

    static class MnIssuesMainAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        MnIssueMaster mnIssueMaster;
        ImageView imageIcon;
        TextView textTitle;
        CardView mnIssueMasterCardView;

        MnIssuesMainAdapterViewHolder(View itemView) {
            super(itemView);

            // Image View
            imageIcon = itemView.findViewById(R.id.item_icon);
            // Text View
            textTitle = itemView.findViewById(R.id.item_title);
            // Card View
            mnIssueMasterCardView = itemView.findViewById(R.id.places_item_cardview);
        }

        public void setItem(MnIssueMaster item) {
            mnIssueMaster = item;

            Glide.with(itemView)
                    .load(mnIssueMaster.getMnIssuePlacePhotoPath())
                    .fitCenter()
                    .centerCrop()
                    .into(imageIcon);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
