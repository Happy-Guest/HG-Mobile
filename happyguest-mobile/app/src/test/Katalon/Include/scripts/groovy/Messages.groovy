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
	void escreve_no_ecrã(String message, Integer number) {
		switch(number) {
			case 1:
			//Sem ligação à internet
				AppiumDriver<?> driver = MobileDriverFactory.getDriver()
				driver.findElementByXPath("//android.widget.Toast[@text='Sem ligação à internet!']")
				break
			case 2:
			//Introduza o seu email
				Mobile.scrollToText('Email')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - Introduza o seu email'), 0)
				break
			case 3:
			//Introduza a sua palavra-passe
				Mobile.scrollToText('Introduza a sua palavra-passe')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - Introduza a sua palavra-passe'), 0)
				break
			case 4:
			//O email não é válido
				Mobile.scrollToText('Email')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - O email no  vlido'), 0)
				break
			case 5:
			//O email inserido não está registado
				Mobile.scrollToText('Email')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - O email inserido no est registado'), 0)
				break
			case 6:
			//A palavra-passe inserida está incorreta
				Mobile.scrollToText('Palavra-passe')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - A palavra-passe inserida est incorreta'), 0)
				break
			case 7:
			//A sua conta foi bloqueada, por favor contacte o administrador!
				AppiumDriver<?> driver = MobileDriverFactory.getDriver()
				driver.findElementByXPath("//android.widget.Toast[@text='A sua conta foi bloqueada, por favor contacte o administrador!']")
				break
			case 8:
			//Não tem permissões para aceder a esta aplicação.
				AppiumDriver<?> driver = MobileDriverFactory.getDriver()
				driver.findElementByXPath("//android.widget.Toast[@text='Não tem permissões para aceder a esta aplicação.']")
				break
			case 9:
			//Introduza o seu nome.
				Mobile.scrollToText('Nome')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - Introduza o seu nome'), 0)
				break
			case 10:
			//O nome é demasiado curto
				Mobile.scrollToText('Nome')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - O nome  demasiado curto'), 0)
				break
			case 11:
			//O Nº telefone não é válido!
				Mobile.scrollToText('Nº Telefone')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - O N telefone no  vlido'), 0)
				break
			case 12:
			//A palavra-passe demasiado curta
				Mobile.scrollToText('Palavra-passe')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - A palavra-passe  demasiado curta'), 0)
				break
			case 13:
			//Confirme a sua palavra-passe.
				Mobile.scrollToText('Confirmar Palavra-passe')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - Confirme a sua palavra-passe'), 0)
				break
			case 14:
			//A confirmação  não corresponde
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - A confirmao no corresponde'), 0)
				break
			case 15:
			//O email já se encontra registado
				Mobile.scrollToText('Email')
				Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - O email j se encontra registado'), 0)
				break
			case 16:
			//Sessão restaurada com sucesso.
				AppiumDriver<?> driver = MobileDriverFactory.getDriver()
				driver.findElementByXPath("//android.widget.Toast[@text='Sessão restaurada com sucesso.']")
				break
		}
	}
}