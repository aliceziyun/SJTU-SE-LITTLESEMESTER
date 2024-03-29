package com.example.myapplication.options.style;

import java.io.Serializable;

import com.example.myapplication.options.style.itemstyle.Emphasis;
import com.example.myapplication.options.style.itemstyle.Normal;

public class ItemStyle implements Serializable {

    private static final long serialVersionUID = 418674375057055357L;

    /**
     * 阳线颜色
     */
    private Object color;

    /**
     * 默认样式
     */
    private Normal normal;
    /**
     * 强调样式（悬浮时样式）
     */
    private Emphasis emphasis;
    /**
     * 面包屑
     */
    private Breadcrumb breadcrumb;
    /**
     * 二级边框宽度
     */
    private Integer borderWidth;
    /**
     * 二级边框颜色
     */
    private Object borderColor;

    private Integer borderRadius;

    private Object shadowColor;

    private Integer shadowBlur;

    /**
     * 获取normal值
     */
    public Normal normal() {
        if (this.normal == null) {
            this.normal = new Normal();
        }
        return this.normal;
    }

    /**
     * 设置normal值
     *
     * @param normal
     */
    public ItemStyle normal(Normal normal) {
        this.normal = normal;
        return this;
    }

    /**
     * 获取emphasis值
     */
    public Emphasis emphasis() {
        if (this.emphasis == null) {
            this.emphasis = new Emphasis();
        }
        return this.emphasis;
    }

    /**
     * 设置emphasis值
     *
     * @param emphasis
     */
    public ItemStyle emphasis(Emphasis emphasis) {
        this.emphasis = emphasis;
        return this;
    }

    /**
     * 获取normal值
     */
    public Normal getNormal() {
        return normal;
    }

    //以下属性是TreeMap特有

    /**
     * 设置normal值
     *
     * @param normal
     */
    public void setNormal(Normal normal) {
        this.normal = normal;
    }

    /**
     * 获取emphasis值
     */
    public Emphasis getEmphasis() {
        return emphasis;
    }

    /**
     * 设置emphasis值
     *
     * @param emphasis
     */
    public void setEmphasis(Emphasis emphasis) {
        this.emphasis = emphasis;
    }

    /**
     * 设置breadcrumb值
     *
     * @param breadcrumb
     */
    public ItemStyle breadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
        return this;
    }

    /**
     * 获取breadcrumb值
     */
    public Breadcrumb breadcrumb() {
        if (this.breadcrumb == null) {
            this.breadcrumb = new Breadcrumb();
        }
        return this.breadcrumb;
    }

    /**
     * 设置borderWidth值
     *
     * @param borderWidth
     */
    public ItemStyle borderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    /**
     * 获取childBorderWidth值
     */
    public Integer borderWidth() {
        return this.borderWidth;
    }

    /**
     * 设置borderColor值
     *
     * @param borderColor
     */
    public ItemStyle borderColor(Object borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    /**
     * 获取borderColor值
     */
    public Object borderColor() {
        return this.borderColor;
    }

    /**
     * 设置borderRadius值
     *
     * @param borderRadius
     */
    public ItemStyle borderRadius(Integer borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }

    /**
     * 获取borderRadius值
     */
    public Integer borderRadius() {
        return this.borderRadius;
    }

    /**
     * 设置shadowColor值
     *
     * @param shadowColor
     */
    public ItemStyle shadowColor(Object shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    /**
     * 获取shadowColor值
     */
    public Object shadowColor() {
        return this.shadowColor;
    }

    /**
     * 设置shadowBlur值
     *
     * @param shadowBlur
     */
    public ItemStyle shadowBlur(Integer shadowBlur) {
        this.shadowBlur = shadowBlur;
        return this;
    }

    /**
     * 获取shadowBlur值
     */
    public Integer shadowBlur() {
        return this.shadowBlur;
    }

    /**
     * 获取breadcrumb值
     */
    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    /**
     * 设置breadcrumb值
     *
     * @param breadcrumb
     */
    public void setBreadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    /**
     * 获取childBorderWidth值
     */
    public Integer getBorderWidth() {
        return borderWidth;
    }

    /**
     * 设置borderWidth值
     *
     * @param borderWidth
     */
    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * 获取borderColor值
     */
    public Object getBorderColor() {
        return borderColor;
    }

    /**
     * 设置borderColor值
     *
     * @param borderColor
     */
    public void setBorderColor(Object borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * 获取borderRadius值
     */
    public Integer getBorderRadius() {
        return borderRadius;
    }

    /**
     * 设置borderRadius值
     *
     * @param borderRadius
     */
    public void setBorderRadius(Integer borderRadius) {
        this.borderRadius = borderRadius;
    }

    /**
     * 获取shadowColor值
     */
    public Object getShadowColor() {
        return shadowColor;
    }

    /**
     * 设置shadowColor值
     *
     * @param shadowColor
     */
    public void setShadowColor(Object shadowColor) {
        this.shadowColor = shadowColor;
    }

    /**
     * 获取shadowBlur值
     */
    public Integer getShadowBlur() {
        return shadowBlur;
    }

    /**
     * 设置shadowBlur值
     *
     * @param shadowBlur
     */
    public void setShadowBlur(Integer shadowBlur) {
        this.shadowBlur = shadowBlur;
    }

    /**
     * 获取color值
     */
    public Object color() {
        return this.color;
    }

    /**
     * 设置color值
     *
     * @param color
     */
    public ItemStyle color(Object color) {
        this.color = color;
        return this;
    }

    /**
     * 获取color值
     */
    public Object getColor() {
        return color;
    }

    /**
     * 设置color值
     *
     * @param color
     */
    public void setColor(Object color) {
        this.color = color;
    }
}
