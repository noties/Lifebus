package io.noties.lifebus.sample;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import java.util.Date;

import io.noties.debug.Debug;
import io.noties.lifebus.Lifebus;
import io.noties.lifebus.arch.LifebusArch;
import io.noties.lifebus.fragment.FragmentEvent;
import io.noties.lifebus.fragment.FragmentLifebus;

public class MainFragment extends Fragment {

    public static MainFragment newInstance() {
        final Bundle bundle = new Bundle();

        final MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Lifebus<FragmentEvent> lifebus;

    {
        final LifebusArch lifebusArch = LifebusArch.create(this);
        for (Lifecycle.Event event : Lifecycle.Event.values()) {
            lifebusArch.on(event, () -> Debug.i("fragment arch, event: %s", event));
        }
    }

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

        lifebus.on(FragmentEvent.VIEW_DESTROYED, () -> {
            handler.removeCallbacksAndMessages(null);
            Debug.i("Removing handler updates");
        });
    }
}
