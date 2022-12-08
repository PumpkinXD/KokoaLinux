package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;

import org.apache.commons.lang3.SystemUtils;

public interface InputNative extends Library {


    InputNative instance = InputNativeLoader.Load();//gonna test jna3.4 in another demo, maybe...(dumb jna3.4)


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
    static InputNative Load(){//dumb jna3.4----I got to do the "job" for it lmao
        InputNative inst;
        try {
            inst=LoadFromJar();
            } 
        catch (Throwable e) {
                LogManager.getLogger().info("failed to load the lib from the jar",e);
                e.printStackTrace();
                inst=(InputNative)Native.loadLibrary(
                    Paths.get("./mods/kokoalinux/libkokoa","libkokoa.so").toAbsolutePath().toString(),
                    InputNative.class);
        }
        return inst;
    }

    static InputNative LoadFromJar() throws IOException {//https://github.com/adamheinrich/native-utils
    if(tempLibFileDir==null)
        {
           tempLibFileDir = createTempDir();
           tempLibFileDir.deleteOnExit();
        }
        File tempLib=new File (tempLibFileDir,"libkokoa.so");

        String libPathInsideTheJar;
        libPathInsideTheJar="/"+SystemUtils.OS_NAME.toLowerCase()+"-";
        if (Platform.isIntel() && Platform.is64Bit())//amd64
        {
            libPathInsideTheJar+="x86-64";
        }
        else 
        {
            libPathInsideTheJar+=SystemUtils.OS_ARCH.toLowerCase();
        }



        try (InputStream is=InputNativeLoader.class.getResourceAsStream(libPathInsideTheJar+"/libkokoa.so")){
            Files.copy(is, tempLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
        } 
        catch (IOException e) {
            tempLib.delete();
            throw e;
            //TODO: handle exception
        }
        catch (NullPointerException e) {
            tempLib.delete();
        }
        InputNative inputnative_inst;
        try {
            inputnative_inst= (InputNative) Native.loadLibrary(tempLib.getAbsolutePath(),InputNative.class);//may throw java.lang.UnsatisfiedLinkError?
        }         
        finally {
            if(SystemUtils.IS_OS_UNIX){tempLib.delete();}else{tempLib.deleteOnExit();}
        }
            return inputnative_inst;
        }

    static File createTempDir() throws IOException {
        File genDir = new File(SystemUtils.getJavaIoTmpDir(),"kokoa"+System.nanoTime());
        if(!genDir.mkdir()){
            throw new IOException("Failed to create temp dir" + genDir.getName());
        }
        return genDir;
    }



    static File tempLibFileDir;
    static String arch;
    static String os;
    
}