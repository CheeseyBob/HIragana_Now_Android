package com.hiraganaNow;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
        String input = binding.kanaInput.getText().toString().toLowerCase();
        if(input.equals("")) {
            showToast(R.string.message_empty_input);
            return true;
        }
        if(input.startsWith("cheat ")) {
            try {
                int skips = Integer.parseInt(input.substring(6));
                Game.cheat(skips);
                refreshView();
                return true;
            } catch (NumberFormatException e) {
                // This is one of the rare cases where we actually want to do nothing in a catch.
            }
        }

        Game.TestResult result = Game.test(input);
        switch (result) {
            case INVALID:
                showToast(R.string.message_invalid_kana);
                break;
            case FAILURE:
                onTestFailure();
                break;
            case SUCCESS:
                new SuccessEffect(Game.getCurrentKana(), binding, requireContext())
                        .start(this::refreshView);
                break;
            case LEVEL_UP:
                String kana = Game.getCurrentKana();
                new LevelUpEffect(kana, binding, requireContext())
                        .start(this::refreshView);
                break;
            case WIN_GAME:
                new WinGameEffect(binding, requireContext())
                        .start(this::onGameWin);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        binding.kanaInput.setText("");
        return true;
    }

    private void onGameOver() {
        Game.reset();
        refreshView();
    }

    private void onGameWin() {
        navigate(R.id.nav_to_main_menu);
    }

    private void onTestFailure() {
        refreshLives();
        if(Game.getLives() > 0) {
            new FailureEffect(binding, requireContext()).start(this::refreshView);
        } else {
            showAlertDialog(R.string.message_game_over, this::onGameOver);
        }
    }

    static class FailureEffect {
        private final TextView view;
        private final Context context;
        private Runnable onEffectEnd;

        public FailureEffect(FragmentGameBinding binding, Context context) {
            this.view = binding.textKana;
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

        public SuccessEffect(String nextKana, FragmentGameBinding binding, Context context) {
            this.nextKana = nextKana;
            this.view = binding.textKana;
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

        public LevelUpEffect(String nextKana, FragmentGameBinding binding, Context context) {
            super(nextKana, binding, context);
            this.view = binding.textLevelUp;
            this.kanaView = binding.textKana;
        }

        @Override
        protected void startAnimation2() {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

            kanaView.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            view.setText(Game.isFinalLevel() ? R.string.text_final_level : R.string.text_level_up);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation3));
        }

        protected void startAnimation3() {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);

            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation4));
        }

        protected void startAnimation4() {
            kanaView.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
            super.startAnimation2();
        }
    }

    static class WinGameEffect extends SuccessEffect {
        private final TextView view;
        private final TextView kanaText;
        private final TextView freePassText;
        private final EditText input;
        private final Button passButton;

        public WinGameEffect(FragmentGameBinding binding, Context context) {
            super(null, binding, context);
            this.view = binding.textLevelUp;
            this.kanaText = binding.textKana;
            this.freePassText = binding.textFreePass;
            this.input = binding.kanaInput;
            this.passButton = binding.buttonPass;
        }

        @Override
        public void start(Runnable onEffectEnd) {
            input.setEnabled(false);
            input.setVisibility(View.INVISIBLE);
            passButton.setEnabled(false);
            passButton.setVisibility(View.INVISIBLE);
            freePassText.setVisibility(View.INVISIBLE);
            super.start(onEffectEnd);
        }

        @Override
        protected void startAnimation2() {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

            kanaText.setVisibility(View.INVISIBLE);
            view.setText(R.string.text_win_game);
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation3));
        }

        protected void startAnimation3() {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation4));
        }

        protected void startAnimation4() {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            view.startAnimation(animation);
            animation.setAnimationListener(new AnimationEndListener(this::startAnimation3));
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