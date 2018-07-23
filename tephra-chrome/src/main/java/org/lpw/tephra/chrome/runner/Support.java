package org.lpw.tephra.chrome.runner;

import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;

/**
 * @author lpw
 */
abstract class Support {
    private static final int BUFFER_SIZE = 1 << 16; //64K
    private String[] args;
    private String host;
    private int port;
    private String uri;
    double width;
    double height;
    private String output;
    private Random random;

    Support(String[] args) {
        this.args = args;
    }

    void execute() throws Exception {
        args();

        try (Socket socket = new Socket(host, port);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            outputStream.write(handshake().getBytes());
            outputStream.flush();
            read(inputStream, false);
            outputStream.write(pack(message().toJSONString()).toByteArray());
            outputStream.flush();
            ByteArrayOutputStream byteArrayOutputStream = read(inputStream, true);

            save(new StringBuilder(byteArrayOutputStream.toString()));
        }
    }

    private void args() {
        for (String arg : args) {
            arg = arg.trim();
            if (arg.startsWith("-host="))
                host = arg.substring(6);
            else if (arg.startsWith("-port="))
                port = Integer.parseInt(arg.substring(6));
            else if (arg.startsWith("-uri="))
                uri = arg.substring(5);
            else if (arg.startsWith("-width="))
                width = Double.parseDouble(arg.substring(7));
            else if (arg.startsWith("-height="))
                height = Double.parseDouble(arg.substring(8));
            else if (arg.startsWith("-output="))
                output = arg.substring(8);
            else
                arg(arg);
        }
        random = new Random();
    }

    void arg(String arg) {
    }

    private String handshake() {
        return "GET " + uri + " HTTP/1.1\r\n"
                + "Host: " + host + ":" + port + "\r\n"
                + "Upgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Protocol: chat, superchat\r\nSec-WebSocket-Version: 13\r\n"
                + "Sec-WebSocket-Key: " + random.nextLong() + "\r\n"
                + "Origin: http://" + host + ":" + port + "\r\n\r\n";
    }

    private JSONObject message() {
        JSONObject object = new JSONObject();
        object.put("id", random(10000000, 99999999));
        object.put("method", method());
        object.put("params", params());

        return object;
    }

    abstract String method();

    abstract JSONObject params();

    private ByteArrayOutputStream read(InputStream inputStream, boolean json) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int length; (length = inputStream.read(buffer)) > -1; ) {
                outputStream.write(buffer, 0, length);
                if (length < buffer.length && (!json || (buffer[length - 1] == '}' && buffer[length - 2] == '}')))
                    break;
            }

            return outputStream;
        }
    }

    private ByteArrayOutputStream pack(String message) {
        byte[] msg = message.getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(0x80 | 1);
        writeLength(outputStream, msg.length);
        byte[] mask = new byte[4];
        for (int i = 0; i < mask.length; i++)
            mask[i] = (byte) random(Byte.MIN_VALUE, Byte.MAX_VALUE);
        outputStream.write(mask, 0, mask.length);
        for (int i = 0; i < msg.length; i++)
            outputStream.write((msg[i] ^ mask[i % 4]) & 0xff);
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream;
    }

    private void writeLength(ByteArrayOutputStream outputStream, int length) {
        if (length <= 125)
            outputStream.write(0x80 | length);
        else if (length <= 0xffff) {
            outputStream.write(0x80 | 126);
            outputStream.write((length >> 8) & 0xff);
            outputStream.write(length & 0xff);
        } else {
            outputStream.write(0x80 | 127);
            for (int i = 7; i >= 0; i--)
                outputStream.write((length >> (i * 8)) & 0xff);
        }
    }

    private void save(StringBuilder sb) throws IOException {
        sb.delete(sb.length() - 3, sb.length());
        sb.delete(0, sb.lastIndexOf("\"") + 1);
        try (OutputStream outputStream = new FileOutputStream(output)) {
            outputStream.write(Base64.getDecoder().decode(sb.toString()));
        }
    }

    private int random(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
