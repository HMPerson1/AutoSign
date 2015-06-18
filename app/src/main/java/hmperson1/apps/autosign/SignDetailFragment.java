package hmperson1.apps.autosign;

import android.app.Fragment;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

import hmperson1.apps.autosign.sign.Sign;
import hmperson1.apps.autosign.sign.Signs;
import nullnull.fontslibrary.TypefaceRecord;
import nullnull.fontslibrary.TypefaceUtils;
import nullnull.fontslibrary.TypefacesAdapter;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * A fragment representing a single Sign detail screen.
 * This fragment is either contained in a {@link SignListActivity}
 * in two-pane mode (on tablets) or a {@link SignDetailActivity}
 * on handsets.
 */
public class SignDetailFragment extends Fragment {

    /**
     * The ID this fragment is presenting.
     */
    private int mId;
    private EditText text;
    private Spinner spinner;
    private boolean mDeleted = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SignDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(SignActivity.ARG_ITEM_ID)) {
            // Load the sign specified by the fragment arguments.
            mId = getArguments().getInt(SignActivity.ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_detail, container, false);

        // Save references to views that contain data
        text = (EditText) rootView.findViewById(R.id.signText);
        spinner = (Spinner) rootView.findViewById(R.id.typeface_spinner);

        // Register listeners
        rootView.findViewById(R.id.text_color_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AmbilWarnaDialog(getActivity(), text.getCurrentTextColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                        text.setTextColor(i | 0xFF000000);
                    }
                }).show();
            }
        });
        rootView.findViewById(R.id.bg_color_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AmbilWarnaDialog(getActivity(), ((ColorDrawable) text.getBackground()).getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                        text.setBackgroundColor(i | 0xFF000000);
                    }
                }).show();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load the sign as late as possible
        Sign sign = Signs.instance(getActivity()).getSign(mId);

        // Show the sign content
        text.setText(sign.getText());
        text.setTypeface(Typeface.create(sign.getTypeface(), Typeface.NORMAL));
        text.setTextColor(sign.getFontColor());
        text.setBackgroundColor(sign.getBgColor());

        final TypefacesAdapter adapter = new TypefacesAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
        spinner.setAdapter(adapter);
        // Fill the adapter with typefaces
        try {
            adapter.addAll(TypefaceUtils.typefaces());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            // If we couldn't get a list of fonts, just use sans-serif
            adapter.add(TypefaceRecord.SANS_SERIF);
        }

        // Find the index of the typeface name, using the index of sans-serif as a
        // fallback, and finally INVALID_POSITION as a fallback for that.
        int selection = AdapterView.INVALID_POSITION;
        int sansSerifSel = AdapterView.INVALID_POSITION;
        for (int i = 0; i < adapter.getCount(); i++) {
            String name = adapter.getItem(i).getName();
            if (Objects.equals(name, sign.getTypeface())) {
                selection = i;
                break;
            }
            if (Objects.equals(name, TypefaceUtils.SANS_SERIF_TYPEFACE_NAME)) {
                sansSerifSel = i;
            }
        }
        spinner.setSelection(selection != AdapterView.INVALID_POSITION ? selection : sansSerifSel);

        // Update the font in the editor when a font is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                text.setTypeface(adapter.getItem(position).getTypeface());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                text.setTypeface(Typeface.SANS_SERIF);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDeleted) return;

        // Get the data from the views and save it
        TypefaceRecord typeface = (TypefaceRecord) spinner.getSelectedItem();
        String typefaceName;
        if (typeface != null && typeface != TypefaceRecord.SANS_SERIF) {
            typefaceName = typeface.getName();
        } else {
            typefaceName = TypefaceUtils.SANS_SERIF_TYPEFACE_NAME;
        }
        try {
            Signs.instance(getActivity()).updateSign(
                    mId,
                    text.getText().toString(),
                    typefaceName,
                    text.getCurrentTextColor(),
                    ((ColorDrawable) text.getBackground()).getColor()).save();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.sign_save_error, Toast.LENGTH_LONG).show();
        }
    }

    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }
}
