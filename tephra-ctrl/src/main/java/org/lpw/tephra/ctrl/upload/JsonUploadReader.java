package org.lpw.tephra.ctrl.upload;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.storage.Storage;
import org.lpw.tephra.util.Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
public class JsonUploadReader implements UploadReader {
    private String fieldName;
    private String fileName;
    private String contentType;
    private String base64;
    private byte[] bytes;

    JsonUploadReader(JSONObject object) {
        this(object.getString("fieldName"), object.getString("fileName"), object.getString("contentType"),
                object.getString("base64"));
    }

    JsonUploadReader(String fieldName, String fileName, String contentType, String base64) {
        this.fieldName = fieldName;
        this.fileName = fileName;
        this.contentType = contentType;
        this.base64 = base64;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        return getByteArray().length;
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, getByteArray());
    }

    private byte[] getByteArray() {
        if (bytes == null)
            bytes = BeanFactory.getBean(Coder.class).decodeBase64(base64);

        return bytes;
    }

    @Override
    public void delete() throws IOException {
    }
}
