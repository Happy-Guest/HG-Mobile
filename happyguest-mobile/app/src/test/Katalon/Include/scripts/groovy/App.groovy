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



class App {
	@Given("a aplicação está pronta")
    void a_aplicação_está_pronta() {
		//Run app (true -> uninstalling the application automatically after run)
		Mobile.startApplication('C:\\GIT\\Happy_Guest\\HG-Mobile\\happyguest-mobile\\app\\build\\outputs\\apk\\debug\\app-debug.apk',  false)

        //TODO

		//on wifi
		Mobile.toggleWifi('true')

        //Delay 5 seg
		Mobile.delay(5)
    }

	@Given("a aplicação está pronta sem internet")
    void a_aplicação_está_pronta_sem_internet() {
		//Run app
		Mobile.startApplication('C:\\GIT\\Happy_Guest\\HG-Mobile\\happyguest-mobile\\app\\build\\outputs\\apk\\debug\\app-debug.apk',  false)

        //TODO

		//off wifi
		Mobile.toggleWifi('false')

    }

	@Given("a aplicação está pronta e tem sessão iniciada")
    void a_aplicação_está_pronta_e_tem_sessão_iniciada() {
		//Run app
		Mobile.startApplication('C:\\GIT\\Happy_Guest\\HG-Mobile\\happyguest-mobile\\app\\build\\outputs\\apk\\debug\\app-debug.apk',  false)
    }


	@Then("é apresentada a página {string}")
    void é_apresentada_a_página(String title) {
		//identificate home
		if(title == "Home") {
			Mobile.getText(findTestObject('Object Repository/Messages/android.widget.TextView - Hotel de Leiria'), 0)
        }
		else if (title == "Login")
		{
			Mobile.getText(findTestObject('Object Repository/Button/android.widget.Button - Login'), 0)
        }
	}

}