package org.lpw.tephra.ctrl.http.upload;

import org.lpw.tephra.ctrl.upload.UploadReader;
import org.lpw.tephra.ctrl.upload.UploadService;
import org.lpw.tephra.util.Json;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * @author lpw
 */
@Service(UploadHelper.PREFIX + "uploader")
public class UploaderImpl implements Uploader {
    @Inject
    private Json json;
    @Inject
    private UploadService uploadService;

    @Override
    public String getName() {
        return UploadHelper.UPLOAD;
    }

    @Override
    public byte[] upload(List<UploadReader> readers) throws IOException {
        if (readers.size() == 1)
            return json.toBytes(uploadService.upload(readers.get(0)));

        return json.toBytes(uploadService.uploads(readers));
    }
}
