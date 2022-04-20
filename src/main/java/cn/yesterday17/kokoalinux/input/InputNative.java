package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface InputNative extends Library {
    InputNative instance = (InputNative)Native.loadLibrary(
        (new File("./native/libkokoa.so")).getAbsolutePath(), 
        InputNative.class);

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