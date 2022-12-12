package cn.yesterday17.kokoalinux.input;

import cn.yesterday17.kokoalinux.utils.NativeLoader;

import com.sun.jna.Library;


public interface InputNative extends Library {

    InputNative instance = (InputNative) NativeLoader.Load("kokoa",InputNative.class);

    /////////////////////////////////////

    void setDisplay(long display);

    void setWindow(long window);

    /////////////////////////////////////

    long openIM();

    long createIC();

    void closeIM();

    void destroyIC();

    /////////////////////////////////////

    long toggleIC(long active);

    void prepareLocale();

    void setDebug(long debug);
}