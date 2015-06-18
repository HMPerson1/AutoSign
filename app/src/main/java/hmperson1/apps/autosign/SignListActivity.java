package hmperson1.apps.autosign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import hmperson1.apps.autosign.sign.Signs;


/**
 * An activity representing a list of Signs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SignDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SignListFragment} and the item details
 * (if present) is a {@link SignDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link SignListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class SignListActivity extends Activity
        implements SignListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_list);
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.sign_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((SignListFragment) getFragmentManager()
                    .findFragmentById(R.id.sign_list))
                    .setActivateOnItemClick(true);
        }

        // Select the item the user was just viewing.
        Intent intent = getIntent();
        if (intent.hasExtra(SignActivity.ARG_ITEM_ID)) {
            int intExtra = intent.getIntExtra(SignActivity.ARG_ITEM_ID, 0);
            ((SignListFragment) getFragmentManager()
                    .findFragmentById(R.id.sign_list))
                    .getListView().setItemChecked(intExtra, true);
            onItemSelected(intExtra);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signs_list_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, SignActivity.class);
                // If the user selected a sign, view that sign
                int position = ((SignListFragment) getFragmentManager()
                        .findFragmentById(R.id.sign_list))
                        .getListView()
                        .getCheckedItemPosition();
                if (position != AdapterView.INVALID_POSITION) {
                    intent.putExtra(SignActivity.ARG_ITEM_ID, position);
                }
                navigateUpTo(intent);
                return true;
            }
            case R.id.action_add: {
                Signs signs = Signs.instance(this);
                ListView listView = ((SignListFragment) getFragmentManager()
                        .findFragmentById(R.id.sign_list))
                        .getListView();
                signs.addSign();
                // Refresh list view
                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                // Select the new sign
                int position = signs.size() - 1;
                listView.setItemChecked(position, true);
                onItemSelected(position);
                return true;
            }
            case R.id.action_delete: {
                ListView listView = ((SignListFragment) getFragmentManager()
                        .findFragmentById(R.id.sign_list))
                        .getListView();
                int position = listView.getCheckedItemPosition();
                if (position != AdapterView.INVALID_POSITION) {
                    Signs signs = Signs.instance(this);
                    signs.removeSign(position);

                    // Notify the detail view
                    SignDetailFragment fragment = (SignDetailFragment) getFragmentManager()
                            .findFragmentById(R.id.sign_detail_container);
                    if (fragment != null) {
                        fragment.setDeleted(true);
                    }

                    // Refresh list view
                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                    // Select the next sign
                    int newSize = signs.size();
                    if (position >= newSize) {
                        // If we're at the end of the list, select the new last element
                        position = newSize - 1;
                    }
                    listView.setItemChecked(position, true);
                    onItemSelected(position);
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Callback method from {@link SignListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(SignActivity.ARG_ITEM_ID, id);
            SignDetailFragment fragment = new SignDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.sign_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, SignDetailActivity.class);
            detailIntent.putExtra(SignActivity.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
