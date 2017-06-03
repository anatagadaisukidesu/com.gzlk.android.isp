package com.gzlk.android.isp.model.common;

import com.gzlk.android.isp.model.Model;

/**
 * <b>功能描述：</b>首页推荐的图片<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/03 23:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/03 23:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FocusImage extends Model {
    //标题
    private String title;
    //链接
    private String targetPath;
    //图片地址
    private String imageUrl;
    //播放间隔
    private String playerInterval;
    //播放帧数
    private String playerFrames;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPlayerInterval() {
        return playerInterval;
    }

    public void setPlayerInterval(String playerInterval) {
        this.playerInterval = playerInterval;
    }

    public String getPlayerFrames() {
        return playerFrames;
    }

    public void setPlayerFrames(String playerFrames) {
        this.playerFrames = playerFrames;
    }
}
