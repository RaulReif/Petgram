package com.example.petgram;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.PublicacionesAdapter;
import com.example.petgram.adapters.PublicacionesPerfilAdapter;
import com.example.petgram.models.Publicacion;
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    // Lista de publicaciones
    private ArrayList<Publicacion> lista;

    // Views
    private TextView tvNombre, tvLocalidad, tvEstado, tvPublicaciones, tvAmigos, tvLblAmigos;
    private CircleImageView ivPerfil, ivMenu;
    private RecyclerView recycler;
    private Button btnEditarPerfil;
    private ImageView ivVisualizacionPublicaciones;
    private SwipeRefreshLayout refreshLayuot;

    private String visualizacionPublicaciones;
    private final String LINEAR = "LinearLayout";
    private final String GRID = "GridLayout";




    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        findViews(view);

        prepareClickListeners();

        prepareRefreshLayout();

        visualizacionPublicaciones = GRID;

        return view;
    }

    private void findViews(View view) {
        this.tvNombre = view.findViewById(R.id.nombreTvPerfil);
        this.tvLocalidad = view.findViewById(R.id.localidadTvPerfil);
        this.tvEstado = view.findViewById(R.id.estadoTvPerfil);
        this.tvPublicaciones = view.findViewById(R.id.publicacionesTvPerfil);
        this.tvAmigos = view.findViewById(R.id.amigosTvPerfil);
        this.tvLblAmigos = view.findViewById(R.id.amiglosLblEtPerfil);
        this.ivPerfil = view.findViewById(R.id.imagenIvPerfil);
        this.ivMenu = view.findViewById(R.id.menuIvPerfil);
        this.recycler = view.findViewById(R.id.publicacionesRecyclerPerfil);
        this.btnEditarPerfil = view.findViewById(R.id.editarPerfilBtnPerfil);
        this.ivVisualizacionPublicaciones = view.findViewById(R.id.cambiarVisualizacionPublicacionesPerfil);
        this.refreshLayuot = view.findViewById(R.id.refreshLayoutPerfil);
    }

    private void prepareRefreshLayout() {
        refreshLayuot.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                refreshLayuot.setRefreshing(false);
            }
        });
        refreshLayuot.setColorSchemeResources(R.color.colorPrimary);
    }

    private void bindData() {
        // Creamos la referencia de los datos del usuario
        Utils.getMyReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtenemos la información y la asociamos con las vistas
                Usuario u = dataSnapshot.getValue(Usuario.class);
                // Asociamos valores
                tvNombre.setText(u.getNombre());
                tvLocalidad.setText(u.getLocalidad());
                tvEstado.setText(u.getEstado());
                tvAmigos.setText(String.valueOf(obtenerNumeroAmigos(u)));
                try {
                    Picasso.get().load(u.getImagen()).into(ivPerfil);
                } catch (IllegalArgumentException e) {
                } catch (Exception e) {
                }

                obtenerListaPublicaciones(u);
                tvPublicaciones.setText(String.valueOf(lista.size()));


                // Montamos nuestro recycler
                if(visualizacionPublicaciones.equals(GRID)) {
                    montarRecyclerGrid();
                } else {
                    montarRecyclerLinear();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void obtenerListaPublicaciones(Usuario u) {
        if (u.getPublicaciones() != null) {
            lista = new ArrayList<>(u.getPublicaciones().values());
            Collections.sort(lista);
        } else {
            lista = new ArrayList<>();
        }
    }

    private void prepareClickListeners() {
        this.btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Registro2Activity.class));
            }
        });
        this.tvAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verAmigos();
            }
        });
        this.tvLblAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verAmigos();
            }
        });
        this.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MenuActivity.class));
            }
        });
        this.ivVisualizacionPublicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisualizacionPublicaciones();
            }
        });
    }

    private int obtenerNumeroAmigos(Usuario u) {
        if (u.getAmigos() != null) {
            return u.getAmigos().size();
        } else {
            return 0;
        }
    }

    public void cambiarVisualizacionPublicaciones() {
        if(visualizacionPublicaciones.equals(GRID)) {
            montarRecyclerLinear();
        } else {
            montarRecyclerGrid();
        }
    }

    private void montarRecyclerLinear() {
        PublicacionesAdapter adapter = new PublicacionesAdapter(getContext(), lista);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        visualizacionPublicaciones = LINEAR;
        ivVisualizacionPublicaciones.setImageResource(R.drawable.ic_grid);
    }

    private void montarRecyclerGrid() {
        PublicacionesPerfilAdapter adapter = new PublicacionesPerfilAdapter(getContext(), lista);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        visualizacionPublicaciones = GRID;
        ivVisualizacionPublicaciones.setImageResource(R.drawable.ic_list);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public  void verAmigos() {
        Intent intent = new Intent(getContext(), AmigosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", FirebaseAuth.getInstance().getUid());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.bindData();
    }

}
