package cn.ieclipse.af.view.expendview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ieclipse.af.util.AppUtils;

/**
 * <p>
 * 使用简介：
 * </p>
 * <ol>
 * <li>菜单控件，封装了下拉动画，动态生成头部按钮个数</li>
 * <li>要实现item点击变色同时记录最后一次点击的位置请在adapter中的item设置background的selector如：demo中的list_item_selector</li>
 * <li>adapter中一级菜单的数据模型需继承{@link cn.ieclipse.af.view.expendview.BasePopItem}</li>
 * <li>顶部Tab中的数据模型需要继承{@link cn.ieclipse.af.view.expendview.TabItem}</li>
 * <li>具体使用详见demo</li>
 * </ol>
 *
 * @author czw
 */
public class ExpandTabView<T extends TabItem, D extends BasePopItem> extends LinearLayout implements OnDismissListener,
    View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    private Context mContext;
    // 点击弹出popview动画
    private int mPopViewShowStyle;
    private int mDividerBgColor;
    // 屏幕宽度
    private int displayWidth;
    // 屏幕高度
    private int displayHeight;
    // 当前tab选择position
    private int mCurrentTabPosition;
    // 弹出popview背景颜色
    private int mPopWindowBg = android.R.color.transparent;
    // 顶部tab高度
    private int mToogleBtnMinHeight;
    // popview默认高度
    private int mPopViewHeiht = LayoutParams.WRAP_CONTENT;
    /**
     * 当前要伸展第一层
     */
    public static final int CUR_POP_LEVLE_1 = 0x01;
    /**
     * 当前要伸展第二层
     */
    public static final int CUR_POP_LEVLE_2 = 0x02;
    /**
     * 当前pop list 显示第几菜单
     */
    private int mCurrentPopLevel = CUR_POP_LEVLE_1;
    /**
     * ToggleButton indicator show left
     */
    public static final int INDICATOR_LEFT = 0x01;
    /**
     * ToggleButton indicator show right
     */
    public static final int INDICATOR_RIGHT = 0x02;
    /**
     * 最后一次点击一级list的position
     */
    private int mFirstPosition = -1;
    /**
     * 最后一次点击二级list的position
     */
    private int mSecondPosition = -1;
    /**
     * 最后一次点击tabitem的position
     */
    private int mTabPosition = -1;
    /**
     * ToggleButton 中文本默认颜色
     */
    private int mTabItemTextFocusColor;
    /**
     * ToggleButton 中文本选择颜色
     */
    private int mTabItemTextNormalColor;

    private int mIndicatorLocation;
    private int mIndicatorResid;

    private int mToggleBtnId;
    // 顶部ToggleButton按钮
    private ToggleButton selectedButton;
    // 顶部button集合
    private ArrayList<ToggleButton> mToggleButtons = new ArrayList<>();

    // 二级菜单对应的adapter集合,以tabbutton 为索引存储
    private HashMap<Integer, List<BaseAdapter>> mSecondAdapterMaps = new HashMap<>();
    // 一级菜单adapter集合
    private List<BaseAdapter> mFirstAdapters = new ArrayList<>();
    // 当前使用的二级adapter
    private BaseAdapter mCurrentSecondAdapter;
    // 一级菜单
    private ListView mListView1;
    // 二级菜单
    private ListView mListView2;
    // 弹出popview
    private PopupWindow popupWindow;

    LinearLayout.LayoutParams mTabLayoutParams;

    public ExpandTabView(Context context) {
        super(context);
        init(context);
    }

    public ExpandTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mToogleBtnMinHeight = AppUtils.dp2px(mContext, 40);
        displayWidth = AppUtils.getScreenWidth(mContext);
        displayHeight = AppUtils.getScreenHeight(mContext);
        setOrientation(LinearLayout.HORIZONTAL);

        mTabLayoutParams = new LinearLayout.LayoutParams(0, mToogleBtnMinHeight);
        mTabLayoutParams.weight = 1;
        mTabLayoutParams.gravity = Gravity.CENTER;
    }

    /**
     * 添加顶部togglebtn
     *
     * @param dataList togglebtn中的数据 如：text ,id etc.
     */
    private void addToggleButtons(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int dataSize = dataList.size();
            for (int i = 0; i < dataSize; i++) {
                T item = dataList.get(i);
                ToggleButton button = getToggleButton();
                button.setText(item.title);
                button.setTag(i);
                // 添加toggle btn
                addView(button);
                button.setOnCheckedChangeListener(this);
                // 保存添加的btn
                mToggleButtons.add(button);
            }
        }

        setsetToggleButtonsIndicator(mIndicatorLocation, mIndicatorResid);
    }

    private ToggleButton getToggleButton() {
        ToggleButton button = (ToggleButton) View.inflate(getContext(), mToggleBtnId, null);
        button.setLayoutParams(mTabLayoutParams);
        return button;
    }

    public void setToogleBtnMinHeight(int height) {
        this.mToogleBtnMinHeight = height;
        mTabLayoutParams.height = mToogleBtnMinHeight;
    }

    /**
     * 设置顶部button分隔线
     *
     * @param resourceid 支持int drawable
     */
    public void setTabDividerResource(int resourceid) {
        setDividerDrawable(getResources().getDrawable(resourceid));
        setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    }

    /**
     * 根据选择的位置设置tab item显示的值
     */
    public void setToggleButtonText(String valueText, int position) {
        if (position < mToggleButtons.size()) {
            mToggleButtons.get(position).setText(valueText);
        }
    }

    /**
     * 根据选择的位置获取tabitem显示的值
     */
    public String getToggleButtonText(int position) {
        if (position < mToggleButtons.size() && mToggleButtons.get(position).getText() != null) {
            return mToggleButtons.get(position).getText().toString();
        }
        return "";
    }

    /**
     * 根据索引位置获取tab button
     *
     * @param position
     * @return
     */
    public ToggleButton getToggleButtonAtPosition(int position) {
        int size = mToggleButtons.size();
        if (mToggleButtons != null && size > 0 && position < size) {
            return mToggleButtons.get(position);
        }
        return null;
    }

    /**
     * 设置tooglebtn 字体颜色
     *
     * @param colorNormal
     * @param colorSelected
     */
    public void setToggleButtonTextColor(int colorNormal, int colorSelected) {
        mTabItemTextNormalColor = colorNormal;
        mTabItemTextFocusColor = colorSelected;
    }

    /**
     * 获取tab button的个数
     *
     * @return
     */
    public int getToggleButtonsSize() {
        return mToggleButtons.size();
    }

    /**
     * 设置单个tooglebtn indicator
     *
     * @param position btn 索引
     * @param location indicator 显示位置 {@link #INDICATOR_LEFT} ,{@link #INDICATOR_RIGHT}
     * @param resid    indicator
     */
    public void setsetToggleButtonIndicator(int position, int location, int resid) {
        ToggleButton button = getToggleButtonAtPosition(position);
        if (button != null) {
            if (location == INDICATOR_LEFT) {
                button.setCompoundDrawablesWithIntrinsicBounds(resid, 0, 0, 0);
            }
            else {
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, resid, 0);
            }
        }
    }

    /**
     * 设置所有tooglebtn indicator
     *
     * @param location indicator 显示位置 {@link #INDICATOR_LEFT} ,{@link #INDICATOR_RIGHT}
     * @param resid    drawable id
     */
    public void setsetToggleButtonsIndicator(int location, int resid) {
        this.mIndicatorLocation = location;
        this.mIndicatorResid = resid;
        if (mToggleButtons != null) {
            for (int i = 0; i < mToggleButtons.size(); i++) {
                setsetToggleButtonIndicator(i, location, resid);
            }
        }
    }

    /**
     * 设置PopView背景颜色
     *
     * @param backgroundResid
     */
    public void setPopViewBackgroundResource(int backgroundResid) {
        this.mPopWindowBg = backgroundResid;
    }

    /**
     * 设置PopView弹出消失动画
     *
     * @param style
     * @see #initTabButton(List, int)
     */
    public void setPopViewShowAnimationStyle(int style) {
        this.mPopViewShowStyle = style;
        if (popupWindow != null) {
            setPopupWindowAnimation();
        }
    }

    /**
     * 如果菜单成展开状态，则让菜单收回去
     */
    private boolean onPressBack() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            if (selectedButton != null) {
                selectedButton.setChecked(false);
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public void onDismiss() {
        selectedButton.setChecked(false);
    }
    
    private static void setPopupWindowModal(PopupWindow popupWindow, boolean modal) {
        try {
            Method method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, modal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化tabButton
     *
     * @param dataArray tab list (item must extends TabItem)
     */
    public void initTabButton(List<T> dataArray, int toggleBtnId) {
        mToggleBtnId = toggleBtnId;
        //清空所有添加的togglebtn和分隔线
        removeAllViews();
        // 清空所有缓存的togglebtn
        mToggleButtons.clear();
        // 添加顶部togglebtn
        addToggleButtons(dataArray);
        // 初始化popview
        initPopView();
    }

    private OnExpendItemSelectListener mOnExpendItemSelectListener;

    public interface OnExpendItemSelectListener {
        void onItemSelected(Object res, int index);

        void onSecondItemSelected(Object firstobj, Object secondobj);

        Level onTabButtonClick(int currentTabPosition);
    }

    // 回调
    public void setOnExpendItemSelectListener(OnExpendItemSelectListener listener) {
        this.mOnExpendItemSelectListener = listener;
    }

    /**
     * 有二级菜单时的一级菜单
     */
    private Object mFirstObj;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getAdapter().getItem(position);
        if (parent.getTag() instanceof Level) {
            Level level = (Level) parent.getTag();
            if (level == Level.ONE_LEVEL) {
                mFirstPosition = position;
                mTabPosition = mCurrentTabPosition;
                mSecondPosition = -1;
                // 仅有一级伸展时监听
                if (mOnExpendItemSelectListener != null) {
                    mOnExpendItemSelectListener.onItemSelected(obj, position);
                    onPressBack();
                }
            }
            // 二级伸展中的一级监听
            else if (level == Level.TWO_LEVEL) {
                if (obj instanceof BasePopItem) {
                    mFirstObj = obj;
                    BasePopItem lable = (BasePopItem) obj;
                    // 无childlist时直接点击结束
                    if (!lable.hasChildList) {
                        // 无二级菜单时，清空记录的
                        mTabPosition = -1;
                        mFirstPosition = -1;
                        mSecondPosition = -1;
                        mOnExpendItemSelectListener.onItemSelected(obj, position);
                        return;
                    }
                    // 有childlist时进入二级菜单
                    else if (mSecondAdapterMaps.size() > 0) {
                        List<BaseAdapter> list = mSecondAdapterMaps.get(mCurrentTabPosition);
                        if (list != null && list.size() > 0) {
                            mFirstPosition = position;
                            mCurrentSecondAdapter = list.get(position);
                            mCurrentPopLevel = CUR_POP_LEVLE_2;
                            initPopView(Level.TWO_LEVEL);
                        }
                    }
                }
            }
        }
        // 二级伸展中的二级监听
        else if (parent.getTag() instanceof Integer) {
            mSecondPosition = position;
            mTabPosition = mCurrentTabPosition;
            int popLevel = (int) parent.getTag();
            if (popLevel == CUR_POP_LEVLE_2 && mOnExpendItemSelectListener != null) {
                mOnExpendItemSelectListener.onSecondItemSelected(mFirstObj, obj);
                popupWindow.dismiss();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isChecked()) {
            buttonView.setTextColor(getResources().getColor(mTabItemTextFocusColor));

            ToggleButton tButton = (ToggleButton) buttonView;
            if (selectedButton != null && selectedButton != tButton) {
                selectedButton.setChecked(false);
            }
            selectedButton = tButton;
            mCurrentTabPosition = (Integer) selectedButton.getTag();

            if (mOnExpendItemSelectListener != null && selectedButton.isChecked()) {
                mCurrentPopLevel = CUR_POP_LEVLE_1;
                Level level = mOnExpendItemSelectListener.onTabButtonClick(mCurrentTabPosition);
                // 获取伸展级别进行伸展
                initPopView(level);
            }

            if (mListView2 != null) {
                if (mTabPosition == mCurrentTabPosition) {// 记录点击二级伸展的位置
                    if (mFirstPosition != -1 && mSecondPosition != -1) {
                        mListView2.setVisibility(VISIBLE);
                        mListView1.setItemChecked(mFirstPosition, true);

                        mCurrentPopLevel = CUR_POP_LEVLE_2;
                        initPopView(Level.TWO_LEVEL);
                        mListView2.setItemChecked(mSecondPosition, true);
                    }
                    else {
                        // 记录点击的一级伸展的位置
                        mCurrentPopLevel = CUR_POP_LEVLE_1;
                        mListView1.setItemChecked(mFirstPosition, true);
                    }
                }
                else {
                    mListView2.setAdapter(null);
                }
            }
        }
        else {
            buttonView.setTextColor(getResources().getColor(mTabItemTextNormalColor));
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof RelativeLayout) {
            onPressBack();
        }
    }

    /**
     * 设置二级菜单对应的adapter集合,以tabbutton position为索引存储
     *
     * @param adapterMaps
     */
    public void setSecondAdapterList(HashMap<Integer, List<BaseAdapter>> adapterMaps) {
        this.mSecondAdapterMaps = adapterMaps;
    }

    /**
     * 设置popview的高度默认是 LayoutParams.WRAP_CONTENT
     *
     * @param heitht
     */
    public void setPopViweHeitht(int heitht) {
        this.mPopViewHeiht = heitht;
        if (popupWindow != null) {
            popupWindow.setHeight(heitht);
        }
    }

    private int popListDivider;

    /**
     * 设置pop list divider
     *
     * @param resid
     * @see #initTabButton(List, int)
     */
    public void setPopListDivider(int resid) {
        popListDivider = resid;
        if (mListView1 != null) {
            setListDivider();
        }
    }

    /**
     * 设置一级菜单adapter集合
     *
     * @param adapters
     */
    public void setFirstAdapters(List<BaseAdapter> adapters) {
        this.mFirstAdapters = adapters;
    }

    /**
     * 初始化popview
     */
    private void initPopView() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        // list 1
        mListView1 = new ListView(mContext);
        mListView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView1.setBackgroundResource(android.R.color.white);

        // list 2
        mListView2 = new ListView(mContext);
        mListView2.setVisibility(GONE);
        mListView2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView2.setBackgroundResource(android.R.color.white);
        setListDivider();

        linearLayout.addView(mListView1);
        linearLayout.addView(mListView2);
        linearLayout.setOrientation(HORIZONTAL);

        popupWindow = new PopupWindow(linearLayout, LayoutParams.WRAP_CONTENT, mPopViewHeiht);
        setPopupWindowAnimation();
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(this);
    }

    private void setListDivider() {
        if (popListDivider > 0) {
            mListView1.setDivider(getResources().getDrawable(popListDivider));
            mListView2.setDivider(getResources().getDrawable(popListDivider));
        }
    }

    /**
     * 设置PopupWindow消失弹出动画
     */
    private void setPopupWindowAnimation() {
        if (mPopViewShowStyle > 0) {
            popupWindow.setAnimationStyle(mPopViewShowStyle);
        }
    }

    /**
     * 显示popview 并绑定数据
     *
     * @param level 有几级伸展 {@link Level} etc.
     */
    private void initPopView(Level level) {
        if (popupWindow == null) {
            initPopView();
        }
        if (mCurrentPopLevel == CUR_POP_LEVLE_1) {
            int width = 0;
            if (level == Level.ONE_LEVEL) {
                width = displayWidth;
                mListView1.setTag(Level.ONE_LEVEL);
            }
            else if (level == Level.TWO_LEVEL) {
                width = displayWidth / 2;
                mListView1.setTag(Level.TWO_LEVEL);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            mListView1.setLayoutParams(params);
            mListView2.setLayoutParams(params);

            if (mFirstAdapters != null) {
                BaseAdapter adapter = mFirstAdapters.get(mCurrentTabPosition);
                mListView1.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            mListView1.setOnItemClickListener(this);
            popupWindow.showAsDropDown(this, 0, 0);
        }
        else if (mCurrentPopLevel == CUR_POP_LEVLE_2) {
            mListView2.setTag(mCurrentPopLevel);
            mListView2.setVisibility(VISIBLE);
            mListView2.setAdapter(mCurrentSecondAdapter);
            mCurrentSecondAdapter.notifyDataSetChanged();
            mListView2.setOnItemClickListener(this);
        }
    }

    public static enum Level {
        /**
         * 只有一级菜单
         */
        ONE_LEVEL,
        /**
         * 只有二级菜单
         */
        TWO_LEVEL,
        /**
         * 其他伸展 系统暂留
         */
        OTHER_LEVEL
    }

}
