package ru.noties.lifebus.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import ru.noties.debug.Debug;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.fragment.FragmentEvent;
import ru.noties.lifebus.fragment.FragmentLifebus;
import ru.noties.lifebus.view.ViewEvent;
import ru.noties.lifebus.view.ViewLifebus;

public class MainFragment extends Fragment {

    public static MainFragment newInstance() {
        final Bundle bundle = new Bundle();

        final MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Lifebus<FragmentEvent> lifebus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lifebus = FragmentLifebus.create(this);

        // we could additionally null-out instance
        lifebus.on(FragmentEvent.DETACH, () -> lifebus = null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView textView = view.findViewById(R.id.text_view);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(new Date().toString());
                handler.postDelayed(this, 1000L);
            }
        });

        handler.postDelayed(() -> {

            final TextView generated = new TextView(requireContext());
            generated.setText("Generated!");

            final ViewLifebus viewLifebus = ViewLifebus.create(generated);
            viewLifebus.on(ViewEvent.ATTACH, () -> Debug.i("Generated view ATTACH"));
            viewLifebus.on(ViewEvent.DETACH, () -> Debug.i("Generated view DETACH"));

            ((ViewGroup) view).addView(generated);

        }, 250L);

        lifebus.on(FragmentEvent.DESTROY_VIEW, () -> {
            handler.removeCallbacksAndMessages(null);
            Debug.i("Removing handler updates");
        });
    }
}
