package com.gzlk.android.isp.nim.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.fragment.common.PdfViewerFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;

import java.io.File;
import java.util.Locale;

/**
 * <b>功能描述：</b>文件预览<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/14 14:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/14 14:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FilePreviewHelper {

    /**
     * 根据文件类型打开相应的文件预览
     */
    public static void previewFile(Context context, String path, String fileName, String extension) {
        if (!TextUtils.isEmpty(extension) && extension.toLowerCase(Locale.getDefault()).equals("pdf")) {
            String param = StringHelper.format("%s,%s", path, fileName);
            BaseActivity.openActivity(context, PdfViewerFragment.class.getName(), param, true, false);
            return;
        }
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            File file = new File(path);
            //String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            intent.setDataAndType(Uri.fromFile(file), mimetype);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_nim_attachment_open_failure));
        }
    }
}
