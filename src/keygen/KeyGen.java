package keygen;

import java.nio.ByteBuffer;


public class KeyGen {

    private static int sum(int i) {
        int j = 0;
        for (; i > 0; i >>= 4)
            j += i & 0xf;

        return j % 10;
    }


    public static String getKey(long machineId, long unixTime) {
        int i = (int) (machineId & -1);
        int j = (int) (machineId >> Integer.SIZE & -1);
        int i1 = i - 529842850 + 287454020;     // - 134833056
        int j1 = (j - 215405537) + 1432778632 & Integer.MAX_VALUE;

        ByteBuffer bb = ByteBuffer.allocateDirect(8);
        bb.putInt(j1);
        bb.putInt(i1);
        bb.rewind();
        long l = bb.getLong();
        int d = (int) unixTime ^ 1450416845;

        return String.format("%dZ%d%d", l, d, sum(d));
    }

}
