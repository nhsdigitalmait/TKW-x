/*
 Copyright 2017  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

/**
 * support compress and decompression using gzip or deflater
 * @author simonfarrow
 */
public class Compressor {

    public enum CompressionType {
        COMPRESSION_GZIP,
        COMPRESSION_DEFLATE
    }

    public Compressor() {
    }

    /**
     *
     * @param uncompressed byte[]
     * @param method compression types gzip and deflate supported
     * @return byte array with compressed content
     * @throws IOException
     */
    public static byte[] compress(byte[] uncompressed, CompressionType method) throws IOException {
        // it can't be bigger than this can it?
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(uncompressed.length);
        switch (method) {
            case COMPRESSION_GZIP:
                try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                    zipStream.write(uncompressed);
                } finally {
                    byteStream.close();
                }
                //System.out.println("Compressing gzip = "+new HexFormatter().format(byteStream.toByteArray()));
                return byteStream.toByteArray();

            case COMPRESSION_DEFLATE:
                Deflater deflater = new Deflater();
                deflater.setInput(uncompressed);
                deflater.finish();
                byte[] buffer = new byte[1024];
                while (!deflater.finished()) {
                    int count = deflater.deflate(buffer);
                    byteStream.write(buffer, 0, count);
                }
                byteStream.close();
                return byteStream.toByteArray();

            default:
                return null;
        }
    }

    /**
     * uncompress a byte array to a String
     * @param compressed
     * @param method
     * @return uncompressed byte array
     * @throws IOException
     * @throws DataFormatException 
     */
    public static byte[] uncompress(byte[] compressed, CompressionType method) throws IOException, DataFormatException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outputStream
                = new ByteArrayOutputStream(compressed.length);
        switch (method) {
            case COMPRESSION_GZIP:
                //System.out.println("Uncompressing gzip = "+new HexFormatter().format(compressed));
                GZIPInputStream gzis
                        = new GZIPInputStream(new ByteArrayInputStream(compressed));

                int len;
                while ((len = gzis.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }

                gzis.close();
                outputStream.close();
                return outputStream.toByteArray();

            case COMPRESSION_DEFLATE:
                Inflater inflater = new Inflater();
                inflater.setInput(compressed);
                while (!inflater.finished()) {
                    int count = inflater.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                outputStream.close();
                return outputStream.toByteArray();

            default:
                return null;
        }
    }
}
