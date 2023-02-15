package com.elabel.api.mvp.model;


import com.elabel.api.mvp.model.service.RepositoryManager;

public class BaseModel {
    protected final RepositoryManager mApiManager;

    public BaseModel(RepositoryManager apiManager) {
        this.mApiManager = apiManager;
    }
}
