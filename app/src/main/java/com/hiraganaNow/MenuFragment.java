package com.hiraganaNow;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.hiraganaNow.databinding.FragmentMenuBinding;

public class MenuFragment extends BoundFragment<FragmentMenuBinding> {

    public MenuFragment() {
        super(FragmentMenuBinding::inflate);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonHiragana.setOnClickListener(this::onClickHiraganaButton);
        binding.buttonKatakana.setOnClickListener(this::onClickKatakanaButton);
    }

    private void onClickHiraganaButton(View view) {
        Game.reset(Game.Mode.HIRAGANA);
        navigate(R.id.nav_to_game_hiragana);
    }

    private void onClickKatakanaButton(View view) {
        Game.reset(Game.Mode.KATAKANA);
        navigate(R.id.nav_to_game_katakana);
    }
}