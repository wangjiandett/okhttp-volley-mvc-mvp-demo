/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.okhttp;

import java.io.File;
import java.util.HashMap;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/25.
 */
public abstract class OKUploadController<Listener> extends OKHttpController<Listener> {


    public OKUploadController() {
        super();
    }

    public OKUploadController(Listener l) {
        super();
        setListener(l);
    }

    protected abstract class BaseUpLoadTask<Output> extends LoadTask<File, Output> {

        @Override
        protected RequestBody postBody(File input) {
            // 设置请求体
            MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
            RequestBody body = MultipartBody.create(MEDIA_TYPE_PNG, input);

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("file", input.getName(), body);

            // 封装请求参数
            HashMap<String, String> params = new HashMap<>();
            addParams(params);
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
                }
            }

            return builder.build();
        }

        /**
         * 添加请求键值对
         */
        protected abstract void addParams(HashMap<String, String> params);
    }
}
