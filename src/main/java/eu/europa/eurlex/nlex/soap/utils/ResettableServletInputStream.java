package eu.europa.eurlex.nlex.soap.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * A wrapper around servlet input stream that allows the stream
 * to be read multiple times.
 * 
 * @author Mariusz Jakubowski
 *
 */
public class ResettableServletInputStream extends ServletInputStream {
    
    private ByteArrayInputStream inputStream;
    
    private byte[] bytes;

    public ResettableServletInputStream(ServletInputStream data) throws IOException {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = data.read(b)) != -1) {
            temp.write(b, 0, len);
        }
        bytes = temp.toByteArray();
        inputStream = new ByteArrayInputStream(bytes);
    }

    public int available() throws IOException {
        return inputStream.available();
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    public boolean markSupported() {
        return inputStream.markSupported();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    public void reset() throws IOException {
        inputStream.reset();
    }

    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public boolean isFinished() {
        try {
            return available() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        try {
            readListener.onAllDataRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public byte[] getBytes() {
        return bytes;
    }

}
