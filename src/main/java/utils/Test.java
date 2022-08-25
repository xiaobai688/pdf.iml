package utils;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.*;
import dto.CommonDTO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    /**
     * 对pdf批量添加签名
     * @param args
     */
    public static void main(String[] args) throws Exception {
        List<CommonDTO> commonDTOList =new ArrayList<>();
        Map<String,String> listMap =new HashMap<String,String>(16);
        ArrayList<String> supply = new ArrayList<String>(0);//用arraylist保存扫描到的路径
        //加载excel文档
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\保密.xlsx");
       Workbook wb = new XSSFWorkbook(fileInputStream);
        //获取工作表
        Sheet sheet = wb.getSheetAt(0);
        for(int i=1 ; i<sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            String s = row.getCell(1).toString().trim();
            supply.add(s);
        }
        Sheet sheet2 = wb.getSheetAt(1);
        for(int i=0 ; i<=sheet2.getLastRowNum(); i++){
            Row row = sheet2.getRow(i);
            CommonDTO commonDTO =new CommonDTO();
            commonDTO.setName(row.getCell(0).toString());
            commonDTO.setText(row.getCell(1).toString());
            commonDTOList.add(commonDTO);
        }
        ArrayList<String> list = new ArrayList<String>(0);//用arraylist保存扫描到的路径
        Scan(list,"E:\\发票\\找不到");
        //循环路径,获取pdf文本,判断文本是否包含对应供应商
        int i=1;
        x:for(String path : list) {
            System.out.println("第" + i +"个,路径: " +path);
            i++;
            //创建PdfDocument实例
            PdfDocument doc = new PdfDocument();
            //加载PDF文件
            doc.loadFromFile(path);
            doc.getPages().add();
//            //创建StringBuilder实例
            StringBuilder sb = new StringBuilder();

            PdfPageBase page;
            //遍历PDF页面，获取每个页面的文本并添加到StringBuilder对象
            page = doc.getPages().get(0);
            doc.getPages().getCount();
            //使用extractImages方法获取指定页上图片
                BufferedImage image = null;
                try {
                    image=doc.saveAsImage(0, PdfImageType.Bitmap, 500, 500);
                    File output = new File(String.format(path.replaceAll("pdf", "jpg").replaceAll("找不到","2021.8")));
                    if (!output.exists()) {
                        ImageIO.write(image, "jpg", output);
                    }
                    //指定输出文件路径及名称

                    //将图片保存为PNG格式文件
                    Tesseract tesseract = new Tesseract();
                    tesseract.setDatapath("E://tessdata");
                    // tesseract.setLanguage("chi_sim");
                    try {
                        sb.append(tesseract.doOCR(output));
                    } catch (TesseractException e) {
                        e.printStackTrace();
                    }
                    try {
                        //要写东西肯定需要笔
                        //创建一个画笔，笔的颜色是黑色的
                        PdfSolidBrush brush1 = new PdfSolidBrush(new PdfRGBColor(Color.black));
                        //你要写什么字体呢？
                        //创建要使用的一个字体
//                        PdfFont newfont = new PdfFont(PdfFontFamily.Zapf_Dingbats, 18f,PdfFontStyle.Italic);

                        String fontFileName = "C:\\Users\\Administrator\\AppData\\Local\\Microsoft\\Windows\\Fonts\\Lucida Handwriting Italic.ttf";
                        PdfTrueTypeFont trueTypeFont = new PdfTrueTypeFont(fontFileName, 18f);
//                        PdfTrueTypeFont font1 = new PdfTrueTypeFont(new Font("Lucida Handwriting", Font.PLAIN, 16), true);
                        //你的文本格式是怎么样的？
                        //创建一个pdf文本格式
                        PdfStringFormat format1 = new PdfStringFormat();
                        //文本左对齐
                        format1.setAlignment(PdfTextAlignment.Right);
                        //你这些肯定要在pdf上进行添加，我们把我们的pdf想象成一个画布
                        //getCanvas()这个就是我们的画布
                        //drawString()我们开始作画
                        //s1 添加的文本, font1 字体格式, brush1 我们用的有颜色的笔,new Point2D.Float()  要放在画布的那个位置, format1 对齐方式
                        String trim = sb.toString().replaceAll(" ", "");
                        String trim1 = trim.replaceAll("0", "O");
                        String s1 = "";
                        int j=0;
                        for (CommonDTO commonDTO : commonDTOList) {
                            String s = commonDTO.getName().replaceAll(" ", "");
                            if (trim1.contains(s)) {
                                j++;
                            }
                        }

                        for (CommonDTO commonDTO : commonDTOList) {
                            String s = commonDTO.getName().replaceAll(" ", "");
                            if(j==1 && trim1.contains("ITALYNIHAOGROUPLIMITED")){
                                s1 = "Ulrica";
                                break ;
                            }else if(j==1 && !trim1.contains("ITALYNIHAOGROUPLIMITED")){
                                if (trim1.contains(s)) {
                                    s1 = commonDTO.getText();
                                    break ;
                                }
                            }else{
                                if (!"ITALYNIHAOGROUPLIMITED".contains(s) && trim1.contains(s)) {
                                    s1 = commonDTO.getText();
                                    break ;
                                }
                            }

                        }
                        System.out.println(trim1);
                        page.getCanvas().drawString(s1, trueTypeFont, brush1, new Point2D.Float((float) (page.getActualSize().getWidth() * 0.7), (float) (page.getActualSize().getHeight() * 0.8)), format1);

                        doc.getPages().remove(doc.getPages().get(doc.getPages().getCount() - 1));

                        //保存文档
                        if (!s1.isEmpty()) {
                            doc.saveToFile(path.replaceAll("找不到", "正式"));
                        } else {
                            doc.saveToFile(path.replaceAll("找不到","找不到 - 副本"));
                        }
                        doc.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    doc.saveToFile(path.replaceAll("找不到", "转图片失败"));
                    doc.close();
                }

        }

    }

    public static void Scan(List<String> list , String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] filenames = file.list();
        if (filenames == null) {
            return;
        }
        for (int i = 0; i < filenames.length; i++) {
            if (files[i].isFile()) {
                if (files[i].getName().endsWith("pdf"))//只获取带png结尾的文件
                {
                    list.add(files[i].getPath());//获取路径
                }
            } else if (files[i].isDirectory()) {
                Scan(list , files[i].getPath());
            }
        }
    }

    private static int imageHeight, imageWidth;
    private static String replaceString = "$@B%8&WM#*oahkbmZO0QJUYXzcvunxrjft/|()1{}[]?-_+~<>i!lI;:,^`'. ";

    private static void imageProcess(String path,StringBuilder imageString) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            imageHeight = image.getHeight();
            imageWidth = image.getWidth();
            for (int height = 0; height < imageHeight; height += 2) {
                for (int width = 0; width < imageWidth; width++) {
                    // 像素点RBG值获取
                    int pixel = image.getRGB(width, height);
                    int R = (pixel & 0xff0000) >> 16;
                    int B = pixel & 0xff;
                    int G = (pixel & 0xff00) >> 8;

                    // 灰度计算公式
                    float pixelGray = 0.299f * R + 0.587f * G + 0.114f * B;
                    int pixelIndex = Math.round(pixelGray * (replaceString.length() + 1) / 255);

                    // 将灰度值转成字符
                    String pixelChar = pixelIndex >= replaceString.length() ? " " : String.valueOf(replaceString.charAt(pixelIndex));

                    // 控制台上输出
                    System.out.print(pixelChar);

                    // 添加到imageString中
                    imageString.append(pixelChar);
                }
                System.out.println();
                imageString.append('\n');
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

}
