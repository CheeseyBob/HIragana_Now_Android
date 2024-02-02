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
        if(input.equals("")) {
            showToast(R.string.message_empty_input);
            return true;
        }

        switch (Game.test(input)) {
            case INVALID:
                showToast(R.string.message_invalid_kana);
                break;
            case FAILURE:
                onTestFailure();
                break;
            case SUCCESS:
                new SuccessEffect(Game.getCurrentKana(), binding.textKana, requireContext())
                        .start(this::refreshView);
                break;
            case LEVEL_UP:
                String kana = Game.getCurrentKana();
                new LevelUpEffect(kana, binding.textLevelUp, binding.textKana, requireContext())
                        .start(this::refreshView);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        binding.kanaInput.setText("");
        return true;
    }

    private void onGameEnd() {
        Game.reset();
        refreshView();
    }

    private void onTestFailure() {
        refreshLives();
        System.out.println("onTestFailure(): Game.getLives()="+Game.getLives());
        if(Game.getLives() > 0) {
            new FailureEffect(binding.textKana, requireContext()).start(this::refreshView);
        } else {
            showAlertDialog(R.string.message_game_over, this::onGameEnd);
        }
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
        protected final Context context;
        private final String nextKana;
        private final TextView view;
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

        protected void startAnimation1() {
            int textColor = ContextCompat.getColor(context, R.color.green_10);
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

            view.setTextColor(textColor);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation2));
        }

        protected void startAnimation2() {
            int textColor = ContextCompat.getColor(context, R.color.white);
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

            view.setText(nextKana);
            view.setTextColor(textColor);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(onEffectEnd));
        }
    }

    static class LevelUpEffect extends SuccessEffect {
        private final TextView view;
        private final TextView kanaView;

        public LevelUpEffect(String nextKana, TextView view, TextView kanaView, Context context) {
            super(nextKana, kanaView, context);
            this.view = view;
            this.kanaView = kanaView;
        }

        @Override
        protected void startAnimation2() {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

            kanaView.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation3));
        }

        private void startAnimation3() {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);

            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation4));
        }

        private void startAnimation4() {
            kanaView.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
            super.startAnimation2();
        }
    }

    private void refreshView() {
        binding.textKana.setText(Game.getCurrentKana());
        refreshLives();
        refreshPasses();
        refreshProgress();
        refreshLevel();
    }

    private void refreshLives() {
        String text = Counters.getVerticalText(Game.getLives(), Game.MAX_LIVES, '♥', '❌');
        binding.textLives.setText(text);
    }

    private void refreshPasses() {
        String text = Counters.getVerticalText(Game.getPasses(), Game.MAX_PASSES, '❓', '❌');
        binding.textPasses.setText(text);
        binding.textFreePass.setVisibility(Game.isPassFree() ? View.VISIBLE : View.INVISIBLE);
    }

    private void refreshProgress() {
        int count = Game.getProgress();
        int max = Game.getMaxProgress();
        String text = Counters.getHorizontalText(count, max, "🔶", "🔸");
        binding.textProgress.setText(text);
    }

    private void refreshLevel() {
        int count = Game.getLevel();
        int max = Game.getMaxLevel();
        String text = Counters.getHorizontalText(count, max, "✴️", "🔒");
        binding.textLevel.setText(text);
    }
}