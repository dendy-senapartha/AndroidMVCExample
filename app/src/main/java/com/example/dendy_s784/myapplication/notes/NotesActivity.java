package com.example.dendy_s784.myapplication.notes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.example.dendy_s784.myapplication.application.Application;
import com.example.dendy_s784.myapplication.R;

import com.example.dendy_s784.myapplication.notes.dagger.DaggerNotesComponent;
import com.example.dendy_s784.myapplication.notes.dagger.NotesContextModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Inject
    NotesPresenter mNotesPresenter;

    @Inject
    NotesFragment notesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_act);
        //inject dependency
        DaggerNotesComponent.builder().applicationComponent(Application.getComponent())
                .notesContextModule(new NotesContextModule(this))
                .build().inject(this);

        //load all defined UI from xml using Butterknife
        ButterKnife.bind(this);

        //setup the toolbar
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //setup the navigation drawer
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        if(navigationView!=null)
        {
            setupDrawerContent(navigationView);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.list_item:
                                //TODO :define the action for list_item
                                break;
                            case R.id.list_item2:
                                //TODO :define the action

                                break;
                            default:
                                break;
                        }
                        //close the navigation drawer when an item is selected
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}