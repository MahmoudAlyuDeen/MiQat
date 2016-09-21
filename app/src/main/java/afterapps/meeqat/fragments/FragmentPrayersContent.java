package afterapps.meeqat.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import afterapps.meeqat.R;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class FragmentPrayersContent extends Fragment {

    private int mIndex;
    @BindView(R.id.test_text_view)
    TextView testTextView;

    public FragmentPrayersContent(int index) {
        mIndex = index;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_prayers_content, container, false);
        ButterKnife.bind(this, rootView);
        testTextView.setText("" + mIndex);
        return rootView;
    }

}
