package com.aman.androidrealunittesting.unittesting.screens.questionslist;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.aman.androidrealunittesting.R;
import com.aman.androidrealunittesting.unittesting.questions.Question;
import com.aman.androidrealunittesting.unittesting.screens.common.ViewMvcFactory;
import com.aman.androidrealunittesting.unittesting.screens.common.navdrawer.NavDrawerHelper;
import com.aman.androidrealunittesting.unittesting.screens.common.toolbar.ToolbarViewMvc;
import com.aman.androidrealunittesting.unittesting.screens.common.views.BaseObservableViewMvc;

import java.util.List;

public class QuestionsListViewMvcImpl extends BaseObservableViewMvc<QuestionsListViewMvc.Listener>
        implements QuestionsListViewMvc, QuestionsRecyclerAdapter.Listener {

    private final ToolbarViewMvc mToolbarViewMvc;
    private final NavDrawerHelper mNavDrawerHelper;

    private final Toolbar mToolbar;
    private final RecyclerView mRecyclerQuestions;
    private final QuestionsRecyclerAdapter mAdapter;
    private final ProgressBar mProgressBar;

    public QuestionsListViewMvcImpl(LayoutInflater inflater,
                                    @Nullable ViewGroup parent,
                                    NavDrawerHelper navDrawerHelper,
                                    ViewMvcFactory viewMvcFactory) {
        mNavDrawerHelper = navDrawerHelper;
        setRootView(inflater.inflate(R.layout.layout_questions_list, parent, false));

        mRecyclerQuestions = findViewById(R.id.recycler_questions);
        mRecyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new QuestionsRecyclerAdapter(this, viewMvcFactory);
        mRecyclerQuestions.setAdapter(mAdapter);

        mProgressBar = findViewById(R.id.progress);

        mToolbar = findViewById(R.id.toolbar);
        mToolbarViewMvc = viewMvcFactory.getToolbarViewMvc(mToolbar);
        initToolbar();
    }

    private void initToolbar() {
        mToolbarViewMvc.setTitle(getString(R.string.questions_list_screen_title));
        mToolbar.addView(mToolbarViewMvc.getRootView());
        mToolbarViewMvc.enableHamburgerButtonAndListen(new ToolbarViewMvc.HamburgerClickListener() {
            @Override
            public void onHamburgerClicked() {
                mNavDrawerHelper.openDrawer();
            }
        });
    }

    @Override
    public void onQuestionClicked(Question question) {
        for (Listener listener : getListeners()) {
            listener.onQuestionClicked(question);
        }
    }

    @Override
    public void bindQuestions(List<Question> questions) {
        mAdapter.bindQuestions(questions);
    }

    @Override
    public void showProgressIndication() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressIndication() {
        mProgressBar.setVisibility(View.GONE);
    }
}
