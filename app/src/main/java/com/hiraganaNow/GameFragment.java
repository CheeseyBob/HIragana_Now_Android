package com.hiraganaNow;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hiraganaNow.databinding.FragmentGameBinding;

public class GameFragment extends BoundFragment<FragmentGameBinding> {

    public GameFragment() {
        super(FragmentGameBinding::inflate);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshView();
        binding.buttonPass.setOnClickListener(this::onClickPassButton);
        binding.kanaInput.setOnEditorActionListener(this::onEditorAction);
        binding.kanaInput.requestFocus();
    }

    private String getLivesText() {
        return getCounterText('♥', Game.getLives());
    }

    private String getPassesText() {
        return getCounterText('❓', Game.getPasses());
    }

    private String getCounterText(char symbol, int count) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i ++) {
            sb.append('\n');
            sb.append(symbol);
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }

    private void onClickPassButton(View view) {
        System.out.println("onClickPassButton()");

        String romaji = Game.usePass();
        binding.kanaInput.setText(romaji);
        refreshView();


        // TODO ...

    }

    private boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
        System.out.println("onEditorAction(): actionId="+actionId);

        String input = binding.kanaInput.getText().toString();
        binding.kanaInput.setText("");
        Game.TestResult result = Game.test(input);

        switch (result) {
            case INVALID:
                // TODO
                break;
            case FAILURE:
                // TODO
                break;
            case SUCCESS:
                // TODO
                break;
        }
        refreshView();

        // TODO ...

        return true;
    }

    private void refreshView() {
        binding.textKana.setText(Game.currentKana.character);
        binding.textLives.setText(getLivesText());
        binding.textPasses.setText(getPassesText());
    }
}