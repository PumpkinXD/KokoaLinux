package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;

//load X11 (libX11.so) from sys lib path
public interface Moka extends Library {
    Moka instance =(Moka) Native.loadLibrary("X11",Moka.class);

    Pointer XOpenIM(Pointer display,Pointer db, Pointer res_name, Pointer res_class);
    int XCloseIM(Pointer im);

    Pointer XCreateIC(Pointer im,Object... args);
    void XDestoryIC(Pointer ic);
    void XSetICFocus(Pointer ic);
    void XUnsetICFocus(Pointer ic);

    String XSetLocaleModifiers(String modifier_list);

}
