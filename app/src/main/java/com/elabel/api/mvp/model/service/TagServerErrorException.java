package com.elabel.api.mvp.model.service;

import androidx.annotation.Nullable;

import com.elabel.api.mvp.model.entity.ResponseBase;

public class TagServerErrorException extends Throwable{
    private ResponseBase responseBase;

    public TagServerErrorException(ResponseBase responseBase, @Nullable Throwable cause) {
        super(cause);
        this.responseBase=responseBase;
    }

    public TagServerErrorException(ResponseBase responseBase) {
        this.responseBase = responseBase;
    }

    public ResponseBase getResponseBase() {
        return responseBase;
    }
}
