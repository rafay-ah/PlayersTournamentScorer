package com.players.nest.HelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.players.nest.ModelClasses.GameFormats;
import com.players.nest.R;

import java.util.ArrayList;

public class MatchSettingsDialog extends DialogFragment implements MatchSettingDialogAdapter.RecyclerViewItemListener {

    public static final String FORMAT_TYPE = "FORMAT_TYPE";
    public static final String RULES_TYPE = "RULES_TYPE";

    String selectedFormatDesc, selectedRulesDesc;

    Context context;
    Button confirmBtn;
    FragmentListener fragmentListener;
    ArrayList<GameFormats> rulesList;
    TextView noDataFound, formatTxt, rulesTxt;
    ArrayList<GameFormats> gameFormatsArrayList;
    RecyclerView formatRecyclerView, recyclerView2;

    public MatchSettingsDialog(Context context, ArrayList<GameFormats> gameFormatsArrayList, ArrayList<GameFormats> rulesList) {
        this.context = context;
        this.gameFormatsArrayList = gameFormatsArrayList;
        this.rulesList = rulesList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match_setting_layout, container, false);

        noDataFound = view.findViewById(R.id.textView82);
        confirmBtn = view.findViewById(R.id.button11);
        formatTxt = view.findViewById(R.id.textView80);
        rulesTxt = view.findViewById(R.id.textView81);
        recyclerView2 = view.findViewById(R.id.recycler_view7);
        formatRecyclerView = view.findViewById(R.id.recycler_view6);


        if (gameFormatsArrayList.size() > 0 || rulesList.size() > 0) {

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            formatRecyclerView.setLayoutManager(layoutManager);
            MatchSettingDialogAdapter adapter = new MatchSettingDialogAdapter(getContext(), FORMAT_TYPE, gameFormatsArrayList, MatchSettingsDialog.this);

            LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView2.setLayoutManager(layoutManager2);
            MatchSettingDialogAdapter adapter2 = new MatchSettingDialogAdapter(getContext(), RULES_TYPE, rulesList, MatchSettingsDialog.this);

            formatRecyclerView.setAdapter(adapter);
            recyclerView2.setAdapter(adapter2);
        } else {
            noDataFound.setVisibility(View.VISIBLE);
            formatTxt.setVisibility(View.GONE);
            rulesTxt.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
        }

        if (rulesList.size() == 0)
            rulesTxt.setVisibility(View.GONE);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }


        confirmBtn.setOnClickListener(view1 -> {
            if (selectedFormatDesc != null) {
                fragmentListener.dataFromMatchSettingDialog(selectedFormatDesc, selectedRulesDesc);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please choose match format.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void formatSelected(String formatDesc) {
        selectedFormatDesc = formatDesc;
    }

    @Override
    public void rulesSelected(String ruleDesc) {
        selectedRulesDesc = ruleDesc;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (FragmentListener) getTargetFragment();
        if (fragmentListener == null)
            fragmentListener = (FragmentListener) context;
    }

    public interface FragmentListener {
        void dataFromMatchSettingDialog(String format, String rule);
    }
}
