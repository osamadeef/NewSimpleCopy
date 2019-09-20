package com.example.simplecopy;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.ViewModeler.MainNotesViewModel;
import com.example.simplecopy.ViewModeler.MainViewModel;
import com.example.simplecopy.adapters.CopyAdapter;
import com.example.simplecopy.adapters.CopyNoteAdapter;
import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.NotesData;
import com.example.simplecopy.data.Numbers;
import com.example.simplecopy.widgets.RecyclerViewObserver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CopyNoteList extends Fragment implements CopyNoteAdapter.ItemClickListener {
    View view;

    // Member variables for the adapter and RecyclerView
    private RecyclerViewObserver mNumbersList;
    private CopyNoteAdapter mAdapter;
    private MainNotesViewModel mainViewModel;
    private AppDatabase mDB;
    View mEmptyView;
    Button empty_btn;


    public CopyNoteList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.copy_notes_fragment, container, false);
        setUpRecycleView ();
        mainViewModel = ViewModelProviders.of (this).get (MainNotesViewModel.class);
        mDB = AppDatabase.getInstance (getContext ());

        setupViewModel ( );

        // Setup FAB to open EditorActivity
        FloatingActionButton fab =  view.findViewById(R.id.fab_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity (), NoteEditorActivity.class);
                startActivity(intent);
            }
        });

        empty_btn = view.findViewById (R.id.btn_empty_note);
        empty_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                addSomeNumber (view);
            }
        });
        return view;

    }

    private void setupViewModel() {
        MainNotesViewModel viewModel = ViewModelProviders.of(this).get(MainNotesViewModel.class);

        viewModel.getNotes ().observe ( this, new Observer<List<NotesData>> ( ) {
            @Override
            public void onChanged(List<NotesData> notesDataList1) {
                mAdapter.setItems (notesDataList1);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu (true);
        super.onCreate (savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_home, menu);
        MenuItem item = menu.findItem (R.id.search);
        SearchView searchView = (SearchView)item.getActionView ();
        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getResults (query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getResults (newText);
                return false;
            }

            private void getResults (final String newText){
                mainViewModel.searchQuery (newText)
                        .observe (getActivity (), new Observer<List<NotesData>> ( ) {
                            @Override
                            public void onChanged(List<NotesData> notesData) {
                                if (notesData == null) return;
                                mAdapter.setSearchItem (notesData,newText);
                            }
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()){

            case R.id.deletall:
                // delete all data
                showDeleteAllConfirmationDialog ( );
//                break;
//            case R.id.settings:
//                Intent StartSettingActivity = new Intent (getActivity (),SettingsActivity.class);
//                startActivity (StartSettingActivity);
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    public void setUpRecycleView(){
        mNumbersList = view.findViewById (R.id.recycle_note);
        mEmptyView = view.findViewById (R.id.empty_notes_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ());
        mNumbersList.setLayoutManager (layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity (), DividerItemDecoration.VERTICAL);
        mNumbersList.addItemDecoration(decoration);
        mNumbersList.setItemAnimator (new DefaultItemAnimator ());
        mNumbersList.showIfEmpty(mEmptyView);
        mNumbersList.setHasFixedSize (true);
        mAdapter = new CopyNoteAdapter(getActivity (), this);
        mAdapter.setHasStableIds (true);
        mNumbersList.setAdapter (mAdapter);
    }

    private void showDeleteConfirmationDialog(final NotesData notesData) {

        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
        builder.setMessage (R.string.delete_note_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Number.
                mainViewModel.delete (notesData);

                Toast.makeText (getActivity (), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );

            }
        });
        builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss ( );
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    private void showDeleteAllConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
        builder.setMessage (R.string.delete_all_notes_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the number.
                mainViewModel.deleteAllNumbers ();
                Toast.makeText (getActivity (), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );
            }
        });
        builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss ( );
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    @Override
    public void onItemClickListener(NotesData notesData) {

        String noteString = notesData.getNote ( );


        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService (Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText ("TextView",noteString);
        clipboard.setPrimaryClip (clip);
        Toast.makeText (getActivity (), getString (R.string.Note_Copied), Toast.LENGTH_SHORT).show ( );

    }

    @Override
    public void onNoteEdit(int itemId) {
        Intent intent = new Intent (getActivity (), NoteEditorActivity.class);
        intent.putExtra (NoteEditorActivity.EXTRA_Note_ID, itemId);
        startActivity (intent);
    }

    @Override
    public void onNoteDelete(NotesData notesData) {
        showDeleteConfirmationDialog (notesData);

    }

    public void addSomeNumber(View view) {
        Intent intent = new Intent(getActivity (), NoteEditorActivity.class);
        startActivity(intent);
    }
}
