package com.gzlk.android.isp.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.RecycleAdapter;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <b>功能描述：</b>横向RecyclerView<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/01/12 10:20 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/01/12 10:20 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HorizontalRecyclerViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_horizontal_recycler_view)
    private RecyclerView recyclerView;

    private ItemAdapter itemAdapter;

    // 提供给Adapter的数据源
    private List<Item> items = new ArrayList<>();
    // 原始数据
    private List<String> data = new ArrayList<>();

    public HorizontalRecyclerViewHolder(View itemView, BaseSwipeRefreshSupportFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        if (null != recyclerView) {
            CustomLinearLayoutManager llm = new CustomLinearLayoutManager(recyclerView.getContext());
            llm.setOrientation(CustomLinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(llm);
        }
    }

    private boolean displaySelectedEffect = false;

    /**
     * 是否显示选中效果
     */
    public void displaySelectedEffect(boolean display) {
        displaySelectedEffect = display;
    }

    public void setDataSources(String[] strings) {
        setDataSources(Arrays.asList(strings));
    }

    public void setDataSources(List<String> strings) {
        data.clear();
        data.addAll(strings);
        initializeItems();
    }

    private void initializeItems() {
        if (null == itemAdapter) {
            itemAdapter = new ItemAdapter();
            recyclerView.setAdapter(itemAdapter);
            // 一个一个增加底部功能列表
            displayItems();
        }
    }

    private int selectedIndex = -1;

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    /**
     * 显示列表
     */
    public void displayItems() {
        itemAdapter.clear();
        for (String string : data) {
            Item item = new Item();
            item.text = string;
            item.selected = string.contains(format("%d|", selectedIndex));
            itemAdapter.add(item);
        }
    }

    private class ItemAdapter extends RecyclerViewAdapter<TextViewHolder> implements RecycleAdapter<Item> {

        @Override
        public TextViewHolder onCreateViewHolder(View itemView, int viewType) {
            TextViewHolder holder = new TextViewHolder(itemView, fragment());
            holder.showSelectedEffect(displaySelectedEffect);
            holder.addOnViewHolderClickListener(holderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_text_olny;
        }

        @Override
        public void onBindViewHolder(TextViewHolder holder, int position) {
            Item item = items.get(position);
            holder.showContent(item.text, item.selected);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void clear() {
            int size = items.size();
            while (size > 0) {
                remove(size - 1);
                size = items.size();
            }
        }

        @Override
        public void remove(int position) {
            items.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public void remove(Item item) {
            int index = items.indexOf(item);
            remove(index);
        }

        @Override
        public void add(Item item) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }

        @Override
        public void add(Item item, int position) {
            items.add(position, item);
            notifyItemInserted(position);
        }

        @Override
        public boolean exist(Item item) {
            return false;
        }

        @Override
        public boolean isFirstItemFullLine() {
            return false;
        }

    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (displaySelectedEffect) {
                resetClickStatus(index);
            }
            if (null != mOnViewHolderClickListener) {
                mOnViewHolderClickListener.onClick(index);
            }
        }
    };

    private void resetClickStatus(int index) {
        for (int i = 0, len = items.size(); i < len; i++) {
            items.get(i).selected = (i == index);
            itemAdapter.notifyItemChanged(i);
        }
    }

    private class Item {
        String text;
        boolean selected;
    }
}
