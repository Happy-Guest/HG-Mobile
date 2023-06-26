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



class Buttons {

	@When("clica na sidebar")
	void utilizador_clica_na_sidebar() {
		//Click on icon sidebar
		Mobile.tap(findTestObject('Object Repository/Button/android.widget.ImageButton_Sidebar'), 0)
	}
	@When("clica no botão {string} no ecrã login")
	void utilizador_clica_no_botão_no_ecra_login(String btn) {
		//Click on the button
		Mobile.tap(findTestObject('Object Repository/Button/android.widget.Button - ' + btn), 0)

	}

	@When("clica no botão {string}")
	void utilizador_clica_no_botão(String btn) {
		//Click on the button
		Mobile.tap(findTestObject('Object Repository/Button/android.widget.Button - ' + btn), 0)

	}

	@When("clica no botão {string} no ecrã registar")
	void utilizador_clica_no_botão_no_ecra_registar(String btn) {
		//Click on the button
		Mobile.tap(findTestObject('Object Repository/Button/android.widget.Button - ' + btn + ' (Register)'), 0)
	}

	@When("clica no botão “Lembrar“ na secção de Login")
	void clica_no_botão_Lembrar_na_secção_de_Login() {
		Mobile.tap(findTestObject('Object Repository/Button/android.widget.CheckBox - Lembrar'), 0)
	}

	@When("utilizador clica no icon de perfil")
	void utilizador_clica_no_icon_de_perfil() {
		Mobile.tap(findTestObject('Object Repository/Button/android.widget.ImageButton_Profile'), 0)
	}

	@When("utilizador clica no ícone de Editar")
	public void utilizador_clica_no_ícone_de_Editar() {
		Mobile.tap(findTestObject('Object Repository/AtualizarPerfil/android.widget.ImageButton'), 0);
	}

	@When("utilizador clica no botão “Atualizar Perfil“")
	public void utilizador_clica_no_botão_Atualizar_Perfil() {
		Mobile.tap(findTestObject('Object Repository/AtualizarPerfil/android.widget.Button - Atualizar Perfil'), 0);
	}
	
	
	@When("utilizador clica no botão “Associar“")
	public void utilizador_clica_no_botão_Associar(String string) {
		Mobile.tap(findTestObject('Object Repository/Inserir_Código/android.widget.Button - Associar'), 0);
	}
	
}