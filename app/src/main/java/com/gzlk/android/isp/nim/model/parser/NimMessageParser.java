package com.gzlk.android.isp.nim.model.parser;

import com.gzlk.android.isp.nim.model.NimMessage;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>自定义消息解析器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/18 23:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/18 23:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NimMessageParser implements MsgAttachmentParser {

    private static final String KEY_TYPE = "type";
    private static final String KEY_CONTENT = "msgContent";
    private static final String KEY_UUID = "uuid";

    @Override
    public MsgAttachment parse(String s) {
        try {
            NimMessage msg = new NimMessage();
            JSONObject object = new JSONObject(s);
            if (object.has(KEY_TYPE)) {
                msg.setType(object.getInt(KEY_TYPE));
            }
            if (object.has(KEY_CONTENT)) {
                msg.setMsgContent(object.getString(KEY_CONTENT));
            }
            if (object.has(KEY_UUID)) {
                msg.setUuid(object.getString(KEY_UUID));
            }
            return msg;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String packData(NimMessage msg) {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_TYPE, msg.getType());
            object.put(KEY_CONTENT, msg.getMsgContent());
            object.put(KEY_UUID, msg.getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}