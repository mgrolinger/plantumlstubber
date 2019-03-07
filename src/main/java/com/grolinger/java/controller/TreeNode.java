package com.grolinger.java.controller;

import java.util.Collection;


public class TreeNode<T> {

    private Collection<TreeNode> children;
    private String caption;

    public TreeNode(String caption) {
        super();
        this.caption = caption;
    }

    public Collection<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(Collection<TreeNode> children) {
        this.children = children;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


}