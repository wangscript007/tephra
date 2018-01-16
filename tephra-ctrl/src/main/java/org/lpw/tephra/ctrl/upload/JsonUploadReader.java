package org.lpw.tephra.ctrl.upload;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.storage.Storage;
import org.lpw.tephra.util.Coder;

import java.io.IOException;

/**
 * @author lpw
 */
public class JsonUploadReader implements UploadReader {
    private String fieldName;
    private String fileName;
    private String contentType;
    private String base64;
    private byte[] bytes;

    public JsonUploadReader(JSONObject object) {
        fieldName = object.getString("fieldName");
        fileName = object.getString("fileName");
        contentType = object.getString("contentType");
        base64 = object.getString("base64");
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
        return getBytes().length;
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, getBytes());
    }

    private byte[] getBytes() {
        if (bytes == null)
            bytes = BeanFactory.getBean(Coder.class).decodeBase64(base64);

        return bytes;
    }

    @Override
    public void delete() throws IOException {
    }
}
