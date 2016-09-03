package com.example.a.myapplication.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a.myapplication.R;
import com.example.a.myapplication.adapter.MailAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ASUS on 2016/8/19.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    //头布局
    private View headerView;
    private int headerViewHeight;
    private ImageView headerArrow;
    private TextView headerState;
    private TextView headerTime;
    //底部布局
    private View footerView;
    private ProgressBar headerProgressBar;
    private int footerViewHeight;
    //Y轴坐标
    private int downY;
    //下拉时状态
    private static final int REFRESH_PULL = 0;
    private static final int REFRESH_RELEASE = 1;
    private static final int REFRESHING = 2;
    //状态
    private int currentState = REFRESH_PULL;
    //动画
    private RotateAnimation upAnimation;
    private RotateAnimation downAnimation;
    //是否加载数据
    private boolean isLoading = false;
    //适配器
    private MailAdapter mailAdapter;
    //
    private long pressTime1;

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setMailAdapter(MailAdapter mailAdapter) {
        this.mailAdapter = mailAdapter;
    }

    private void init() {
        setOnScrollListener(this);
        initHeaderView();
        initRotateAnimation();
        initFooterView();
    }

    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.refresh_header,null);
        headerArrow = (ImageView)headerView.findViewById(R.id.iv_arrow);
        headerProgressBar = (ProgressBar) headerView.findViewById(R.id.pb_rotate);
        headerState = (TextView) headerView.findViewById(R.id.tv_state);
        headerTime = (TextView)headerView.findViewById(R.id.tv_time);

        headerView.measure(0,0);
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0,-headerViewHeight,0,0);
        addHeaderView(headerView);
    }

    private void initRotateAnimation() {
        upAnimation = new RotateAnimation(0,-180,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(300);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180,-360,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);
    }

    private void initFooterView() {
        footerView = View.inflate(getContext(),R.layout.refresh_footer,null);
        footerView.measure(0,0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0,-footerViewHeight,0,0);
        addFooterView(footerView);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getY();
                pressTime1 = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == REFRESHING) {
                    break;
                }
                int deltaY = (int) (event.getY() - downY);
                int paddingTop = -headerViewHeight + deltaY;
                if (paddingTop > -headerViewHeight && getFirstVisiblePosition() == 0) {
                    headerView.setPadding(0,paddingTop,0,0);
                    if (paddingTop >= 0 && currentState == REFRESH_PULL) {
                        mailAdapter.disableAllItemChooser();
                        currentState = REFRESH_RELEASE;
                        refreshHeaderView();
                    } else if (paddingTop < 0 && currentState == REFRESH_RELEASE) {
                        mailAdapter.disableAllItemChooser();
                        currentState = REFRESH_PULL;
                        refreshHeaderView();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                int pressLength = (int)(event.getY() - downY);
                long pressTime2 = System.currentTimeMillis() - pressTime1;
                if (pressLength < 50 && pressLength < 1000) {
                    break;
                }
                if(currentState==REFRESH_PULL){
                    //仍处于下拉刷新状态，未滑动一定距离，不加载数据，隐藏headView
                    headerView.setPadding(0, -headerViewHeight, 0, 0);
                }else if (currentState==REFRESH_RELEASE) {
                    //滑倒一定距离，显示无padding值得headerView
                    headerView.setPadding(0, 0, 0, 0);
                    //设置状态为刷新
                    currentState = REFRESHING;
                    //刷新头部布局
                    refreshHeaderView();
                    if(listener!=null){
                        //接口回调加载数据
                        listener.onPullRefresh();
                    }
                }
                mailAdapter.enableAllItemChooser();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void refreshHeaderView() {
        switch (currentState) {
            case REFRESH_PULL:
                headerState.setText("下拉刷新");
                headerArrow.startAnimation(downAnimation);
                break;
            case REFRESH_RELEASE:
                headerState.setText("松开刷新");
                headerArrow.startAnimation(upAnimation);
                break;
            case REFRESHING:
                headerArrow.clearAnimation();
                headerArrow.setVisibility(View.INVISIBLE);
                headerProgressBar.setVisibility(View.VISIBLE);
                headerState.setText("正在刷新....");
                break;
        }
    }

    public void completeRefresh() {
        if (isLoading) {
            footerView.setPadding(0,-footerViewHeight,0,0);
            isLoading = false;
        } else {
            headerView.setPadding(0,-headerViewHeight,0,0);
            currentState = REFRESH_PULL;
            headerProgressBar.setVisibility(View.INVISIBLE);
            headerArrow.setVisibility(View.VISIBLE);
            headerState.setText("下拉刷新");
            headerTime.setText("最后刷新" + getCurrentTime());
        }
    }

    private String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    private OnRefreshListener listener;

    public void setOnRefreshListener(OnRefreshListener listener){
        this.listener = listener;
    }

    public interface OnRefreshListener{
        void onPullRefresh();
        void onLoadingMore();
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==OnScrollListener.SCROLL_STATE_IDLE && getLastVisiblePosition()==(getCount()-1) && !isLoading){
            isLoading = true;
            footerView.setPadding(0, 0, 0, 0);
            setSelection(getCount());
            if(listener!=null){
                listener.onLoadingMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
