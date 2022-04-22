package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Library;
import com.sun.jna.Native;
//import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface InputNative extends Library {
/*
    InputNative instance = (InputNative)Native.loadLibrary(
        (new File("./native/libkokoa.so")).getAbsolutePath(), 
        InputNative.class);
*/
InputNative instance = (InputNative)Native.loadLibrary(
        Paths.get("./native/libkokoa.so").toAbsolutePath().toString(), 
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