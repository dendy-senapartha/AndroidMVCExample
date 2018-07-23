package com.example.dendy_s784.myapplication.notes;

import android.content.Context;
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
import com.example.dendy_s784.myapplication.utils.ActivityUtils;
import com.example.dendy_s784.myapplication.utils.Injection;

import javax.inject.Inject;

public class NotesActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Inject
    NotesPresenter mNotesPresenter;

    @Inject
    NotesFragment notesFragment;

    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_act);
        //inject dependency
        DaggerNotesComponent.builder().applicationComponent(Application.getComponent())
                .notesContextModule(new NotesContextModule(this))
                .build().inject(this);

        //appContext = getApplicationContext();
        //notesFragment = null;
        //setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //setup the navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null)
        {
            setupDrawerContent(navigationView);
        }

        //we using fragment
        /*
        NotesFragment notesFragment = (NotesFragment) getSupportFragmentManager().
                findFragmentById(R.id.contentFrame);
        if(notesFragment == null)
        {
            //create fragment
            notesFragment = NotesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), notesFragment,R.id.contentFrame);
        }*/

        // Create the presenter
        //presenter will fill the fragment using data taken remotely or locally
        /*mNotesPresenter = new NotesPresenter(
                Injection.provideUseCaseHandler(),Injection.provideGetNotes(appContext),
                Injection.provideDeleteNote(appContext),notesFragment);*/
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
