package Data;

import com.spire.xls.CellRange;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import java.util.ArrayList;
import java.util.List;

public class excelImportTest {

    public static Object[][] excelDataTransform(String str, int sheetIndex)
    {
        Object[][] res;
        int width = 0, height = 0;
        //创建Workbook对象
        Workbook wb = new Workbook();

        //加载Excel文件
        wb.loadFromFile("src/main/resources/testData/" + str + ".xlsx");

        //获取第index张工作表
        Worksheet sheet = wb.getWorksheets().get(sheetIndex);

        //获取工作表中的数据区域
        CellRange locatedRange = sheet.getAllocatedRange();

        width = locatedRange.getColumnCount();
        height = locatedRange.getRows().length - 1;
        res = new Object[height][width];

        //遍历行和列，第一行为表头，默认从第二行数据开始读取
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int temp = 0;
                try
                {
                    //接受到的是一个数字
                    temp = Integer.parseInt(locatedRange.get(i + 2, j + 1).getValue());
                    res[i][j] = temp;
                }catch (NumberFormatException numberFormatException)
                {
                    //接收到的是一个字符串
                    res[i][j] = locatedRange.get(i + 2, j + 1).getValue();
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Object[][] excelDataTransform = excelDataTransform("collisionTestData", 1);
            for (int i = 0; i < excelDataTransform.length; i++)
            {
                for (int j = 0; j < excelDataTransform[i].length; j++)
                    System.out.print(excelDataTransform[i][j] + "\t");
                System.out.println();
            }
    }
}
