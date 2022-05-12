package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.SystemUtils;

public interface InputNative extends Library {


    InputNative instance =  InputNativeLoader.load();


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

/**
 * InputNativeLoader
 */
 class InputNativeLoader {
    static InputNative load(){
        //todo: extract native lib to ./config/kokoalinux/libkokoa/os-arch/
        String libpath; 
        os=SystemUtils.OS_NAME.toLowerCase();
        if(Platform.isIntel())
        {
            if(Platform.is64Bit())
            {arch="x86-64";}
            else
            {arch="x86";}
        }
        else
        arch=SystemUtils.OS_ARCH;
        
        libpath=Paths.get("./config/kokoalinux/libkokoa",os + "-" + arch,"libkokoa.so").toAbsolutePath().toString();
        return (InputNative)Native.loadLibrary(libpath,InputNative.class);
    }
    static String arch;
    static String os;
    
}