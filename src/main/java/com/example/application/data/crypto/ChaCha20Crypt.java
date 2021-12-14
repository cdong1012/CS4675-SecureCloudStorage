package com.example.application.data.crypto;

public class ChaCha20Crypt {
    public static final int KEY_SIZE = 32;
    public static final int NONCE_SIZE = 8;
    private int[] state = new int[16];
    public static final int CHACHA_BLOCKLENGTH = 64;


    private static int littleEndianToInt(byte[] le, int index) {
        return (le[index] & 0xff) | ((le[index + 1] & 0xff) << 8) | ((le[index + 2] & 0xff) << 16) | ((le[index + 3] & 0xff) << 24);
    }

    private static void intToLittleEndian(int n, byte[] le, int offset) {
        le[offset] = (byte)(n);
        le[++offset] = (byte)(n >>> 8);
        le[++offset] = (byte)(n >>> 16);
        le[++offset] = (byte)(n >>> 24);
    }

    private static int ROTATE(int v, int c) {
        return (v << c) | (v >>> (32 - c));
    }

    private static void quarterRound(int[] x, int a, int b, int c, int d) {
        x[a] += x[b];
        x[d] = ROTATE(x[d] ^ x[a], 16);
        x[c] += x[d];
        x[b] = ROTATE(x[b] ^ x[c], 12);
        x[a] += x[b];
        x[d] = ROTATE(x[d] ^ x[a], 8);
        x[c] += x[d];
        x[b] = ROTATE(x[b] ^ x[c], 7);
    }


    public ChaCha20Crypt(byte[] key, byte[] nonce) {
        this.state[0] = 0x61707865;
        this.state[1] = 0x3320646e;
        this.state[2] = 0x79622d32;
        this.state[3] = 0x6b206574; // expand 32-byte k

        if (key.length != KEY_SIZE || nonce.length != NONCE_SIZE) {
            System.out.println("Error... Key or nonce size is not valid");
            return;
        }

        this.state[4] = littleEndianToInt(key, 0);
        this.state[5] = littleEndianToInt(key, 4);
        this.state[6] = littleEndianToInt(key, 8);
        this.state[7] = littleEndianToInt(key, 12);
        this.state[8] = littleEndianToInt(key, 16);
        this.state[9] = littleEndianToInt(key, 20);
        this.state[10] = littleEndianToInt(key, 24);
        this.state[11] = littleEndianToInt(key, 28); // add key to state

        this.state[12] = 0; // default counter
        this.state[13] = 0; // add nonce to state
        this.state[14] = littleEndianToInt(nonce, 0);
        this.state[15] = littleEndianToInt(nonce, 4);
    }

    public void ChaChaEncrypt(byte[] src, byte[] dest, int length) {
        int[] keyStream = new int[16];
        byte[] xorStream = new byte[64];
        int sourcePosition = 0;
        int destPosition = 0;

        while (length > 0) {
            for (int i = 0; i < 16; i++) {
                keyStream[i] = this.state[i];
            }
            for (int i = 0; i < 10; i++){
                quarterRound(keyStream, 0, 4, 8, 12);
                quarterRound(keyStream, 1, 5, 9, 13);
                quarterRound(keyStream, 2, 6, 10, 14);
                quarterRound(keyStream, 3, 7, 11, 15);
                quarterRound(keyStream, 0, 5, 10, 15);
                quarterRound(keyStream, 1, 6, 11, 12);
                quarterRound(keyStream, 2, 7, 8, 13);
                quarterRound(keyStream, 3, 4, 9, 14);
            }

            for (int i = 0; i < 16; i++) {
                keyStream[i] += this.state[i];
            }

            for (int i = 0; i < 16; i++) {
                intToLittleEndian(keyStream[i], xorStream, 4 * i);
            }

            // update counter
            this.state[12]++;
            if (this.state[12] == 0) {
                this.state[13] += 1;
            }

            // If length to encrypt is less than 64, this is the last block

            if (length <= 64) {
                for (int i = 0; i < length; i++) {
                    dest[i + destPosition] = (byte) (src[i + sourcePosition] ^ xorStream[i]);
                }
                return;
            }
            for (int i = 0; i < 64; i++) {
                dest[i + destPosition] = (byte) (src[i + sourcePosition] ^ xorStream[i]);
            }

            length -= 64;
            sourcePosition += 64;
            destPosition += 64;
        }
    }
}
