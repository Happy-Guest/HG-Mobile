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



class TextBox {
	@When("utilizador insere {string} no campo de email")
	void utilizador_insere_NotValid_no_campo_de_email(String email) {
		//Insert email
		Mobile.scrollToText('Email')
		Mobile.setText(findTestObject('Object Repository/EditText/android.widget.EditText - Email'), email, 0)
	}

	@When("utilizador insere {string} no campo de palavra-passe")
	void utilizador_insere_XPTO_no_campo_de_palavra_passe(String password) {
		//Insert password
		Mobile.scrollToText('Palavra-passe')
		Mobile.setText(findTestObject('Object Repository/EditText/android.widget.EditText - Palavra-passe'), password, 0)
	}

	@When("utilizador insere {string} no campo do nome")
	void utilizador_insere_no_campo_do_nome(String name) {
		Mobile.scrollToText('Nome')
		Mobile.setText(findTestObject('Object Repository/EditText/android.widget.EditText - Nome'), name, 0)
	}

	@When("utilizador insere {string} no campo de confirmar palavra-passe")
	void utilizador_insere_no_campo_de_confirmar_palavra_passe(String pass_conf) {
		Mobile.scrollToText('Confirmar Palavra-passe')
		Mobile.setText(findTestObject('Object Repository/EditText/android.widget.EditText - Confirmar Palavra-passe'), pass_conf, 0)
	}

	@When("utilizador insere {string} no campo de nº telefone")
	void utilizador_insere_no_campo_de_n_telefone(String phone) {
		Mobile.scrollToText('Nº Telefone')
		Mobile.setText(findTestObject('Object Repository/EditText/android.widget.EditText - N Telefone'), phone, 0)
	}

	@When("utilizador insere {string} no campo do nome perfil")
	void utilizador_insere_no_campo_do_nome_perfil(String name) {
		Mobile.scrollToText('Nome')
		Mobile.setText(findTestObject('Object Repository/AtualizarPerfil/android.widget.EditText - XPTO'), name , 0)
	}
	@When("utilizador insere {string} no campo de email perfil")
	void utilizador_insere_NotValid_no_campo_de_email_perfil(String email) {
		//Insert email
		Mobile.scrollToText('Email')
		Mobile.setText(findTestObject('Object Repository/AtualizarPerfil/android.widget.EditText - XPTOmail.pt'),email, 0)
	}

	@When("utilizador insere {string} no campo de nº telefone perfil")
	void utilizador_insere_no_campo_de_n_telefone_perfil(String phone) {
		Mobile.scrollToText('Nº Telefone')
		Mobile.setText(findTestObject('Object Repository/AtualizarPerfil/android.widget.EditText - 999999999'), phone, 0)
	}

	@When("utilizador insere {string} no campo de morada perfil")
	public void utilizador_insere_no_campo_de_morada_perfil(String address) {
		Mobile.setText(findTestObject('Object Repository/AtualizarPerfil/android.widget.EditText - Morada'), address, 0);
	}

	@When("utilizador insere {string} no campo de data de nascimento perfil")
	public void utilizador_insere_no_campo_de_data_de_nascimento_perfil(String date) {
		Mobile.setText(findTestObject('Object Repository/AtualizarPerfil/android.widget.EditText - Data Nascimento'), date, 0);
	}

	@When("utilizador insere {string} no campo de código")
	public void utilizador_insere_no_campo_de_código(String code) {
		Mobile.scrollToText('Código')
		Mobile.setText(findTestObject('Object Repository/Inserir_Código/android.widget.EditText - Cdigo'), code, 0)
	}
}