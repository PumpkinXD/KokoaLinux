package cn.yesterday17.kokoalinux.input;

import cn.yesterday17.kokoalinux.input.Moka;
import cn.yesterday17.kokoalinux.util.clocale;
import com.sun.jna.Pointer;
import org.apache.commons.lang3.SystemUtils;

public class Mocha {



    static final String XNClientWindow="clientWindow";
    static final String XNFocusWindow="focusWindow";
    static final String XNInputStyle="inputStyle";
    static final long XIMPreeditNothing=0x0008L;  //#define XIMPreeditNothing	0x0008L
    static final long XIMStatusNothing=0x0400L; //#define XIMStatusNothing	0x0400L
    static final long nullptr_c=0;


    static int LC_CTYPE;//0 on linux and solaris and 2 on freebsd(or *BSD?)

    static Pointer display;//too lazy to use X11.*
    static Pointer window;
    static Pointer xim;
    static Pointer xic;



    void setDisplay(long display){
        Mocha.display = new Pointer(display);
    }

    void setWindow(long window){
        Mocha.window=new Pointer(window);
    }

    long openIM(){
        Mocha.xim=Moka.instance.XOpenIM(Mocha.display,new Pointer(0),new Pointer(0),new Pointer(0));
        return Pointer.nativeValue(Mocha.xim);
    }
    long createIC(){
        Mocha.xic=Moka.instance.XCreateIC(
                Mocha.xim,
                XNClientWindow,
                Mocha.window,
                XNFocusWindow,
                Mocha.window,
                XNInputStyle,
                XIMPreeditNothing | XIMStatusNothing,
                0
        );
        return Pointer.nativeValue(Mocha.xic);
    }
    void closeIM(){
        if(xim!=null&&Pointer.nativeValue(xim)!=0){
            Moka.instance.XCloseIM(xim);
        }
    }
    void destroyIC(){
        if(xic!=null&&Pointer.nativeValue(xic)!=0){
            Moka.instance.XDestoryIC(xic);
        }
    }

    Pointer toggleIC(long active)
    {
        if(active!=0){
            Moka.instance.XSetICFocus(xic);
        }else{
            Moka.instance.XUnsetICFocus(xic);
        }
        return xic;
    }
    void prepareLocale()
    {
        Moka.instance.XSetLocaleModifiers("");
        
        if(SystemUtils.IS_OS_LINUX||SystemUtils.IS_OS_SOLARIS) {
            clocale.instance.setlocale(0, "");
        }else if (SystemUtils.IS_OS_NET_BSD||SystemUtils.IS_OS_FREE_BSD||SystemUtils.IS_OS_OPEN_BSD){
            clocale.instance.setlocale(2, "");
        }else
        {
            clocale.instance.setlocale(0, "");
        }
    }





/*
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
    /////////////////////////////////////
*/

}
