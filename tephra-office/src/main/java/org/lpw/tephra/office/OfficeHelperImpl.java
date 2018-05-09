package org.lpw.tephra.office;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Numeric;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.office.helper")
public class OfficeHelperImpl implements OfficeHelper {
    @Inject
    private Generator generator;
    @Inject
    private Context context;
    @Inject
    private Numeric numeric;
    @Value("${tephra.office.temp-path:}")
    private String tempPath;

    @Override
    public String getTempPath(String name) {
        return context.getAbsolutePath(tempPath + "/" + name + "/" + generator.random(32));
    }

    @Override
    public int pixelToEmu(int pixel) {
        return pixel * 9525;
    }

    @Override
    public int emuToPixel(int emu) {
        return emu / 9525;
    }

    @Override
    public int pointToPixel(double point) {
        return numeric.toInt(point * 96 / 72);
    }

    @Override
    public double pixelToPoint(int pixel) {
        return pixel * 72 / 96.0D;
    }

    @Override
    public double fromPercent(int percent) {
        return percent / 100000.0D;
    }

    @Override
    public int toPercent(double value) {
        return numeric.toInt(value * 100000);
    }

    @Override
    public JSONObject colorToJson(Color color) {
        JSONObject object = new JSONObject();
        object.put("red", color.getRed());
        object.put("green", color.getGreen());
        object.put("blue", color.getBlue());
        object.put("alpha", color.getAlpha());

        return object;
    }

    @Override
    public Color jsonToColor(JSONObject object) {
        return null;
    }
}
