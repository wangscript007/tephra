package org.lpw.tephra.ctrl.http.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.ctrl.http.IgnoreUri;
import org.lpw.tephra.ctrl.http.ServiceHelper;
import org.lpw.tephra.dao.Commitable;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Service("tephra.ctrl.http.upload-helper")
public class UploadHelperImpl implements UploadHelper, IgnoreUri, ContextRefreshedListener {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Context context;
    @Autowired
    protected Generator generator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Logger logger;
    @Autowired
    protected Set<Commitable> commitables;
    @Autowired
    protected ServiceHelper serviceHelper;
    @Autowired
    protected JsonConfigs jsonConfigs;
    @Value("${tephra.ctrl.http.upload.max-size:1m}")
    protected String maxSize;
    protected ServletFileUpload upload;
    protected Map<String, UploadListener> listeners;

    @Override
    public void upload(HttpServletRequest request, HttpServletResponse response) {
        try {
            OutputStream outputStream = serviceHelper.setContext(request, response, URI);
            getUpload(request).parseRequest(request).forEach(item -> {
                if (item.isFormField())
                    return;

                UploadListener listener = listeners.get(item.getFieldName());
                if (listener == null)
                    listener = jsonConfigs.get(item.getFieldName());
                if (listener == null || !listener.isUploadEnable(item.getContentType(), item.getName())) {
                    if (logger.isDebugEnable())
                        logger.debug("无法获得上传监听器[{}]。", item.getFieldName());

                    return;
                }

                try {
                    int[] size = listener.getImageSize();
                    boolean image = item.getContentType().startsWith("image/") && size != null && size.length == 2 && (size[0] > 0 || size[1] > 0);
                    String path = getPath(listener, item);
                    File file = new File(context.getAbsolutePath(path));
                    file.getParentFile().mkdirs();
                    if (!image || !image(item, size, file))
                        item.write(file);
                    String result = listener.upload(item.getName(), converter.toBitSize(item.getSize()), path);
                    item.delete();

                    if (!validator.isEmpty(result))
                        outputStream.write(result.getBytes("UTF-8"));

                    if (logger.isDebugEnable())
                        logger.debug("保存上传[{}:{}]的文件[{}:{}]。", item.getFieldName(), item.getName(), path, converter.toBitSize(item.getSize()));
                } catch (Exception e) {
                    logger.warn(e, "保存上传文件时发生异常！");
                }
            });
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            logger.warn(e, "处理文件上传时发生异常！");
        } finally {
            commitables.forEach(Commitable::close);
        }
    }

    protected ServletFileUpload getUpload(HttpServletRequest request) {
        if (upload == null) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository((File) request.getServletContext().getAttribute("javax.servlet.context.tempdir"));
            upload = new ServletFileUpload(factory);
            upload.setSizeMax(converter.toBitSize(maxSize));
        }

        return upload;
    }

    protected String getPath(UploadListener listener, FileItem item) {
        StringBuilder path = new StringBuilder(ROOT).append(item.getContentType()).append('/').append(listener.getPath(item.getContentType(), item.getName())).append('/')
                .append(converter.toString(new Date(), "yyyyMMdd")).append('/').append(generator.random(32)).append(item.getName().substring(item.getName().lastIndexOf('.')));

        return path.toString().replaceAll("[/]+", "/");
    }

    protected boolean image(FileItem item, int[] size, File file) {
        try {
            Image image = ImageIO.read(item.getInputStream());
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            if (size[0] > 0 && width > size[0]) {
                height = height * size[0] / width;
                width = size[0];
            }
            if (size[1] > 0 && height > size[1]) {
                width = width * size[1] / height;
                height = size[1];
            }
            if (width == 0 || height < 0)
                return false;

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();
            ImageIO.write(bufferedImage, item.getContentType().substring(item.getContentType().indexOf('/') + 1), file);

            return true;
        } catch (Exception e) {
            logger.warn(e, "生成压缩图片时发生异常！");

            return false;
        }
    }

    @Override
    public void remove(String uri) {
        new File(context.getAbsolutePath(uri)).delete();

        if (logger.isDebugEnable())
            logger.debug("删除上传的文件[{}]。", uri);
    }

    @Override
    public String[] getIgnoreUris() {
        return new String[]{URI};
    }

    @Override
    public int getContextRefreshedSort() {
        return 19;
    }

    @Override
    public void onContextRefreshed() {
        if (listeners != null)
            return;

        listeners = new HashMap<>();
        BeanFactory.getBeans(UploadListener.class).forEach(listener -> listeners.put(listener.getKey(), listener));
    }
}
