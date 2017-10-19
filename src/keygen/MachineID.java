package keygen;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MachineID {

    private static String user_name = System.getProperty("user.name", "Lambda");
    private static String os_name = System.getProperty("os.name", "");
    private static long[] counts = new long[isWindows()];

    static {
        for (int i = 0; i < counts.length; i++) {
            counts[i] = -1;
        }
    }
    public static synchronized long[] get() {
        long[] j;
        synchronized (MachineID.class) {
            for (int i = 0; i < counts.length; i++) {
                if (counts[i] < 0) {
                    counts[i] = create(i);
                }
            }
            j = counts;
        }
        return j;
    }

    private static int isWindows() {
        if (os_name.startsWith("Windows")) {
            return 2;
        }
        return 1;
    }

    private static long create(int i) {
        String str = null;
        if (os_name.startsWith("Windows")) {
            if (i == 0) {
                try {
                    str = (machine_uuid() + "__") + serialNumber();
                } catch (Exception ignored) {
                }
            } else if (i == 1) {
                try {
                    str = serialNumber();
                } catch (Exception ignored) {
                }
            }
        } else if (os_name.startsWith("Mac")) {
            try {
                str = getIDfromMac();
            } catch (Exception ignored) {
            }
        } else if (os_name.startsWith("Linux")) {
            try {
                str = GetLinuxSerialNum();
                if (str == null) {
                 str = GetLinuxSerialNum2();
                }
            } catch (Exception e4) {
            }
        }
        if (str == null) {
            str = ("LambdaLambda" + "__") + user_name;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            ByteBuffer wrap = ByteBuffer.wrap(instance.digest());
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            return wrap.getLong() & Long.MAX_VALUE;
        } catch (NoSuchAlgorithmException e5) {
            return 0;
        }
    }
    private static String GetLinuxSerialNum2() throws Exception {
        String serial = null;
        try {
            BufferedReader fstabFilebuf = new BufferedReader(new InputStreamReader(new FileInputStream("/etc/fstab")));
            String buffer = null;
            while ((buffer = fstabFilebuf.readLine()) != null) {
                buffer = buffer.trim();
                if (buffer.length() == 0) {
                    continue;
                }
                if (buffer.startsWith("#")) {
                    continue;
                }
                String[] Keyvalue = buffer.split("[ \\t]+");
                if (Keyvalue.length < 2) {
                    continue;
                }
                if (!Keyvalue[1].equals("/")) {
                    continue;
                }
                buffer = Keyvalue[0];
                if (buffer.startsWith("UUID=")) {
                    serial = buffer.substring(5);
                } else if (buffer.startsWith("LABEL=")) {
                    serial = buffer.substring(6);
                }
                if (serial != null) {
                    serial.toLowerCase();
                }
            }

            fstabFilebuf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serial;
    }
    private static String machine_uuid() {
        return exec("wmic csproduct MachineID uuid", "UUID");
    }

    private static String serialNumber() {
        return exec("wmic bios MachineID serialnumber", "SerialNumber");
    }

    private static String exec(String str, String str2) {
        String str3 = null;
        try {
            Process exec = Runtime.getRuntime().exec(str.split(" "));
            OutputStream outputStream = exec.getOutputStream();
            InputStream inputStream = exec.getInputStream();
            try {
                outputStream.close();
                Scanner scanner = new Scanner(inputStream);
                do {
                    try {
                        if (!scanner.hasNext()) {
                            break;
                        }
                    } catch (Throwable th) {
                        scanner.close();
                    }
                } while (!str2.equals(scanner.next()));
                str3 = scanner.next().trim();
                scanner.close();
            } catch (IOException e) {
            }
        } catch (IOException e2) {
        }
        return str3;
    }

    private static String getIDfromMac() {
        Process locProcess;
        String serial = null;
        Runtime locRuntime = Runtime.getRuntime();

        try {
            locProcess = locRuntime.exec("/usr/sbin/system_profiler SPHardwareDataType".split(" "));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(locProcess.getInputStream()));
            String buffer = null;
            while ((buffer = bufferedReader.readLine()) != null) {
                if (buffer.contains("Serial Number")) {
                    String buf1 = buffer.split("Serial Number")[1];
                    int iPoint = buf1.indexOf(":");
                    if (iPoint >= 0) {
                        serial = buf1.substring(iPoint + 1).trim();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serial;
    }

    private static String GetLinuxSerialNum() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("/var/lib/dbus/machine-id")));
            String readLine;
            try {
                readLine = bufferedReader.readLine();
                if (readLine != null) {
                    readLine = readLine.trim();
                } else {
                    readLine = null;
                }
                return readLine;
            } catch (IOException e) {

                return null;
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e2) {
                    return null;
                }
            }
        } catch (FileNotFoundException e3) {
            return null;
        }
    }

}
