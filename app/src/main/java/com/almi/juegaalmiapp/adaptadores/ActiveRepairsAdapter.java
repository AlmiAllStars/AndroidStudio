package com.almi.juegaalmiapp.adaptadores;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.ActiveReparation;

import java.util.List;

public class ActiveRepairsAdapter extends RecyclerView.Adapter<ActiveRepairsAdapter.ViewHolder> {

    private final List<ActiveReparation> repairList;
    private final Context context;
    private final String[] estadosReparacion = {"Pendiente", "En progreso", "Esperando Piezas", "Listo para Recoger"};

    public ActiveRepairsAdapter(List<ActiveReparation> repairList, Context context) {
        this.repairList = repairList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_active_repair, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActiveReparation repair = repairList.get(position);
        holder.description.setText(repair.getDescription());

        int estadoActual = getEstadoIndex(repair.getStatus());

        holder.containerStates.removeAllViews();
        for (int i = 0; i < estadosReparacion.length; i++) {
            // Crear un LinearLayout para cada estado
            LinearLayout stateLayout = new LinearLayout(context);
            stateLayout.setOrientation(LinearLayout.VERTICAL);
            stateLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams stateLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            stateLayoutParams = new LinearLayout.LayoutParams(
                    0, // Anchura (0dp para usar weight)
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Altura
                    1f // Peso (1f para distribuir equitativamente)
            );
            stateLayout.setLayoutParams(stateLayoutParams);

            // Crear círculo con número
            TextView circle = new TextView(context);
            LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(70, 70);
            circle.setLayoutParams(circleParams);
            circle.setGravity(Gravity.CENTER);
            circle.setText(String.valueOf(i + 1));
            circle.setTextColor(context.getResources().getColor(android.R.color.white));
            circle.setTextSize(16);
            circle.setBackgroundResource(i <= estadoActual ? R.drawable.circle_blue : R.drawable.circle_gray);
            stateLayout.addView(circle);

            // Crear texto debajo del círculo
            TextView stateText = new TextView(context);
            stateText.setText(estadosReparacion[i]);
            stateText.setTextSize(12);
            stateText.setGravity(Gravity.CENTER);
            stateText.setTextColor(context.getResources().getColor(android.R.color.black));
            stateLayout.addView(stateText);

            holder.containerStates.addView(stateLayout);

            // Crear línea entre estados (excepto después del último estado)
            if (i < estadosReparacion.length - 1) {
                View line = new View(context);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(60, 4);
                lineParams.setMargins(0, 0, 0, 8);
                line.setLayoutParams(lineParams);
                line.setBackgroundColor(context.getResources().getColor(i < estadoActual ? R.color.blue : R.color.gray));
                holder.containerStates.addView(line);
            }
        }
    }


    private int getEstadoIndex(String status) {
        for (int i = 0; i < estadosReparacion.length; i++) {
            if (estadosReparacion[i].equalsIgnoreCase(status)) {
                return i;
            }
        }
        return 0; // Default al primer estado
    }

    @Override
    public int getItemCount() {
        return repairList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        LinearLayout containerStates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.text_description);
            containerStates = itemView.findViewById(R.id.container_states);
        }
    }
}
