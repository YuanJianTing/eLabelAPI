package com.elabel.api.mvp.model.service;

import com.elabel.api.mvp.model.entity.ResponseBase;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class BaseResponseFunc<T> implements Function<ResponseBase<T>, Observable<T>> {

    @Override
    public Observable<T> apply(ResponseBase<T> tBaseResponse) throws Exception {

        if (tBaseResponse.getCode() == 0) {
            T t= tBaseResponse.getBody();
            return Observable.just(t);
        }
        else{
            return Observable.error(new TagServerErrorException(tBaseResponse));
        }
    }
}
