package com.hiraganaNow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    interface BindingInflater<X extends ViewBinding> {
        X inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState);
    }
}
