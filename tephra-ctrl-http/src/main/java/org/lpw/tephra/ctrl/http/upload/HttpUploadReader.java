package org.lpw.tephra.ctrl.http.upload;

import org.apache.commons.fileupload.FileItem;
import org.lpw.tephra.ctrl.upload.UploadReader;
import org.lpw.tephra.storage.Storage;

import java.io.IOException;

/**
 * @author lpw
 */
public class HttpUploadReader implements UploadReader {
    private FileItem item;

    public HttpUploadReader(FileItem item) {
        this.item = item;
    }

    @Override
    public String getFieldName() {
        return item.getFieldName();
    }

    @Override
    public String getFileName() {
        return item.getName();
    }

    @Override
    public String getContentType() {
        return item.getContentType();
    }

    @Override
    public long getSize() {
        return item.getSize();
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, item.getInputStream());
    }

    @Override
    public void delete() throws IOException {
        item.delete();
    }
}
