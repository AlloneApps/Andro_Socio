package com.apps.andro_socio.ui.roledetails.mnofficer.viewuserissuesbycity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.model.issue.MnIssueSubDetails;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserCityWiseIssueByOfficerMainAdapter extends RecyclerView.Adapter<UserCityWiseIssueByOfficerMainAdapter.UserIssueAdapterViewHolder> {

    private static final String TAG = UserCityWiseIssueByOfficerMainAdapter.class.getSimpleName();
    /**
     * ArrayList of type PlaceItem
     */
    private List<MnIssueMaster> mnIssueMasterList;
    private Context context;
    // endregion

    private UserIssueItemClickListener listener;

    public UserCityWiseIssueByOfficerMainAdapter(Context context, List<MnIssueMaster> mnIssueMasterList, UserIssueItemClickListener listener) {
        this.context = context;
        this.mnIssueMasterList = mnIssueMasterList;
        this.listener = listener;
    }

    @Override
    public UserIssueAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_issue_only_view_main, parent, false);
        return new UserIssueAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserIssueAdapterViewHolder holder, int position) {
        try {
            int mainPosition = holder.getAdapterPosition();
            if (mnIssueMasterList.size() > 0) {
                holder.setItem(mnIssueMasterList.get(mainPosition));
                MnIssueMaster mnIssueMaster = mnIssueMasterList.get(mainPosition);
                if (mnIssueMaster != null) {
                    holder.textUserIssueHeader.setText(mnIssueMaster.getMnIssueHeader());
                    holder.textUserIssueNumber.setText(mnIssueMaster.getMnIssueType());
                    holder.textUserIssueId.setText(mnIssueMaster.getMnIssuePlacePhotoId());
                    holder.textUserIssueCity.setText(mnIssueMaster.getMnIssueCity());

                    int detailsLatest = mnIssueMaster.getMnIssueSubDetailsList().size() - 1;
                    MnIssueSubDetails mnIssueSubDetails = null;
                    if (detailsLatest >= 0) {
                        mnIssueSubDetails = mnIssueMaster.getMnIssueSubDetailsList().get(detailsLatest);

                        if (mnIssueSubDetails != null) {
                            holder.textUserIssueStatus.setText(mnIssueSubDetails.getMnIssueStatus());
                            switch (mnIssueSubDetails.getMnIssueStatus()){
                                case AppConstants
                                        .NEW_STATUS:{
                                    holder.textUserIssueStatus.setTextColor(context.getResources().getColor(R.color.colorNewUxBlue,null));
                                    break;
                                }
                                case AppConstants
                                        .ACCEPTED_STATUS:{
                                    holder.textUserIssueStatus.setTextColor(context.getResources().getColor(R.color.colorVeniceBlue,null));
                                    break;
                                }
                                case AppConstants
                                        .COMPLETED_STATUS:{
                                    holder.textUserIssueStatus.setTextColor(context.getResources().getColor(R.color.colorSuccess,null));
                                    break;
                                }case AppConstants
                                        .CANCELLED_STATUS:{
                                    holder.textUserIssueStatus.setTextColor(context.getResources().getColor(R.color.colorError,null));
                                    break;
                                }case AppConstants
                                        .REJECTED_STATUS:{
                                    holder.textUserIssueStatus.setTextColor(context.getResources().getColor(R.color.colorError,null));
                                    break;
                                }
                                default:{
                                    holder.textUserIssueStatus.setTextColor(context.getResources().getColor(R.color.colorNewUxBlue,null));
                                    break;
                                }
                            }
                        }
                    }

                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.userIssueViewClicked(mainPosition, mnIssueMaster, holder.userIssueImage,holder.textUserIssueHeader);
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

    public interface UserIssueItemClickListener {
        void userIssueViewClicked(int position, MnIssueMaster mnIssueMaster,ImageView imageView, TextView textView);
    }


    @Override
    public int getItemCount() {
        return mnIssueMasterList.size();
    }

    static class UserIssueAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        MnIssueMaster userIssue;
        ImageView userIssueImage;
        TextView textUserIssueNumber, textUserIssueStatus, textUserIssueId, textUserIssueCity,
                textUserIssueHeader, textView;

        UserIssueAdapterViewHolder(View itemView) {
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
        }

        public void setItem(MnIssueMaster mnIssueMaster) {
            userIssue = mnIssueMaster;

            try {
                Glide.with(itemView)
                        .load(mnIssueMaster.getMnIssuePlacePhotoPath())
                        .fitCenter()
                        .centerCrop()
                        .into(userIssueImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
