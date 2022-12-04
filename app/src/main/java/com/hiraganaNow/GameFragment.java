package com.hiraganaNow;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hiraganaNow.databinding.FragmentGameBinding;

public class GameFragment extends BoundFragment<FragmentGameBinding> {

    public GameFragment() {
        super(FragmentGameBinding::inflate);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonPass.setOnClickListener(this::onClickPassButton);
        binding.kanaInput.setOnEditorActionListener(this::onEditorAction);
        refreshView();
        requestInputFocus(binding.kanaInput);
    }

    private void onClickPassButton(View view) {
        String romaji = Game.usePass();
        if(romaji == null) {
            showToast(R.string.message_out_of_passes);
        } else {
            binding.kanaInput.setText(romaji);
            refreshPasses();
        }
    }

    private boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
        String input = binding.kanaInput.getText().toString();

        switch (Game.test(input)) {
            case INVALID:
                showToast(R.string.message_invalid_kana);
                break;
            case FAILURE:
                // TODO
                refreshLives();
                new FailureEffect(binding.textKana, requireContext())
                        .start(this::onEffectEnd);
                break;
            case SUCCESS:
                binding.kanaInput.setText("");
                String nextKana = Game.getCurrentKana();
                new SuccessEffect(nextKana, binding.textKana, requireContext())
                        .start(this::onEffectEnd);
                break;
        }

        // TODO ...

        return true;
    }

    private void onEffectEnd() {
        System.out.println("onEffectEnd(): Game.currentKana.character="+Game.getCurrentKana());

        // TODO ...

        refreshView();
    }

    static class FailureEffect {
        private final TextView view;
        private final Context context;
        private Runnable onEffectEnd;

        public FailureEffect(TextView view, Context context) {
            this.view = view;
            this.context = context;
        }

        public void start(Runnable onEffectEnd) {
            this.onEffectEnd = onEffectEnd;
            startAnimation();
        }

        private void startAnimation() {
            int textColor = ContextCompat.getColor(context, R.color.red_10);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake);

            view.setTextColor(textColor);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::onAnimationEnd));
        }

        private void onAnimationEnd() {
            int textColor = ContextCompat.getColor(context, R.color.white);

            view.setTextColor(textColor);
            onEffectEnd.run();
        }
    }

    static class SuccessEffect {
        private final String nextKana;
        private final TextView view;
        private final Context context;
        private Runnable onEffectEnd;

        public SuccessEffect(String nextKana, TextView view, Context context) {
            this.nextKana = nextKana;
            this.view = view;
            this.context = context;
        }

        public void start(Runnable onEffectEnd) {
            this.onEffectEnd = onEffectEnd;
            startAnimation1();
        }

        private void startAnimation1() {
            int textColor = ContextCompat.getColor(context, R.color.green_10);
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

            view.setTextColor(textColor);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation2));
        }

        private void startAnimation2() {
            int textColor = ContextCompat.getColor(context, R.color.white);
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

            view.setText(nextKana);
            view.setTextColor(textColor);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(onEffectEnd));
        }
    }

    private void refreshView() {
        binding.textKana.setText(Game.getCurrentKana());
        refreshLives();
        refreshPasses();
    }

    private void refreshLives() {
        String text = Counters.getText(Game.getLives(), Game.MAX_LIVES, '♥', '❌');
        binding.textLives.setText(text);
    }

    private void refreshPasses() {
        String text = Counters.getText(Game.getPasses(), Game.MAX_PASSES, '❓', '❌');
        binding.textPasses.setText(text);
        binding.textFreePass.setVisibility(Game.isPassFree() ? View.VISIBLE : View.INVISIBLE);
    }
}