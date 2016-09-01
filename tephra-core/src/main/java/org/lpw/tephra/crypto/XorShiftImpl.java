package org.lpw.tephra.crypto;

import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.crypto.xor-shift")
public class XorShiftImpl implements XorShift {
    @Override
    public byte[] encrypt(byte[] key, byte[] message) {
        byte[] k = key(key, message.length);
        byte[] msg = copy(message);
        xor(k, msg);
        for (int i = 0; i < msg.length; i++)
            shift(k, msg, i);

        return msg;
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] message) {
        byte[] k = key(key, message.length);
        byte[] msg = copy(message);
        for (int i = msg.length - 1; i > -1; i--)
            shift(k, msg, i);
        xor(k, msg);

        return msg;
    }

    private byte[] copy(byte[] message) {
        byte[] msg = new byte[message.length];
        for (int i = 0; i < msg.length; i++)
            msg[i] = message[i];

        return msg;
    }

    private byte[] key(byte[] key, int length) {
        if (key.length >= length)
            return key;

        byte[] k = new byte[length];
        int i = 0;
        for (; i < key.length; i++)
            k[i] = key[i];
        for (; i < length; i++)
            k[i] = (byte) (key[i % key.length] ^ k[i >> 1]);

        return k;
    }

    private void xor(byte[] key, byte[] message) {
        for (int i = 0; i < message.length; i++)
            message[i] ^= key[i % key.length];
    }

    private void shift(byte[] key, byte[] message, int i) {
        int n = (key[i] & 0xff) % message.length;
        byte by = message[i];
        message[i] = message[n];
        message[n] = by;
    }
}
