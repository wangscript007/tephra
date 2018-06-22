package org.lpw.tephra.chrome.runner;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public class Pdf extends Support {
    private String range = "";

    private Pdf(String[] args) {
        super(args);
    }

    @Override
    void arg(String arg) {
        if (arg.startsWith("-range="))
            range = arg.substring(7);
    }

    @Override
    String method() {
        return "Page.printToPDF";
    }

    @Override
    JSONObject params() {
        JSONObject params = new JSONObject();
        params.put("printBackground", true);
        params.put("paperWidth", width / 96.0D);
        params.put("paperHeight", height / 96.0D);
        params.put("marginTop", 0.0D);
        params.put("marginBottom", 0.0D);
        params.put("marginLeft", 0.0D);
        params.put("marginRight", 0.0D);
        params.put("pageRanges", range);

        return params;
    }

    public static void main(String[] args) throws Exception {
        new Pdf(args).execute();
    }
}
