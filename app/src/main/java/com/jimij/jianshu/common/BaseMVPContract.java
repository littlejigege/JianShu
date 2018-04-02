package com.jimij.jianshu.common;


import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;

/**
 * Created by Chatikyan on 20.05.2017.
 */

public interface BaseMVPContract {

    interface View {

    }

    interface Presenter<V extends BaseMVPContract.View> {

    }
}
