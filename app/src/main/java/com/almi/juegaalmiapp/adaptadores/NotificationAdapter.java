    package com.almi.juegaalmiapp.adaptadores;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.almi.juegaalmiapp.modelo.Notification;
    import com.almi.juegaalmiapp.R;

    import java.util.List;

    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

        private List<Notification> notifications;

        public NotificationAdapter(List<Notification> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            Notification notification = notifications.get(position);
            holder.title.setText(notification.getTitle());
            holder.icon.setImageResource(notification.getIconResId());
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        public static class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView icon;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.notification_title);
                icon = itemView.findViewById(R.id.notification_icon);
            }
        }
    }
