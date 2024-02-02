package com.hiraganaNow;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;

public class BoundFragment<B extends ViewBinding> extends Fragment {

    protected B binding;

    private final BindingInflater<B> bindingInflater;

    public BoundFragment(BindingInflater<B> bindingInflater) {
        this.bindingInflater = bindingInflater;
    }

    protected void navigate(int actionId) {
        NavHostFragment.findNavController(this).navigate(actionId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = bindingInflater.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void requestInputFocus(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public void showAlertDialog(int stringResourceId, Runnable callback) {
        new AlertDialog.Builder(requireContext())
                .setMessage(stringResourceId)
                .setOnDismissListener((dialog) -> callback.run())
                .create()
                .show();
    }

    public void showToast(int stringResourceId) {
        Toast toast = Toast.makeText(requireContext(), stringResourceId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    interface BindingInflater<X extends ViewBinding> {
        X inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState);
    }
}
