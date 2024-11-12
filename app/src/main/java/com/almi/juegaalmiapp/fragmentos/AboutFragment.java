package com.almi.juegaalmiapp.fragmentos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import com.almi.juegaalmiapp.R;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView historyTitle = view.findViewById(R.id.history_title);
        TextView historyContent = view.findViewById(R.id.history_content);
        TextView valuesTitle = view.findViewById(R.id.values_title);
        TextView teamTitle = view.findViewById(R.id.team_title);
        AppCompatButton contactButton = view.findViewById(R.id.button_previous);

        ImageView facebookIcon = view.findViewById(R.id.facebook_icon);
        ImageView instagramIcon = view.findViewById(R.id.instagram_icon);
        ImageView twitterIcon = view.findViewById(R.id.twitter_icon);
        ImageView linkedinIcon = view.findViewById(R.id.linkedin_icon);

        contactButton.setOnClickListener(v -> openUrl("https://www.microsoft.com/es-es/microsoft-365/outlook/email-and-calendar-software-microsoft-outlook"));
        facebookIcon.setOnClickListener(v -> openUrl("https://www.facebook.com"));
        instagramIcon.setOnClickListener(v -> openUrl("https://www.instagram.com"));
        twitterIcon.setOnClickListener(v -> openUrl("https://www.twitter.com"));
        linkedinIcon.setOnClickListener(v -> openUrl("https://www.linkedin.com"));

        return view;
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
