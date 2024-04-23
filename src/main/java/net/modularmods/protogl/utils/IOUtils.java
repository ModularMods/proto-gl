package net.modularmods.protogl.utils;

import net.modularmods.protogl.ProtoGL;

import java.io.DataInputStream;
import java.io.IOException;

public class IOUtils {

    public static String readMagicNumber(DataInputStream dis) throws IOException {
        byte[] magicNumber = new byte[4];
        dis.readFully(magicNumber);
        ProtoGL.getLogger().debug("Magic number read: {}", new String(magicNumber));
        return new String(magicNumber);
    }

    public static int readInt(DataInputStream dis, String data) throws IOException {
        int i = dis.readInt();
        ProtoGL.getLogger().debug("{}: {}", data, i);
        return i;
    }

    public static float[] readFloats(DataInputStream dis, int count, String data) throws IOException {
        float[] floats = new float[count];
        for (int i = 0; i < count; i++) {
            floats[i] = dis.readFloat();
        }
        ProtoGL.getLogger().debug("{}: {}", data, floats);
        return floats;
    }

    public static int[] readInts(DataInputStream dis, int count, String data) throws IOException {
        int[] ints = new int[count];
        for (int i = 0; i < count; i++) {
            ints[i] = dis.readInt();
        }
        ProtoGL.getLogger().debug("{}: {}", data, ints);
        return ints;
    }

    public static byte readByte(DataInputStream dis, String data) throws IOException {
        byte b = dis.readByte();
        ProtoGL.getLogger().debug("{}: {}", data, b);
        return b;
    }

    public static String readString(DataInputStream dis, String data) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b;
        while (Byte.toUnsignedInt(b = dis.readByte()) != 0) {
            sb.append((char) b);
        }
        String s = sb.toString();
        ProtoGL.getLogger().debug("{}: {}", data, s);
        return s;
    }
}
