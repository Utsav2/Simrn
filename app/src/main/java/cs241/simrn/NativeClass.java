package cs241.simrn;

public class NativeClass {
    static {
        System.loadLibrary("ndkModule");
    }

    public native String helloNdkString();

    public native String helloSimrn();
}