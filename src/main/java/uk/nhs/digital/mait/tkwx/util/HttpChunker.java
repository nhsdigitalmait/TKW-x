/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package uk.nhs.digital.mait.tkwx.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author simonfarrow
 */
public class HttpChunker {

    /**
     * http chunk up the message to the output stream
     *
     * @param message byte array containing message
     * @param chunkSize
     * @param os OutputStream to write to
     * @throws IOException
     */
    public static void chunk(byte[] message, int chunkSize, OutputStream os) throws IOException {
        int bytesWritten = 0;
        int cs = 0;
        while (bytesWritten < message.length) {
            cs = ((message.length - bytesWritten) < chunkSize) ? (message.length - bytesWritten) : chunkSize;
            String clen = Integer.toHexString(cs);
            os.write(clen.getBytes());
            os.flush();
            os.write("\r\n".getBytes());
            os.flush();
            os.write(Arrays.copyOfRange(message, bytesWritten, bytesWritten + cs));
            bytesWritten += cs;
            os.flush();
            os.write("\r\n".getBytes());
            os.flush();
        }
        // if the last block sent was not zero length then send a zero length terminating block
        // or if theree no data at all do the same
        if (cs != 0 || message.length == 0) {
            //  original code
            //  os.write("\r\n0\r\n".getBytes());
            os.write("0\r\n\r\n".getBytes());
            os.flush();
        }
    }

    /**
     * unchunk an inputstream
     *
     * @param is
     * @return the assembled byte array after unchunking
     * @throws Exception
     */
    public static byte[] unchunk(InputStream is)
            throws Exception {
        // Skip any leading blank lines, then when not reading chunk get chunk
        // size (hex digits) until first space or CRLF. After CRLF read chunk size
        // bytes and add to content length running total. Then skip CRLF and read
        // new chunk size. Repeat until end.

        byte[] currentBuffer = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        // Read...
        while ((currentBuffer = readChunk(is)) != null) {
            buffer.write(currentBuffer);
        }

        return buffer.toByteArray();
    }

    /**
     * read a chunk from the input stream
     *
     * @param is InputStream
     * @return byte array containing returned chunk
     * @throws Exception
     */
    private static byte[] readChunk(InputStream is)
            throws Exception {
        int c = 0;
        boolean readingChunkSize = true;
        boolean leader = true;
        StringBuilder sb = new StringBuilder();
        while (readingChunkSize) {
            c = is.read();
            if (c == 0 || c == -1) {
                return null;
            }
            // Skip any leading whitespace (mainly extra blank lines)
            if (leader) {
                if (Character.isLetterOrDigit((char) c)) {
                    sb.append((char) c);
                    leader = false;
                }
            } else if (Character.isLetterOrDigit((char) c)) {
                sb.append((char) c);
            } else {
                readingChunkSize = false;
            }
        }
        int chunkSize = Integer.parseInt(sb.toString(), 16);
        if (chunkSize == 0) {
            return null;
        }
        // Skip to the next CRLF, or at least LF
        while (c != '\n') {
            c = (char) is.read();
        }
        byte[] b = new byte[chunkSize];
        int read = 0;
        while (read < chunkSize) {
            int r = is.read(b, read, b.length - read);
            if (r == -1) {
                throw new Exception("Premature EOF reading chunked input. Read: " + read + " Expected: " + chunkSize);
            }
            read += r;
        }
        return b;
    }
}
