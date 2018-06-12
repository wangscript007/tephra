package org.lpw.tephra.office.excel;

import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Io;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author lpw
 */
public class ExcelReaderTest extends CoreTestSupport {
    @Inject
    private Io io;
    @Inject
    private ExcelReader excelReader;

    @Test
    public void read() throws IOException {
        long time = System.currentTimeMillis();
        io.write("target/excel.json", excelReader.read(new FileInputStream("/mnt/hgfs/share/excel/Workbook1.xlsx"),
                (type, fileName, inputStream) -> "" + io.read(inputStream).length).toJSONString().getBytes());
        System.out.println((System.currentTimeMillis() - time) / 1000.0);
    }
}
