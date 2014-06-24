package org.sfsteam.easyscrum.data;

import android.os.Environment;

import java.io.Serializable;

/**
 * Created by warmount on 24.06.2014.
 */
public class ImageDT implements Serializable {
    private String alias;
    private String path;
    private String thumbnailName;
    private String thumbnailPath;

    public ImageDT(String alias, String path) {
        this.alias = alias;
        this.path = path;
        this.thumbnailName = Integer.toHexString(path.hashCode()+alias.hashCode()) + ".png";
        this.thumbnailPath = Environment.getExternalStorageDirectory() + "/easyScrum/.thumbnails/" + thumbnailName;

    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageDT other = (ImageDT) obj;
        if (!thumbnailName.equals(other.getThumbnailName()))
            return false;
        if (!alias.equals(other.getAlias()))
            return false;
        if (!path.equals(other.getPath()))
            return false;
        if (!thumbnailPath.equals(other.getThumbnailPath()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 22 * alias.hashCode() + thumbnailPath.hashCode() + thumbnailName.hashCode() + path.hashCode();
    }
}
