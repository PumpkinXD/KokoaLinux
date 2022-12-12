package cn.yesterday17.kokoalinux.utils;

import cn.yesterday17.kokoalinux.KokoaLinux;


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




public class NativeLoader {

    private NativeLoader(){}//lol

    public static Object Load(String libname_simp, Class interfaceclass){
        String plat_libname=System.mapLibraryName(libname_simp);
        Object theLIB=null;

        boolean loadFromFallbackDir=false;
        if(tempLibFileDir==null) {
            try {
                tempLibFileDir = createTempDir();
                tempLibFileDir.deleteOnExit();
            } catch (IOException e) {
                loadFromFallbackDir = true;
            }
        }
        if(tempLibFileDir!=null)
        {
            File tempLib=new File (tempLibFileDir,plat_libname);
            try(InputStream is = NativeLoader.class.getResourceAsStream(getNativeLibraryResourcePrefix()+File.pathSeparator+plat_libname)) {
                Files.copy(is, tempLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                loadFromFallbackDir = true;
            } catch (NullPointerException e) {
                tempLib.delete();
                loadFromFallbackDir = true;
            }
            try {
                theLIB = Native.loadLibrary(tempLib.getAbsolutePath(), interfaceclass);
            }
            catch (Throwable e){
                loadFromFallbackDir = true;
            }
            finally {
                if(SystemUtils.IS_OS_UNIX){tempLib.delete();}else{tempLib.deleteOnExit();}
            }
        }
        if (loadFromFallbackDir)
        {
            try {
                theLIB = Native.loadLibrary(
                        Paths.get("./mods/kokoalinux/libkokoa", getNativeLibraryResourcePrefix(), plat_libname).toAbsolutePath().toString(),
                        interfaceclass
                );
            }
            catch(Throwable e){
                LogManager.getLogger().error("Failed to load lib \""+plat_libname+"\"from fallback path "+"./mods/kokoalinux/libkokoa"+File.pathSeparator+getNativeLibraryResourcePrefix()+" !!!\n\n\n");
                throw e;
            }
        }

        return theLIB;
    }



    static String getCanonicalArchitecture(String arch, int platform) {//borrowed from jna5.12.1, still wondering should I change this project's license to LGPL or keep it?
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
//        if("arm".equals(arch) && platform == Platform.LINUX && isSoftFloat()) { //TODO:detect arm soft float (well... Is that someone playing minecraft on armel?)
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
    static File createTempDir() throws IOException {
        File genDir = new File(SystemUtils.getJavaIoTmpDir(),KokoaLinux.MOD_ID+System.nanoTime());
        if(!genDir.mkdir()){
            throw new IOException("Failed to create temp dir" + genDir.getName());
        }
        return genDir;
    }


    static File tempLibFileDir=null;
}
