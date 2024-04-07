package com.example.seleniumJava;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

@SpringBootTest
class SeleniumJavaApplicationTests {

	private ExtentReports extentReport;
	private WebDriver webDriver;
	private List<String> lista_usuarios = new ArrayList<>();
	private List<String> lista_contrasennas = new ArrayList<>();

	@BeforeEach
	void getUp() {
		webDriver = new ChromeDriver();
		extentReport = new ExtentReports();
		webDriver.get("https://practicetestautomation.com/practice-test-login/");
		lista_usuarios.add("user1");
		lista_usuarios.add("admin");
		lista_usuarios.add("student");
		lista_usuarios.add("miguel");
		lista_usuarios.add("gheasfy");
		lista_usuarios.add("guest");
		lista_contrasennas.add("securePass");
		lista_contrasennas.add("Password123");
		lista_contrasennas.add("123456");
		lista_contrasennas.add("P@ssw0rd");
		lista_contrasennas.add("rfgdghf");
		lista_contrasennas.add("rsgda");
	}

	@Test
	void TestWebLogin() {	
		try{


			ExtentSparkReporter extentSparkReporter = new ExtentSparkReporter("reports/spark_report_login_status.html");
			
			extentReport.attachReporter(extentSparkReporter);

			ExtentTest testlog = extentReport.createTest("report_login_status");

			Workbook workbook = WorkbookFactory.create(true);

			Sheet sheet = workbook.createSheet("status_usuario_contrasena");

			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue("Status");
			row.createCell(1).setCellValue("Usuario");
			row.createCell(2).setCellValue("Contraseña");
			
			int rowIndex = 1;
			for(int usuario = 0; usuario < lista_usuarios.size(); usuario++ ){

				for(int contrasenna = 0; contrasenna < lista_contrasennas.size(); contrasenna++){
					System.out.println(lista_usuarios.get(usuario));
					System.out.println(lista_contrasennas.get(contrasenna));
					webDriver.findElement(By.id("username")).sendKeys(lista_usuarios.get(usuario));
					webDriver.findElement(By.id("password")).sendKeys(lista_contrasennas.get(contrasenna));
					webDriver.findElement(By.xpath("//*[@id='submit']")).click();

					if (webDriver.getPageSource().contains("Your username is invalid!")){
						System.out.println("Error al realizar el test de login con autenticaciones correctas.");
						testlog.log(Status.FAIL, "Error al intentar logearse, usuario: "+lista_usuarios.get(usuario)+" , contraseña: "+lista_contrasennas.get(contrasenna));
						Row dataRow = sheet.createRow(rowIndex);

						Cell statusCell = dataRow.createCell(0);
						statusCell.setCellValue("Fallido");
						CellStyle redCellStyle = workbook.createCellStyle();
						redCellStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
						redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						statusCell.setCellStyle(redCellStyle);

						dataRow.createCell(1).setCellValue(lista_usuarios.get(usuario));
						dataRow.createCell(2).setCellValue(lista_contrasennas.get(contrasenna));
						rowIndex++;
					}
					else{
						System.out.println("Éxito al realizar el test de login con autenticaciones correctas.");
						testlog.log(Status.PASS, "Éxito al intentar logearse, usuario: "+lista_usuarios.get(usuario)+" , contraseña: "+lista_contrasennas.get(contrasenna));
						webDriver.findElement(By.xpath("//*[@id=\"loop-container\"]")).click();
						Row dataRow = sheet.createRow(rowIndex);

						Cell statusCell = dataRow.createCell(0);
						statusCell.setCellValue("Éxito");
						CellStyle greenCellStyle = workbook.createCellStyle();
						greenCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
						greenCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						statusCell.setCellStyle(greenCellStyle);

						dataRow.createCell(1).setCellValue(lista_usuarios.get(usuario));
						dataRow.createCell(2).setCellValue(lista_contrasennas.get(contrasenna));
						rowIndex++;
					}
				}

			}

			FileOutputStream fileOut = new FileOutputStream("reports/xssf_report_login_status.xlsx");
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			extentReport.flush();
			webDriver.close();
			System.out.println(rowIndex);
		}
		catch (IOException e) {
			// Manejar la excepción aquí
			e.printStackTrace();
			System.out.println("Excepción: " + e.getMessage());
		}
	}
}
