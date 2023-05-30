import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import io.appium.java_client.AppiumDriver


class Messages {
	@Then("escreve no ecrã {string} - {int}")
	public void escreve_no_ecrã(String message, Integer number) {
		switch(number) {
			case 1:
				//Sem ligação à internet
				AppiumDriver<?> driver = MobileDriverFactory.getDriver()
				driver.findElementByXPath("//android.widget.Toast[@text='Sem ligação à internet!']")
				break;
			case 2:
				//Introduza o seu email
				Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - Introduza o seu email'), 0);
				break;
			case 3:
				//Introduza a sua palavra-passe
				Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - Introduza a sua palavra-passe'), 0);
				break;
			case 4:
				//O email inserido não é válido 
				Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - O email inserido no  vlido'), 0);
				break;
			case 5:
				//O email inserido não está registado
				Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - O email inserido no est registado'), 0);
				break;
			case 6:
				//A palavra-passe inserida está incorreta
				Mobile.getText(findTestObject('Object Repository/Login/android.widget.TextView - A palavra-passe inserida est incorreta'), 0);
				break;
			case 7:
				//A sua conta foi bloqueada, por favor contacte o administrador!
				AppiumDriver<?> driver = MobileDriverFactory.getDriver()
				driver.findElementByXPath("//android.widget.Toast[@text='A sua conta foi bloqueada, por favor contacte o administrador!']")
				break;
			case 8:
				//Não tem permissões para aceder a esta aplicação.
				AppiumDriver<?> driver = MobileDriverFactory.getDriver()
				driver.findElementByXPath("//android.widget.Toast[@text='Não tem permissões para aceder a esta aplicação.']")
				break;
		}
	}
}