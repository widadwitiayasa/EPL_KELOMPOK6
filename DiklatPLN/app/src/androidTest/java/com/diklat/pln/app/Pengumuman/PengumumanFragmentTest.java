package com.diklat.pln.app.Pengumuman;

import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.diklat.pln.app.R;
import com.diklat.pln.app.TabbedActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;

/**
 * Created by Fandy Aditya on 7/1/2017.
 */

public class PengumumanFragmentTest {
    @Rule
    public ActivityTestRule<TabbedActivity> tabbedActivityActivityTestRule = new ActivityTestRule<TabbedActivity>(TabbedActivity.class);
    private RecyclerView recyclerView;

    @Before
    public void init(){
        recyclerView = (RecyclerView)tabbedActivityActivityTestRule.getActivity().findViewById(R.id.pengumuman_rv);
        PengumumanAdapter pengumumanAdapter = mock(PengumumanAdapter.class);
        PengumumanObject pengumumanObject = mock(PengumumanObject.class);
        List<PengumumanObject> list = new ArrayList<>();
        list.add(new PengumumanObject("1","judul","12","deskripsi"));
        recyclerView.setAdapter(new PengumumanAdapter(tabbedActivityActivityTestRule.getActivity().getBaseContext(),list));
        recyclerView.setLayoutManager(new LinearLayoutManager(tabbedActivityActivityTestRule.getActivity().getBaseContext()));
    }

    @Test
    public void testLaunch(){
        //tabbedActivityActivityTestRule.launchActivity(null);
        onView(withText("judul"))
                .check(matches(isDisplayed()));
    }
}