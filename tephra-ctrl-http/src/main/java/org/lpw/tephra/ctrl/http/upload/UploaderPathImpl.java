package org.lpw.tephra.ctrl.http.upload;

import org.lpw.tephra.ctrl.upload.UploadReader;
import org.lpw.tephra.ctrl.upload.UploadService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * @author lpw
 */
@Service(UploadHelper.PREFIX + "uploader.path")
public class UploaderPathImpl implements Uploader {
    @Inject
    private UploadService uploadService;

    @Override
    public String getName() {
        return UploadHelper.UPLOAD_PATH;
    }

    @Override
    public byte[] upload(List<UploadReader> readers) throws IOException {
        return uploadService.upload(readers.get(0)).getString("path").getBytes();
    }
}
