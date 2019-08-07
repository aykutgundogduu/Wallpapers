package com.example.hrwallpapers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.hrwallpapers.ImageProcessor.ImageProcessActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseWallpaperActivity extends AppCompatActivity {

    private int ANIMATION_DURATION = 250;

    private static final String TAG ="BaseWallpaperTAG";
    private static final int VIEW_PAGER_LOAD_LIMIT = 5;
    private static BaseWallpaperActivity baseWallpaper;


    private View customActionBar;
    private View rightArea;
    private View leftArea;
    private View mainView;
    private View progressingArea;
    public ViewPager viewPager;
    private Activity thisActivity;

    private boolean mVisible;


    private ImageView leftIcon;
    private ImageView rightIcon;
    private ImageView shareImageView;
    private ImageView likeImageView;
    private ImageView backImageView;
    private ImageView wizardImageView;
    private ImageView setAsImageView;
    private ImageView downloadAsHighQuality;
    private ImageView downloadAsActualSize;
    public wallpaperModel model;


    public List<wallpaperModel>wallpaperModelList;
    public queryModel queryModel;

    public BaseWallpaperPagerAdapter adapter;

    private RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .priority(Priority.HIGH)
            .fitCenter();


    private Fragment popupFragment;

    private SlidingUpPanelLayout mSlidingPanel;
    private FlexboxLayout tagsContainer;
    private TextView resolutionTextView;
    private RecyclerView popupRecyclerView;
    private FrameLayout fragmentHolder;
    public wallpaperRecyclerViewAdapter similiarAdapter;
    private ExpandableLinearLayout expandableArea;
    private ExpandableLinearLayout downloadExpandableContainer;
    private CircleProgressBar progressingAreaCircleBar;

    private HttpGetTagsAsync getTagsAsync = new HttpGetTagsAsync();
    private HttpGetImagesAsync getSimiliarAsync = new HttpGetImagesAsync();
    private final Context baseWallpaperContext = this;
    private static HttpGetImagesAsync task = new HttpGetImagesAsync();


    BottomDownloadDialog downloadDialog= new BottomDownloadDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_base_wallpaper);
        baseWallpaper = this;
        popupFragment = setFragment(new wallpaperPopupFragment(),R.id.base_wallpaper_fragment_holder);
        thisActivity = this;
        if(this.getIntent().getExtras().containsKey("wallpaperList"))
        {
            Type listType = new TypeToken<List<wallpaperModel>>(){}.getType();
            String listData = getIntent().getStringExtra("wallpaperList");

            wallpaperModelList = new Gson().fromJson(listData,listType);
        }
        if(this.getIntent().getExtras().containsKey("listIndex"))
        {
            int index = getIntent().getIntExtra("listIndex",0);
            if(index == -1) index = 0;
            model = wallpaperModelList.get(index);

        }
        if(this.getIntent().getExtras().containsKey("queryData"))
        {
            String menuData = getIntent().getStringExtra("queryData");
            queryModel = new Gson().fromJson(menuData, queryModel.class);
        }


        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }


        mVisible = false;
        leftArea = findViewById(R.id.base_wallpaper_left_area);
        rightArea = findViewById(R.id.base_wallpaper_right_area);
        leftIcon = findViewById(R.id.base_wallpaper_left_icon);
        rightIcon = findViewById(R.id.base_wallpaper_right_icon);
        viewPager =findViewById(R.id.base_wallpaper_viewPager);
        mainView = findViewById(R.id.base_wallpaper_container);
        mSlidingPanel = findViewById(R.id.base_wallpaper_slidingpanel);
        tagsContainer = findViewById(R.id.base_wallpaper_tags_container);
        resolutionTextView = findViewById(R.id.base_wallpaper_resolution_textview);
        shareImageView = findViewById(R.id.fullscreen_share_button);
        expandableArea = findViewById(R.id.base_wallpaper_tags_expandable);
        customActionBar = findViewById(R.id.base_wallpaper_custom_actionbar);
        likeImageView = findViewById(R.id.base_wallpaper_like);
        backImageView = findViewById(R.id.base_wallpaper_back_button);
        downloadExpandableContainer =findViewById(R.id.base_wallpaper_download_expandable);
        wizardImageView =findViewById(R.id.base_wallpaper_wizard_button);
        setAsImageView = findViewById(R.id.base_wallpaper_setas);
        downloadAsActualSize = findViewById(R.id.base_wallpaper_download_as_actualsize);
        downloadAsHighQuality = findViewById(R.id.base_wallpaper_download_as_highquality);
        progressingArea = findViewById(R.id.base_wallpaper_progressing_area);
        progressingAreaCircleBar = findViewById(R.id.base_wallpaper_progressing_area_circle);


        mSlidingPanel.setAnchorPoint(0.7f); // it will up to %70 of screen size


        if(model.isFavorite.isTrue())MainActivity.changeImageViewAsLiked(likeImageView);
        else MainActivity.changeImageViewAsUnliked(likeImageView);


        adapter = new BaseWallpaperPagerAdapter(this,wallpaperModelList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(wallpaperModelList.indexOf(model));
        viewPager.setOffscreenPageLimit(2);
        loadTags(model);


        popupRecyclerView = findViewById(R.id.base_Wallpaper_similiar_recyclerView);
        fragmentHolder = findViewById(R.id.base_wallpaper_fragment_holder);

        List<wallpaperModel> similiarList =new ArrayList<>();
        similiarAdapter = new wallpaperRecyclerViewAdapter(similiarList,fragmentHolder,popupFragment,mainView,this,queryModel,popupRecyclerView);
        popupRecyclerView.setAdapter(similiarAdapter);


        popupRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        popupRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int calculatedYPos = 0;
            private int actualHeight = 0;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                calculatedYPos = recyclerView.computeVerticalScrollOffset();
                actualHeight = recyclerView.computeVerticalScrollRange();



                if(actualHeight - (recyclerView.computeVerticalScrollExtent() + calculatedYPos) < MainActivity.LOAD_MORE_SCROLL_RANGE)
                {
                    Log.i("Scroll", "onScrolled: " + MainActivity.LOAD_MORE_SCROLL_RANGE + " < " + (actualHeight - (recyclerView.computeVerticalScrollExtent() + calculatedYPos)));
                    wallpaperModel activeModel = wallpaperModelList.get(viewPager.getCurrentItem());
                    //Load smiliar images


                    if(activeModel.tagList.size() > 0 && mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
                    {
                        if((task == null || task.getStatus() != AsyncTask.Status.RUNNING))
                        {
                            task = new HttpGetImagesAsync();

                            Object[] container = new Object[] {activeModel.tagList,activeModel};


                            ((HttpGetImagesAsync)  task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                                @Override
                                public void taskFinished(List<wallpaperModel> list) {
                                }

                                @Override
                                public void onOneTagLoaded(List<wallpaperModel> list) {
                                    similiarAdapter.addModelListToList(list);
                                }
                            });
                            task.execute(container);
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy);

            }

        });

        mSlidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            float offset = 0;
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                offset = slideOffset;
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                {
                    expandableArea.setSTATE(expandableArea.COLLAPSED); // CLOSE TAGS AREA
                }
                else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                {
                    if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    {
                        cleanSimiliars();
                        mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);// Fix the bug to the fixed state as ANCHORED
                    }
                }
                else if(newState == SlidingUpPanelLayout.PanelState.ANCHORED && offset < 0.2f)
                {
                    mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this.getApplicationContext(),2);
        layoutManager.setRecycleChildrenOnDetach(true);
        popupRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) popupRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        final GestureDetector tapDetector = new GestureDetector(this, new GestureListener());


        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(tapDetector.onTouchEvent(event))
                {
                    return true;
                }
                else return false;
            }
        });


        if(wallpaperModelList != null && model != null)
        {
            if(wallpaperModelList.size() - wallpaperModelList.indexOf(model) < VIEW_PAGER_LOAD_LIMIT)
            {

                if(task.getStatus() == AsyncTask.Status.FINISHED)
                {
                    queryModel.setActivePage(queryModel.getActivePage() + 1);
                    queryModel.prepareUrl();
                    MainActivity.setMenuClickListenerForViewPager(queryModel,viewPager,adapter,task);
                }
            }
        }

        leftArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = viewPager.getCurrentItem();
                index--;

                if(index > -1 && index <= adapter.getCount())
                {
                    viewPager.setCurrentItem(index);
                }
            }
        });

        rightArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = viewPager.getCurrentItem();
                index++;


                if(index > -1 && index <= adapter.getCount())
                {
                    viewPager.setCurrentItem(index);
                }
            }
        });


        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                View view = viewPager.findViewWithTag("container" + viewPager.getCurrentItem());
                if(view != null)
                {
                    ImageView im = view.findViewById(R.id.base_wallpaper_main_image);
                    if(im !=null)
                    {
                        BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
                        if (drawable != null)
                        {
                            downloadDialog.setActiveBitmap(drawable.getBitmap());
                            downloadDialog.setActiveModel(model);
                            downloadDialog.setDialogType(BottomDownloadDialog.BottomDownloadDialogType.SHARE);
                            downloadDialog.show(getSupportFragmentManager(),"Share");
                        }
                        else
                        {
                            Toast.makeText(thisActivity, "Please wait for the load", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        setAsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                final CircleProgressBar bar = progressingAreaCircleBar;

            }
        });

        downloadAsHighQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        downloadAsActualSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = viewPager.findViewWithTag("container" + viewPager.getCurrentItem());
                if(view != null)
                {
                    ImageView im = view.findViewById(R.id.base_wallpaper_main_image);
                    if(im !=null)
                    {
                        BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
                        if (drawable != null)
                        {
                            Bitmap bitmap = drawable.getBitmap();

                        }
                        else Log.i(TAG, "onClick: Drawable is null");
                    }
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                model.tagsCurrentPage = 0;
                resolutionTextView.setText("");
                similiarAdapter.clearModels();
                getSimiliarAsync.cancel(true);
                loadTags(model);

                if(wallpaperModelList.size() - i < VIEW_PAGER_LOAD_LIMIT)
                {
                    if(task == null) task = new HttpGetImagesAsync();
                    if(task.getStatus() != AsyncTask.Status.RUNNING)
                    {
                       queryModel.setActivePage(queryModel.getActivePage() + 1);
                       MainActivity.setMenuClickListenerForViewPager(queryModel,viewPager,adapter,task);
                    }
                }

                //Eğer liked olarak geldiyse likeimageview ona göre şekillenecek.
                if(model.isFavorite.isTrue()) MainActivity.changeImageViewAsLiked(likeImageView);
                else MainActivity.changeImageViewAsUnliked(likeImageView); // Eğer bir önceki wallpaper liked durumda ise yeni görüntü unliked olarak açılacak.
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                MainActivity.likeWallpaper(model,likeImageView);
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        wizardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final wallpaperModel activeModel = getModelOnViewPager();

                if(activeModel != null)
                {
                    MainActivity.ma.showFullScreenActivity(activeModel,baseWallpaperContext, ImageProcessActivity.class,null,null);
                }
            }
        });
    }

    private void cleanSimiliars()
    {
        similiarAdapter.modelList.clear();
        similiarAdapter.notifyDataSetChanged();
        wallpaperModel activeModel = this.wallpaperModelList.get(viewPager.getCurrentItem());
        activeModel.tagsCurrentPage = 0;
    }

    @Override
    public void onBackPressed() {

        if(mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
        {
            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
        {
            int currentIndex =viewPager.getCurrentItem();
            String listData = new Gson().toJson(wallpaperModelList);
            Intent intent = new Intent();


            intent.putExtra("wallpaperList",listData);
            intent.putExtra("listIndex",currentIndex);
            setResult(RESULT_OK,intent);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if(this.popupRecyclerView.getChildCount() == 0 && this.similiarAdapter.getModelList().size() > 0)
        {
            // Bu alan yeni full screen activity açılınca recyclerviewin ve adapterin childlarının temizlenmesinden dolayı reload amacıyla kullanılmaktadır.
            final wallpaperModel activeModel = wallpaperModelList.get(viewPager.getCurrentItem());
            loadSimiliars(activeModel);
        }
    }


    private wallpaperModel getModelOnViewPager()
    {
        if(this.wallpaperModelList.size() >= this.viewPager.getCurrentItem())
        {
            return this.wallpaperModelList.get(viewPager.getCurrentItem());
        }
        else  return null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    protected Fragment setFragment(Fragment fragment, int layoutID) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutID, fragment);
        fragmentTransaction.commit();
        return fragment;
    }

    public void setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.RIGHT);
            slide.setDuration(200);
            slide.setInterpolator(new AccelerateDecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }
    }

    private void toogleUI()
    {
        if(mVisible)
        {
            Animations.slideDown(wizardImageView,1,Animations.TOGGLE_HIDE);
            Animations.slideDown(downloadExpandableContainer,1,Animations.TOGGLE_HIDE);
            Animations.slideDown(shareImageView,1,Animations.TOGGLE_HIDE);
            Animations.slideLeft(leftArea,-1,Animations.TOGGLE_HIDE);
            Animations.slideLeft(rightArea,1,Animations.TOGGLE_HIDE);
            Animations.slideDown(customActionBar,-1,Animations.TOGGLE_HIDE);
            if(downloadExpandableContainer != null) downloadExpandableContainer.setSTATE(ExpandableLinearLayout.COLLAPSED);
        }
        else {

            Animations.slideUp(wizardImageView,1,Animations.TOGGLE_SHOW);
            Animations.slideUp(downloadExpandableContainer,1,Animations.TOGGLE_SHOW);
            Animations.slideUp(shareImageView,1,Animations.TOGGLE_SHOW);
            Animations.slideRight(leftArea, -1,Animations.TOGGLE_SHOW);
            Animations.slideRight(rightArea, 1,Animations.TOGGLE_SHOW);
            Animations.slideUp(customActionBar,-1,Animations.TOGGLE_SHOW);
        }
        mVisible = mVisible ? false : true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        toogleUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void onSwipeDown() {
        if(mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
        {
            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
            model.tagsCurrentPage = 0;
            getSimiliarAsync.cancel(true);
        }
    }

    public void onSwipeUp() {
        Log.i(TAG, "onSwipeUp: " + mSlidingPanel.getPanelState());
        if (mSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            final wallpaperModel activeModel = wallpaperModelList.get(viewPager.getCurrentItem());
            if(activeModel.resolution != null)
            {
                resolutionTextView.setText(activeModel.resolution); //resolution textview content*/
            }
            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            loadSimiliars(activeModel);
        }
    }

    private void loadTagViews(String s,final wallpaperModel activeModel) {
        LayoutInflater layoutInflater = (LayoutInflater) baseWallpaperContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newTag = layoutInflater.inflate(R.layout.tag_textviews, null);
        TextView tag = newTag.findViewById(R.id.tag_view);
        tag.setText(s);
        tagsContainer.addView(newTag);
        final queryModel tagQueryModel = activeModel.getTagQueryModel(activeModel.tagList.indexOf(s));
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: " + tagQueryModel);
                if (tagQueryModel != null && (task == null || task.getStatus() != AsyncTask.Status.RUNNING)) {


                    tagQueryModel.setActivePage(1);
                    task = new HttpGetImagesAsync();

                    String url = tagQueryModel.getUrl();
                    Log.i(TAG, "getImagesOnHttp: " + url);

                    Object[] container = new Object[]{url};

                    ((HttpGetImagesAsync) task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                        @Override
                        public void taskFinished(List<wallpaperModel> list) {
                            MainActivity.ma.showFullScreenActivity(activeModel, baseWallpaperContext, BaseWallpaperActivity.class, list, tagQueryModel);
                        }

                        @Override
                        public void onOneTagLoaded(List<wallpaperModel> list) {

                        }
                    });
                    task.execute(container);
                }
            }
        });
    }

    private void loadTags(final wallpaperModel mModel)
    {

        if(mModel.tagList.size() == 0)
        {
            if(getTagsAsync.getStatus() == AsyncTask.Status.RUNNING || getTagsAsync.getStatus() == AsyncTask.Status.PENDING)
            {
                getTagsAsync.cancel(true);
            }
            resolutionTextView.setText("");
            getTagsAsync = new HttpGetTagsAsync();
            getTagsAsync.setTaskFisinhed(new HttpGetTagsAsync.onAsyncTaskFisinhed() {
                @Override
                public void taskFinished(wallpaperModel model) {
                    mModel.tagList = model.tagList;

                    if(mModel.id == model.id)
                    {
                        tagsContainer.removeAllViews();
                        for (String s :
                                mModel.tagList) {
                            if (s != "") {
                                loadTagViews(s,mModel);
                            }
                        }
                    }

                    if(mSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
                    {
                        loadSimiliars(model);
                    }
                }
            });
            getTagsAsync.execute(mModel);
        }
        else
        {
            Log.i(TAG, "loadTags: model has tags");
        }
    }

    private void loadSimiliars(final wallpaperModel mModel)
    {
        if (mModel.tagList.size() != 0) {

            if(mModel.tagList.size() == 0)
            {
                tagsContainer.removeAllViews();
            }

            if (mModel != null) {


                if (mModel.tagList.size() > 0 ) {
                    if(popupRecyclerView.getAdapter() == null) popupRecyclerView.setAdapter(similiarAdapter);
                    expandableArea.setSTATE(ExpandableLinearLayout.EXPANDED);
                    if(getSimiliarAsync.getStatus() != AsyncTask.Status.PENDING || getSimiliarAsync.getStatus() == AsyncTask.Status.RUNNING)
                    {
                        getSimiliarAsync.cancel(true);
                    }
                    if ((getSimiliarAsync != null || getSimiliarAsync.getStatus() != AsyncTask.Status.RUNNING)) {
                        getSimiliarAsync = new HttpGetImagesAsync();

                        Object[] container = new Object[]{mModel.tagList, mModel};

                        (getSimiliarAsync).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                            @Override
                            public void taskFinished(List<wallpaperModel> list) {
                            }

                            @Override
                            public void onOneTagLoaded(List<wallpaperModel> list) {
                                int firstIndex = similiarAdapter.getItemCount() - 1;
                                similiarAdapter.addModelListToList(list);
                                similiarAdapter.notifyItemRangeChanged(firstIndex,list.size());
                            }
                        });
                        getSimiliarAsync.execute(container);
                    }
                }
            }
        }
    }


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            toogleUI();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            //onSwipeRight();
                        } else {
                            //onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeDown();
                            result =true;
                        } else {
                            onSwipeUp();
                            result =true;
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }


    }


}