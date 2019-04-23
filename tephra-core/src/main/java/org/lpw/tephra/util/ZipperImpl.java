package org.lpw.tephra.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author lpw
 */
@Component("tephra.util.zipper")
public class ZipperImpl implements Zipper {
    @Inject
    private Io io;

    @Override
    public void zip(List<File> input, File output) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(output));
        for (File file : input) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);
            io.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
        }
        zipOutputStream.close();
    }

    @Override
    public void zip(Map<String, File> input, File output) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(output));
        for (String name : input.keySet()) {
            zipOutputStream.putNextEntry(new ZipEntry(name));
            FileInputStream fileInputStream = new FileInputStream(input.get(name));
            io.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
        }
        zipOutputStream.close();
    }

    @Override
    public void unzip(File input, File output) throws IOException {
        unzip(new FileInputStream(input), output);
    }

    @Override
    public void unzip(InputStream inputStream, File output) throws IOException {
        unzip(new ZipInputStream(inputStream), output);
    }

    private void unzip(ZipInputStream zipInputStream, File output) throws IOException {
        String path = output.getAbsolutePath() + "/";
        for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null; ) {
            if (zipEntry.isDirectory())
                continue;

            File file = new File(path + zipEntry.getName());
            io.mkdirs(file.getParentFile());
            OutputStream outputStream = new FileOutputStream(file);
            io.copy(zipInputStream, outputStream);
            outputStream.close();
            zipInputStream.closeEntry();
        }
        zipInputStream.close();
    }
}
