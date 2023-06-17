package com.ambulanceapp.client.ui.feedback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambulanceapp.client.common.MapForm;
import com.ambulanceapp.client.databinding.FragmentFeedbackBinding;
import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.Feedback;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.preference.UserPref;
import com.ambulanceapp.client.services.FirebaseRequest;

public class FeedbackFragment extends Fragment {

    Context mContext;
    FragmentFeedbackBinding binding;
    FirebaseRequest request;
    ProgressDialog pdLoad;

    public FeedbackFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        request = new FirebaseRequest();
        pdLoad = new ProgressDialog(mContext);
        pdLoad.setMessage("Sending Request ...");
        pdLoad.setCancelable(false);
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.btnSendFeedback.setOnClickListener(v -> {
            String feedback = binding.editFeedback.getText().toString();
            if (feedback.equals("")) {
                Toast.makeText(mContext, "Please Don't Leave Empty Field", Toast.LENGTH_SHORT).show();
            } else {
                pdLoad.show();
                Users users = new UserPref(mContext).getUsers();
                Feedback fb = new Feedback.FeedbackBuilder()
                        .setFeedBack(feedback)
                        .setUserID(users.getDocumentID())
                        .build();

                FirebaseRequestBody body = new FirebaseRequestBody.RequestBodyBuilder()
                        .setCollectionName(FirebaseRequest.FEEDBACK_COLLECTION)
                        .setParams(MapForm.convertObjectToMap(fb))
                        .build();

                request.insertData(body, new FirebaseListener() {
                    @Override
                    public <T> void onSuccessAny(T any) {
                        pdLoad.dismiss();
                        Toast.makeText(mContext, "Successfully send feedback", Toast.LENGTH_SHORT).show();
                        binding.editFeedback.setText("");
                    }

                    @Override
                    public void onError() {
                        pdLoad.dismiss();
                        Toast.makeText(mContext, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
