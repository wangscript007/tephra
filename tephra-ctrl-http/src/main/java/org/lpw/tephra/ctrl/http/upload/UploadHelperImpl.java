package org.lpw.tephra.ctrl.http.upload;

import org.lpw.tephra.atomic.Closables;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.ctrl.http.Cors;
import org.lpw.tephra.ctrl.http.IgnoreUri;
import org.lpw.tephra.ctrl.http.ServiceHelper;
import org.lpw.tephra.ctrl.upload.UploadReader;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
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
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private Closables closables;
    @Inject
    private Cors cors;
    @Inject
    private ServiceHelper serviceHelper;
    @Value("${" + UploadHelper.PREFIX + "max-size:1m}")
    private String maxSize;
    private Map<String, Uploader> uploaders;
    private long maxFileSize;

    @Override
    public void upload(HttpServletRequest request, HttpServletResponse response, String uploader) {
        cors.set(request, response);
        if (cors.is(request, response))
            return;

        try {
            OutputStream outputStream = serviceHelper.setContext(request, response, uploader);
            List<UploadReader> readers = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            request.getParameterMap().forEach((name, value) -> map.put(name, converter.toString(value)));
            for (Part part : request.getParts())
                if (!map.containsKey(part.getName()) && !validator.isEmpty(part.getSubmittedFileName()) && part.getSize() <= maxFileSize)
                    readers.add(new HttpUploadReader(part, map));
            if (readers.isEmpty())
                return;

            response.setCharacterEncoding(context.getCharset(null));
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
