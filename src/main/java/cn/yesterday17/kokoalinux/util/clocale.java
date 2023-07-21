package cn.yesterday17.kokoalinux.util;
import com.sun.jna.Library;
import com.sun.jna.Native;


public interface clocale extends Library {
    clocale instance=(clocale) Native.loadLibrary("c", clocale.class);
    String setlocale(int category, String locale);
}


//C marco Const LC_CTYPE
//on linux-amd64 equals 0 but on freebsd-amd64 it equals 2
//on other un*x IDK, maybe need to read /usr/include/locale.h to figure out
//maybe on most SVR4 it's 0 and *BSD it's 2