package com.saturn.controllers;

/**
 * @deprecated
 */
class Follow {
    public Follow() {
    }

    public Follow(Long id, Boolean follow) {
        this.id = id;
        this.follow = follow;

    }

    private Long id;
    private Boolean follow;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }
}
