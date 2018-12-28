package com.mvp.lt.airlineview.kml.bean;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/11/24/024
 */


public class KmlFileBean {
    /**
     * 上级路径s/s/s/name
     */
    private String parentPath;
    /**
     * 当前名
     */
    private String currentFileName;
    /**
     * 当前完整路径 s/s/s/name
     */
    private String currentPath;
    /**
     * 是否是KML文件
     */
    private boolean isKml;

    /**
     * 是否选中显示航线
     */
    private boolean isCHeck = false;

    /**
     * 是否被选中
     */
    private Boolean isCheckBoxed = false;
    /**
     * 是否是编辑状态
     */
    private Boolean isShowCheckBox = false;

    public KmlFileBean(String parentPath, boolean isKml, boolean isCHeck,
                       String currentFileName, String currentPath,
                       Boolean isCheckBoxed, Boolean isShowCheckBox) {
        this.parentPath = parentPath;
        this.isKml = isKml;
        this.isCHeck = isCHeck;
        this.currentFileName = currentFileName;
        this.currentPath = currentPath;
        this.isCheckBoxed = isCheckBoxed;
        this.isShowCheckBox = isShowCheckBox;
    }

    public KmlFileBean() {
    }

    public boolean isCHeck() {
        return isCHeck;
    }

    public void setCHeck(boolean CHeck) {
        isCHeck = CHeck;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public boolean isKml() {
        return isKml;
    }

    public void setKml(boolean kml) {
        isKml = kml;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    public Boolean getCheckBoxed() {
        return isCheckBoxed;
    }

    public void setCheckBoxed(Boolean checkBoxed) {
        isCheckBoxed = checkBoxed;
    }

    public Boolean getShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(Boolean showCheckBox) {

        isShowCheckBox = showCheckBox;
    }

    @Override
    public String toString() {
        return "KmlFileBean{" +
                "parentPath='" + parentPath + '\'' +
                ", currentFileName='" + currentFileName + '\'' +
                ", currentPath='" + currentPath + '\'' +
                ", isKml=" + isKml +
                ", isCHeck=" + isCHeck +
                ", isCheckBoxed=" + isCheckBoxed +
                ", isShowCheckBox=" + isShowCheckBox +
                '}';
    }
}
