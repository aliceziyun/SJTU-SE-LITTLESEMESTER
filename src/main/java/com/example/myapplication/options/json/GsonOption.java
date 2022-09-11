package com.example.myapplication.options.json;

import com.example.myapplication.options.Option;
import com.google.firebase.database.annotations.NotNull;

public class GsonOption extends Option {

    /**
     * 获取toString值
     */
    @Override
    public @NotNull
    String toString() {
        return GsonUtil.format(this);
    }

    /**
     * 获取toPrettyString值
     */
    public String toPrettyString() {
        return GsonUtil.prettyFormat(this);
    }

}
