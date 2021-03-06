package com.gzlk.android.isp.fragment.individual;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.common.ImageViewerFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.individual.MomentCommentViewHolder;
import com.gzlk.android.isp.holder.individual.MomentDetailsViewHolder;
import com.gzlk.android.isp.holder.individual.MomentPraiseViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.Moment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>说说详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/23 22:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/23 22:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentDetailsFragment extends BaseMomentFragment {

    public static MomentDetailsFragment newInstance(String params) {
        MomentDetailsFragment mdf = new MomentDetailsFragment();
        Bundle bundle = new Bundle();
        // 单个说说的id
        bundle.putString(PARAM_QUERY_ID, params);
        mdf.setArguments(bundle);
        return mdf;
    }

    public static void open(BaseFragment fragment, String momentId) {
        fragment.openActivity(MomentDetailsFragment.class.getName(), momentId, true, false);
    }

    private static final String PARAM_INDEX = "mdf_selected_index";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mSelectedIndex = bundle.getInt(PARAM_INDEX, -1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_INDEX, mSelectedIndex);
    }

    private MomentDetailsAdapter mAdapter;
    private MomentPraiseViewHolder praiseViewHolder;
    private int mSelectedIndex = -1;

    @Override
    public void doingInResume() {
        showRecorder = false;
        showAppend = false;
        setSendText(R.string.ui_base_text_publish);
        super.doingInResume();
        setCustomTitle(R.string.ui_base_text_details);
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_normal_chatable;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void onSwipeRefreshing() {
        if (null != praiseViewHolder) {
            praiseViewHolder.setHasShown(false);
        }
        fetchingPraises();
        fetchingComments();
    }

    @Override
    protected void onLoadingMore() {
        fetchingComments();
    }

    private ArrayList<ArchiveLike> likes = new ArrayList<>();

    @Override
    protected void onFetchingPraisesComplete(List<ArchiveLike> list, boolean success, int pageSize) {
        if (success) {
            if (null != list) {
                likes.clear();
                likes.addAll(list);
            }
            checkPraises(likes.size() <= 0);
        }
    }

    private void removeComments() {
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            // 移除旧列表里不在list中的记录
            Model model = iterator.next();
            if (model instanceof Comment) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
    }

    @Override
    protected void onFetchingCommentsComplete(List<Comment> list, boolean success, int pageSize) {
        if (success) {
            if (null != list) {
                if (remotePageNumber <= 1) {
                    // 第一页时清空评论列表
                    removeComments();
                }
                if (list.size() >= pageSize) {
                    remotePageNumber++;
                    isLoadingComplete(false);
                } else {
                    isLoadingComplete(true);
                }
                for (Comment comment : list) {
                    if (!mAdapter.exist(comment)) {
                        mAdapter.add(comment);
                    } else {
                        mAdapter.update(comment);
                    }
                }
                smoothScrollToBottom(mAdapter.getItemCount() - 1);
            } else {
                isLoadingComplete(true);
            }
        } else {
            isLoadingComplete(true);
        }
        stopRefreshing();
    }

    @Override
    protected void onFetchingMomentComplete(Moment moment, boolean success) {
        if (success) {
            mMoment = moment;
            // 拉取回来之后立即显示
            if (!mAdapter.exist(moment)) {
                mAdapter.add(moment, 0);
            } else {
                mAdapter.update(moment);
            }
            checkIsMyPraised();
            onSwipeRefreshing();
        }
    }

    @Override
    protected void onCheckIsMyPraisedComplete(boolean success) {
        mMoment.setMyPraised(success);
    }

    private void checkPraises(boolean removable) {
        Model model = new Model();
        model.setId("praises");
        if (removable) {
            if (mAdapter.exist(model)) {
                mAdapter.remove(model);
            }
        } else {
            if (null != praiseViewHolder) {
                praiseViewHolder.setHasShown(false);
            }
            if (!mAdapter.exist(model)) {
                mAdapter.add(model, 1);
            } else {
                mAdapter.notifyItemChanged(mAdapter.indexOf(model));
            }
        }
    }

    @Override
    protected void onPraiseMomentComplete(ArchiveLike archiveLike, boolean success) {
        if (success) {
            mMoment.setMyPraised(true);
            if (null != archiveLike) {
                likes.add(archiveLike);
                checkPraises(false);
            } else {
                fetchingPraises();
            }
        }
    }

    @Override
    protected void onDeletePraiseMomentComplete(boolean success) {
        if (success) {
            mMoment.setMyPraised(false);
            fetchingPraises();
        }
    }

    @Override
    protected void onCommentMomentComplete(Comment comment, boolean success) {
        if (success) {
            if (null != comment) {
                mAdapter.add(comment);
                smoothScrollToBottom(mAdapter.getItemCount() - 1);
            } else {
                fetchingComments();
            }
        }
    }

    private OnInputCompleteListener onInputCompleteListener = new OnInputCompleteListener() {
        @Override
        public void onInputComplete(String text, int length, int type) {
            if (!isEmpty(text)) {
                commentMoment(text);
            }
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mRootView.setBackgroundColor(Color.WHITE);
            addOnInputCompleteListener(onInputCompleteListener);
            mAdapter = new MomentDetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingMoment();
        }
    }

    private MomentDetailsViewHolder.OnMoreClickListener onMoreClickListener = new MomentDetailsViewHolder.OnMoreClickListener() {
        @Override
        public void onClick(View view, int index) {
            mSelectedIndex = index;
            // 已赞和未赞
            int layout = mMoment.isMyPraised() ? R.id.ui_tooltip_moment_comment_praised : R.id.ui_tooltip_moment_comment;
            showTooltip(view, layout, true, TooltipHelper.TYPE_RIGHT, onClickListener);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tooltip_menu_moment_comment:
                case R.id.ui_tooltip_menu_moment_comment1:
                    // 发表评论
                    break;
                case R.id.ui_tooltip_menu_moment_praise:
                    praiseMoment();
                    break;
                case R.id.ui_tooltip_menu_moment_praised:
                    deletePraiseMoment();
                    break;
            }
        }
    };

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            int index = mMoment.getImage().indexOf(url);
            String json = StringHelper.replaceJson(Json.gson().toJson(mMoment.getImage(), new TypeToken<ArrayList<String>>() {
            }.getType()), false);
            openActivity(ImageViewerFragment.class.getName(), format("%d,%s", index, json), false, false, true);
        }
    };

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Comment) {
                UserPropertyFragment.open(MomentDetailsFragment.this, ((Comment) model).getUserId());
            }
        }
    };

    private class MomentDetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_MOMENT = 0, VT_PRAISE = 1, VT_COMMENT = 2;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_MOMENT:
                    MomentDetailsViewHolder holder = new MomentDetailsViewHolder(itemView, MomentDetailsFragment.this);
                    // 赞、评论快捷菜单
                    holder.setOnMoreClickListener(onMoreClickListener);
                    // 图片点击
                    holder.setOnImageClickListener(onImageClickListener);
                    return holder;
                case VT_PRAISE:
                    if (null == praiseViewHolder) {
                        praiseViewHolder = new MomentPraiseViewHolder(itemView, MomentDetailsFragment.this);
                        praiseViewHolder.showContent(likes);
                    }
                    return praiseViewHolder;
                default:
                    MomentCommentViewHolder mcv = new MomentCommentViewHolder(itemView, MomentDetailsFragment.this);
                    mcv.addOnViewHolderClickListener(onViewHolderClickListener);
                    return mcv;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Moment) {
                return VT_MOMENT;
            } else if (model instanceof Comment) {
                return VT_COMMENT;
            }
            return VT_PRAISE;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_MOMENT:
                    return R.layout.holder_view_individual_moment_details;
                case VT_PRAISE:
                    return R.layout.holder_view_individual_moment_praise;
                default:
                    return R.layout.holder_view_individual_moment_comment;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof MomentDetailsViewHolder) {
                ((MomentDetailsViewHolder) holder).showContent((Moment) item);
            } else if (holder instanceof MomentCommentViewHolder) {
                MomentCommentViewHolder hld = (MomentCommentViewHolder) holder;
                hld.showIcon(position == (likes.size() > 0 ? 2 : 1));
                hld.showContent((Comment) item);
            } else if (holder instanceof MomentPraiseViewHolder) {
                MomentPraiseViewHolder hd = (MomentPraiseViewHolder) holder;
                if (!hd.hasShown()) {
                    hd.showContent(likes);
                }
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
