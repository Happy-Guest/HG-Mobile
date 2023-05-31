import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

Mobile.startApplication('C:\\GIT\\Happy_Guest\\HG-Mobile\\happyguest-mobile\\app\\build\\outputs\\apk\\debug\\app-debug.apk', 
    true)


Mobile.setText(findTestObject('null'), 'NotValid', 0)

Mobile.tap(findTestObject('Object Repository/Button/android.widget.Button - Login'), 0)

Mobile.setText(findTestObject('null'), 'XPTO1', 0)

Mobile.switchToNative()

Mobile.switchToNative()

Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - Introduza o seu email'), 0)

Mobile.switchToNative()

Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - Introduza a sua palavra-passe'), 0)

Mobile.switchToNative()

Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - O email inserido no  vlido'), 0)

Mobile.switchToNative()

Mobile.getText(findTestObject('null'), 0)

Mobile.switchToNative()

Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - A palavra-passe inserida est incorreta'), 
    0)

Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - Hotel de Leiria'), 0)

Mobile.closeApplication()

