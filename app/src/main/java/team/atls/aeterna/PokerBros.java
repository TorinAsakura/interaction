// Automatically generated by flapigen
package team.atls.aeterna;
import android.support.annotation.NonNull;

public final class PokerBros {

    public PokerBros() {
        mNativeObj = init();
    }
    private static native long init();

    public static native @NonNull String login(@NonNull String username, @NonNull String password);

    public synchronized void delete() {
        if (mNativeObj != 0) {
            do_delete(mNativeObj);
            mNativeObj = 0;
       }
    }
    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        }
        finally {
             super.finalize();
        }
    }
    private static native void do_delete(long me);
    /*package*/ PokerBros(InternalPointerMarker marker, long ptr) {
        assert marker == InternalPointerMarker.RAW_PTR;
        this.mNativeObj = ptr;
    }
    /*package*/ long mNativeObj;
}