package org.lpw.tephra.ctrl.http.upload;

import org.lpw.tephra.atomic.Closables;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.ctrl.http.IgnoreUri;
import org.lpw.tephra.ctrl.http.ServiceHelper;
import org.lpw.tephra.ctrl.upload.UploadReader;
import org.lpw.tephra.ctrl.upload.UploadService;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
@Service(UploadHelper.PREFIX + "helper")
public class UploadHelperImpl implements UploadHelper, IgnoreUri, ContextRefreshedListener {
    @Inject
    private Converter converter;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private Closables closables;
    @Inject
    private UploadService uploadService;
    @Inject
    private ServiceHelper serviceHelper;
    @Value("${" + UploadHelper.PREFIX + "max-size:1m}")
    private String maxSize;
    private Map<String, Uploader> uploaders;
    private long maxFileSize;

    @Override
    public void upload(HttpServletRequest request, HttpServletResponse response, String uploader) {
        try {
            serviceHelper.setCors(request, response);
            OutputStream outputStream = serviceHelper.setContext(request, response, UPLOAD);
            List<UploadReader> readers = new ArrayList<>();
            for (Part part : request.getParts())
                if (part.getSize() <= maxFileSize)
                    readers.add(new HttpUploadReader(part));
            if (readers.isEmpty())
                return;

            outputStream.write(uploaders.get(uploader).upload(readers));
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            logger.warn(e, "处理文件上传时发生异常！");
        } finally {
            closables.close();
        }
    }

    @Override
    public String[] getIgnoreUris() {
        return new String[]{UPLOAD, UPLOAD_PATH};
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        uploaders = new HashMap<>();
        BeanFactory.getBeans(Uploader.class).forEach(uploader -> uploaders.put(uploader.getName(), uploader));
        maxFileSize = converter.toBitSize(maxSize);
    }
}
