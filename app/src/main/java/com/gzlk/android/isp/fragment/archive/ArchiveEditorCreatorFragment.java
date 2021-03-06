package com.gzlk.android.isp.fragment.archive;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.etc.ImageCompress;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseImageSelectableSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.richeditor.RichEditor;

/**
 * <b>功能描述：</b>通过编辑器新建档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/04 18:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveEditorCreatorFragment extends BaseImageSelectableSupportFragment {

    private static final String PARAM_UPLOAD_TYPE = "aecf_upload_type";
    private static final String PARAM_ARCHIVE = "aecf_archive_content";

    private static final int UP_NOTHING = 0;
    private static final int UP_IMAGE = 1;
    private static final int UP_MUSIC = 2;
    private static final int UP_VIDEO = 3;
    private static final int UP_ATTACH = 4;

    public static ArchiveEditorCreatorFragment newInstance(String params) {
        ArchiveEditorCreatorFragment aecf = new ArchiveEditorCreatorFragment();
        Bundle bundle = new Bundle();
        // 传过来的组织id，也即要创建的档案所属的组织，为empty时创建的是个人档案
        bundle.putString(PARAM_QUERY_ID, params);
        aecf.setArguments(bundle);
        return aecf;
    }

    public static void open(BaseFragment fragment, String groupId) {
        fragment.openActivity(ArchiveEditorCreatorFragment.class.getName(), groupId, true, true);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        uploadType = bundle.getInt(PARAM_UPLOAD_TYPE, UP_NOTHING);
        String json = bundle.getString(PARAM_ARCHIVE, "");
        if (!isEmpty(json)) {
            mArchive = Json.gson().fromJson(json, new TypeToken<Archive>() {
            }.getType());
        } else {
            mArchive = new Archive();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_UPLOAD_TYPE, uploadType);
        bundle.putString(PARAM_ARCHIVE, Json.gson().toJson(mArchive, new TypeToken<Archive>() {
        }.getType()));
    }

    @ViewId(R.id.ui_archive_creator_rich_editor_title)
    private ClearEditText titleView;
    @ViewId(R.id.ui_archive_creator_rich_editor_content)
    private RichEditor mEditor;
    @ViewId(R.id.ui_archive_creator_toolbar_top_line)
    private View toolbarTopLine;
    @ViewId(R.id.ui_archive_creator_action_font)
    private CustomTextView mFontIcon;
    @ViewId(R.id.ui_archive_creator_font_style_layout)
    private View fontStyleLayout;
    @ViewId(R.id.ui_archive_creator_rich_editor_uploader)
    private View updatingIndicator;
    // 创建成功的档案信息
    private Archive mArchive;
    /**
     * 当前上传的文件类型：1=图片，2=音乐，3=视频
     */
    private int uploadType = UP_NOTHING;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setBackgroundColor(Color.WHITE);
        mEditor.setPlaceholder(StringHelper.getString(R.string.ui_text_archive_creator_content_hint));
        mEditor.setOnTextChangeListener(textChangeListener);
        // 每次最大选取1张图片
        maxSelectable = 1;
        // 压缩图片
        isSupportCompress = true;
        // 直接上传图片
        isSupportDirectlyUpload = true;
        // 不需要显示上传进度
        needShowUploading = false;
        // 本地图片选择
        addOnImageSelectedListener(imageSelectedListener);
        // 文件上传完毕
        setOnFileUploadingListener(uploadingListener);

        if (Build.VERSION.SDK_INT < 19) {
            toolbarTopLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_creator_richeditor;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_document_create_fragment_title);
        resetRightIcons();
    }

    private void resetRightIcons() {
        setRightText(null == mArchive || isEmpty(mArchive.getId()) ? R.string.ui_base_text_save : 0);
        setRightIcon(null != mArchive && !isEmpty(mArchive.getId()) ? R.string.ui_icon_more : 0);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (null == mArchive || isEmpty(mArchive.getId())) {
                    // 创建档案
                    tryCreateArchive();
                } else {
                    // 显示附加菜单信息
                }
            }
        });
    }

    private void tryCreateArchive() {
        String title = titleView.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_title_invalid);
            return;
        }
        mArchive.setTitle(title);
        String html = mEditor.getHtml();
        if (isEmpty(html)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_content_invalid);
            return;
        }
        createArchive();
    }

    private RichEditor.OnTextChangeListener textChangeListener = new RichEditor.OnTextChangeListener() {
        @Override
        public void onTextChange(String s) {
            String text = s.replace("\\u003C", "<");
            // 文本为空或者首字符是 < 时，说明一般为 HTML 字符串
            if (isEmpty(text) || text.contains("html:")) {
                String html = text.replace("html:", "");
                mArchive.setContent(html);
                log("HTML: " + text);
                mEditor.getMarkdown();
            } else if (text.contains("mark:")) {
                mArchive.setMarkdown(text.replace("mark:", ""));
                log("MARK: " + text);
            }
        }
    };

    private void createArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    mArchive = archive;
                    resetRightIcons();
                }
            }
        }).add(mArchive);
    }

    @Override
    protected boolean checkStillEditing() {
        return !isEmpty(titleView.getValue());
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    // 插入图片的对话框
    private View imageDialogView;
    private ClearEditText imageAlt, imageUrl, imageWidth, imageHeight;

    private void openImageDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == imageDialogView) {
                    imageDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_image, null);
                    imageAlt = (ClearEditText) imageDialogView.findViewById(R.id.ui_popup_rich_editor_image_alt);
                    imageUrl = (ClearEditText) imageDialogView.findViewById(R.id.ui_popup_rich_editor_image_url);
                    imageWidth = (ClearEditText) imageDialogView.findViewById(R.id.ui_popup_rich_editor_image_width);
                    imageHeight = (ClearEditText) imageDialogView.findViewById(R.id.ui_popup_rich_editor_image_height);
                }
                return imageDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_closer, R.id.ui_popup_rich_editor_image_navigate};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_image_navigate:
                        openImageSelector(true);
                        // 图片选择时，不需要关闭对话框，返回之后还要上传
                        return false;
                }
                return true;
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String url = imageUrl.getValue();
                if (!isEmpty(url)) {
                    if (Utils.isUrl(url)) {
                        // 如果是粘贴的网络地址，则直接插入图片
                        insertImage(url, imageAlt.getValue());
                    } else {
                        // 压缩并上传图片，然后插入
                        uploadType = UP_IMAGE;
                        showUploading(true);
                        compressImage();
                    }
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_image_select_url_error);
                }
                return true;
            }
        }).setPopupType(DialogHelper.TYPE_FADE).show();
    }

    private void insertImage(String url, String alt) {
        String width = imageWidth.getValue();
        String height = imageHeight.getValue();
        int w = Integer.valueOf(isEmpty(width) ? "0" : width);
        int h = Integer.valueOf(isEmpty(height) ? "0" : height);
        if (w <= 0 || h <= 0) {
            mEditor.insertImage(url, alt);
        } else {
            mEditor.insertImage(url, alt, w, h);
        }
        if (null != imageUrl) {
            imageUrl.setValue("");
            imageAlt.setValue("");
            imageWidth.setValue("");
            imageHeight.setValue("");
        }
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            if (null != selected && selected.size() > 0) {
                imageUrl.setValue(selected.get(0));
            } else {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_image_selected_nothing);
            }
        }
    };

    private void showUploading(boolean shown) {
        updatingIndicator.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onUploadingFailed() {
        showUploading(false);
        ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_attachment_uploading_failed);
    }

    private OnFileUploadingListener uploadingListener = new OnFileUploadingListener() {
        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            showUploading(false);
            switch (uploadType) {
                case UP_IMAGE:
                    // 图片上传完毕，插入图片
                    String url = uploaded.get(0).getUrl();
                    // 如果上传完毕的是图片，则插入图片
                    if (ImageCompress.isImage(Attachment.getExtension(url))) {
                        insertImage(url, imageAlt.getValue());
                        mArchive.getImage().add(uploaded.get(0));
                    }
                    break;
                case UP_MUSIC:
                    // 视频上传完毕，插入视频到编辑框中
                    String music = uploaded.get(0).getUrl();
                    mEditor.insertAudio(music);
                    musicUrl.setValue("");
                    musicSize.setVisibility(View.GONE);
                    mArchive.getAttach().add(uploaded.get(0));
                    break;
                case UP_VIDEO:
                    // 视频上传完毕，插入视频到编辑框中
                    String video = uploaded.get(0).getUrl();
                    mEditor.insertVideo(StringHelper.getString(R.string.ui_text_archive_creator_editor_video_cover_default), video);
                    videoUrl.setValue("");
                    videoSize.setVisibility(View.GONE);
                    mArchive.getVideo().add(uploaded.get(0));
                    break;
            }
            // 插入完毕之后清空已上传的文件列表
            getUploadedFiles().clear();
        }
    };

    // 插入音乐的对话框
    private View musicDialogView;
    private ClearEditText musicUrl;
    private TextView musicSize;

    private void openMusicDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == musicDialogView) {
                    musicDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_music, null);
                    musicUrl = (ClearEditText) musicDialogView.findViewById(R.id.ui_popup_rich_editor_music_url);
                    musicSize = (TextView) musicDialogView.findViewById(R.id.ui_popup_rich_editor_music_size);
                }
                return musicDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_closer, R.id.ui_popup_rich_editor_music_navigate};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_music_navigate:
                        // 打开文件选择器
                        chooseVideoFromLocalBeforeKitKat(REQUEST_MUSIC);
                        return false;
                }
                return true;
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String url = musicUrl.getValue();
                if (isEmpty(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_music_select_dialog_url_empty);
                    return false;
                }
                if (Utils.isUrl(url)) {
                    mEditor.insertAudio(url);
                } else {
                    uploadType = UP_MUSIC;
                    showUploading(true);
                    // 上传本地视频文件
                    getWaitingForUploadFiles().clear();
                    getWaitingForUploadFiles().add(url);
                    uploadFiles();
                }
                return true;
            }
        }).setPopupType(DialogHelper.TYPE_FADE).show();
    }

    // 视频选择对话框
    private View videoDialogView;
    private ClearEditText videoUrl;
    private TextView videoSize;

    private void openVideoDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == videoDialogView) {
                    videoDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_video, null);
                    //videoCover = (ClearEditText) videoDialogView.findViewById(R.id.ui_popup_rich_editor_video_cover);
                    videoUrl = (ClearEditText) videoDialogView.findViewById(R.id.ui_popup_rich_editor_video_url);
                    videoSize = (TextView) videoDialogView.findViewById(R.id.ui_popup_rich_editor_video_size);
                }
                return videoDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_closer, R.id.ui_popup_rich_editor_video_navigate};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_video_navigate:
                        // 打开文件选择器选择视频文件
                        chooseLocalVideo();
                        return false;
                }
                return true;
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String url = videoUrl.getValue();
                if (isEmpty(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_video_select_dialog_url_empty);
                    return false;
                }
                if (Utils.isUrl(url)) {
                    mEditor.insertVideo(StringHelper.getString(R.string.ui_text_archive_creator_editor_video_cover_default), url);
                } else {
                    uploadType = UP_VIDEO;
                    showUploading(true);
                    // 上传本地视频文件
                    getWaitingForUploadFiles().clear();
                    getWaitingForUploadFiles().add(url);
                    uploadFiles();
                }
                return true;
            }
        }).setPopupType(DialogHelper.TYPE_FADE).show();
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_VIDEO) {
            // 视频选择返回了
            String path = getGalleryResultedPath(data);
            videoUrl.setValue(path);
            showFileSize(true, path, videoSize);
        } else if (requestCode == REQUEST_MUSIC) {
            // 音乐文件选择返回了
            String path = getGalleryResultedPath(data);
            musicUrl.setValue(path);
            showFileSize(false, path, musicSize);
        }
        super.onActivityResult(requestCode, data);
    }

    private void showFileSize(boolean video, String path, TextView view) {
        File file = new File(path);
        view.setText(StringHelper.getString((video ? R.string.ui_text_archive_creator_editor_video_select_dialog_file_size : R.string.ui_text_archive_creator_editor_music_select_dialog_file_size), Utils.formatSize(file.length())));
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 获取文件路径
     *
     * @param data intent数据
     * @return 文件路径
     */
    private String filePathFromIntent(Intent data) {
        if (null == data) {
            return null;
        }

        Uri uri = data.getData();

        String path = null;
        try {
            Cursor cursor = Activity().getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                //miui 2.3 有可能为null
                path = uri.getPath();
            } else {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex("_data")); // 文件路径
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private void chooseLocalVideo() {
        //if (Build.VERSION.SDK_INT >= 19) {
        //    chooseVideoFromLocalKitKat();
        //} else {
        chooseVideoFromLocalBeforeKitKat(REQUEST_VIDEO);
        //}
    }

    /**
     * API19 之后选择视频
     */
    protected void chooseVideoFromLocalKitKat() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        try {
            startActivityForResult(intent, REQUEST_VIDEO);
        } catch (ActivityNotFoundException e) {
            ToastHelper.make().showMsg(com.netease.nim.uikit.R.string.gallery_invalid);
        } catch (SecurityException e) {
            e.printStackTrace();
            ToastHelper.make().showMsg("看起来您的手机无法浏览视频文件，请联系开发人员");
        }
    }

    /**
     * API19 之前选择视频
     */
    protected void chooseVideoFromLocalBeforeKitKat(int request) {
        Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mIntent.setType(request == REQUEST_VIDEO ? "video/*" : "audio/*");
        mIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        try {
            startActivityForResult(mIntent, request);
        } catch (ActivityNotFoundException e) {
            ToastHelper.make().showMsg(com.netease.nim.uikit.R.string.gallery_invalid);
        }
    }

    // 插入超链接
    private View linkDialogView;
    private ClearEditText linkLabel, linkUrl;

    private void openLinkDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == linkDialogView) {
                    linkDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_link, null);
                    linkLabel = (ClearEditText) linkDialogView.findViewById(R.id.ui_popup_rich_editor_link_label);
                    linkUrl = (ClearEditText) linkDialogView.findViewById(R.id.ui_popup_rich_editor_link_url);
                }
                return linkDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_closer};
            }

            @Override
            public boolean onClick(View view) {
                return true;
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String label = linkLabel.getValue();
                String url = linkUrl.getValue();
                if (isEmpty(label) || isEmpty(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_link_dialog_invalid_input);
                    return false;
                }
                if (!Utils.isUrl(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_link_dialog_invalid_url);
                    return false;
                }
                mEditor.insertLink(url, label);
                return true;
            }
        }).setPopupType(DialogHelper.TYPE_FADE).show();
    }

    @Click({R.id.ui_archive_creator_action_undo, R.id.ui_archive_creator_action_redo,
            R.id.ui_archive_creator_action_image, R.id.ui_archive_creator_action_font,
            R.id.ui_archive_creator_action_attachment, R.id.ui_archive_creator_action_video,
            R.id.ui_archive_creator_action_audio, R.id.ui_archive_creator_action_quote,
            R.id.ui_archive_creator_action_link, R.id.ui_archive_creator_action_ordered_list,
            R.id.ui_archive_creator_action_unordered_list,
            // 一下为字体格式设置
            R.id.ui_archive_creator_action_bold, R.id.ui_archive_creator_action_italic,
            R.id.ui_archive_creator_action_underline, R.id.ui_archive_creator_action_strike_through,
            R.id.ui_archive_creator_action_superscript, R.id.ui_archive_creator_action_subscript,
            R.id.ui_archive_creator_action_align_left, R.id.ui_archive_creator_action_align_center,
            R.id.ui_archive_creator_action_align_right,
            R.id.ui_archive_creator_action_heading1, R.id.ui_archive_creator_action_heading2,
            R.id.ui_archive_creator_action_heading3, R.id.ui_archive_creator_action_heading4,
            R.id.ui_archive_creator_action_heading5, R.id.ui_archive_creator_action_heading6})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_archive_creator_action_undo:
                mEditor.undo();
                break;
            case R.id.ui_archive_creator_action_redo:
                mEditor.redo();
                break;
            case R.id.ui_archive_creator_action_image:
                openImageDialog();
                break;
            case R.id.ui_archive_creator_action_font:
                fontStyleLayout.setVisibility(fontStyleLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mFontIcon.setTextColor(getColor(fontStyleLayout.getVisibility() == View.GONE ? R.color.textColorHint : R.color.colorAccent));
                break;
            case R.id.ui_archive_creator_action_attachment:
                // 插入一个附件
                // 附件的图标地址(24x24)http://120.25.124.199:8008/group1/M00/00/13/eBk66lngZ0-AW_1aAAAJiqCeKro517.png
                break;
            case R.id.ui_archive_creator_action_video:
                // 插入或上传一段视频
                // 视频封面地址：http://120.25.124.199:8008/group1/M00/00/13/eBk66lngcsOAUoWUAAAcAlhYhMk172.png
                openVideoDialog();
                break;
            case R.id.ui_archive_creator_action_audio:
                // 插入或上传一段音乐
                openMusicDialog();
                break;
            case R.id.ui_archive_creator_action_quote:
                mEditor.setBlockquote();
                break;
            case R.id.ui_archive_creator_action_link:
                // 插入一个超链接
                openLinkDialog();
                break;
            case R.id.ui_archive_creator_action_ordered_list:
                mEditor.setOrderedList();
                break;
            case R.id.ui_archive_creator_action_unordered_list:
                mEditor.setUnorderedList();
                break;

            case R.id.ui_archive_creator_action_bold:
                mEditor.setBold();
                break;
            case R.id.ui_archive_creator_action_italic:
                mEditor.setItalic();
                break;
            case R.id.ui_archive_creator_action_underline:
                mEditor.setUnderline();
                break;
            case R.id.ui_archive_creator_action_strike_through:
                mEditor.setStrikeThrough();
                break;
            case R.id.ui_archive_creator_action_superscript:
                mEditor.setSuperscript();
                break;
            case R.id.ui_archive_creator_action_subscript:
                mEditor.setSubscript();
                break;
            case R.id.ui_archive_creator_action_align_left:
                mEditor.setAlignLeft();
                break;
            case R.id.ui_archive_creator_action_align_center:
                mEditor.setAlignCenter();
                break;
            case R.id.ui_archive_creator_action_align_right:
                mEditor.setAlignRight();
                break;
            case R.id.ui_archive_creator_action_heading1:
                mEditor.setHeading(1);
                break;
            case R.id.ui_archive_creator_action_heading2:
                mEditor.setHeading(2);
                break;
            case R.id.ui_archive_creator_action_heading3:
                mEditor.setHeading(3);
                break;
            case R.id.ui_archive_creator_action_heading4:
                mEditor.setHeading(4);
                break;
            case R.id.ui_archive_creator_action_heading5:
                mEditor.setHeading(5);
                break;
            case R.id.ui_archive_creator_action_heading6:
                mEditor.setHeading(6);
                break;
        }
    }
}
