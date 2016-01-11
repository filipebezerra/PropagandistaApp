package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnTouch;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

public class CadastroMedicoActivity extends AppCompatActivity {

    private FragmentAdapter mFragmentAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medico);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mFragmentAdapter.addFragment(DetalhesMedicoFragment.newInstance(),
                getString(R.string.title_fragment_detalhes_medico));
        mFragmentAdapter.addFragment(EnderecosMedicoFragment.newInstance(),
                getString(R.string.title_fragment_enderecos_medico));

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    public static class DetalhesMedicoFragment extends Fragment
            implements CalendarDatePickerDialogFragment.OnDateSetListener {

        @Bind(R.id.txtNomeMedico) protected EditText mNomeMedicoView;
        @Bind(R.id.txtDtAniversario) protected EditText mDataAniversarioView;
        @Bind(R.id.txtSecretaria) protected EditText mSecretariaView;

        private boolean mHasDialogFrame;

        private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

        public DetalhesMedicoFragment() {}

        public static DetalhesMedicoFragment newInstance() {
            return new DetalhesMedicoFragment();
        }

        @Override
        public void onCreate(Bundle inState) {
            super.onCreate(inState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_cadastro_medico, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_salvar) {
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_cadastro_medico,
                    container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle inState) {
            super.onViewCreated(view, inState);

            if (inState == null) {
                mHasDialogFrame = view.findViewById(R.id.frame) != null;
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            final CalendarDatePickerDialogFragment calendarDialogFragment =
                    (CalendarDatePickerDialogFragment) getChildFragmentManager()
                            .findFragmentByTag(FRAG_TAG_DATE_PICKER);
            if (calendarDialogFragment != null) {
                calendarDialogFragment.setOnDateSetListener(this);
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }

        @OnEditorAction(R.id.txtNomeMedico)
        public boolean onEditorActionTxtNomeMedico(final int actionId) {
            if (actionId == EditorInfo.IME_ACTION_NEXT)  {
                if (!mDataAniversarioView.requestFocusFromTouch()) {
                    final long downTime = SystemClock.uptimeMillis();
                    final long eventTime = SystemClock.uptimeMillis() + 100;
                    mDataAniversarioView.dispatchTouchEvent(MotionEvent.obtain(downTime, eventTime,
                            MotionEvent.ACTION_UP, 0.0f, 0.0f, 0));
                }
                return true;
            }
            return false;
        }

        @OnTouch(R.id.txtDtAniversario)
        public boolean onTouchTxtDtAniversario(final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePicker();
                return true;
            }
            return false;
        }

        private DateTime obtainDateFromField() {
            if (!TextUtils.isEmpty(mDataAniversarioView.getText())) {
                final String dateText = mDataAniversarioView.getText().toString();
                return DateUtil.toDate(dateText);
            }

            return null;
        }

        private void showDatePicker() {
            int year, monthOfYear, dayOfMonth;
            final DateTime dateSet = obtainDateFromField();

            if (dateSet != null) {
                year = dateSet.getYear();
                monthOfYear = dateSet.getMonthOfYear();
                dayOfMonth = dateSet.getDayOfMonth();
            } else {
                final DateTime now = DateTime.now();
                year = now.getYear();
                monthOfYear = now.getMonthOfYear();
                dayOfMonth = now.getDayOfMonth();
            }

            final CalendarDatePickerDialogFragment dialogFragment = CalendarDatePickerDialogFragment
                    .newInstance(this, year, monthOfYear - 1, dayOfMonth);

            if (mHasDialogFrame) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();

                ft.add(R.id.frame, dialogFragment, FRAG_TAG_DATE_PICKER)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            } else {
                dialogFragment.show(getChildFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        }

        @Override
        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year,
                int monthOfYear, int dayOfMonth) {
            mDataAniversarioView.setText(DateUtil.format(year, monthOfYear, dayOfMonth));
            mSecretariaView.requestFocus();
        }
    }

    public static class EnderecosMedicoFragment extends Fragment {
        public EnderecosMedicoFragment() {}

        public static EnderecosMedicoFragment newInstance() {
            return new EnderecosMedicoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_cadastro_enderecos_medico, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_adicionar) {
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_cadastro_enderecos_medico,
                    container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }
    }

    public class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 0 || position >= mFragments.size()) {
                return null;
            }

            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < 0 || position >= mFragmentTitles.size()) {
                return null;
            }

            return mFragmentTitles.get(position);
        }
    }
}
