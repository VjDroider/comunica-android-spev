package edu.campusvirtual.comunica.library;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import edu.campusvirtual.comunica.R;

public class SupportDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetListener listener;

    public static SupportDialogFragment newInstance() {
        return new SupportDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.layout_support, container,
                false);

        // get the views and attach the listener
        TextView call = view.findViewById(R.id.callId);
        TextView send = view.findViewById(R.id.msgId);

        call.setOnClickListener(this);
        send.setOnClickListener(this);

        return view;

    }

    public void setListener(BottomSheetListener l) {
        listener = l;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.callId:
                listener.onSelect(R.id.callId);
                break;
            case R.id.msgId:
                listener.onSelect(R.id.msgId);
                break;
        }
    }

    public interface BottomSheetListener {
        public void onSelect(int id);
    }
}