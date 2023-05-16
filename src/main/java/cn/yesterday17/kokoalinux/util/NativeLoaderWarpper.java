package cn.yesterday17.kokoalinux.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.SystemUtils;

import net.minecraft.client.Minecraft;

import cn.yesterday17.kokoalinux.exceptions.LoadingException;


/**
 * @author PumpkinXD
 * Not good at naming, just don't blame me
 * (In fact I borrowed some codes from jna5.13 lol)
 */


public class NativeLoaderWarpper {

    /**
     *
     * @param name Library base name
     * @param interfaceClass The implementation wrapper interface
     * @param <T> Type of expected wrapper
     * @return
     */
    public static <T extends Library> T load(String name, Class<T> interfaceClass) {

        NativeLibrary.addSearchPath(name,(new File(Minecraft.getMinecraft().mcDataDir,"mods/kokoalinux/natives")).getAbsolutePath());

        try {
            return interfaceClass.cast(Native.loadLibrary(name,interfaceClass));
        }catch (UnsatisfiedLinkError e){
            try{
                LogManager.getLogger().debug("failed to load natives from System paths and"
                        +(new File(Minecraft.getMinecraft().mcDataDir,"mods/kokoalinux/natives")).getAbsolutePath()
                        +"with following Exception:",
                        e);
                LogManager.getLogger().debug("loading from the resource path!");
                return loadFromResourcePath(name,interfaceClass);
            }catch (Throwable e2) {
                //
                //throw new LoadingException("Failed to load Native Library",e2,true);
                throw new UnsatisfiedLinkError("Unable to load library "+name+" : "+ System.mapLibraryName(name)
                        +" While starting the game."
                        +"\nYou may solve this issue by building the library on your own machine, \nthen copy "
                        +System.mapLibraryName(name)
                        +" to path \n"
                        +"mods/kokoalinux/natives");
                        //+(new File(Minecraft.getMinecraft().mcDataDir,"mods/kokoalinux/natives")).getAbsolutePath());
            }
        }
    }



    public static <T extends Library> T loadFromResourcePath(String name, Class<T> interfaceClass) throws IOException {
        ClassLoader loader = NativeLibrary.class.getClassLoader();

        File tempdir = new File(SystemUtils.getJavaIoTmpDir(),"ACupOfHotCocoa"+System.nanoTime());
        File lib = new File (tempdir,System.mapLibraryName(name));

        String libPathInsideTheJar = "/"+getNativeLibraryResourcePrefix()+System.mapLibraryName(name);


        try (InputStream is= loader.getResourceAsStream(libPathInsideTheJar)){
            Files.copy(is, lib.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            lib.delete();
            throw e;
            //TODO: handle exception
        }
        catch (NullPointerException e) {
            lib.delete();
            throw e;
        }
        Object inst;
        try{
            inst=Native.loadLibrary(lib.getAbsolutePath(),interfaceClass);
        }
        finally {
            if(SystemUtils.IS_OS_UNIX){
                lib.delete();
            }
            else{
                lib.deleteOnExit();
            }
        }
        return interfaceClass.cast(inst);
    }





    static String getCanonicalArchitecture(String arch, int platform) {//borrowed from jna5.13
        arch = arch.toLowerCase().trim();
        if ("powerpc".equals(arch)) {
            arch = "ppc";
        }
        else if ("powerpc64".equals(arch)) {
            arch = "ppc64";
        }
        else if ("i386".equals(arch) || "i686".equals(arch)) {
            arch = "x86";
        }
        else if ("x86_64".equals(arch) || "amd64".equals(arch)) {
            arch = "x86-64";
        }
        else if ("zarch_64".equals(arch)) {
            arch = "s390x";
        }
        // Work around OpenJDK mis-reporting os.arch
        // https://bugs.openjdk.java.net/browse/JDK-8073139
        if ("ppc64".equals(arch) && "little".equals(System.getProperty("sun.cpu.endian"))) {
            arch = "ppc64le";
        }
//        // Map arm to armel if the binary is running as softfloat build
//        if("arm".equals(arch) && platform == Platform.LINUX && isSoftFloat()) { //TODO:detect arm soft float (well... Is that someone playing mcje on armel?)
//            arch = "armel";
//        }
        return arch;
    }


    static String getNativeLibraryResourcePrefix() {
        String osPrefix;
        String arch=getCanonicalArchitecture(SystemUtils.OS_ARCH,Platform.getOSType());
        switch (Platform.getOSType())
        {
            case Platform.WINDOWS:
                osPrefix = "win32-" + arch;
                break;
            case Platform.WINDOWSCE:
                osPrefix = "w32ce-" + arch;
                break;
            case Platform.MAC:
                osPrefix = "darwin-" + arch;
                break;
            case Platform.LINUX:
                osPrefix = "linux-" + arch;
                break;
            case Platform.SOLARIS:
                osPrefix = "sunos-" + arch;
                break;
            case Platform.FREEBSD:
                osPrefix = "freebsd-" + arch;
                break;
            case Platform.OPENBSD:
                osPrefix = "openbsd-" + arch;
                break;
            default:
                if (SystemUtils.IS_OS_NET_BSD)
                {
                    osPrefix = "netbsd-" + arch;
                }
                else {
                    osPrefix = SystemUtils.OS_NAME.toLowerCase();
                    int space = osPrefix.indexOf(" ");
                    if (space != -1) {
                        osPrefix = osPrefix.substring(0, space);
                    }
                    osPrefix += "-" + arch;
                }
                break;
        }
        return osPrefix;
    }
}
