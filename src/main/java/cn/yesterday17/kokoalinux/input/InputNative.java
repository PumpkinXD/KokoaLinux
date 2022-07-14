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
        //todo: ~~extract native lib to ./config/kokoalinux/libkokoa/os-arch/~~ gonna deprecate this method, f*** jna3.4
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
    static InputNative LoadFromJar() throws IOException {//https://github.com/adamheinrich/native-utils
    if(tempLibFileDir==null)
        {
           tempLibFileDir = createTempDir();
           tempLibFileDir.deleteOnExit();
        }
        File tempLib=new File (tempLibFileDir,"libkokoa.so");

        try (InputStream is=InputNativeLoader.class.getResourceAsStream(/*path to so, with plat dect*/)){
            Files.copy(is, tempLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            tempLib.delete();
            throw e;
            //TODO: handle exception
        }
        catch (NullPointerException e) {
            tempLib.delete();
        }
        InputNative inputnative_inst;
        try {
            inputnative_inst=(Native)Native.loadLibrary(tempLib.getAbsolutePath(),InputNative.class);
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