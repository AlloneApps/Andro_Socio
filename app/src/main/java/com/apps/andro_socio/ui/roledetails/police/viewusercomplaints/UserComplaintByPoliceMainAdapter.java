package com.apps.andro_socio.ui.roledetails.police.viewusercomplaints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.complaint.ComplaintSubDetails;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserComplaintByPoliceMainAdapter extends RecyclerView.Adapter<UserComplaintByPoliceMainAdapter.UserComplaintAdapterViewHolder> {

    private static final String TAG = UserComplaintByPoliceMainAdapter.class.getSimpleName();
    /**
     * ArrayList of type PlaceItem
     */
    private List<ComplaintMaster> complaintMasterList;
    private Context context;

    private UserComplaintItemClickListener listener;
    // endregion

    public UserComplaintByPoliceMainAdapter(Context context, List<ComplaintMaster> complaintMasterList, UserComplaintItemClickListener listener) {
        this.context = context;
        this.complaintMasterList = complaintMasterList;
        this.listener = listener;
    }

    @Override
    public UserComplaintAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_issue_main, parent, false);
        return new UserComplaintAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserComplaintAdapterViewHolder holder, int position) {
        try {
            int mainPosition = holder.getAdapterPosition();
            if (complaintMasterList.size() > 0) {
                holder.setItem(complaintMasterList.get(mainPosition));
                ComplaintMaster complaintMaster = complaintMasterList.get(mainPosition);
                if (complaintMaster != null) {
                    holder.textUserIssueHeader.setText(complaintMaster.getComplaintHeader());
                    holder.textUserIssueNumber.setText(complaintMaster.getComplaintType());
                    holder.textUserIssueId.setText(complaintMaster.getComplaintPlacePhotoId());
                    holder.textUserIssueCity.setText(complaintMaster.getComplaintCity());

                    int detailsLatest = complaintMaster.getComplaintsSubDetailsList().size() - 1;
                    ComplaintSubDetails complaintSubDetails = null;
                    if (detailsLatest >= 0) {
                        complaintSubDetails = complaintMaster.getComplaintsSubDetailsList().get(detailsLatest);

                        if (complaintSubDetails != null) {
                            holder.textUserIssueStatus.setText(complaintSubDetails.getComplaintStatus());
                        }
                    }

                    holder.textUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.userComplaintUpdateClicked(mainPosition, complaintMaster, holder.textUserIssueStatus.getText().toString());
                        }
                    });

                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.userComplaintViewClicked(mainPosition, complaintMaster);
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
        return complaintMasterList.size();
    }

    public interface UserComplaintItemClickListener {
        void userComplaintUpdateClicked(int position, ComplaintMaster complaintMaster, String userComplaintStatus);

        void userComplaintViewClicked(int position, ComplaintMaster complaintMaster);
    }

    static class UserComplaintAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ComplaintMaster userIssue;
        ImageView userIssueImage;
        TextView textUserIssueNumber, textUserIssueStatus, textUserIssueId, textUserIssueCity,
                textUserIssueHeader, textView, textUpdate;

        UserComplaintAdapterViewHolder(View itemView) {
            super(itemView);

            // Image View
            userIssueImage = itemView.findViewById(R.id.user_issue_image);
            // Text View
            textUserIssueNumber = itemView.findViewById(R.id.text_user_issue_number);
            textUserIssueStatus = itemView.findViewById(R.id.text_user_issue_status);
            textUserIssueId = itemView.findViewById(R.id.text_user_issue_id);
            textUserIssueCity = itemView.findViewById(R.id.text_user_issue_city);
            textUserIssueHeader = itemView.findViewById(R.id.text_user_issue_header);
            textView = itemView.findViewById(R.id.text_view);
            textUpdate = itemView.findViewById(R.id.text_update_status);
        }

        public void setItem(ComplaintMaster complaintMaster) {
            userIssue = complaintMaster;

            try {
                Glide.with(itemView)
                        .load(complaintMaster.getComplaintPlacePhotoPath())
                        .fitCenter()
                        .centerCrop()
                        .into(userIssueImage);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
